package com.chailease.tw.app.android.apiclient;

import android.content.Context;

import com.chailease.tw.app.android.apiclient.config.APIRes;
import com.chailease.tw.app.android.core.APPInfo;
import com.chailease.tw.app.android.utils.LogUtility;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

/**
 *  透過 API Client Factory 來執行 ApiClient
 */
abstract public class ApiClientFactory {

	public static final int API_RTN_CODE_APP_OUTOFVER = -9008;
	public static final int API_RTN_CODE_VERIFY_ERROR = -9009;
	public static final int API_RTN_CODE_GET_ERROR = -9011;
	public static final int API_RTN_CODE_GET_ALERT = -9012;
	public static final int API_RTN_CODE_EMPTY_RESP = -9010;
	public static final int API_RTN_CODE_NETWORK_ERROR = -9100;
	public static final int API_RTN_CODE_NO_NETWORK = -9200;
	public static final int API_RTN_CODE_BAD_INIT_PROCESS = -9300;

	public static final String LOG_TAG = "API_CLIENT";
	public static final String ACTION_TYPE_LOGIN = "1";
	public static final String ACTION_TYPE_FUNC = "2";
	@Deprecated
	public static final String ACTION_TYPE_FUNC_II    =   "4";   //  4 - 功能型
	public static final String ACTION_TYPE_NONAUTH_FUNC      =   "5";   //  5 - 無需認證功能型
	public static final String VERIFY_TYPE_DEFINED   =   "1";
	public static final String VERIFY_TYPE_WINAD     =   "2";   //  2 - WINDOW AD 驗證

	private static ApiClientFactory instance = null;

	protected String mAPP_ID;
	protected String mAPP_VERSION;
	protected String mAPP_DEVICE_ID;
	protected String mAPP_OS_VERSION;
	protected String mAPP_PUSH_KEY;
	protected String mAPP_VERIFY_CODE;

	protected String mAPI_HOST;
	protected String mUPLOAD_HOST;
	protected String mSECU_HOST;

	private boolean enableDebugLog = false;

	protected ApiClientFactory() {
	}

	static private void createFactory(Context context) {
		createFactory(context, null);
	}

	static private void createFactory(Context context, String apiClientFactoryClassName) {
		APPInfo appInfo = APPInfo.getAppInfo(context);
		String apiClientFactoryClass = StringUtils.isBlank(apiClientFactoryClassName) ? appInfo.getApiClientClassName() : apiClientFactoryClassName;
		try {
			APIRes.getInstance(context);
			instance = (ApiClientFactory) Class.forName(apiClientFactoryClass).newInstance();
			instance.init(context);
			LogUtility.info(ApiClientFactory.class, "createFactory", "init ApiClientFactory finishing impl:" + apiClientFactoryClass);
		} catch (ClassNotFoundException e) {
			LogUtility.error(ApiClientFactory.class, "createFactory", "can not load ApiClientFactory Class by " + apiClientFactoryClass, e);
		} catch (InstantiationException e) {
			LogUtility.error(ApiClientFactory.class, "createFactory", "can not create ApiClientFactory as " + apiClientFactoryClass, e);
		} catch (IllegalAccessException e) {
			LogUtility.error(ApiClientFactory.class, "createFactory", "can not create ApiClientFactory because " + apiClientFactoryClass + " without stand constructor", e);
		}
	}

	/**
	 * 取得 ApiClientFactory, 如果 ApiClientFactory 未被產生則依定要提供 Context 否則無法運作
	 *
	 * @param context
	 * @return
	 */
	public static ApiClientFactory getInstance(Context context) {
		if (null == instance) {
			createFactory(context);
		}
		return instance;
	}

	public static ApiClientFactory getInstance(Context context, String apiClientFactoryClassName) {
		if (null == instance) {
			createFactory(context, apiClientFactoryClassName);
		}
		return instance;
	}

	abstract protected void init(Context context);

	public void executeAPI(ApiClient api) {
		if (null != api.getProgressDialog()
				&& !api.getProgressDialog().isShowing()) {
			api.getProgressDialog().show();
		}
		activeAPI(api);
		if (enableDebugLog)
			LogUtility.debug(this, "executeAPI", "active api " + api);
	}
	abstract protected void activeAPI(ApiClient api);
	abstract public void renewVerifyCode(Context context);

	abstract protected APIToken createAPIToken(ApiClient api);

	abstract protected String doRequest(ApiClient api, APIRequestParam params, APIResultInfo resultInfo);

	abstract protected String doRequest(ApiClient api, APIRequestParam[] params, APIResultInfo resultInfo);

	final public APIResultInfo convertToAPIResult(String resultJson) {
		APIResultInfo temp = null;
		if (!StringUtils.isBlank(resultJson) ) {
			Gson gson = new Gson();
			if (StringUtils.contains(resultJson, "\"Data\"")
					&& StringUtils.contains(resultJson, "\"Result\"")) {
				APIResultInfoGeneral generalResult = gson.fromJson(resultJson, APIResultInfoGeneral.class);
				if (null != generalResult && null != generalResult.Result) {
					temp = generalResult;
					temp.RTN_CODE = String.valueOf(generalResult.Result.ReturnCode);
				}
			} else if (StringUtils.contains(resultJson, "\"ALERT\"")
					|| StringUtils.contains(resultJson, "\"Alert\"")
					|| StringUtils.contains(resultJson, "\"BackLogin\"")
					|| StringUtils.contains(resultJson, "\"IS_NEWAPP\"")
					) {
				temp = gson.fromJson(resultJson, APIResultInfo.class);
			}
			if (null != temp) {
				if (!StringUtils.isBlank(temp.BackLogin)) {
					temp.STATUS_CODE = API_RTN_CODE_VERIFY_ERROR;
				} else if (!StringUtils.isBlank(temp.Alert)) {
					temp.STATUS_CODE = API_RTN_CODE_GET_ERROR;
				} else if (!StringUtils.isBlank(temp.ALERT)) {
					temp.STATUS_CODE = API_RTN_CODE_GET_ALERT;
				}
				if (StringUtils.isBlank(temp.RTN_CODE) || "0".equals(temp.RTN_CODE))
					temp.RTN_CODE = String.valueOf(temp.STATUS_CODE);
			}
		}
		if (null == temp) {
			temp = new APIResultInfo();
			temp.RTN_CODE = "0";
		}
		return temp;
	}

	protected void setEnableDebugLog(boolean enabled) {
		this.enableDebugLog = enabled;
	}
	public final boolean isEnableDebugLog() {
		return this.enableDebugLog;
	}
}