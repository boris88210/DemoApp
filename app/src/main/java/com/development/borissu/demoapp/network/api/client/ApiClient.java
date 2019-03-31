package com.development.borissu.demoapp.network.api.client;


/**
 *
 */
public abstract class ApiClient {


    /**
     * 執行Api任務
     */
    public abstract void execute();

    /**
     *
     */
    public abstract void onPostExecute();

    /**
     * 取得目前任務狀態
     *
     * @return 任務狀態
     */
    public abstract int getStatus();
}
