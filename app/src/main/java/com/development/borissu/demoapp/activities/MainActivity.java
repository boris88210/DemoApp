package com.development.borissu.demoapp.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.dataModel.SelectionItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNavigationActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.tv_api_data)
    TextView apiData;
    @BindView(R.id.tv_api_progress)
    TextView tvProgress;
    @BindView(R.id.tv_api_progress_time)
    TextView tvProgressTime;
    @BindView(R.id.tv_version_code)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        ButterKnife.bind(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        List<SelectionItem> itemList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SelectionItem item = new SelectionItem(i, "Item " + i);
            itemList.add(item);
        }

        Log.i("TEST", "is equal? " + itemList.get(0).equals(new SelectionItem(0, "1234")));
        Log.i("TEST", "is equal? " + itemList.contains(new SelectionItem(0, "1234")));

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText(info.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.test_firebase)
    public void testFirebase() {
//        String id = "Test_ID";
//        String name = "Test_Name";
//        Bundle bundle = new Bundle();
//        bundle.putString("Btn_test_Firebase", id);
//        bundle.putString("User_name", name);
//        mFirebaseAnalytics.logEvent("Test_Firebase", bundle);
        testInteger();
    }
//
//    @OnClick(R.id.btn_call_api)
//    public void callApi() {
//        String name = "使用者名稱";
//        int id = 123456;
//        ApiParamGetDataList param = new ApiParamGetDataList(name, id);
//        ApiManager.callApi(this, this, param);
//    }
//
//    @Override
//    public void ApiCallBack(String index, Object o) {
//
//        switch (index) {
//            case "1":
//
//                String[] apidataArray = (String[]) o;
//                apiData.setText(index + apidataArray[0]);
//                break;
//        }
//
//    }

    public void updateApiProgress(int progress) {
        tvProgress.setText("目前API進度: " + progress);
    }

    public void updateApiProgressTime(long progress) {
        tvProgressTime.setText("API費時: " + progress / 1000);
    }


    private void testInteger() {
        Gson gson = new Gson();
        String jsonStr = "{\"intObject\":null,\"intType\":321}";
        IntegerClass integerClass = gson.fromJson(jsonStr, IntegerClass.class);
        int j = integerClass.intObject;
        Log.i("Json", "test" + j);

    }

    public class IntegerClass {

        public Integer intObject;

        public int intType;

    }

}
