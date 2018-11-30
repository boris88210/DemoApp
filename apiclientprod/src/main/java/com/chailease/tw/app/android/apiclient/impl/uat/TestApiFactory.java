package com.chailease.tw.app.android.apiclient.impl.uat;

import android.content.Context;

import com.chailease.tw.app.android.apiclient.APIToken;
import com.chailease.tw.app.android.apiclient.ApiClient;
import com.chailease.tw.app.android.apiclient.impl.prod.ProdApiFactory;
import com.chailease.tw.app.android.core.EncryptForCallAPI;
import com.chailease.tw.app.android.utils.LogUtility;

import java.io.IOException;
import java.security.cert.CertificateException;


/**
 *
 */
public class TestApiFactory extends ProdApiFactory {

    @Override
    protected void init(Context context) {
        try {
            EncryptForCallAPI.initPublicKey(context, "testkey.der", isEnableDebugLog());
            super.init(context);
        } catch (IOException e) {
            LogUtility.warn(this, "init", " API Encrypt Key failure >>" + e.getMessage() , e);
        } catch (CertificateException e) {
            LogUtility.warn(this, "init", " API Encrypt Key failure >>" + e.getMessage() , e);
        }
    }

    @Override
    protected APIToken createAPIToken(ApiClient api) {
        APIToken token = super.createAPIToken(api);
        token.VERIFY_TYPE = "1";
        return token;
    }

}