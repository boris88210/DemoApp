package com.chailease.tw.app.android.update;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.chailease.tw.app.android.commonlib.R;
import com.chailease.tw.app.android.update.parcelable.LoginDataParcelable;
import com.chailease.tw.app.android.utils.LogUtility;

import static com.chailease.tw.app.android.update.UpdateInstaller.TAG_DOWNLOAD_URL;
import static com.chailease.tw.app.android.update.UpdateInstaller.TAG_FILE_PROVIDER_AUTHOR;
import static com.chailease.tw.app.android.update.UpdateInstaller.TAG_LOGIN_DATA;
import static com.chailease.tw.app.android.update.UpdateInstaller.TAG_RES_LAYOUT;



public class UpdateActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 20;
    UpdateInstaller mInstaller;
    private String apkUrl;
    private Dialog mProgressDialog;
    private Dialog mUpdateInfoDialog;
    private Dialog mDangerousPermissionWarning;
    private Dialog mWarningDialog;
    private boolean isDownloadComplete;

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取得UpdateInstaller
//        mInstaller = UpdateInstaller.getInstance(this, null);

        mInstaller = new UpdateInstaller(this, getIntent().getStringExtra(TAG_FILE_PROVIDER_AUTHOR));
        //指定layout
        setActivityLayout(savedInstanceState);
        //設定需要初始化的資料
        initData();

        //設定未取得權限提示 dialog
//        mDangerousPermissionWarning = setDangerousPermissionWarning();
        CreateDangerousPermissionWarningDialog();

        //設定進度Progress dialog
//        mProgressDialog = setProgressDialog();
        CreateProgressDialog();

        //將progress dialog樣式設定給installer
        mInstaller.setProgressDialog(mProgressDialog);

        //設定下載不安裝警告 dialog
//        mWarningDialog = setWarningDialog();
        createWarningDialog();
        obtainDownloadUrl();
        //判斷是否需要下載
        if (mInstaller.needToDownloadApk()) {
            //取得下載路徑
//            obtainDownloadUrl();
            //設定版本更新提示 dialog
            mUpdateInfoDialog = setDialog();
            showUpdateInfo();//顯示更版提示訊息
        } else {
            mInstaller.installApk();//安裝apk
        }
    }

    //處裡Intent傳來的資料
    protected void initData() {

    }

    //設定activity layout & UI
    protected void setActivityLayout(Bundle savedInstanceState) {
        int layoutId = getIntent().getIntExtra(TAG_RES_LAYOUT, -1);
        if (layoutId != -1) {
            setContentView(layoutId);
        } else {
            setContentView(R.layout.activity_update);
        }
    }

    //可客製化訊息
    protected Dialog setDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = alertBuilder.setTitle("版本更新")
                .setMessage("已有新版本提供下載\n警告: 在未更新前將無法使用APP，請斟酌網路環境選擇是否下載")
                .setNegativeButton("關閉APP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();//關閉APP
                    }
                })
                .setPositiveButton("下載更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        showProgress();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkStoragePermission();
                        } else {
                            mInstaller.downlaodApk(apkUrl);
                        }
                    }
                })
                .create();
        return alertDialog;
    }


    public  void showUpdateInfo() {
        mUpdateInfoDialog.show();
    }

    protected void obtainDownloadUrl() {
        LoginDataParcelable data = (LoginDataParcelable) getIntent().getParcelableExtra(TAG_LOGIN_DATA);
        if (null != data) {
            apkUrl = data.APP_URL;
        } else {
            apkUrl = getIntent().getStringExtra(TAG_DOWNLOAD_URL);
        }
        LogUtility.debug(this, "obtainDownloadUrl()", "Download Url: " + apkUrl);
    }

    protected void CreateProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("更新檔下載中");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog = progressDialog;
    }

    //可客製化進度條
    @Deprecated
    protected Dialog setProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("更新檔下載中");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        return progressDialog;
    }

    //顯示進度條
    protected void showProgress() {
        mProgressDialog.show();
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mInstaller.downlaodApk(apkUrl);
                } else {
                    mDangerousPermissionWarning.show();
                }
                break;
        }
    }

    @Deprecated
    protected Dialog setDangerousPermissionWarning() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UpdateActivity.this);
        AlertDialog dialog = alertBuilder.setTitle("版本更新")
                .setMessage("若無權限，此APP將進行無法更新")
                .setPositiveButton("重新獲取權限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkStoragePermission();

                    }
                })
                .setNegativeButton("關閉APP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();//關閉APP
                    }
                }).create();
        return dialog;
    }

    protected void CreateDangerousPermissionWarningDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UpdateActivity.this);
        AlertDialog dialog = alertBuilder.setTitle("版本更新")
                .setMessage("若無權限，此APP將進行無法更新")
                .setPositiveButton("重新獲取權限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkStoragePermission();

                    }
                })
                .setNegativeButton("關閉APP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();//關閉APP
                    }
                }).create();
        mDangerousPermissionWarning = dialog;
    }


    public void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.REQUEST_INSTALL_PACKAGES},
                    PERMISSION_REQUEST_CODE);
        } else {
            mInstaller.downlaodApk(apkUrl);
        }
    }

    @Deprecated
    protected Dialog setWarningDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog warningDialog = alertBuilder.setTitle("版本更新")
                .setMessage("警告: 在未更新前將無法使用APP!")
                .setNegativeButton("關閉APP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInstaller.close();
                        finishAffinity();//關閉APP
                    }
                })
                .setPositiveButton("安裝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(UpdateActivity.this, "開始安裝更新", Toast.LENGTH_SHORT).show();
                        mInstaller.installApk();
                    }
                })
                .create();
        return warningDialog;
    }

    protected void createWarningDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog warningDialog = alertBuilder.setTitle("版本更新")
                .setMessage("警告: 在未更新前將無法使用APP!")
                .setNegativeButton("關閉APP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInstaller.close();
                        finishAffinity();//關閉APP
                    }
                })
                .setPositiveButton("安裝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(UpdateActivity.this, "開始安裝更新", Toast.LENGTH_SHORT).show();
                        mInstaller.installApk();
                    }
                })
                .setNeutralButton("重新下載", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(UpdateActivity.this, "開始重新下載", Toast.LENGTH_SHORT).show();
                        mInstaller.reDownload(apkUrl);
                    }
                })
                .create();
        mWarningDialog = warningDialog;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isDownloadComplete) {
            mWarningDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isDownloadComplete = true;
    }


}
