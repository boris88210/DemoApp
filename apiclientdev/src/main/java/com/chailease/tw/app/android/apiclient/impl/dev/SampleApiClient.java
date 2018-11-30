package com.chailease.tw.app.android.apiclient.impl.dev;

import android.app.ProgressDialog;

import com.chailease.tw.app.android.apiclient.APIRequestParam;
import com.chailease.tw.app.android.apiclient.APIResultInfo;
import com.chailease.tw.app.android.apiclient.ApiClient;
import com.chailease.tw.app.android.apiclient.ApiClientFactory;
import com.chailease.tw.app.android.utils.LogUtility;
import com.google.gson.Gson;

/**
 *
 */
abstract public class SampleApiClient<T>{

    protected APIRequestParam mParam;
    protected APIResultInfo mRsInfo;
    protected T mResult;
    protected Class<T> tClass;

    protected String ACTION_TYPE;
    protected String VERIFY_TYPE;
    protected String LOGIN_ID;
    protected String COMP_ID;
    protected String VERIFY_CODE;
    protected String FORM_ID;
    protected String PRIV_ID;
    protected String API_ID;
    protected ApiClient.API_WAY mApiWay;

    public SampleApiClient(APIRequestParam param, ProgressDialog progress, Class<T> tClass) {
        super();
        this.mParam = param;
        this.tClass = tClass;
    }
    public SampleApiClient(APIRequestParam param, Class<T> tClass) {
        this(param, null, tClass);
    }

    /**
         * 自訂處理結果
         * @param rsInfo 執行結果基本參數
         * @param resJsonStr 如果執行成功且有回傳內容，依據規格制定的 JSON 字串
         * @return
         */
    public T parseApiResult(APIResultInfo rsInfo, String resJsonStr) {
        LogUtility.debug(this, "parseApiResult", "ready to convert to " + tClass.getCanonicalName() + "\n check out resJsonStr:\n" + resJsonStr);
        Gson gson = new Gson();
        return gson.fromJson(resJsonStr, tClass);
    }

//     @Override
//    public ProgressDialog getProgressDialog() {
//        return mProgress;
//    }
//
//    @Override
//    final public T getResult() {
//        return mResult;
//    }
//
//    @Override
//    final public SalappAPIRequestParam[] getRequestParam() {
//        return new SalappAPIRequestParam[] {mParam};
//    }
//
////    final protected T doInBackground(SalappAPIRequestParam... params) {
////        APIResultInfo rsInfo = new APIResultInfo();
////        String resJsonStr = mFactory.doRequest(this, params, rsInfo);
////        if (!StringUtils.isBlank(resJsonStr)) {
////            mResult = parseApiResult(rsInfo, resJsonStr);
////        }
////        return mResult;
////    }
//
//    @Override
//    final public void active() {
//    }
//
//    public void executeSuccess(String response) {
//        Log.d(ApiClientFactory.LOG_TAG, "executeSuccess and get " + response);
//        APIResultInfo temp = mFactory.convertToAPIResult(response);
//        Log.d(ApiClientFactory.LOG_TAG, "executeSuccess and trans as APIResultInfo " + temp);
//        if (StringUtils.isBlank(temp.ALERT)) {
//            mRsInfo.RTN_CODE = "0";
//            mResult = parseApiResult(mRsInfo, response);
//            doPostExecute(mResult);
//        } else {
//            mRsInfo.RTN_CODE = temp.RTN_CODE;
//            mRsInfo.ALERT = temp.ALERT;
//            doPostExecute(null);
//        }
//    }
//    public void executeFailure(VolleyError error) {
//        mRsInfo.RTN_CODE = "1";
//        if (StringUtils.isBlank(error.getMessage()))
//            mRsInfo.ALERT = "Unknown Error";
//        else mRsInfo.ALERT = error.getMessage();
//        doPostExecute(null);
//        Log.e(ApiClientFactory.LOG_TAG, "executeFailure by VolleyError", error.getCause());
//    }
//    @Override
//    final public void doProgressUpdate(Integer... progress) {}
//
//    @Override
//    public String getACTION_TYPE() {
//        return ACTION_TYPE;
//    }
//
//    @Override
//    public String getVERIFY_TYPE() {
//        return VERIFY_TYPE;
//    }
//
//    @Override
//    public String getLOGIN_ID() {
//        return LOGIN_ID;
//    }
//
//    @Override
//    public String getCOMP_ID() {
//        return COMP_ID;
//    }
//
//    @Override
//    public String getVERIFY_CODE() {
//        return VERIFY_CODE;
//    }
//
//    @Override
//    public String getFORM_ID() {
//        return FORM_ID;
//    }
//
//    @Override
//    public String getPRIV_ID() {
//        return PRIV_ID;
//    }
//
//    @Override
//    public String getAPI_ID() {
//        return API_ID;
//    }
//
//    @Override
//    public API_WAY getAPI_WAY() {
//        return mApiWay;
//    }
}
