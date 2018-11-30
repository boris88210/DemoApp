package com.chailease.tw.app.android.apiclient;

import android.app.ProgressDialog;
import android.util.Log;

import com.chailease.tw.app.android.apiclient.config.APIInfo;
import com.chailease.tw.app.android.utils.LogUtility;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  ApiClient 為 APP 執行功能 API 功能之介面
 *  實作者透過 ApiClient 提供 API 參數給 ApiClientFactory 進行 API 處理,
 *  實作者需自訂回傳類別, 泛用者可訂為 JSONObject, 專用者可訂為 Data Model,
 *  特殊用途則可定義而外的輸出類別(ex: file)
 *  實作 ApiClient 的類別一定要有一個帶入 String apiId 的建構子
 */
public abstract class ApiClient<T> {

	public enum API_WAY {
		READ, WRITE, DOWNLOAD, UPLOAD
	}

	/**
	 *  suggest to use #ApiClientFactory.ACTION_TYPE_LOGIN
	 */
	@Deprecated
	public static final String ACTION_TYPE_SECURE   =   ApiClientFactory.ACTION_TYPE_LOGIN;
	/**
	 *  suggest to use #ApiClientFactory.ACTION_TYPE_FUNC
	 */
	@Deprecated
	public static final String ACTION_TYPE_FUNC      =   ApiClientFactory.ACTION_TYPE_FUNC;   //  2 - 功能型
	@Deprecated
	public static final String ACTION_TYPE_FUNC_II    =   ApiClientFactory.ACTION_TYPE_FUNC_II;   //  4 - 功能型
	@Deprecated
	/**
	 *  suggest to use #ApiClientFactory.ACTION_TYPE_NONAUTH_FUNC
	 */
	public static final String ACTION_TYPE_NONAUTH_FUNC      =   ApiClientFactory.ACTION_TYPE_NONAUTH_FUNC;   //  5 - 無需認證功能型
	@Deprecated
	/**
	 *  suggest to use #ApiClientFactory.VERIFY_TYPE_DEFINED
	 */
	public static final String VERIFY_TYPE_DEFINED   =   ApiClientFactory.VERIFY_TYPE_DEFINED;
	@Deprecated
	/**
	 *  suggest to use #ApiClientFactory.VERIFY_TYPE_WINAD
	 */
	public static final String VERIFY_TYPE_WINAD     =   ApiClientFactory.VERIFY_TYPE_WINAD;   //  2 - WINDOW AD 驗證

	protected boolean logDebug = false;
	final private APIInfo apiInfo;
	//  API Request Parameter Base
	protected String API_ID;
	protected String FUNC_ID;
	protected API_WAY APIWAY;
	protected String VERIFY_TYPE;
	protected String ACTION_TYPE;
	protected String LOGIN_ID;
	protected String COMP_ID;
	protected String FORM_ID;
	protected String PRIV_ID;
	protected APIRequestParam mReqParam;
	protected APIRequestParam[] mReqParamArray;
	protected ProgressDialog mProgress;

	//  API Request Arguments
	protected boolean isSuccess;
	protected APIResultInfo mResultInfo;
	private Class<? extends T> tClass;

	public ApiClient(String apiId, APIRequestParam param, Class<? extends T> tClass) {
		this((APIInfo) null, param, tClass);
		this.API_ID = apiId;
	}

