package com.chailease.tw.app.android.io;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.chailease.tw.app.android.utils.Cachable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 *  to read single asset file
 */
public class AssetFileReader implements Readable, Cachable {

    private static HashMap<String, Object> mCache;
    private AssetManager mAssetManager;
    private String fileName;

    public AssetFileReader(Context context) {
        mAssetManager = context.getAssets();
    }
    public AssetFileReader(Activity activity) {
        mAssetManager = activity.getAssets();
    }

    @Override
    public boolean open(String target) {
        if (null == mCache) {
            synchronized (AssetFileReader.class) {
                if (null == mCache) {
                    mCache = new HashMap<String, Object>();
                }
            }
        }
        fileName = target;
        return true;
    }

    @Override
    public String getText() throws IOException {
        if (null == fileName) return null;
        if (mCache.containsKey(fileName))
            return mCache.get(fileName).toString();

        BufferedReader reader = null;
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try{
            inputStream = mAssetManager.open(fileName);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while((line = reader.readLine()) != null)
            {
                builder.append(line);
                builder.append("\n");
            }
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if(reader != null) {
                reader.close();
            }
        }

        mCache.put(fileName, builder.toString());
        return builder.toString();
    }

    @Override
    public boolean close() {
        fileName = null;
        return true;
    }
    @Override
    public void release() {
        //this.mAssetManager.close();
        this.mAssetManager = null;
    }
    @Override
    public void refresh() {
        if (null != mCache)
            mCache.clear();
        mCache = null;
    }
    @Override
    public void refresh(String key) {
        if (null != mCache)
            mCache.remove(key);
    }
}