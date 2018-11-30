package com.chailease.tw.app.android.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.chailease.tw.app.android.core.IBaseApplication;
import com.chailease.tw.app.android.utils.LogUtility;

/**
 * 此類別專屬於update這個功能使用，用來記錄目前APP的目前APP的版號及新版APP
 *
 */

public class SharedPreferencesHelper {

    public final String DOWNLOAD_ID = "LoginID";
    public final String CURRENT_VERSION = "CurrentVersion";
    public final String DOWNLOAD_URL = "Download_Url";

    private String spName;

    public SharedPreferences setting;
    public SharedPreferences.Editor editor;

    private IBaseApplication application;
    private Context mContext;

    public SharedPreferencesHelper(Context context, String sharedPreferneceName) {
//        Context ApplicationContext = context.getApplicationContext();
//        if (ApplicationContext instanceof IBaseApplication) {
//            application = (IBaseApplication) context;
//        }
        mContext = context;
        spName = sharedPreferneceName;
        setting = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        editor = setting.edit();
    }

    public String getSharedPreferneceName() {
        return spName;
    }

    public void cleanAllData() {
        editor.clear();
        editor.commit();
    }


    public void setDownloadId(long id) {
        editor.putLong(DOWNLOAD_ID, id);
        editor.commit();
//        application.setPrefValue(DOWNLOAD_ID, String.valueOf(id));
    }

    public long getDownloadId() {
        return setting.getLong(DOWNLOAD_ID, -1);
//        return application.getPrefValue(DOWNLOAD_ID);
    }

    public void setCurrentVersion() {
        String CurrentAppVersion = "";
        try {
            PackageInfo packageInfo;
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            CurrentAppVersion = String.valueOf(packageInfo.versionCode);
            LogUtility.debug(this, "setCurrentVersion", "CurrentAppVersion: " + CurrentAppVersion);
            editor.putString(CURRENT_VERSION, CurrentAppVersion);
            editor.commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        application.setPrefValue(CURRENT_VERSION, currentVersion);
    }

    public String getCurrentVersion() {
//        return application.getPr下載的URLefValue(CURRENT_VERSION);
        return setting.getString(CURRENT_VERSION, "-1");
    }

    public void setCurrentVersion4TEST() {
        String CurrentAppVersion = "";
        editor.putString(CURRENT_VERSION, CurrentAppVersion);
        editor.commit();
    }

    public void setDownloadUrl(String url) {
        editor.putString(DOWNLOAD_URL, url);
        editor.commit();
    }

    public String getDownloadUrl() {
        return setting.getString(DOWNLOAD_URL, "");
    }
}
