package com.chailease.tw.app.android.update;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;


import com.chailease.tw.app.android.data.api.LoginData;
import com.chailease.tw.app.android.update.parcelable.LoginDataParcelable;
import com.chailease.tw.app.android.utils.LogUtility;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 此類別為APP更新功能，包含下載、安裝
 */

public class UpdateInstaller {

    private static final String CURRENT_VERSION = "CURRENT_VERSION";
    private static final String SHAREDPREFERENCE_NAME = "UPDATE_INFO";
    private SharedPreferencesHelper mSpHelper;

    private Context mContext;
    private Class<? extends UpdateActivity> updateActivity;
    private int resLayoutId = -1;
    public static final String TAG_RES_LAYOUT = "TAG_RES_LAYOUT";
    public static final String TAG_LOGIN_DATA = "LOGIN_DATA";
    public static final String TAG_DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String TAG_FILE_PROVIDER_AUTHOR = "FILE_PROVIDER_AUTHOR";

    private DownloadManager mDownloadManager;
    private DownloadManager.Request mDownloadRequest;
    private DownloadCompleteReceiver downloadCompleteReceiver;

    private Dialog mProgressDialog;

    public static final String APK_FILE_TYPE = "application/vnd.android.package-archive";
    final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private long latestDownloadID;//紀錄下載ID，用來觸發APK的安裝
    private DownloadObserver mDownloadObserver;//用來監控下載進度

    private String fileProviderAuthor;
    private boolean isRegistered;



    public UpdateInstaller(Context context, String fileProviderAuthor) {
        mContext = context;//不可用ApplicationContext
        mSpHelper = new SharedPreferencesHelper(context, SHAREDPREFERENCE_NAME);
        this.fileProviderAuthor = fileProviderAuthor;
    }

    @Deprecated
    public static UpdateInstaller getInstance(Context context, @Nullable String fileProviderAuthor) {
        UpdateInstaller INSTANCE = new UpdateInstaller(context, fileProviderAuthor);
        return INSTANCE;
    }


    //可set專案的updateActivity
    public void setupdateActivity(Class<? extends UpdateActivity> customUpdateActivity) {
        updateActivity = customUpdateActivity;
    }

    public void setProgressDialog(Dialog progressDialog) {
        mProgressDialog = progressDialog;
    }

    //確認是否需要更新版本，使用version code比較，若相同則需要安裝已下載的更新檔
    public boolean needToUpdate() {
        //取得SharedPreference比對版本
        String spAppVersion = mSpHelper.getCurrentVersion();
        String currentAppVersion = "";

        try {
            PackageInfo packageInfo;
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            currentAppVersion = String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtility.debug(this, "needToUpdate()", "SP App Version: " + spAppVersion);
        LogUtility.debug(this, "needToUpdate()", "Current App Version: " + currentAppVersion);
        return currentAppVersion.equals(spAppVersion);
    }

    public boolean needToDownloadApk() {
        return !needToUpdate();
    }

    public void setUpdateActivityLayout(@LayoutRes int layoutResID) {
        resLayoutId = layoutResID;
    }

    //跳轉至更新頁面，傳送login資訊
    public void gotoUpdateActivity(@Nullable LoginData.LOGIN_DATA loginData) {
        if (null == updateActivity) {
            updateActivity = UpdateActivity.class;
        }
        Intent it = new Intent(mContext, updateActivity);
//        it.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if (null != loginData) {
            LoginDataParcelable data = creatParcelabelData(loginData);
            it.putExtra(TAG_LOGIN_DATA, data);
        }
        it.putExtra(TAG_FILE_PROVIDER_AUTHOR, fileProviderAuthor);
        it.putExtra(TAG_RES_LAYOUT, resLayoutId);
        mContext.startActivity(it);
    }

