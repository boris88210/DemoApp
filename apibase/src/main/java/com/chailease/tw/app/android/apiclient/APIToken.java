package com.chailease.tw.app.android.apiclient;

import com.google.gson.annotations.SerializedName;

import static com.chailease.tw.app.android.constants.SystemConstants.APP_TYPE_ANDROID;

/**
 * {
 *      "APP_ID" : "The APP_ID"
 *      , "LOGIN_ID" : "USER ID"
 *      , "DEVICE_REF_ID" : "35E21CD3-F65B-4C94-9916-D8B9B4EF02FF"        設備APP識別碼 UUID
 *      , "SYSTEM_TYPE" : "2"          1 - Android   2 - iOS
 *      , "APP_VERSION" : "3"       APP 版號
 *      , "ACTION_TYPE" : "2"       '2 - 功能型
 *      , "VERIFY_TYPE" : "2"       1 - 用其他驗證   2 - WINDOW AD 驗證
 *      , "OS_VERSION" : ""
 *      , "PUSH_KEY" : ""
 * }
 */
public class APIToken {

    public static final String SYSTEM_TYPE_ALIAS_APP_TYPE   =   "APP_TYPE";
    public static final String SYSTEM_TYPE_ALIAS_DEVICE_TYPE   =   "DEVICE_TYPE";

    public static final String DEVICE_REF_ID_ALIAS_MAC  =   "MAC";
    public static final String DEVICE_REF_ID_ALIAS_UUID =   "UUID";

    @SerializedName(value="SYSTEM_TYPE", alternate = {"APP_TYPE", "DEVICE_TYPE"})
    final public String SYSTEM_TYPE = APP_TYPE_ANDROID;
    public String ACTION_TYPE;
    public String VERIFY_TYPE = "2";
    public String APP_ID;
    public String APP_VERSION;
    public String LOGIN_ID;
    @SerializedName(value="DEVICE_REF_ID", alternate={DEVICE_REF_ID_ALIAS_MAC, DEVICE_REF_ID_ALIAS_UUID})
    public String DEVICE_REF_ID;
    public String OS_VERSION;
    public String PUSH_KEY;
    public String API_INPUT_JSON;
}