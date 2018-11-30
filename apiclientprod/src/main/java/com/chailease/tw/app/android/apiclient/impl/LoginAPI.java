package com.chailease.tw.app.android.apiclient.impl;

import android.app.ProgressDialog;

import com.chailease.tw.app.android.apiclient.APIRequestParam;
import com.chailease.tw.app.android.apiclient.ApiClient;
import com.chailease.tw.app.android.data.UserProfile;
import com.chailease.tw.app.android.data.api.LoginData;

import org.apache.commons.lang3.StringUtils;

import static com.chailease.tw.app.android.apiclient.ApiClientFactory.ACTION_TYPE_LOGIN;

/**
 *
 */
abstract public class LoginAPI extends ApiClient<LoginData.RSP_CNT> {

	public static final String SUCCESS_INTENT_EXTRA_ACTIVITY    =   "login.goto.activity";
	public static final String SUCCESS_INTENT_EXTRA_PASS_EXTRADATA_ALL  =   "login.goto.pass.extra.all";
	public static final String SUCCESS_INTENT_EXTRA_PASS_EXTRADATA_NAMES  =   "login.goto.pass.extra.names";
	public static final String INTENT_EXTRA_PASS_EXTRADATA_NAMES_SPLIT  =   ";";
	public static final String LOGIN_INTENT_EXTRA_BACK_TO_END     =   "login.back.toend";

	protected LoginAPIToken token;
	protected String PWD;
	protected int HEIGHT_PIXELS;
	protected int WIDTH_PIXELS;
	protected String API_INPUT_JSON;

	public LoginAPI(APIRequestParam param, ProgressDialog progress, Class<? extends LoginData.RSP_CNT> tClass) {
		super((String)null, param, tClass);
		ACTION_TYPE = ACTION_TYPE_LOGIN;
	}
	public LoginAPI(APIRequestParam param, ProgressDialog progress) {
		this(param, progress, LoginData.RSP_CNT.class);
	}

	public String getPWD() {
		return PWD;
	}

	public int getHEIGHT_PIXELS() {
		return HEIGHT_PIXELS;
	}

	public int getWIDTH_PIXELS() {
		return WIDTH_PIXELS;
	}

	public void setLoginToken(LoginAPIToken token) {
		this.token = token;
	}

	public String getAPI_INPUT_JSON() {
		return API_INPUT_JSON;
	}
	public void setAPI_INPUT_JSON(String API_INPUT_JSON) {
		this.API_INPUT_JSON = API_INPUT_JSON;
	}
	protected UserProfile genUserProfile(LoginData.RSP_CNT resp) {
		if (null != resp && StringUtils.isBlank(resp.Alert)) {
			if (StringUtils.isBlank(resp.ALERT_MSG)
					&& null!=resp.OUTPUT) {
				UserProfile usr = new UserProfile(resp.OUTPUT.USR_PRFL[0]);
				return usr;
			}
		}
		return null;
	}
	protected void needToUpdate() {

	}
}