	//TODO:接受API傳參為JSON Array，暫時先這樣使用，後續要和JSON Object的使用整合
	public ApiClient(APIInfo apiInfo, Class<? extends T> tClass, APIRequestParam[] paramArray) {
		this.tClass = tClass;
		this.apiInfo = apiInfo;
		this.mReqParamArray = paramArray;
		this.tClass = tClass;
		if (null != paramArray) {
			this.LOGIN_ID = paramArray[0].getUsrId();
			this.COMP_ID = paramArray[0].getCompId();
		}
		if (null != apiInfo) {
			API_ID = apiInfo.getAPI_ID();
			FORM_ID = String.valueOf(apiInfo.getFORM_ID());
			PRIV_ID = String.valueOf(apiInfo.getPRIV_ID());
			if (!StringUtils.isBlank(apiInfo.getAPI_WAY())) {
				if ("R".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.READ;
				} else if ("W".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.WRITE;
				} else if ("DL".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.DOWNLOAD;
				} else if ("UL".equalsIgnoreCase(apiInfo.getAPI_WAY())||"U".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.UPLOAD;
				} else {
					APIWAY = API_WAY.valueOf(apiInfo.getAPI_WAY().toUpperCase());
				}
			}
			if (availableActionType()) {
				ACTION_TYPE = apiInfo.getACTION_TYPE();
			}
		}
		init();
	}
	public ApiClient(APIInfo apiInfo, APIRequestParam param, Class<? extends T> tClass) {
		//this(apiInfo.getAPP_API_ID(), param, tClass);
		this.tClass = tClass;
		this.apiInfo = apiInfo;
		this.mReqParam = param;
		this.tClass = tClass;
		if (null != param) {
			this.LOGIN_ID = param.getUsrId();
			this.COMP_ID = param.getCompId();
		}
		if (null != apiInfo) {
			API_ID = apiInfo.getAPI_ID();
			FORM_ID = String.valueOf(apiInfo.getFORM_ID());
			PRIV_ID = String.valueOf(apiInfo.getPRIV_ID());
			if (!StringUtils.isBlank(apiInfo.getAPI_WAY())) {
				if ("R".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.READ;
				} else if ("W".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.WRITE;
				} else if ("DL".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.DOWNLOAD;
				} else if ("UL".equalsIgnoreCase(apiInfo.getAPI_WAY())||"U".equalsIgnoreCase(apiInfo.getAPI_WAY())) {
					APIWAY = API_WAY.UPLOAD;
				} else {
					APIWAY = API_WAY.valueOf(apiInfo.getAPI_WAY().toUpperCase());
				}
			}
			if (availableActionType()) {
				ACTION_TYPE = apiInfo.getACTION_TYPE();
			}
		}
		init();
	}


	protected boolean availableActionType() {
		return (StringUtils.isNoneBlank(apiInfo.getACTION_TYPE())
				&& apiInfo.getACTION_TYPE().matches("\\d+"));
	}

	public String getAPI_ID() {
		return this.API_ID;
	}
	public String getVERIFY_TYPE() {
		return VERIFY_TYPE;
	}
	public String getACTION_TYPE() {
		return ACTION_TYPE;
	}
	public String getLOGIN_ID() {
		return LOGIN_ID;
	}
	public String getCOMP_ID() {
		return this.COMP_ID;
	}
	public String getFORM_ID() {
		return FORM_ID;
	}
	public String getPRIV_ID() {
		return PRIV_ID;
	}
	public String getFUNC_ID() {
		return this.FUNC_ID;
	}
	public API_WAY getAPI_WAY() {
		return this.APIWAY;
	}
	public APIInfo getApiInfo() {return this.apiInfo;}

	/**
	 * 執行結果成功與否; 由 ApiClientFactory 決定
	 * @return
	 */
	public boolean isSuccess(){
		return this.isSuccess;
	}
	/**
	 * 提供 API Parameter 給 ApiClientFactory 以執行 API 請求
	 * @return
	 */
	public APIRequestParam getRequestParam() {
		return this.mReqParam;
	}
	//TODO:接受API傳參為JSON Array，暫時先這樣使用，後續要和JSON Object的使用整合
	public APIRequestParam[] getRequestParamArray() {
		return this.mReqParamArray;
	}
	/**
	 * 若需支援 Progressing 可搭配 doProgressUpdate 使用
	 * @return
	 */
	public ProgressDialog getProgressDialog() {
		return this.mProgress;
	}

