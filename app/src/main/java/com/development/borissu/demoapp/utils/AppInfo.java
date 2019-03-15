package com.development.borissu.demoapp.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 彙整APP相關資訊，請使用AppInfo.getInstance()取得物件
 * 並於Application.onCreate()中使用loadAppInfo()
 */
public class AppInfo {
    //-- Attribute --
    private String APP_ID;
    private int APP_VERSION_CODE;
    private long APP_LONG_VERSION_CODE;
    private String APP_VERSION_NAME;
    private String APP_PACKAGE_NAME;
    private boolean LOGGABLE;

    //Singleton設計
    private static AppInfo INSTANCE;

    private AppInfo() {
    }

    /**
     * 取得AppInfo的singleton物件
     *
     * @return AppInfo的singleton物件
     */
    public static AppInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppInfo();
        }
        return INSTANCE;
    }

    /**
     * 將APP資訊讀入AppInfo物件中
     *
     * @param context 任何Context，用來幫助AppInfo取得資訊
     */
    public static void loadAppInfo(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            ApplicationInfo appInfo = manager.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);

            INSTANCE.APP_ID = appInfo.metaData.getString("APP_ID");
            INSTANCE.APP_VERSION_NAME = info.versionName; //版本名
            //版本號碼
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                INSTANCE.APP_LONG_VERSION_CODE = info.getLongVersionCode();
                if (INSTANCE.APP_LONG_VERSION_CODE < Integer.MAX_VALUE) {
                    INSTANCE.APP_VERSION_CODE = info.versionCode;
                }
            } else {
                INSTANCE.APP_VERSION_CODE = info.versionCode;
                INSTANCE.APP_LONG_VERSION_CODE = INSTANCE.APP_VERSION_CODE;
            }
            INSTANCE.APP_PACKAGE_NAME = info.packageName;
            INSTANCE.LOGGABLE = appInfo.metaData.getBoolean("Loggable");
//            INSTANCE.ApiClientClassName = readMetaData(appInfo, SystemConstants.APP_META_DATA_NAME_API_CLIENT_FACTORY);
//            INSTANCE.PREF_CRYPT_KEY = readMetaData(appInfo, SystemConstants.APP_META_DATA_NAME_PREF_CRYPT_KEY);
//            INSTANCE.prefCKey = StringUtils.isBlank(APP_INFO.PREF_CRYPT_KEY) ? SystemConstants.DEMO_SECU_KEY : APP_INFO.PREF_CRYPT_KEY;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtility.error("load APP INFO failure", e);
        }
    }


    //--- Getter ---

    public String getAPP_ID() {
        return APP_ID == null ? "" : APP_ID;
    }

    public int getAPP_VERSION_CODE() {
        return APP_VERSION_CODE;
    }

    public long getAPP_LONG_VERSION_CODE() {
        return APP_LONG_VERSION_CODE;
    }

    public String getAPP_VERSION_NAME() {
        return APP_VERSION_NAME == null ? "" : APP_VERSION_NAME;
    }

    public String getAPP_PACKAGE_NAME() {
        return APP_PACKAGE_NAME == null ? "" : APP_PACKAGE_NAME;
    }

    public boolean isLOGGABLE() {
        return LOGGABLE;
    }
}
