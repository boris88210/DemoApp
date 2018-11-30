package com.chailease.tw.app.android.provider;

import android.app.Activity;

import com.chailease.tw.app.android.io.FileReader;
import com.chailease.tw.app.android.io.Readable;
import com.chailease.tw.app.android.utils.Cachable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 */
public class JSONFileDataProvider implements Cachable {

    static HashMap<String, JsonObject> mCache;
    protected Readable mFileReader;

    public JsonObject getJsonContent(Activity activity, String fileName) throws IOException {
        return getJsonContent(activity, fileName, false);
    }
    public JsonObject getJsonContent(Activity activity, String fileName, boolean caching) throws IOException {

        JsonObject json = null;
        if (caching) {
            if (null == mCache) {
                synchronized (JSONFileDataProvider.class) {
                    if (null == mCache) {
                        mCache = new HashMap<String, JsonObject>();
                    }
                }
            }
            if (mCache.containsKey(fileName)) {
                return mCache.get(fileName);
            }
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
    public <T> T getContent(Activity activity, String assetFileName, Class<T> clazz) throws IOException {
        String jsonStr = getFileContent(activity, assetFileName);
        if (!StringUtils.isBlank(jsonStr)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, clazz);
        }
        return null;
    }

    protected String getFileContent(Activity activity, String fileName) throws IOException {
        mFileReader = new FileReader(activity);
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

    @Override
    public void refresh() {
        if (null != mCache)
            mCache.clear();
        mCache = null;
        mFileReader = null;
    }

    @Override
    public void refresh(String key) {
        if (null != mCache)
            mCache.remove(key);
        if (null != mFileReader && mFileReader instanceof Cachable) {
            ((Cachable)mFileReader).refresh(key);
        }
    }

}