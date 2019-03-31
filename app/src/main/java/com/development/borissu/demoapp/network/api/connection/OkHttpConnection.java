package com.development.borissu.demoapp.network.api.connection;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.development.borissu.demoapp.network.api.client.ApiClient;
import com.development.borissu.demoapp.utilities.LogUtility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpConnection implements ApiConnection {

    Handler handler;
    OkHttpClient mClient;

    public OkHttpConnection() {
        //取得MainThread
        handler = new Handler(Looper.getMainLooper());
        mClient = new OkHttpClient().newBuilder().build();

    }

    @Override
    public void connectApi(final ApiClient apiClient) {
        //建立
        Request request = new Request.Builder()
                .url("https://ptx.transportdata.tw/MOTC/v2/Bus/DataVersion/City/Taipei?$format=JSON")
                .build();

        //建立任務
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponse(e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(apiClient, response);
            }
        });
    }

    /**
     * 處理錯誤
     *
     * @param e
     */
    private void handleResponse(Throwable e) {
        LogUtility.error("handleResponse", "Api connection fail.", e);

    }

    /**
     * 處理Api回傳，並回到Main thread
     *
     * @param response
     */
    private void handleResponse(final ApiClient apiClient, Response response) {
        String responseJson = response.toString();

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (apiClient != null) {
                    apiClient.onPostExecute();
                }
            }
        });
    }
}
