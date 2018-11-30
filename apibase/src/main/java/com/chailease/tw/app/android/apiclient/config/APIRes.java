package com.chailease.tw.app.android.apiclient.config;

import android.content.Context;

import com.chailease.tw.app.android.provider.JSONAssetProvider;
import com.chailease.tw.app.android.utils.LogUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class APIRes {

    static String res = "api_res.json";
    static APIRes instance;
    static HashMap<String, APIInfo> apiInfoMap = new HashMap<String, APIInfo>();
    static HashMap<String, Map<String, APIInfo>> apiInfoMaps = new HashMap<String, Map<String, APIInfo>>();

    private String API_HOST;
    private APIInfo[] API_LIST;

    public static APIRes getInstance(Context context, String... apiConfigs) {
        if (null == instance) {
            JSONAssetProvider dataProvider = new JSONAssetProvider();
            try {
                instance = dataProvider.getContent(context, res, APIRes.class);
                if (null != instance) {
                    for (APIInfo api : instance.API_LIST)
                        if (apiInfoMap.containsKey(api.getIDX_CODE())) LogUtility.warn(APIRes.class, "getInstance", "duplicate IDX_CODE " + api.getIDX_CODE());
                        else apiInfoMap.put(api.getIDX_CODE(), api);
                }
                register(context, apiConfigs);
            } catch (IOException ioe) {
                LogUtility.error(APIRes.class, "getInstance", "initial API Res failure " + ioe.getMessage(), ioe);
                instance = null;
            } finally {
                dataProvider.refresh(res);
                //dataProvider.refresh();
                dataProvider = null;
            }
        }
        return instance;
    }
    public static void register(Context context, String... apiConfigs) {
        if (null != apiConfigs && apiConfigs.length>0) {
            JSONAssetProvider dataProvider = new JSONAssetProvider();
            try {
                for (String config : apiConfigs) {
                    if (!apiInfoMaps.containsKey(config)) {
                        if (res.equals(config)) {
                            apiInfoMaps.put(config, apiInfoMap);
                        } else {
                            try {
                                APIRes infos = dataProvider.getContent(context, config, APIRes.class);
                                HashMap<String, APIInfo> apis = new HashMap<String, APIInfo>();
                                for (APIInfo api : infos.API_LIST)
                                    if (apis.containsKey(api.getIDX_CODE())) LogUtility.warn(APIRes.class, "register", config + " duplicate IDX_CODE " + api.getIDX_CODE());
                                    else apis.put(api.getIDX_CODE(), api);
                                LogUtility.info(APIRes.class, "register", "initial API Res [" + config + "] finish " + apis.size());
                                apiInfoMaps.put(config, apis);
                            } catch (IOException ioe) {
                                LogUtility.error(APIRes.class, "register", "initial API Res failure [" + config + "] catch " + ioe.getMessage(), ioe);
                            }
                        }
                    }
                }
            } finally {
                for (String config : apiConfigs)
                    dataProvider.refresh(config);
                dataProvider = null;
            }
        }
    }

    public APIInfo getAPIInfo(String idxCode) {
        return apiInfoMap.get(idxCode);
    }
    public APIInfo getAPIInfo(String config, String idxCode) {
        if (apiInfoMaps.containsKey(config)) {
            return apiInfoMaps.get(config).get(idxCode);
        }
        return null;
    }
    public <T extends APIInfo> T getAPIInfo(Class<T> tClass, String config, String idxCode)
            throws IllegalAccessException, InstantiationException {
        if (apiInfoMaps.containsKey(config)) {
            APIInfo dest = tClass.newInstance();
            return (T) dest.convert(apiInfoMaps.get(config).get(idxCode));
        }
        return null;
    }

    public String getAPI_HOST() {
        return API_HOST;
    }

    public void setAPI_HOST(String API_HOST) {
        this.API_HOST = API_HOST;
    }

    public APIInfo[] getAPI_LIST() {
        return API_LIST;
    }

    public void setAPI_LIST(APIInfo[] API_LIST) {
        this.API_LIST = API_LIST;
    }
}