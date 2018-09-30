package com.development.borissu.demoapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.development.borissu.demoapp.R;

import butterknife.OnClick;

public class ThreadActivity extends BaseActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

    }

    @OnClick(R.id.btn_start_thread)
    public void onClickBtn() {
        try {
            Thread.sleep(5000);
            showAlert();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thread")
                .setMessage("Thread Start")
                .setPositiveButton("OK",null)
                .show();
    }
}
