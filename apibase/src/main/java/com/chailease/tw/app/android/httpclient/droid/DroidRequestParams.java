package com.chailease.tw.app.android.httpclient.droid;

import com.chailease.tw.app.android.httpclient.IRequestParams;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DroidRequestParams implements IRequestParams {

    HashMap<String, Object> params = new HashMap<String, Object>();

    @Override
    public Map<String, Object> getParams() {
        return params;
    }
    @Override
    public void putStringParam(String name, String value) {
        this.params.put(name, value);
    }
    @Override
    public void putIntParam(String name, int value) {
        this.params.put(name, value);
        //this.params.put(name, new Integer(value));
    }
    @Override
    public void putDoubleParam(String name, double value) {
        this.params.put(name, value);
    }
}