package com.chailease.tw.app.android.httpclient;

import android.app.Activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Map;

/**
 *  HttpClientAgent 為提供 Http Request 的基礎介面,
 *  不論採用任何第三方 Http Request 套件均透過此一介面程式與 APP 橋接
 *  第三方套件的配套元件虛實作 IRequestParams, IHttpResponseHandler 及 ProgressListener
 *
 */
public abstract class HttpClientAgent {

    public static final String LOG_TAG = "HttpClientAgent";

//	public static final int UPLOAD_SAMPLE_INPUTSTREAM	= 0;
//	public static final int UPLOAD_SAMPLE_FILE			= 1;
//	public static final int UPLOAD_SAMPLE_BYTEARRAY	= 2;

	public enum UPLOAD_TYPE {
		INPUT_STREAM, FILE, BYTE_ARRAY
	}

    protected boolean DEBUGLOG_ENABLED = false;
	protected Config config;
	protected String servicePath;

	public HttpClientAgent(Config _config) {
		super();
        this.config = _config;
	}
	public HttpClientAgent(String baseURL) {
		this(new Config(baseURL));
	}
	public HttpClientAgent(String baseURL, String authService, String userName, String password) {
		this(baseURL);
	}

	abstract public void setCookie(Activity context, HttpClientCookie[] cookies);

	abstract public void upload(String servicePath, IRequestParams params, IHttpResponseHandler handler, UPLOAD_TYPE uploadType, String paramName, Object... source);

	abstract public void doPost(String servicePath, IRequestParams params, IHttpResponseHandler handler);

	abstract public void doGet(String servicePath, IRequestParams params, IHttpResponseHandler handler);

	protected String getAbsoluteUrl(String relativeUrl) {
		return config.baseURL + relativeUrl;
	}

	public class HttpClientCookie {
		String name;
		String value;
		public HttpClientCookie(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
	}

	public String getBaseURL() {
		return config.baseURL;
	}
	public void setBaseURL(String baseURL) {
		config.baseURL = baseURL;
	}
	public String getDomain() {
		return config.domain;
	}
	public void setDomain(String domain) {
		config.domain = domain;
	}
	public String getServicePath() {
		return servicePath;
	}
	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

	public void setDebugLogEnabled(boolean enabled) {
		this.DEBUGLOG_ENABLED = enabled;
	}

    protected String converToQueryParams(IRequestParams params) throws UnsupportedEncodingException {
        Map<String, Object> paramKV = params.getParams();
        if (null != paramKV && paramKV.size()>0) {
            StringBuilder paramStr = new StringBuilder();
            Map.Entry<String, Object>[] kvs = paramKV.entrySet().toArray(new Map.Entry[paramKV.size()]);
            boolean first = true;
            for (Map.Entry kv : kvs) {
                if (!first) paramStr.append("&");
                first = false;
                if(kv.getValue() instanceof String) {
                    paramStr.append(kv.getKey()).append("=").append(URLEncoder.encode(kv.getValue().toString(), "UTF8"));
                } else if (kv.getValue() instanceof Integer) {
                    paramStr.append(kv.getKey()).append("=").append(String.valueOf(kv.getValue()));
                } else if (kv.getValue() instanceof Long) {
					DecimalFormat df = new DecimalFormat("###############");
                    paramStr.append(kv.getKey()).append("=").append(df.format(kv.getValue()));
                } else if (kv.getValue() instanceof Double) {
                    paramStr.append(kv.getKey()).append("=").append(URLEncoder.encode(String.valueOf(kv.getValue()), "UTF8"));
                } else if (kv.getValue()!=null){
                    paramStr.append(kv.getKey()).append("=").append(URLEncoder.encode(kv.getValue().toString(), "UTF8"));
                } else {
                    paramStr.append(kv.getKey()).append("=");
                }
            }
            return paramStr.toString();
        }
        return null;
    }

    public int getConfigReadTimeout() {
	    return config.readTimeout;
    }
    public int getConfigWriteTimeout() {
	    return config.writeTimeout;
    }
	public int getConfigConnTimeout() {
		return config.connTimeout;
	}
}