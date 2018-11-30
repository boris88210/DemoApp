package com.chailease.tw.app.android.apiclient.impl.dev;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chailease.tw.app.android.apiclient.APIRequestParam;
import com.chailease.tw.app.android.apiclient.APIResultInfo;
import com.chailease.tw.app.android.apiclient.APIResultInfoGeneral;
import com.chailease.tw.app.android.apiclient.APIToken;
import com.chailease.tw.app.android.apiclient.ApiClient;
import com.chailease.tw.app.android.apiclient.ApiClientFactory;
import com.chailease.tw.app.android.apiclient.config.APIRes;
import com.chailease.tw.app.android.apiclient.impl.FuncAPIToken;
import com.chailease.tw.app.android.apiclient.impl.FuncItemTrans;
import com.chailease.tw.app.android.apiclient.impl.GFLSApiCleint;
import com.chailease.tw.app.android.provider.JSONAssetProvider;
import com.chailease.tw.app.android.provider.SampleDataProvider;
import com.chailease.tw.app.android.utils.LogUtility;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  To Request Sample Site for API
 */
abstract public class SampleApiFactory extends ApiClientFactory {

    protected String sampleSite = "http://219.87.8.112/sd";
    final String resAssets = "assets://";
    final protected HashMap<String, String> URLs = new HashMap<String, String>();
    private RequestQueue mRequestQueue;
    private Class<? extends APIResultInfoGeneral> resInfoClass = APIResultInfoGeneral.class;

    abstract public void loadApiURL();

    @Override
    protected void init(Context context) {
        this.loadApiURL();
        APIRes apiRes = APIRes.getInstance(context);
        if (null != apiRes && !StringUtils.isBlank(apiRes.getAPI_HOST())) {
            sampleSite = apiRes.getAPI_HOST();
        }
        mRequestQueue = Volley.newRequestQueue(context);
    }

    @Override
    protected APIToken createAPIToken(ApiClient api) {
        FuncAPIToken token = new FuncAPIToken();
        token.API_ID = api.getAPI_ID();
        token.FORM_ID = api.getFORM_ID();
        token.PRIV_ID = api.getPRIV_ID();
        token.LOGIN_ID = api.getLOGIN_ID();
        return token;
    }
    protected Context findContext() {
        return null;
    }
    protected String getSrcOfSimulateGFLS() {
        return "active_func.json";
    }
    protected FuncItemTrans[] simulateGFLS() {
        FuncItemTrans[] funcs = null;
        try {
            ActiveFunc[] actFuncs = new JSONAssetProvider().getContent(findContext(),
                    getSrcOfSimulateGFLS(), ActiveFunc[].class);
            ArrayList<FuncItemTrans> fs = new ArrayList<>();
            for (ActiveFunc af : actFuncs) {
                if ("Y".equals(af.IN_USED)) {
                    FuncItemTrans fi = new FuncItemTrans();
                    fi.setEnabled(true);
                    fi.setFuncId(Integer.parseInt(af.FUNC_ID));
                    fi.setParentId(af.PARENT_ID);
                    fi.setName(af.FUNC_NAME);
                    fi.setCount(af.ORD_BY);
                    fs.add(fi);
                }
            }
            funcs = fs.toArray(new FuncItemTrans[fs.size()]);
        } catch (IOException ioex) {
            LogUtility.error(this, "doRequest", "get GFLS failure from assets ", ioex);
        }
        return funcs;
    }

