package com.chailease.tw.app.android.core;

import android.content.Context;

import com.chailease.tw.app.android.utils.CommonUtils;
import com.chailease.tw.app.android.utils.PreferenceTool;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 *
 */
public class PrivatePreferenceManager {

    static PrivatePreferenceManager instance;
    boolean useDefPref = true;

    public static PrivatePreferenceManager getInstance(Context context) {
        return getInstance(context, null);
    }
    public static PrivatePreferenceManager getInstance(Context context, String cryptKey) {
        if (null == instance) {
            APPInfo appInfo = APPInfo.getAppInfo(context);
            instance = new PrivatePreferenceManager();
            instance.keeper = new PreferenceTool.PrefernceKeeper(Context.MODE_PRIVATE,
                    appInfo.getAPP_ID(),
                    StringUtils.isBlank(cryptKey) ? appInfo.getPREF_CRYPT_KEY() : cryptKey);
        }
        return instance;
    }

    private PreferenceTool.PrefernceKeeper keeper;

    public void savePreference(String key, String value, Context context) {
        if (useDefPref)
            PreferenceTool.setDefaultSharedPrefernce(key, value, context, keeper);
        else
            PreferenceTool.setSharedPrefernce(key, value, context, keeper);
    }
    public void savePreference(HashMap<String, String> datas, Context context) {
        String[][] kvs = new String[datas.size()][];
        int i=0;
        String[] keys = datas.keySet().toArray(new String[kvs.length]);
        for (String key : keys) {
            kvs[i] = new String[] {key, datas.get(key)};
            i++;
        }
        if (useDefPref)
            PreferenceTool.setDefaultSharedPrefernce(kvs, context, keeper);
        else
            PreferenceTool.setSharedPrefernce(kvs, context, keeper);
    }

    public void clear(Context context) {
        PreferenceTool.clear(context, keeper, true);
    }

    public String getPreference(String key, Context context) {
        return getPreference(key, context, false);
    }
    public String getPreference(String key, Context context, boolean withoutBak) {
        if (useDefPref)
            return PreferenceTool.getDefaultSharedPrefernce(key, context, keeper);
        else
            return PreferenceTool.getSharedPrefernce(key, context, keeper, !withoutBak);
    }
    public String getPreference(String key, String defaultValue, Context context) {
        if (useDefPref)
            return CommonUtils.getString(PreferenceTool.getDefaultSharedPrefernce(key, context, keeper), defaultValue);
        else
            return CommonUtils.getString(PreferenceTool.getSharedPrefernce(key, context, keeper), defaultValue);
    }

}