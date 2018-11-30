package com.chailease.tw.app.android.core;

import android.content.Context;

/**
 *      String mAPI_HOST, mUPLOAD_HOST, mSECU_HOST 在專案中得定
 *      Please change to use BaseMultiDexApplication
 */
@Deprecated
public class BaseApplication extends BaseMultiDexApplication {

	private static BaseApplication instance;

	public static BaseApplication getInstance(Context context) {
		if (null == instance) {
			instance = (BaseApplication) context.getApplicationContext();
		}
		return instance;
	}

}