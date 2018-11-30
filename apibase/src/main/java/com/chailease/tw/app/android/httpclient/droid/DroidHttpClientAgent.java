package com.chailease.tw.app.android.httpclient.droid;

import android.app.Activity;

import com.chailease.tw.app.android.httpclient.Config;
import com.chailease.tw.app.android.httpclient.HttpClientAgent;
import com.chailease.tw.app.android.httpclient.IHttpResponseHandler;
import com.chailease.tw.app.android.httpclient.IRequestParams;
import com.chailease.tw.app.android.utils.LogUtility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
public class DroidHttpClientAgent extends HttpClientAgent {

	public DroidHttpClientAgent(Config _config) {
		super(_config);
	}

	@Override
	public void setCookie(Activity context, HttpClientCookie[] cookies) {

	}

	@Override
	public void upload(String servicePath, IRequestParams params, IHttpResponseHandler handler, UPLOAD_TYPE uploadType, String paramName, Object... source) {
	}

	@Override
	public void doPost(String servicePath, IRequestParams params, IHttpResponseHandler handler) {
		String result = "";
		OutputStreamWriter wr = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		try {
			URL realUrl = new URL(getAbsoluteUrl(servicePath));
			// Open Connection
			try {
				URLConnection urlConnection = realUrl.openConnection();
				// Setting Request Property
				urlConnection.setRequestProperty("accept", "*/*");
				urlConnection.setRequestProperty("connection", "Keep-Alive");
				if (getConfigConnTimeout()>0)
					urlConnection.setConnectTimeout(getConfigConnTimeout()*1000);
				if (getConfigReadTimeout()>0)
					urlConnection.setReadTimeout(getConfigReadTimeout()*1000);
				//urlConnection.setRequestProperty("user-agent", "Mozilla/4.0(compatible; MSIE 6.0; Windows NT 5.1; SV1)");

				if (null != params) {   //  for POST params
					urlConnection.setDoOutput(true);
					urlConnection.setDoInput(true);
					wr = new OutputStreamWriter(urlConnection.getOutputStream());
					wr.write(converToQueryParams(params));
					wr.flush();
//                    printWriter = new PrintWriter(urlConnection.getOutputStream());
//                    printWriter.print(converToQueryParams(params));
//                    printWriter.flush();
				} else {
					urlConnection.connect();
				}
				if (DEBUGLOG_ENABLED) {
					LogUtility.info(this, "doPost", "url >>" + urlConnection.getURL().toString());
					LogUtility.debug(this, "doPost", "url.path >>" + urlConnection.getURL().getPath());
					LogUtility.debug(this, "doPost", "contentType >>"+urlConnection.getContentType());
					LogUtility.info(this, "doPost", "contentLength >>"+urlConnection.getContentLength());
					LogUtility.debug(this, "doPost", "contentEncoding >>"+urlConnection.getContentEncoding());
					LogUtility.debug(this, "doPost", "contentDate >>"+urlConnection.getDate());
				}

				//Map<String, List<String>> map = urlConnection.getHeaderFields();
				bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String line;
				for (;(line = bufferedReader.readLine()) != null;){
					result += "\n" + line;
				}
				handler.setResponseContent(result);
				handler.setStatusCode(IHttpResponseHandler.STATUS_CODE_COMPLETED);
			} catch (FileNotFoundException e) {
				handler.setStatusCode(IHttpResponseHandler.STATUS_CODE_FILENOTFOUND);
				LogUtility.error(this, "doPost", realUrl.getPath() + " failure >> "+e.getMessage(), e);
			} catch (IOException e) {
				handler.setStatusCode(IHttpResponseHandler.STATUS_CODE_FAILURED);
				LogUtility.error(this, "doPost", realUrl.getPath() + " failure >> "+e.getMessage(), e);
			}
		} catch (MalformedURLException e) {
			handler.setStatusCode(IHttpResponseHandler.STATUS_CODE_FAILURED);
			LogUtility.error(this, "doPost", "request failure >> " + e.getMessage(), e);
		} finally {
			if (null != bufferedReader){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					LogUtility.error(this, "doPost", "close response reader failure >> " + e.getMessage(), e);
				}
			}
			if (null != printWriter){
				printWriter.close();
			}
			if (null != wr){
				try {
					wr.close();
				} catch (IOException e) {
					LogUtility.error(this, "doPost", "close output writer failure >> " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void doGet(String servicePath, IRequestParams params, IHttpResponseHandler handler) {
		try {
			if (servicePath.indexOf("?")<0) {
				servicePath += "?" + converToQueryParams(params);
			} else {
				servicePath += "&" + converToQueryParams(params);
			}
		} catch (UnsupportedEncodingException e) {
			LogUtility.error(this, "doGet", "converToQueryParams", e);
		}
		doPost(servicePath, null, handler);
	}

}