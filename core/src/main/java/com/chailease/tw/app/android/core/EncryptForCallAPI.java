package com.chailease.tw.app.android.core;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.chailease.tw.app.android.utils.Encrypt;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;

import static com.chailease.tw.app.android.constants.CoreConstants.PUBLICKEY_FILENAME;
import static com.chailease.tw.app.android.utils.LogUtility.LOG_TAG;

/**
 *
 */
public class EncryptForCallAPI {

	static PublicKey _PK = null;
	static boolean enabledDebugLog = false;

	public static void initPublicKey(Context context) throws IOException, CertificateException {
		initPublicKey(context, (String) null);
	}
	public static void initPublicKey(Context context, boolean enabledLog) throws IOException, CertificateException {
		initPublicKey(context, (String) null, enabledLog);
	}
	public static void initPublicKey(Context context, String keyFileAssetPath) throws IOException, CertificateException {
		initPublicKey(context, keyFileAssetPath, false);
	}
	public static void initPublicKey(Context context, String keyFileAssetPath, boolean enabledLog) throws IOException, CertificateException {
		enabledDebugLog = enabledLog;
		if (null == _PK && null != context) {
			AssetManager assetManager = context.getAssets();
			if (StringUtils.isBlank(keyFileAssetPath)) {
				try (InputStream fin = assetManager.open(PUBLICKEY_FILENAME)) {
                    /* 產生PublicKey物件 */
					PublicKey pk = Encrypt.transToPublicKey(fin);
					if (enabledDebugLog)
						Log.d(LOG_TAG, "check out PublicKey encoded length " + pk.getEncoded().length);
					_PK = pk;
				}
			} else {
				try (InputStream fin = assetManager.open(keyFileAssetPath)) {
                    /* 產生PublicKey物件 */
					PublicKey pk = Encrypt.transToPublicKey(fin);
					if (enabledDebugLog)
						Log.d(LOG_TAG, "check out PublicKey encoded length " + pk.getEncoded().length);
					_PK = pk;
				}
			}
		}
	}
	public static void initPublicKey(Context context, Uri keyFile) throws IOException, CertificateException {
		initPublicKey(context, keyFile, false);
	}
	public static void initPublicKey(Context context, Uri keyFile, boolean enabledLog) throws IOException, CertificateException {
		enabledDebugLog = enabledLog;
		if (null == _PK && null != context) {
			if (null != keyFile) {
				ContentResolver resolver = context.getContentResolver();
				try (InputStream fin = resolver.openInputStream(keyFile)) {
                    /* 產生PublicKey物件 */
					PublicKey pk = Encrypt.transToPublicKey(fin);
					if (enabledDebugLog)
						Log.d(LOG_TAG, "check out PublicKey encoded length " + pk.getEncoded().length);
					_PK = pk;
				}
			}
		}
	}

	public static String API_INPUT_JSON(Object data, Context context) {
		Gson gson = new Gson();
		String mAPI_INPUT_JSON = gson.toJson(data);
		//將公鑰讀出
		if (enabledDebugLog)
			Log.d(LOG_TAG, "check out api_input_json >>" + mAPI_INPUT_JSON);
		try  {
			initPublicKey(context, enabledDebugLog);
			if (null == _PK) {
				return mAPI_INPUT_JSON;
			}
			byte[] EncryptionBytes = Encrypt.encryptRSA(_PK, mAPI_INPUT_JSON);
			mAPI_INPUT_JSON = Base64.encodeToString(EncryptionBytes, Base64.DEFAULT);
			//System.out.println("mAPI_INPUT_JSON 字串加密 -->" + mAPI_INPUT_JSON);
			if (enabledDebugLog)
				Log.d(LOG_TAG, "check out api_output_json after encrypt >>" + mAPI_INPUT_JSON);
			return mAPI_INPUT_JSON;
		} catch (Encrypt.EncryptException e) {
			Log.e(LOG_TAG, "transfer to API_INPUT_JSON throws EncryptException >> " + e.getCause(), e);
		} catch (CertificateException e) {
			Log.e(LOG_TAG, "transfer to API_INPUT_JSON throws CertificateException >> " + e.getMessage(), e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "transfer to API_INPUT_JSON throws IOException >> " + e.getMessage(), e);
		}
		return null;
	}

}