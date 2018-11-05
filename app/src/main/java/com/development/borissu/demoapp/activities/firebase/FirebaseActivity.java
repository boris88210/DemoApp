package com.development.borissu.demoapp.activities.firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import butterknife.OnClick;

public class FirebaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
    }

    @OnClick(R.id.btn_test_crash)
    public void onClickTestCrashlytics() {
        Crashlytics.getInstance().crash();
    }
}
