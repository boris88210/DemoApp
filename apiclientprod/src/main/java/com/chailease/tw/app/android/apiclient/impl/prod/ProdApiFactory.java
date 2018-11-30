package com.chailease.tw.app.android.apiclient.impl.prod;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.chailease.tw.app.android.apiclient.APIRequestParam;
import com.chailease.tw.app.android.apiclient.APIResultInfo;
import com.chailease.tw.app.android.apiclient.APIToken;
import com.chailease.tw.app.android.apiclient.ApiClient;
import com.chailease.tw.app.android.apiclient.ApiClientFactory;
import com.chailease.tw.app.android.apiclient.AsyncTaskApiClient;
import com.chailease.tw.app.android.apiclient.impl.FuncAPIToken;
import com.chailease.tw.app.android.apiclient.impl.LoginAPI;
import com.chailease.tw.app.android.apiclient.impl.LoginAPIToken;
import com.chailease.tw.app.android.apiclient.impl.OrdinaryAPIToken;
import com.chailease.tw.app.android.core.APPInfo;
import com.chailease.tw.app.android.core.DeviceInfo;
import com.chailease.tw.app.android.core.EncryptForCallAPI;
import com.chailease.tw.app.android.core.IBaseApplication;
import com.chailease.tw.app.android.data.UserProfile;
import com.chailease.tw.app.android.httpclient.Config;
import com.chailease.tw.app.android.json.gson.GsonUtils;
import com.chailease.tw.app.android.utils.CommonUtils;
import com.chailease.tw.app.android.utils.Encrypt;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;


/**
 *
 */
public class ProdApiFactory extends ApiClientFactory {

    protected Config apiHostConfig;
    protected Config secuHostConfig;

    @Override
    protected void init(Context context) {
        try {
            IBaseApplication app = (IBaseApplication) context.getApplicationContext();
            APPInfo appInfo = APPInfo.getAppInfo(context);
            UserProfile prfl = app.getUserProfile();
            EncryptForCallAPI.initPublicKey(context, isEnableDebugLog());
            mAPP_ID = appInfo.getAPP_ID();
            mAPP_VERSION = String.valueOf(appInfo.getAPP_VERSION());
            mAPP_OS_VERSION = String.valueOf(DeviceInfo.getSDK_INT());
            mAPP_PUSH_KEY = app.getAPP_PUSH_KEY();
            mAPP_DEVICE_ID = DeviceInfo.getDEVICE_ID();
            mAPP_VERIFY_CODE = prfl != null ? prfl.getVerifyCode() : "";
            loadConfigs();
        } catch (IOException e) {
            LogUtility.warn(this, "init", " API Encrypt Key failure >>" + e.getMessage(), e);
        } catch (CertificateException e) {
            LogUtility.warn(this, "init", " API Encrypt Key failure >>" + e.getMessage(), e);
        }
    }

    protected void loadConfigs() {
//        this.mUPLOAD_HOST = null;
//        this.mAPI_HOST = null;
//        this.mSECU_HOST = null;
        this.apiHostConfig = new Config(mAPI_HOST, 80, Config.HTTP_PORT_SSL_DEFAULT
                , false);
        this.secuHostConfig = new Config(mSECU_HOST, 80, Config.HTTP_PORT_SSL_DEFAULT
                , false);
    }

    @Override
    public void renewVerifyCode(Context context) {
        UserProfile prfl = ((IBaseApplication) context.getApplicationContext()).getUserProfile();
        if (null != prfl)
            this.mAPP_VERIFY_CODE = prfl.getVerifyCode();
    }

