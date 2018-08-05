package com.development.borissu.demoapp.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.api.ApiManager;
import com.development.borissu.demoapp.api.params.ApiParamGetDataList;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends BaseNavigationActivity implements ApiUser {

    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.tv_api_data)
    TextView apiData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        ButterKnife.bind(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    }

    @OnClick(R.id.test_firebase)
    public void testFirebase() {
        String id = "Test_ID";
        String name = "Test_Name";
        Bundle bundle = new Bundle();
        bundle.putString("Btn_test_Firebase", id);
        bundle.putString("User_name", name);
        mFirebaseAnalytics.logEvent("Test_Firebase", bundle);
    }

    @OnClick(R.id.btn_call_api)
    public void callApi() {
        String name = "使用者名稱";
        int id = 123456;
        ApiParamGetDataList param = new ApiParamGetDataList(name, id);
        ApiManager.callApi(this, this, param);
    }

    @Override
    public void ApiCallBack(String index, Object o) {

        switch (index) {
            case "1":

                String[] apidataArray = (String[]) o;
                apiData.setText(index + apidataArray[0]);
                break;
        }

    }
}