    @Override
    protected String doRequest(ApiClient api, APIRequestParam params, APIResultInfo resultInfo) {

        String url = api.getApiInfo()!=null ? api.getApiInfo().getDEV_SAMPLE_URI() : null;
        if (api instanceof GFLSApiCleint) {
            if (null == resultInfo) {
                resultInfo = new APIResultInfo();
                resultInfo.RTN_CODE = "0";
            }
            FuncItemTrans[] funcs = simulateGFLS();
            api.setResultInfo(resultInfo);
            api.doPostExecute(funcs);
        } else if (!StringUtils.isBlank(url)) {
            if (url.startsWith(resAssets)) {
                String datasource = url.substring(resAssets.length());
                try {
                    APIResultInfoGeneral apiResponseObj = SampleDataProvider.getInstance().getSampleInstance(findContext(),
                            datasource, resInfoClass);
                    Object data = null;
                    if (null == resultInfo) {
                        if (null == apiResponseObj) {
                            resultInfo = new APIResultInfo();
                            resultInfo.RTN_CODE = "-1";
                            resultInfo.Alert = "Unable to load data from " + url;
                        } else {
                            resultInfo = apiResponseObj;
                        }
                    }
                    if (apiResponseObj != null) {
                        resultInfo.RTN_CODE = apiResponseObj.RTN_CODE;
                        resultInfo.ALERT = apiResponseObj.ALERT;
                        resultInfo.Alert = apiResponseObj.Alert;
                        Gson gson = new Gson();
                        data = gson.fromJson(apiResponseObj.getData(), api.getTargetClass());
                    }
                    api.setResultInfo(resultInfo);
                    api.doPostExecute(data);
                    Log.d("API_CLIENT", " do " + url + " by API:" + api.getAPI_ID() + " >> " + api);
                } catch (Exception e) {
                    LogUtility.error(this, "doRequest", "get data failure of assets for " + api + " from " + url, e);
                }
            } else {
                APIToken token = createAPIToken(api);
                url = sampleSite;
                HashMap<String, String> postParams = new HashMap<String, String>();
                if (token instanceof FuncAPIToken) {
                    FuncAPIToken func = (FuncAPIToken) token;
                    if (null != api.getApiInfo() && !StringUtils.isBlank(api.getApiInfo().getDEV_SAMPLE_URI())) {
                        url += api.getApiInfo().getDEV_SAMPLE_URI();
                    } else {
                        url += URLs.get(func.API_ID);
                    }
                    Gson gson = new Gson();
                    func.API_INPUT_JSON = gson.toJson(params);
                    postParams.put("api_param", gson.toJson(func));
                    //  fname=[APP_API_ID]&param=[INPUT_JSON]
                    postParams.put("fname", api.getApiInfo().getAPP_API_ID());
                    postParams.put("param", func.API_INPUT_JSON);
                }
                Log.d(LOG_TAG, " do " + url + " by API:" + api.getAPI_ID() + " >> " + api);
                final String[] rsp = new String[1];
                final VolleyRequest post = new VolleyRequest(url, postParams, api);
                Log.d(LOG_TAG, "ready to add request in queue " + api);
                mRequestQueue.add(post);
                return rsp[0];
            }
        }
        return null;
    }
    //TODO:接受API傳參為JSON Array，暫時先這樣使用，後續要和JSON Object的使用整合
    @Override
    protected String doRequest(ApiClient api, APIRequestParam[] params, APIResultInfo resultInfo) {

        String url = api.getApiInfo()!=null ? api.getApiInfo().getDEV_SAMPLE_URI() : null;
        if (api instanceof GFLSApiCleint) {
            if (null == resultInfo) {
                resultInfo = new APIResultInfo();
                resultInfo.RTN_CODE = "0";
            }
            FuncItemTrans[] funcs = simulateGFLS();
            api.setResultInfo(resultInfo);
            api.doPostExecute(funcs);
        } else if (!StringUtils.isBlank(url)) {
            if (url.startsWith(resAssets)) {
                String datasource = url.substring(resAssets.length());
                try {
                    APIResultInfoGeneral apiResponseObj = SampleDataProvider.getInstance().getSampleInstance(findContext(),
                            datasource, resInfoClass);
                    Object data = null;
                    if (null == resultInfo) {
                        if (null == apiResponseObj) {
                            resultInfo = new APIResultInfo();
                            resultInfo.RTN_CODE = "-1";
                            resultInfo.Alert = "Unable to load data from " + url;
                        } else {
                            resultInfo = apiResponseObj;
                        }
                    }
                    if (apiResponseObj != null) {
                        resultInfo.RTN_CODE = apiResponseObj.RTN_CODE;
                        resultInfo.ALERT = apiResponseObj.ALERT;
                        resultInfo.Alert = apiResponseObj.Alert;
                        Gson gson = new Gson();
                        data = gson.fromJson(apiResponseObj.getData(), api.getTargetClass());
                    }
                    api.setResultInfo(resultInfo);
                    api.doPostExecute(data);
                    Log.d("API_CLIENT", " do " + url + " by API:" + api.getAPI_ID() + " >> " + api);
                } catch (Exception e) {
                    LogUtility.error(this, "doRequest", "get data failure of assets for " + api + " from " + url, e);
                }
            } else {
                APIToken token = createAPIToken(api);
                url = sampleSite;
                HashMap<String, String> postParams = new HashMap<String, String>();
                if (token instanceof FuncAPIToken) {
                    FuncAPIToken func = (FuncAPIToken) token;
                    if (null != api.getApiInfo() && !StringUtils.isBlank(api.getApiInfo().getDEV_SAMPLE_URI())) {
                        url += api.getApiInfo().getDEV_SAMPLE_URI();
                    } else {
                        url += URLs.get(func.API_ID);
                    }
                    Gson gson = new Gson();
                    func.API_INPUT_JSON = gson.toJson(params);
                    postParams.put("api_param", gson.toJson(func));
                    //  fname=[APP_API_ID]&param=[INPUT_JSON]
                    postParams.put("fname", api.getApiInfo().getAPP_API_ID());
                    postParams.put("param", func.API_INPUT_JSON);
                }
                Log.d(LOG_TAG, " do " + url + " by API:" + api.getAPI_ID() + " >> " + api);
                final String[] rsp = new String[1];
                final VolleyRequest post = new VolleyRequest(url, postParams, api);
                Log.d(LOG_TAG, "ready to add request in queue " + api);
                mRequestQueue.add(post);
                return rsp[0];
            }
        }
        return null;
    }

