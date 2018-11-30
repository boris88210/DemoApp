package com.chailease.tw.app.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Access Shared-Preferences
 */
public class PreferenceTool {

	/*解密金鑰*/
	private static final String CRYPT_KEY = "_@*#9de_";

	public static void setSharedPrefernce(String key, String value, Context context, PrefernceKeeper keeper) {
		setSharedPrefernce(new String[][]{{key, value}}, context, keeper);
	}
	public static void setSharedPrefernce(String[][] keyValues, Context context, PrefernceKeeper keeper) {

		SharedPreferences pref = context.getSharedPreferences(keeper.prefName,
				keeper.prefMode);
		SharedPreferences prefBack = context.getSharedPreferences(keeper.prefName+"_BAK",
				keeper.prefMode);

		synchronized (keeper) {
			for (String[] prefData : keyValues) {
				String key = prefData[0];
				String value = prefData[1];
				String data = Encrypt.encrypt(value, keeper.cryptKey);
				/* 回寫資料 */
				pref.edit().putString(key, data).commit();
				/* 備份資料 */
				prefBack.edit().putString(key, data).commit();
			}
		}

	}
	public static void clear(Context context, PrefernceKeeper keeper, boolean clearBak) {
		SharedPreferences pref = context.getSharedPreferences(keeper.prefName,
				keeper.prefMode);
		SharedPreferences prefBack = context.getSharedPreferences(keeper.prefName+"_BAK",
				keeper.prefMode);

		synchronized (keeper) {
			/* 回寫資料 */
			pref.edit().clear().commit();
			/* 備份資料 */
			if (clearBak)
				prefBack.edit().clear().commit();
		}
	}

	public static String getSharedPrefernce(String key, Context context, PrefernceKeeper keeper) {
		return getSharedPrefernce(key, context, keeper, true);
	}
	public static String getSharedPrefernce(String key, Context context, PrefernceKeeper keeper, boolean checkBak) {

		SharedPreferences pref=context
				.getSharedPreferences(keeper.prefName, keeper.prefMode);

		if (LogUtility.isCompilerLog)
			LogUtility.d(PreferenceTool.class.getName()
				, "getSharedPrefernce"
				, pref.getAll().toString());

		/*取得使用者名稱  */
		String value = pref.getString(key, "");
		if (LogUtility.isCompilerLog)
			LogUtility.d(PreferenceTool.class.getName()
				, "getSharedPrefernce"
				, key+"="+value);
		/* 確認資料是否有損壞,若有則使用備份資料 */
		value = checkBak ? checkBackUp(key,	value, "", context, keeper) : value;
		value = Encrypt.decrypt(value, keeper.cryptKey);
		if (LogUtility.isCompilerLog)
			LogUtility.d(PreferenceTool.class.getName()
				, "getSharedPrefernce"
				, key+"="+value);
		return value;
	}

	/*********************************************************
	 * 私有函式
	 ********************************************************/
	private static String checkBackUp(String key, String data, String defaultData, Context context, PrefernceKeeper keeper) {
		if (data.equals("")) {
			/* 若因檔案損壞而讀不到帳號名稱,確認備份檔中有無備份可救回 */
			SharedPreferences backupPreference = context.getSharedPreferences(keeper.prefName+"_BAK",
					keeper.prefMode);
			String backup = backupPreference.getString(key, "");
			if (!backup.equals("")) {
				synchronized (keeper) {
					/* 將備份資料寫回原始資料中 */
					SharedPreferences pref = context.getSharedPreferences(keeper.prefName,
							keeper.prefMode);
					pref.edit().putString(key, backup).commit();
				}
				/* 使用備份帳號資料 */
				return backup;
			} else {
				return defaultData;
			}
		}
		return data;
	}

	public static void setDefaultSharedPrefernce(String[][] keyValues, Context context, PrefernceKeeper keeper) {

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

		synchronized (keeper) {
			for (String[] prefData : keyValues) {
				String key = prefData[0];
				String value = prefData[1];
				String data = Encrypt.encrypt(value, keeper.cryptKey);
				/* 回寫資料 */
				pref.edit().putString(key, data).commit();
			}
		}

	}
	public static void setDefaultSharedPrefernce(String key, String value, Context context, PrefernceKeeper keeper) {
		setDefaultSharedPrefernce(new String[][]{{key, value}}, context, keeper);
	}

	public static String getDefaultSharedPrefernce(String key, Context context, PrefernceKeeper keeper) {

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

		if (LogUtility.isCompilerLog)
			LogUtility.d(PreferenceTool.class.getName()
					, "getDefaultSharedPrefernce"
					, pref.getAll().toString());

		/*取得使用者名稱  */
		String value = pref.getString(key, "");
		/* 確認資料是否有損壞,若有則使用備份資料 */
		value = Encrypt.decrypt(value, keeper.cryptKey);
		return value;
	}


	public static class PrefernceKeeper {
		String prefName;
		String cryptKey;
		int prefMode;
		public PrefernceKeeper(int prefMode, String prefName, String cryptKey) {
			super();
			this.prefMode = prefMode;
			this.prefName = prefName;
			this.cryptKey = cryptKey==null||"".equals(cryptKey)?CRYPT_KEY:cryptKey;
		}		
	}

}