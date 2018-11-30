package com.chailease.tw.app.android.apiclient.impl;

import android.util.Log;

import com.chailease.tw.app.android.apiclient.APIRequestParam;
import com.chailease.tw.app.android.apiclient.ApiClient;
import com.chailease.tw.app.android.apiclient.ApiClientFactory;
import com.google.gson.annotations.Expose;

import static com.chailease.tw.app.android.apiclient.ApiClientFactory.LOG_TAG;
import static com.chailease.tw.app.android.apiclient.impl.GFLSApiCleint.GFL_MODE.GFLS;

/**
 *
 */
abstract public class GFLSApiCleint extends ApiClient<FuncItemTrans[]> {

	public static enum GFL_MODE {
		GFL, GFLS
	}

	@Expose(serialize = false)
	GFL_MODE mode;

	public GFLSApiCleint(String baseId, APIRequestParam param) {
		this(baseId, param, GFLS);
	}
	public GFLSApiCleint(String baseId, APIRequestParam param, GFL_MODE mode) {
		super(baseId, param, FuncItemTrans[].class);
		this.mode = mode;
		FORM_ID = baseId;
		init();
	}

	@Override
	protected void init() {
		ACTION_TYPE = ApiClientFactory.ACTION_TYPE_FUNC;
		VERIFY_TYPE = ApiClientFactory.VERIFY_TYPE_WINAD;
		if (null != mode)
			API_ID = mode.toString();
		PRIV_ID = API_ID;
		APIWAY = API_WAY.READ;
	}

	@Override
	public void doProgressUpdate(Integer... integers) {}

	@Override
	public void doPostExecute(FuncItemTrans[] funcs) {
		if (logDebug)
			Log.d(LOG_TAG, "get func list >> \n" + funcs);
	}
}