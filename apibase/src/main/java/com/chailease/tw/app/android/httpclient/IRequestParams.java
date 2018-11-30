package com.chailease.tw.app.android.httpclient;

import java.util.Map;

/**
 *
 */
public interface IRequestParams {

    public Map<String, Object> getParams();
    public void putStringParam(String name, String value);
    public void putIntParam(String name, int value);
    public void putDoubleParam(String name, double value);

}