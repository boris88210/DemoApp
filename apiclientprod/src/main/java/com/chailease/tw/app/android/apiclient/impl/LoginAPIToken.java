package com.chailease.tw.app.android.apiclient.impl;

import com.chailease.tw.app.android.apiclient.APIToken;
import com.chailease.tw.app.android.json.gson.GsonTypeAdapter.OverwriteSuper;
import com.chailease.tw.app.android.utils.CommonUtils;
import com.google.gson.annotations.JsonAdapter;

import static com.chailease.tw.app.android.apiclient.APIToken.DEVICE_REF_ID_ALIAS_MAC;
import static com.chailease.tw.app.android.apiclient.APIToken.SYSTEM_TYPE_ALIAS_APP_TYPE;
import static com.chailease.tw.app.android.constants.SystemConstants.AES_KEY_LENGTH;
import static com.chailease.tw.app.android.constants.SystemConstants.AES_VI_LENGTH;

/**
 * {
 *      "PWD" : "password"
 *      , "MANUFACTURER" : ""
 *      , "PHONEMODEL" : ""
 *      , "WIDTHPIXELS" : ""
 *      , "HEIGHTPIXELS" : ""
 *      , "ARG0" : ""
 *      , "ARG1" : ""
 * }
 *  ACTION_TYPE
 *  APP_ID
 *  APP_TYPE
 *  VERIFY_TYPE
 *  APP_VERSION
 *  MAC
 *  OS_VERSION
 *  MANUFACTURER
 *  PLATFORM
 *  WIDTHPIXELS
 *  HEIGHTPIXES
 *  PUSH_KEY
 *  ARG0
 *  ARG1
 *
 *  =======================================================
 *  APIToken
 *  -----------------------------------------------------------------------------------
 *      SYSTEM_TYPE           -->     [APP_TYPE]
 *      ACTION_TYPE           -->     ACTION_TYPE
 *      VERIFY_TYPE           -->     VERIFY_TYPE
 *      APP_ID                        -->     APP_ID
 *      APP_VERSION           -->      APP_VERSION
 *      LOGIN_ID                  -->       ???
 *      DEVICE_REF_ID       -->     [MAC]
 *      OS_VERSION             -->      OS_VERSION
 *      PUSH_KEY                 -->     PUSH_KEY
 *  =======================================================
 *  LoginAPIToken
 *  -----------------------------------------------------------------------------------
 *      PWD                           -->       ???
 *      MANUFACTURER    -->      MANUFACTURER
 *      PHONEMODEL          -->     [PLATFORM]
 *      WIDTHPIXELS          -->      WIDTHPIXELS
 *      HEIGHTPIXELS        -->      HEIGHTPIXES
 *      ARG0                         -->      ARG0
 *      ARG1                        -->       ARG1
 */
@JsonAdapter(APITokenGsonAdapter.class)
@OverwriteSuper(renameField = {"SYSTEM_TYPE", "DEVICE_REF_ID"}, renameTo = {SYSTEM_TYPE_ALIAS_APP_TYPE, DEVICE_REF_ID_ALIAS_MAC})
public class LoginAPIToken extends APIToken {
    public LoginAPIToken() {
        this("1");
    }
    public LoginAPIToken(String actionType) {
        super();
        this.ACTION_TYPE = actionType;
        // Key
        ARG0 = CommonUtils.getRandomString(AES_KEY_LENGTH);
        // VI
        ARG1 = CommonUtils.getRandomString(AES_VI_LENGTH);
    }
    public String PWD;
    public String MANUFACTURER;
    public String PHONEMODEL;
    public String WIDTHPIXELS;
    public String HEIGHTPIXELS;
    public String ARG0;
    public String ARG1;
}