package com.chailease.tw.app.android.apiclient.impl.prod;

import android.os.AsyncTask;

import com.chailease.tw.app.android.apiclient.APIRequestParam;
import com.chailease.tw.app.android.apiclient.APIResultInfo;
import com.chailease.tw.app.android.apiclient.ApiClient;
import com.chailease.tw.app.android.apiclient.ApiClientFactory;
import com.chailease.tw.app.android.apiclient.AsyncTaskApiClient;
import com.chailease.tw.app.android.apiclient.impl.FuncAPIToken;
import com.chailease.tw.app.android.httpclient.HttpClientAgent;
import com.chailease.tw.app.android.httpclient.IHttpResponseHandler;
import com.chailease.tw.app.android.httpclient.IRequestParams;
import com.chailease.tw.app.android.httpclient.droid.DroidHttpClientAgent;
import com.chailease.tw.app.android.httpclient.droid.DroidRequestParams;
import com.chailease.tw.app.android.httpclient.droid.DroidResponseHandler;
import com.chailease.tw.app.android.utils.Encrypt;
import com.chailease.tw.app.android.utils.LogUtility;

import java.io.IOException;

import static com.chailease.tw.app.android.apiclient.ApiClientFactory.ACTION_TYPE_LOGIN;

/**
 *
 */
public class ProdApiClient<T> extends AsyncTaskApiClient<APIRequestParam, Integer, T> {

    ProdApiFactory apiFactory;
    boolean secuApi = false;


    public ProdApiClient(ApiClient<T> api) {
        super(api);
    }

    public ProdApiClient(ApiClient<T> api, boolean isParamArray) {
        super(api, isParamArray);
    }
    @Override
    public void setFactory(ApiClientFactory factory) {
        super.setFactory(factory);
        apiFactory = (ProdApiFactory) mFactory;
    }

    @Override
    public void onPreExecute() {
        //   ： 執行前，一些基本設定可以在這邊做。
        mRsInfo = new APIResultInfo();
        mToken = apiFactory.createAPIToken(mApi);
        if (ACTION_TYPE_LOGIN.equals(mApi.getACTION_TYPE())) {
            secuApi = true;
        } else {
            secuApi = false;
        }
    }

    @Override
    protected HttpClientAgent createHttpClientAgent() {
        if (secuApi) {
            return new DroidHttpClientAgent(apiFactory.secuHostConfig);
        } else {
            return new DroidHttpClientAgent(apiFactory.apiHostConfig);
        }
    }

    @Override
    protected IRequestParams[] createRequestParameter(APIRequestParam... params) {
        if (null != params) {
            if (isParamArray){
                IRequestParams[] reqParams = new IRequestParams[1];

                DroidRequestParams apiParams = new DroidRequestParams();
                if (secuApi) {
                    if (null == params[0])
                        apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken));
                    else
                        apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken, params));
                        //apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken));
                } else {    //  put params into API_INPUT_JSON
                    apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken, params));
                }
                reqParams[0] = apiParams;

                return reqParams;
            } else {
                IRequestParams[] reqParams = new IRequestParams[params.length];
                for (int i = 0; i < reqParams.length; i++) {
                    DroidRequestParams apiParams = new DroidRequestParams();
                    if (secuApi) {
                        if (null == params[i])
                            apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken));
                        else
                            apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken, params[i]));
                        //apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken));
                    } else {    //  put params into API_INPUT_JSON
                        apiParams.putStringParam("EN_STR", apiFactory.encodeRequestParameter(mToken, params[i]));
                    }
                    reqParams[i] = apiParams;
                }
                return reqParams;
            }
        }
        return new IRequestParams[0];
    }

    @Override
    protected IHttpResponseHandler createResponseHandler(AsyncTask task, APIRequestParam... params) {
        DroidResponseHandler respHandler = new DroidResponseHandler(params);
        return respHandler;
    }

    @Override
    protected String convertResult(String respContent) {
        try {
            return apiFactory.decrypt(mToken, respContent);
        } catch (IOException e) {
            LogUtility.error(this, "convertResult", "convertResult throws IOException ! " + e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Encrypt.EncryptException e) {
            LogUtility.error(this, "convertResult", "convertResult throws EncryptException ! " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}