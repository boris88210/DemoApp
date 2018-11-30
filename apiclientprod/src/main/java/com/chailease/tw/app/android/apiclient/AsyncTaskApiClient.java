package com.chailease.tw.app.android.apiclient;

import android.os.AsyncTask;

import com.chailease.tw.app.android.httpclient.HttpClientAgent;
import com.chailease.tw.app.android.httpclient.IHttpResponseHandler;
import com.chailease.tw.app.android.httpclient.IRequestParams;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import static com.chailease.tw.app.android.apiclient.ApiClientFactory.ACTION_TYPE_LOGIN;
import static com.chailease.tw.app.android.httpclient.IHttpResponseHandler.STATUS_CODE_COMPLETED;

/**
 * ApiClient 提供 APP 執行功能 API 的調用
 * 實作 interface 者需自訂回傳類別, 泛用者可訂為 JSONObject, 專用者可訂為 Data Model,
 * 特殊用途則可定義而外的輸出類別(ex: file)
 * <p>
 * 實作IApiClient 的類別一定要有一個帶入 String appId 的建構子
 * AsyncTask<Params, Progress, Result>
 */
public abstract class AsyncTaskApiClient<P, I, T> extends AsyncTask<P, I, T> {

    protected ApiClientFactory mFactory;
    protected ApiClient<T> mApi;
    protected APIToken mToken;
    protected APIResultInfo mRsInfo;
    protected T mResult;

    protected String ACTION_TYPE;
    protected String VERIFY_TYPE;
    protected String LOGIN_ID;
    protected String COMP_ID;
    protected String VERIFY_CODE;
    protected String FORM_ID;
    protected String PRIV_ID;
    protected String API_ID;

    protected boolean isParamArray;

    public AsyncTaskApiClient(ApiClient<T> api) {
        this.mApi = api;
    }

    public AsyncTaskApiClient(ApiClient<T> api, boolean isParamArray) {
        this(api);
        this.isParamArray = isParamArray;
    }

    public void setFactory(ApiClientFactory factory) {
        mFactory = factory;
    }

    //abstract protected APIToken createAPIToken();
    abstract protected HttpClientAgent createHttpClientAgent();

    abstract protected IRequestParams[] createRequestParameter(APIRequestParam... params);

    abstract protected IHttpResponseHandler createResponseHandler(AsyncTask task, APIRequestParam... params);

    abstract protected String convertResult(String respContent);
    //abstract protected APIResultInfo convertResultInfo(String resContent);


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    final public T doInBackground(P... params) {

        if (mFactory.isEnableDebugLog())
            LogUtility.info(this, "doInBackground", "before execute and check prarms " + params);

        try {

            //APIRequestParam[] ps = (APIRequestParam[]) params;
            APIRequestParam[] ps = new APIRequestParam[params.length];
            for (int i = 0; i < ps.length; i++)
                ps[i] = (APIRequestParam) params[i];

            // need to wait for response
            HttpClientAgent httpClientAgent = createHttpClientAgent();
            httpClientAgent.setDebugLogEnabled(mFactory.isEnableDebugLog());
            IRequestParams[] apiParams = createRequestParameter(ps);

            // if need support async-process for progressing there is a big change point
            // some how the http-client need to support passing progress for task by doPushProgress and the doPushProgress of task need to use api doProgressUpdate by handler
            IHttpResponseHandler respHandler = createResponseHandler(this, ps);
            try {
                for (IRequestParams param : apiParams)
                    httpClientAgent.doPost("", param, respHandler);

                APIResultInfo temp = null;
                String serverResponse = null;
                switch (respHandler.getStatusCode()) {
                    case STATUS_CODE_COMPLETED:
                        serverResponse = convertResult(respHandler.getResponseContent());
                        temp = mFactory.convertToAPIResult(serverResponse);
                        mRsInfo = temp;
                        break;
                    default:
                        temp = new APIResultInfo();
                        temp.RTN_CODE = String.valueOf(respHandler.getStatusCode());
                        break;
                }
                if (mFactory.isEnableDebugLog()) {
                    LogUtility.info(this, "doInBackground", "execute done and get " + serverResponse);
                    LogUtility.debug(this, "doInBackground", "execute done and trans as APIResultInfo " + temp);
                }
                if ("0".equals(temp.RTN_CODE) && StringUtils.isBlank(temp.ALERT)) {
                    mRsInfo.RTN_CODE = "0";
                    mResult = mApi.parseApiResult(mRsInfo, serverResponse);
                } else if (ACTION_TYPE_LOGIN.equals(ACTION_TYPE)) {
                    mResult = mApi.parseApiResult(mRsInfo, serverResponse);
                } else {
                    mRsInfo.RTN_CODE = temp.RTN_CODE;
                    mRsInfo.ALERT = temp.ALERT;
                    mRsInfo.BackLogin = temp.BackLogin;
                    mRsInfo.Alert = temp.Alert;
                }
                mApi.setResultInfo(mRsInfo);
            } catch (Exception e) {
                LogUtility.error(this, "doInBackground", " failure! " + e.getMessage(), e);
                if (mRsInfo != null) mRsInfo = new APIResultInfo();
                mRsInfo.RTN_CODE = String.valueOf(ApiClientFactory.API_RTN_CODE_GET_ERROR);
                mRsInfo.ALERT = e.getMessage();
            }
        } catch (Exception e) {
            LogUtility.error(this, "doInBackground", "  catch exception >> " + e.getMessage(), e);
            if (mRsInfo != null) mRsInfo = new APIResultInfo();
            mRsInfo.RTN_CODE = String.valueOf(ApiClientFactory.API_RTN_CODE_GET_ERROR);
            mRsInfo.ALERT = e.getMessage();
        } finally {
            if (mFactory.isEnableDebugLog())
                LogUtility.info(this, "doInBackground", "end of API doInBackground " + mResult);
        }
        return mResult;
    }

    // Async Task to do async request and also support progressing info passing by publishProgress
    //  protected void onProgressUpdate(Integer... progress) {
    //      setProgressPercent(progress[0]);
    //  }
    public void doPushProgress(I progress) {
        publishProgress(progress);
    }

    @Override
    final public void onProgressUpdate(I... progs) {
        //Integer[] progress = (Integer[]) progs;
        Integer[] progress = new Integer[progs.length];
        for (int i = 0; i < progress.length; i++)
            progress[i] = (Integer) progs[i];
        mApi.doProgressUpdate(progress);
    }

    //  Async Task to do when processing finish
    //  protected void onPostExecute(Long result) {
    //      showDialog("Downloaded " + result + " bytes");
    //  }
    @Override
    final public void onPostExecute(T result) {
        mApi.doPostExecute(result);
    }

}