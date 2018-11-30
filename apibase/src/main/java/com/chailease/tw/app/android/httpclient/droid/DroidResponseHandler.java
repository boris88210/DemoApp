package com.chailease.tw.app.android.httpclient.droid;

import android.net.Uri;

import com.chailease.tw.app.android.apiclient.APIRequestParam;
import com.chailease.tw.app.android.httpclient.IHttpResponseHandler;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 */
public class DroidResponseHandler implements IHttpResponseHandler {

    APIRequestParam[] params;
    int statusCode;
    String content;
    Uri tmpUri;
    File tmpFile;

    public DroidResponseHandler(APIRequestParam... params) {
        this.params = params;
    }

    @Override
    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void setResponseContent(String responseContent) {
        this.content = responseContent;
    }

    @Override
    public String getResponseContent() {
        return content;
    }

    @Override
    public void setResponseTempFilePath(Uri uri) {
        this.tmpUri = uri;
        this.tmpFile = new File(uri.getPath());
        try {
            this.tmpFile = new File(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setResponseTempFilePath(File file) {
        this.tmpFile = file;
        this.tmpUri = Uri.fromFile(file);
    }

    @Override
    public Uri getResponseTempFileURL() {
        return tmpUri;
    }

    @Override
    public File getResponseTempFile() {
        return tmpFile;
    }

}