package com.chailease.tw.app.android.apiclient.impl;


import com.chailease.tw.app.android.apiclient.APIToken;
import com.chailease.tw.app.android.json.gson.GsonTypeAdapter.OverwriteSuper;
import com.google.gson.annotations.JsonAdapter;

import static com.chailease.tw.app.android.apiclient.APIToken.DEVICE_REF_ID_ALIAS_MAC;
import static com.chailease.tw.app.android.apiclient.APIToken.SYSTEM_TYPE_ALIAS_APP_TYPE;

/**
 * {
 *      "ACTION_TYPE" : "2"       '2 - 功能型
 *       ,"COMP_ID" : "1"           權限設定公司別
 *       ,"VERIFY_CODE" : "nkuQQkxWV6WL9QGT70dntGDsLPdveT9P"    認證碼
 *       ,"FORM_ID" : "100003"      功能碼 Level 1
 *       ,"PRIV_ID" : "1"           功能碼 Level 2
 *       ,"API_ID" : "1"            APP_API_ID
 *       ,"API_INPUT_JSON" :  "{\n  \"PageSize\" : \"10000\",\n  \"StartIndex\" : \"0\",\n  \"strQUERY_NAME\" : \"\",\n  \"strQUERY_DEPT\" : \"\",\n  \"strQUERY_USR_ID\" : \"07834\"\n}"   API 完整參數 JSON格式字串
 * }
 *
 * ACTION_TYPE
 * APP_ID
 * APP_TYPE
 * VERIFY_TYPE
 * APP_VERSION
 * MAC
 * LOGIN_ ID
 * VERIFY_CODE
 * COMP_ID
 * FORM_ID
 * PRIV_ID
 * API_ID
 * API_INPUT_JSON
 *
 *  =======================================================
 *  APIToken
 *  -----------------------------------------------------------------------------------
 *      SYSTEM_TYPE           -->     [APP_TYPE]
 *      ACTION_TYPE           -->     ACTION_TYPE
 *      VERIFY_TYPE           -->     VERIFY_TYPE
 *      APP_ID                        -->     APP_ID
 *      APP_VERSION           -->      APP_VERSION
 *      LOGIN_ID                  -->       LOGIN_ ID
 *      DEVICE_REF_ID       -->     [MAC]
 *      OS_VERSION             -->      (Disabled)
 *      PUSH_KEY                 -->      (Disabled)
 *  =======================================================
 *  FuncAPIToken
 *  -----------------------------------------------------------------------------------
 *      COMP_ID                     -->     COMP_ID
 *      VERIFY_CODE           -->      VERIFY_CODE
 *      FORM_ID                     -->     FORM_ID
 *      PRIV_ID                       -->     PRIV_ID
 *      API_ID                          -->     API_ID
 *      API_INPUT_JSON        -->     API_INPUT_JSON
 *
 */
@JsonAdapter(APITokenGsonAdapter.class)
@OverwriteSuper(renameField = {"SYSTEM_TYPE", "DEVICE_REF_ID"}, renameTo = {SYSTEM_TYPE_ALIAS_APP_TYPE, DEVICE_REF_ID_ALIAS_MAC},
        disableSerialize = {"OS_VERSION", "PUSH_KEY"})
public class FuncAPIToken extends APIToken {
    public FuncAPIToken(String actionType) {
        super();
        this.ACTION_TYPE = actionType;
    }
    public FuncAPIToken() {
        this("2");
    }
    public String COMP_ID;
    public String VERIFY_CODE;
    public String FORM_ID;
    public String PRIV_ID;
    public String API_ID;
}