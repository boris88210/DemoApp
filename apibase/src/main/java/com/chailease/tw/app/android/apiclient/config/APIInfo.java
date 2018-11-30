package com.chailease.tw.app.android.apiclient.config;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class APIInfo {

    protected String API_WAY;
    protected String IDX_CODE;
    protected String API_ID;
    protected int FORM_ID;
    protected int PRIV_ID;
    protected String ACT_NAME;
    protected String DEV_SAMPLE_URI;
    protected String ACTION_TYPE;

    public String getAPI_WAY() {
        return API_WAY;
    }

    public void setAPI_WAY(String API_WAY) {
        this.API_WAY = API_WAY;
    }

    public String getIDX_CODE() {
        return IDX_CODE;
    }

    public void setIDX_CODE(String IDX_CODE) {
        this.IDX_CODE = IDX_CODE;
    }

    public String getAPI_ID() {
        return API_ID;
    }

    public void setAPI_ID(String API_ID) {
        this.API_ID = API_ID;
    }

    public int getFORM_ID() {
        return FORM_ID;
    }

    public void setFORM_ID(int FORM_ID) {
        this.FORM_ID = FORM_ID;
    }

    public int getPRIV_ID() {
        return PRIV_ID;
    }

    public void setPRIV_ID(int PRIV_ID) {
        this.PRIV_ID = PRIV_ID;
    }

    public String getACT_NAME() {
        return ACT_NAME;
    }

    public void setACT_NAME(String ACT_NAME) {
        this.ACT_NAME = ACT_NAME;
    }

    public String getDEV_SAMPLE_URI() {
        return DEV_SAMPLE_URI;
    }

    public void setDEV_SAMPLE_URI(String DEV_SAMPLE_URI) {
        this.DEV_SAMPLE_URI = DEV_SAMPLE_URI;
    }

    public String getAPP_API_ID() {
        if (StringUtils.isBlank(ACT_NAME)) return API_ID;
        return API_ID + "_" + ACT_NAME;
    }

    public String getACTION_TYPE() {
        return ACTION_TYPE;
    }

    public void setACTION_TYPE(String ACTION_TYPE) {
        this.ACTION_TYPE = ACTION_TYPE;
    }

    public APIInfo convert(APIInfo source) {
        return source;
    }
}