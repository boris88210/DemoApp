package com.development.borissu.demoapp.network.api.client;

import com.development.borissu.demoapp.network.api.connection.ApiConnection;
import com.google.android.gms.common.api.Api;

public class FuncApiClient extends ApiClient {

    ApiConnection mApiConnection;

    public void setApiConnection(ApiConnection apiConnection) {
        mApiConnection = apiConnection;
    }

    @Override
    public void execute() {
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public void onPostExecute() {

    }
}