    @Override
    protected APIToken createAPIToken(ApiClient api) {
        APIToken token = null;
        if (ACTION_TYPE_LOGIN.equals(api.getACTION_TYPE())) {
            LoginAPIToken loginToken = new LoginAPIToken();
            LoginAPI loginApi = (LoginAPI) api;
            loginToken.APP_ID = mAPP_ID;
            loginToken.APP_VERSION = mAPP_VERSION;
            loginToken.DEVICE_REF_ID = mAPP_DEVICE_ID;
            loginToken.OS_VERSION = mAPP_OS_VERSION;
            loginToken.PUSH_KEY = mAPP_PUSH_KEY;
            if (!StringUtils.isBlank(api.getVERIFY_TYPE()))
                loginToken.VERIFY_TYPE = api.getVERIFY_TYPE();
            if (!StringUtils.isBlank(api.getACTION_TYPE()))
                loginToken.ACTION_TYPE = api.getACTION_TYPE();
            loginToken.LOGIN_ID = api.getLOGIN_ID();
            loginToken.MANUFACTURER = DeviceInfo.getManufacturer();
            loginToken.PHONEMODEL = DeviceInfo.getPHONE_MODEL();
            loginToken.HEIGHTPIXELS = String.valueOf(loginApi.getHEIGHT_PIXELS());
            loginToken.WIDTHPIXELS = String.valueOf(loginApi.getWIDTH_PIXELS());
            loginToken.PWD = loginApi.getPWD();
            loginToken.API_INPUT_JSON = loginApi.getAPI_INPUT_JSON();
            token = loginToken;
            loginApi.setLoginToken(loginToken);
        } else if (ACTION_TYPE_FUNC.equals(api.getACTION_TYPE())
                || ACTION_TYPE_FUNC_II.equals(api.getACTION_TYPE())) {
            FuncAPIToken funcToken = new FuncAPIToken();
            funcToken.APP_ID = mAPP_ID;
            funcToken.APP_VERSION = mAPP_VERSION;
            funcToken.DEVICE_REF_ID = mAPP_DEVICE_ID;
            funcToken.OS_VERSION = mAPP_OS_VERSION;
            funcToken.PUSH_KEY = mAPP_PUSH_KEY;
            if (api.getRequestParam() != null) {
                funcToken.VERIFY_CODE = api.getRequestParam().getUserProfile() != null ? api.getRequestParam().getUserProfile().getVerifyCode() : mAPP_VERIFY_CODE;
            } else {
                funcToken.VERIFY_CODE = api.getRequestParamArray()[0].getUserProfile() != null ? api.getRequestParamArray()[0].getUserProfile().getVerifyCode() : mAPP_VERIFY_CODE;
            }
            funcToken.API_ID = api.getApiInfo() != null ? api.getApiInfo().getAPP_API_ID() : api.getAPI_ID();
            funcToken.COMP_ID = api.getCOMP_ID();
            funcToken.VERIFY_TYPE = api.getVERIFY_TYPE();
            funcToken.ACTION_TYPE = api.getACTION_TYPE();
            funcToken.LOGIN_ID = api.getLOGIN_ID();
            funcToken.FORM_ID = api.getFORM_ID();
            funcToken.PRIV_ID = api.getPRIV_ID();
            token = funcToken;
        } else if (ACTION_TYPE_NONAUTH_FUNC.equals(api.getACTION_TYPE())) {
            OrdinaryAPIToken apiToken = new OrdinaryAPIToken();
            apiToken.APP_ID = mAPP_ID;
            apiToken.LOGIN_ID = api.getLOGIN_ID();
            apiToken.API_ID = api.getApiInfo() != null ? api.getApiInfo().getAPP_API_ID() : api.getAPI_ID();
            apiToken.ACTION_TYPE = api.getACTION_TYPE();
            token = apiToken;
        }
        return token;
    }

    @Override
    protected void activeAPI(ApiClient api) {

        if (api.getRequestParam() != null) {//判斷參數為Json object
            doRequest(api, api.getRequestParam(), null);
        } else if (api.getRequestParamArray() != null) {//判斷參數為Json array
            doRequest(api, api.getRequestParamArray(), null);
        } else if (api instanceof LoginAPI) {//判斷是否呼叫LoginAPI
            if (((LoginAPI) api).getPWD() != null) {
                doRequest(api, api.getRequestParam(), null);
            }
        }
    }

    @Override
    protected String doRequest(ApiClient api, APIRequestParam params, APIResultInfo resultInfo) {
        AsyncTask task = new ProdApiClient(api);  //  create a async-task
        AsyncTaskApiClient apiTask = (ProdApiClient) task;  //  create a async-task
        apiTask.setFactory(this);
//        apiTask.execute(params);//API 13後預設是排序
        apiTask.executeOnExecutor(THREAD_POOL_EXECUTOR, params);//改用併發，加速API
        return null;
    }

    //TODO:接受API傳參為JSON Array，暫時先這樣使用，後續要和JSON Object的使用整合
    @Override
    protected String doRequest(ApiClient api, APIRequestParam[] params, APIResultInfo resultInfo) {
        AsyncTask task = new ProdApiClient(api, true);  //  create a async-task
        AsyncTaskApiClient apiTask = (ProdApiClient) task;  //  create a async-task
        apiTask.setFactory(this);
//        apiTask.execute(params);//API 13後預設是排序
        apiTask.executeOnExecutor(THREAD_POOL_EXECUTOR, params);//改用併發，加速API
        return null;
    }

