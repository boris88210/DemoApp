package com.chailease.tw.app.android.httpclient;

import android.net.Uri;

import java.io.File;

/**
 * 定義 HTTP Response 的相關屬性及結果
 */
public interface IHttpResponseHandler {

	public static final int STATUS_CODE_BAD_NETWORK	=	-999;
	public static final int STATUS_CODE_INPROCESS	=	1;
	public static final int STATUS_CODE_UNKNOWN	=	-1;
	public static final int STATUS_CODE_CANCELLED	=	99;
	public static final int STATUS_CODE_FAILURED	=	-99;
	public static final int STATUS_CODE_COMPLETED	=	200;
	public static final int STATUS_CODE_FILENOTFOUND	=	404;

	/**
	 * 設定 HTTP STATUS CODE, 依據實作結果將 HTTP STATUS CODE 回饋至 ResponseHandler
	 * @param code
	 */
	public void setStatusCode(int code);

	/**
	 * 使用套件者可透過 Response Handler 取得 HTTP STATUS CODE
	 * @return
	 */
	public int getStatusCode();

	/**
	 * 依據實作結果將 Response Content Body 回饋至 Response Handler
	 * @param responseContent
	 */
	public void setResponseContent(String responseContent);

	/**
	 * 使用套件者可透過 Response Handler 取得 Response Content Body
	 * @return
	 */
	public String getResponseContent();

	/**
	 * 依據實作結果將 Response Content 回饋至 Response Handler 如果結果為檔案下載
	 * @param uri
	 */
	public void setResponseTempFilePath(Uri uri);
	public void setResponseTempFilePath(File file);

	/**
	 * 使用套件者可透過 Response Handler 取得 Response Content 如果結果為檔案下載
	 * @return
	 */
	public Uri getResponseTempFileURL();
	/**
	 * 使用套件者可透過 Response Handler 取得 Response Content 如果結果為檔案下載
	 * @return
	 */
	public File getResponseTempFile();

}