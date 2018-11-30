package com.chailease.tw.app.android.apiclient.impl;

import com.chailease.tw.app.android.apiclient.APIToken;
import com.chailease.tw.app.android.json.gson.GsonTypeAdapter;
import com.google.gson.stream.JsonReader;

/**
 *  APIToken for write only
 */
public class APITokenGsonAdapter extends GsonTypeAdapter<APIToken> {

    @Override
    protected Object readObject(Class aClass, String s, JsonReader jsonReader) {
        return null;
    }

    @Override
    protected APIToken newTypeInstance() {
        return new APIToken();
    }
}