	abstract protected void init();
	abstract public void doProgressUpdate(Integer... progress);
	/**
	 * 若是成功, 則回傳結果對應 Data Instance T
	 * @param result
	 */
	abstract public void doPostExecute(T result);

	public T parseApiResult(APIResultInfo rsInfo, String resJsonStr) {
		String dataJsonStr = resJsonStr;
		if (null!=rsInfo && rsInfo instanceof APIResultInfoGeneral) {
			APIResultInfoGeneral rsInfoG = (APIResultInfoGeneral) rsInfo;
			if (null == rsInfoG.Data) dataJsonStr = null;
			else dataJsonStr = rsInfoG.Data.toString();
		}
		if (StringUtils.isBlank(dataJsonStr)) return null;
		if (Map.class.isAssignableFrom(tClass)) {
			return (T) convertToMap(dataJsonStr);
		} else if (Map[].class.isAssignableFrom(tClass)) {
			return (T) convertToArray(dataJsonStr);
		} else if (String.class.isAssignableFrom(tClass)) {
			return (T) dataJsonStr;
		}
		if (logDebug) {
			Log.d(ApiClientFactory.LOG_TAG, "ready to convert to " + tClass.getCanonicalName() + "\n check out resJsonStr:\n" + resJsonStr);
			LogUtility.debug(this, "parseApiResult", "check out resJsonStr:\n" + resJsonStr);
		}
		Gson gson = new Gson();
		return gson.fromJson(dataJsonStr, tClass);
	}
	public void setResultInfo(APIResultInfo resultInfo) {
		this.mResultInfo = resultInfo;
		if (null != mResultInfo) {
			setSuccess("0".equals(mResultInfo.RTN_CODE));
		} else {
			setSuccess(false);
		}
	}
	public void setSuccess(boolean success) {
		this.isSuccess = success;
	}

	public Class<? extends T> getTargetClass() {
		return tClass;
	}

	protected Map createMap4Json() {
		if (Map.class.equals(tClass) || Map[].class.equals(tClass))
			return new HashMap();
		else try {
			return (Map) tClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected Map convertToMap(String resJson) {
		Map rsMap = null;
		try {
			JSONObject obj = new JSONObject(resJson);
			if (obj.length()>1) {
				rsMap = convertToMap(obj);
			} else {
				rsMap = createMap4Json();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rsMap;
	}
	protected Object[] convertToArray(String resJson) {
		Object[] objArray = null;
		try {
			JSONArray array = new JSONArray(resJson);
			objArray = new Map[array.length()];
			for (int i=0; i<objArray.length; i++) {
				Object item = array.get(i);
				if (item instanceof JSONObject) {
					objArray[i] = convertToMap((JSONObject) item);
				} else if (item instanceof JSONArray) {
					objArray[i] = convertToArray((JSONArray) item);
				} else {
					objArray[i] = item;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return objArray;
	}
	protected Object[] convertToArray(JSONArray jArray) {
		Object[] array = new Object[jArray.length()];
		for (int i=0; i<array.length; i++) {
			Object aitem = jArray.opt(i);
			if (aitem instanceof JSONObject) {
				array[i] = convertToMap((JSONObject) aitem);
			} else if (aitem instanceof JSONArray) {
				array[i] = convertToArray((JSONArray) aitem);
			} else {
				array[i] = aitem;
			}
		}
		return array;
	}
	protected Map convertToMap(JSONObject json) {
		Map jMap = createMap4Json();
		Iterator<String> names = json.keys();
		while(names.hasNext()) {
			String name = names.next();
			try {
				Object temp = json.get(name);
				if (temp instanceof JSONObject) {
					jMap.put(name, convertToMap((JSONObject) temp));
				} else if (temp instanceof JSONArray) {
					jMap.put(name, convertToArray((JSONArray) temp));
				} else {
					jMap.put(name, temp);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jMap;
	}
}