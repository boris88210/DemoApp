package com.chailease.tw.app.android.apiclient.impl;

import com.chailease.tw.app.android.apiclient.APIToken;
import com.chailease.tw.app.android.json.gson.GsonTypeAdapter.OverwriteSuper;
import com.chailease.tw.app.android.utils.CommonUtils;
import com.google.gson.annotations.JsonAdapter;

import static com.chailease.tw.app.android.apiclient.APIToken.SYSTEM_TYPE_ALIAS_APP_TYPE;
import static com.chailease.tw.app.android.constants.SystemConstants.AES_KEY_LENGTH;
import static com.chailease.tw.app.android.constants.SystemConstants.AES_VI_LENGTH;

/**
 *
 */
@JsonAdapter(APITokenGsonAdapter.class)
@OverwriteSuper(renameField = {"SYSTEM_TYPE"}, renameTo = {SYSTEM_TYPE_ALIAS_APP_TYPE},
		disableSerialize = {"OS_VERSION", "PUSH_KEY", "DEVICE_REF_ID"})
public class OrdinaryAPIToken extends APIToken {
	public OrdinaryAPIToken() {
		super();
		this.ACTION_TYPE = "5";
		// Key
		ARG0 = CommonUtils.getRandomString(AES_KEY_LENGTH);
		// VI
		ARG1 = CommonUtils.getRandomString(AES_VI_LENGTH);
	}
	public String ARG0;
	public String ARG1;
	public String API_ID;
}