    //跳轉至更新頁面，可用於主動發動下載
    public void gotoUpdateActivity(String downloadUrl) {
        if (null == updateActivity) {
            updateActivity = UpdateActivity.class;
        }
        Intent it = new Intent(mContext, updateActivity);
//        it.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if (null != downloadUrl) {
            it.putExtra(TAG_DOWNLOAD_URL, downloadUrl);
        }
        it.putExtra(TAG_FILE_PROVIDER_AUTHOR, fileProviderAuthor);
        it.putExtra(TAG_RES_LAYOUT, resLayoutId);
        mContext.startActivity(it);
    }


    //將目前APP版本(version code)記錄在SharedPreference
    public void commitCurrentAppVersion() {
        mSpHelper.setCurrentVersion();
    }

    //清除SharedPreference的紀錄
    public void cleanSharedPreference() {
//        if (mContext instanceof BaseSDexApplication) {
//            BaseSDexApplication baseApplication = (BaseSDexApplication) mContext;
//            baseApplication.setPrefValue(CURRENT_VERSION, "");
//        }
        mSpHelper.cleanAllData();
    }

    //下載新版本APK
    public void downlaodApk(String apkUrl) {
        mProgressDialog.show();
        mDownloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        if (apkUrl == null) {
            apkUrl = mSpHelper.getDownloadUrl();
        }
        Uri uri = Uri.parse(apkUrl);
        mDownloadRequest = new DownloadManager.Request(uri);
        mDownloadRequest.setMimeType(APK_FILE_TYPE);//設定MIME為APK檔案
        downloadManagerEnqueue();
    }

    //發動安裝APK
    public void installApk() {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        //檔案路徑
        File newVersionApk = queryDownloadApk(mContext);
        LogUtility.debug(this, "installApk()", "newVersionApk file path: " + newVersionApk.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//Android 7以上需要使用FileProvider
            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (newVersionApk != null) {
                Uri contentUri = FileProvider.getUriForFile(mContext, fileProviderAuthor, newVersionApk);
                installIntent.setDataAndType(contentUri, APK_FILE_TYPE);
            }
        } else {
            installIntent.setDataAndType(Uri.fromFile(newVersionApk), APK_FILE_TYPE);
            installIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(installIntent);
    }

    private File queryDownloadApk(Context context) {
        File targetApk = null;
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = mSpHelper.getDownloadId();
        LogUtility.debug(this, "queryDownloadApk()", "query download id: " + downloadId);
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cursor = dm.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (!TextUtils.isEmpty(uriString)) {
                    targetApk = new File(Uri.parse(uriString).getPath());
                }
                cursor.close();
            }
        }
        return targetApk;
    }

    //刪除APK檔案
    public void deleteApkFile() {
        File file = queryDownloadApk(mContext);
        if (null == file) {
            return;
        }
        LogUtility.debug(this, "deleteApkFile()", "delete File, File exists: " + (file.exists()));
        if (file.exists()) {
            boolean success = file.delete();
            LogUtility.debug(this, "deleteApkFile()", "delete File: " + success);
        }
    }

    public void downloadManagerEnqueue() {
        //設定儲存位置
        mDownloadRequest.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS + "/update", "NewVersion.apk");
        //註冊DownloadObserver，監控下載進度
        mDownloadObserver = new DownloadObserver(null);
        mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, mDownloadObserver);
        //註冊Receiver，確認下載完畢後啟動APK
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        mContext.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        isRegistered = true;
        //取得Download ID
        latestDownloadID = mDownloadManager.enqueue(mDownloadRequest);
        mSpHelper.setDownloadId(latestDownloadID);
    }

    public void close() {
        if (isRegistered) {
            mContext.unregisterReceiver(downloadCompleteReceiver);
        }
    }

    //監控進度
    protected class DownloadObserver extends ContentObserver {

        public DownloadObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query downloadQuery = new DownloadManager.Query();
            downloadQuery.setFilterById(latestDownloadID);//根據ID來指定監控目標
            final Cursor cursor = mDownloadManager.query(downloadQuery);
            if (cursor != null && cursor.moveToFirst()) {
                final int totolColumns = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalSize = cursor.getInt(totolColumns);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                final int progress = Math.round(percent * 100);
//                runOnUiThread(new Runnable() {//確保在UI thread執行
//                    @Override
//                    public void run() {
//                        updateProgress(progress);
//                        if (progress == 100) {
//                            mProgressDialog.dismiss();
//                        }
//                    }
//                });
                updateProgress(totalSize, currentSize, progress);
            }
            super.onChange(selfChange);
        }
    }

    public void updateProgress(int totalSize, int currentSize, int progress) {
        LogUtility.debug(this, "updateProgress()", "Download Progressing totalSize: " + totalSize);
        LogUtility.debug(this, "updateProgress()", "Download Progressing currentSize: " + currentSize);
        LogUtility.debug(this, "updateProgress()", "Download Progressing: " + progress);
        ProgressDialog progressDialog = (ProgressDialog) mProgressDialog;
        progressDialog.setMax(100);
        progressDialog.setProgress(progress);
    }

    public class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long cacheDownloadId = mSpHelper.getDownloadId();
            LogUtility.debug(this, "onReceive()", "Download complete!");
            LogUtility.debug(this, "onReceive()", "downloadId: " + downloadId);
            LogUtility.debug(this, "onReceive()", "cacheDownloadId: " + cacheDownloadId);
            if (cacheDownloadId == downloadId) {
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                File newVersionApk = queryDownloadApk(context);
//                LogUtility.debug(this, "onReceive()", "FilePath:  " + newVersionApk.getAbsolutePath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//Android 7以上需要使用FileProvider
                    installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (newVersionApk != null) {
                        LogUtility.debug(this, "onReceive()", "FileProviderAuthor:  " + fileProviderAuthor);
                        Uri contentUri = FileProvider.getUriForFile(context,
                                fileProviderAuthor, newVersionApk);
                        installIntent.setDataAndType(contentUri, APK_FILE_TYPE);
                    }
                } else {
                    installIntent.setDataAndType(Uri.fromFile(newVersionApk), APK_FILE_TYPE);
                    installIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                }
                if (newVersionApk != null) {
                    mProgressDialog.dismiss();
                    commitCurrentAppVersion();
                    context.startActivity(installIntent);
                }
            }
        }

        private File queryDownloadApk(Context context) {
            File targetApk = null;
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = mSpHelper.getDownloadId();
            if (downloadId != -1) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                Cursor cursor = dm.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApk = new File(Uri.parse(uriString).getPath());
                    }
                    cursor.close();
                }
            }
            return targetApk;
        }
    }

    //資料轉移
    protected LoginDataParcelable creatParcelabelData(LoginData.LOGIN_DATA loginData) {
        LoginDataParcelable data = new LoginDataParcelable();
        data.MEMBER_ID = loginData.MEMBER_ID;
        data.CUSTNAME = loginData.CUSTNAME;
        data.APPROVE_POLICY = loginData.APPROVE_POLICY;
        data.VERIFY_CODE = loginData.VERIFY_CODE;
        data.IS_NEWAPP = loginData.IS_NEWAPP;
        data.APP_URL = loginData.APP_URL;
        data.USR_EMAIL = loginData.USR_EMAIL;
        data.DEPT_LVL = loginData.DEPT_LVL;
        data.DEPT_ID = loginData.DEPT_ID;
        data.DEPT_CD = loginData.DEPT_CD;
        data.DEPT_NME = loginData.DEPT_NME;
        data.DEPT_HD_USR_ID = loginData.DEPT_HD_USR_ID;
        data.DEPT_COMP_ID = loginData.DEPT_COMP_ID;
        data.COMP_SHRT_NME = loginData.COMP_SHRT_NME;
        data.MVPN = loginData.MVPN;
        return data;
    }

    public void reDownload(String apkUrl) {
        deleteApkFile();
        cleanSharedPreference();
        downlaodApk(apkUrl);
    }


}