    public String encodeRequestParameter(APIToken req, APIRequestParam params) {
        GsonUtils gson = GsonUtils.genGsonUtils(GsonUtils.EXPOSE_STRATEGY_SERIALIZE, GsonUtils.EXPOSE_STRATEGY_DESERIALIZE);
        //Gson gson = new Gson();
        req.API_INPUT_JSON = gson.toJsonString(params);
        return encodeRequestParameter(req);
    }

    //TODO:接受API傳參為JSON Array，暫時先這樣使用，後續要和JSON Object的使用整合
    public String encodeRequestParameter(APIToken req, APIRequestParam[] params) {
        GsonUtils gson = GsonUtils.genGsonUtils(GsonUtils.EXPOSE_STRATEGY_SERIALIZE, GsonUtils.EXPOSE_STRATEGY_DESERIALIZE);
        //Gson gson = new Gson();
        req.API_INPUT_JSON = gson.toJsonString(params);
        return encodeRequestParameter(req);
    }

    public String encodeRequestParameter(APIToken req) {
        String encStr = EncryptForCallAPI.API_INPUT_JSON(req, null);
        return encStr;
    }

    final public String decrypt(APIToken token, String responseContent) throws IOException, Encrypt.EncryptException {
        if (isEnableDebugLog())
            LogUtility.info(this, "decrypt", "decrypt with  >>" + responseContent);
        String AESKey = "";
        String AESVi = "";
        if (token instanceof LoginAPIToken) {
            LoginAPIToken loginToken = (LoginAPIToken) token;
            AESKey = loginToken.ARG0;
            AESVi = loginToken.ARG1;
        } else if (token instanceof OrdinaryAPIToken) {
            OrdinaryAPIToken loginToken = (OrdinaryAPIToken) token;
            AESKey = loginToken.ARG0;
            AESVi = loginToken.ARG1;
        } else {
            FuncAPIToken funcToken = (FuncAPIToken) token;
            AESKey = funcToken.VERIFY_CODE;
            AESVi = funcToken.VERIFY_CODE.substring(0, 16);
        }
        if (isEnableDebugLog())
            LogUtility.debug(this, "decrypt", " with  AESKey:" + AESKey + "; AESVi:" + AESVi);
        byte[] textByte = Encrypt.DecryptAES(AESVi.getBytes(), AESKey.getBytes(), Base64.decode(responseContent.getBytes("UTF-8"), Base64.DEFAULT));
        try (InputStream myInputStream = new ByteArrayInputStream(textByte)) {
            responseContent = CommonUtils.processInputStream(myInputStream);
            if (isEnableDebugLog())
                LogUtility.info(this, "decrypt", " as " + responseContent);
            return responseContent;
        } catch (IOException e) {
            throw e;
        }
    }

//    int uploadCount = 0;
//    void requestMode1(ApiClient api, SalappAPIRequestParam[] params, APIResultInfo resultInfo) {
//        AsyncTaskApiClient apiTask = (AsyncTaskApiClient) api;
//        try {
//            URL url = new URL(mAPI_HOST);
//            URLConnection conection = url.openConnection();
//            conection.setConnectTimeout(NETWORK_TIMEOUT);
//            conection.connect();
//            // Getting file length
//            int lenghtOfFile = conection.getContentLength();
//            // Create a Input stream to read file - with 8k buffer
//            InputStream input = new BufferedInputStream(url.openStream(),
//                    8192);
//            // Output stream to write file
//            OutputStream output = new FileOutputStream(
//                    "/sdcard/9androidnet.jpg");
//
//            byte data[] = new byte[1024];
//            long total = 0;
//            while ((uploadCount = input.read(data)) != -1) {
//                total += uploadCount;
//                // publishing the progress....
//                // After this onProgressUpdate will be called
//                apiTask.doPushProgress((int) ((total * 100) / lenghtOfFile));
//                // writing data to file
//                output.write(data, 0, uploadCount);
//            }
//            // flushing output
//            output.flush();
//            // closing streams
//            output.close();
//            input.close();
//        } catch (SocketTimeoutException e) {
//            Log.e(LOG_TAG, "Network Timeout: " + e.getMessage(), e);
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Error: " + e.getMessage(), e);
//        }
//    }

}