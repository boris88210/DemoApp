package com.chailease.tw.app.android.provider;

import android.app.Activity;
import android.content.Context;

import com.chailease.tw.app.android.io.AssetFileReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 */
public class JSONAssetProvider extends JSONFileDataProvider {

    public JsonObject getJsonContent(Context context, String fileName) throws IOException {
        return getJsonContent(context, fileName, false);
    }
    public JsonObject getJsonContent(Context context, String fileName, boolean caching) throws IOException {
        JsonObject json = null;
        if (caching) {
            return getContentFromCache(fileName);
        }
        String jsonStr = getFileContent(context, fileName);
        if (!StringUtils.isBlank(jsonStr)) {
            JsonParser parser = new JsonParser();
            json = (JsonObject) parser.parse(jsonStr);
            if (caching)
                mCache.put(fileName, json);
        }

        return json;
    }
    private JsonObject getContentFromCache(String fileName) {
        if (null == mCache) {
            synchronized (JSONAssetProvider.class) {
                if (null == mCache) {
                    mCache = new HashMap<String, JsonObject>();
                }
            }
        }
        if (mCache.containsKey(fileName)) {
            return mCache.get(fileName);
        }
        return null;
    }
    public JsonObject getJsonContent(Activity activity, String fileName) throws IOException {
        return getJsonContent(activity, fileName, false);
    }
    public JsonObject getJsonContent(Activity activity, String fileName, boolean caching) throws IOException {

        JsonObject json = null;
        if (caching) {
            return getContentFromCache(fileName);
        }
        String jsonStr = getFileContent(activity, fileName);
        if (!StringUtils.isBlank(jsonStr)) {
            JsonParser parser = new JsonParser();
            json = (JsonObject) parser.parse(jsonStr);
            if (caching)
                mCache.put(fileName, json);
        }

        return json;
    }

    protected String getFileContent(Context context, String fileName) throws IOException {
        mFileReader = new AssetFileReader(context);
        return getFileContent(fileName);
    }
    @Override
    protected String getFileContent(Activity activity, String fileName) throws IOException {
        mFileReader = new AssetFileReader(activity);
        return getFileContent(fileName);
    }
    private String getFileContent(String fileName) throws IOException {
        if (mFileReader.open(fileName)) {
            try {
                String jsonStr = mFileReader.getText();
                return jsonStr;
            } finally {
                mFileReader.close();
                mFileReader.release();
            }
        }
        return null;
    }
    public <T> T getContent(Context context, String assetFileName, Class<T> clazz) throws IOException {
        String jsonStr = getFileContent(context, assetFileName);
        if (!StringUtils.isBlank(jsonStr)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, clazz);
        }
        return null;
    }

}