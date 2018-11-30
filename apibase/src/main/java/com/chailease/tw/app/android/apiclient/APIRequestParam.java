package com.chailease.tw.app.android.apiclient;


import com.chailease.tw.app.android.apiclient.config.APIInfo;
import com.chailease.tw.app.android.data.UserProfile;
import com.google.gson.annotations.Expose;

/**
 *
 */
public class APIRequestParam {
	@Expose(serialize = false)
	protected UserProfile userPrfl;

	@Deprecated//直接使用此方法會造成API卻少傳參usr_id, comp_id
	public APIRequestParam(String apiId) {
		this.API_ID = apiId;
	}

	public APIRequestParam(String apiId, UserProfile usr) {
		this(apiId);
		this.userPrfl = usr;
	}

	public APIRequestParam(APIInfo apiId, UserProfile usr) {
		this(apiId.getAPI_ID());
		this.userPrfl = usr;
	}
	/**
	 * 	Mapping to MOD_FORM_PRIV
	 */
	public String API_ID;

	public String getCompId() {
		return userPrfl!=null ? String.valueOf(userPrfl.getDeptCompId()) : "";
	}
	public String getUsrId() {
		return userPrfl!=null ? String.valueOf(userPrfl.getUserId()) : "";
	}

	public UserProfile getUserProfile() {
		return userPrfl!=null ? userPrfl.clone() : null;
	}
}