package com.chailease.tw.app.android.httpclient;

public class Config {

	public static final int HTTP_PORT_DEFAULT		=	80;
	public static final int HTTP_PORT_SSL_DEFAULT	=	443;
	public static final boolean OMITTED_VERIFY_SSL	=	false;

	String baseURL;
	int basePort;
	int sslPort;
	boolean omittingVerifySSLMode;
	String domain;
	int readTimeout;
	int writeTimeout;
	int connTimeout;

	public Config(String baseURL, int basePort, int sslPort
			, boolean omittingVerifySSLMode, int connTimeout, int readTimeout, int writeTimeout) {
		super();
		this.omittingVerifySSLMode = omittingVerifySSLMode;
		this.basePort = basePort;
		this.sslPort = sslPort;
		this.baseURL = baseURL;
		this.connTimeout = connTimeout;
		this.readTimeout = readTimeout;
		this.writeTimeout = writeTimeout;
	}
	public Config(String baseURL, int basePort, int sslPort
			, boolean omittingVerifySSLMode, int readTimeout, int writeTimeout) {
		this(baseURL, basePort, sslPort, omittingVerifySSLMode, 0, readTimeout, writeTimeout);
	}
	public Config(String baseURL, int basePort, int sslPort
			, boolean omittingVerifySSLMode) {
		this(baseURL, basePort, sslPort
			, omittingVerifySSLMode, 0, 0);
	}
	public Config(String baseURL) {
		this(baseURL, HTTP_PORT_DEFAULT);
	}
	public Config(String baseURL, int httpPort) {
		this(baseURL, httpPort
				, HTTP_PORT_SSL_DEFAULT
				, OMITTED_VERIFY_SSL);
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