    @Override
    protected void activeAPI(ApiClient api) {
        APIResultInfo rsInfo = null;    //new APIResultInfo();
        if (api.getRequestParam() != null) {
            doRequest(api, api.getRequestParam(), rsInfo);
        } else {
            doRequest(api, api.getRequestParamArray(), rsInfo);
        }
    }

    public class VolleyRequest extends StringRequest {
        private Map<String, String> mParams;
        public VolleyRequest(String url, Map<String, String> params, final ApiClient api){
            //準備參數
            super(Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(ApiClientFactory.LOG_TAG, "executeSuccess and get " + response);
                            APIResultInfo temp = convertToAPIResult(response);
                            api.setResultInfo(temp);
                            Log.d(ApiClientFactory.LOG_TAG, "executeSuccess and trans as APIResultInfo " + temp);
                            if (StringUtils.isBlank(temp.ALERT)) {
                                //temp.RTN_CODE = "0";
                                api.doPostExecute(api.parseApiResult(temp, response));
                            } else {
                                api.doPostExecute(null);
                            }
                            Log.d(ApiClientFactory.LOG_TAG, " request success by " + api
                                    + "\n and response :" + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            APIResultInfo rsInfo = new APIResultInfo();
                            rsInfo.RTN_CODE = String.valueOf(API_RTN_CODE_GET_ERROR);
                            rsInfo.ALERT = error.getMessage();
                            rsInfo.STATUS_CODE = API_RTN_CODE_GET_ERROR;
                            api.setResultInfo(rsInfo);
                            api.doPostExecute(null);
                            Log.d(ApiClientFactory.LOG_TAG, " request error by " + api
                                    + "\n and response :" + error.getMessage(), error);
                        }
                    });
            mParams = handleParams(params);
        }
        private Map<String, String> handleParams(Map<String,String> map){
            return map;
        }
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return mParams;
        }

    }

    public static class ActiveFunc {
        public int APP_SEQ;
        public String FUNC_ID;
        public String FUNC_NAME;
        public int PARENT_ID;
        public int ROOT_ID;
        public String IN_USED;
        public int ORD_BY;
    }

}