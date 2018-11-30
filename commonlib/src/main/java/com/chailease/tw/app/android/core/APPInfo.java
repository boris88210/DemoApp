package com.chailease.tw.app.android.core;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.chailease.tw.app.android.constants.SystemConstants;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import static com.chailease.tw.app.android.constants.SystemConstants.LOG_TAG_INIT;


/**
 *
 */
public class APPInfo {

	final static APPInfo APP_INFO = new APPInfo();

	boolean inited = false;
	private String APP_PACKAGE_NAME;
	private String APP_VERSION_NAME;
	private int APP_VERSION;
	private String APP_ID;
	private String PREF_CRYPT_KEY;
	private String prefCKey;
	private String APP_BUILD_TYPE;

	private String ApiClientClassName;

	public static APPInfo getAppInfo(IBaseApplication context) {
		return getAppInfo((Application) context);
	}
	public static synchronized APPInfo getAppInfo(Context context) {
		if (!APP_INFO.inited) {
			PackageManager manager = context.getPackageManager();
			try {
				PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
				ApplicationInfo appInfo = manager.getApplicationInfo(context.getPackageName(),
						PackageManager.GET_META_DATA);
				APP_INFO.APP_VERSION_NAME = info.versionName; //版本名
				APP_INFO.APP_VERSION = info.versionCode;
				APP_INFO.APP_PACKAGE_NAME = info.packageName;
				APP_INFO.APP_ID = readMetaData(appInfo, SystemConstants.APP_META_DATA_NAME_APP_ID);
				APP_INFO.ApiClientClassName = readMetaData(appInfo, SystemConstants.APP_META_DATA_NAME_API_CLIENT_FACTORY);
				APP_INFO.PREF_CRYPT_KEY = readMetaData(appInfo, SystemConstants.APP_META_DATA_NAME_PREF_CRYPT_KEY);
				APP_INFO.prefCKey = StringUtils.isBlank(APP_INFO.PREF_CRYPT_KEY) ? SystemConstants.DEMO_SECU_KEY : APP_INFO.PREF_CRYPT_KEY;
			} catch (PackageManager.NameNotFoundException e) {
				LogUtility.error(APPInfo.class, "getAppInfo", "load APP INFO failure", e);
			}
		}
		return APP_INFO;
	}
	private static String readMetaData(ApplicationInfo appInfo, String key) {
		try {
			return appInfo.metaData.getString(key);
		} catch (Exception e) {
			LogUtility.warn(APPInfo.class, "readMetaData", "Package Meta Data not found >>" + key, e);
			return null;
		}
	}
	public static void setBuildType(String bt) {
		if (null != APP_INFO && !StringUtils.isBlank(APP_INFO.APP_BUILD_TYPE))
			APP_INFO.APP_BUILD_TYPE = bt;
	}

	public String getAPP_PACKAGE_NAME() {
		return APP_PACKAGE_NAME;
	}

	public String getAPP_VERSION_NAME() {
		return APP_VERSION_NAME;
	}

	public int getAPP_VERSION() {
		return APP_VERSION;
	}

	public String getAPP_ID() {
		return APP_ID;
	}

	public String getApiClientClassName() {
		return ApiClientClassName;
	}

	public String getPREF_CRYPT_KEY() {
		return prefCKey;
	}

	public String getAPP_BUILD_TYPE() {
		return APP_BUILD_TYPE;
	}

}