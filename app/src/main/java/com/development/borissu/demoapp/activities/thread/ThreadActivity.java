package com.development.borissu.demoapp.activities.thread;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.development.borissu.demoapp.AsyncTask.ApiTask;
import com.development.borissu.demoapp.DemoApp;
import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import butterknife.OnClick;

public class ThreadActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        initData();
    }

    private void initData() {

        for (int i = 0; i < 20; i++) {
            ApiTask task = new ApiTask(i);
            DemoApp.addNewTask(task);
        }
    }

    @OnClick(R.id.btn_start_thread)
    public void onClickBtn() {
        DemoApp.clearTaskList();
        for (int i = 0; i < 8; i++) {
            ApiTask task = new ApiTask(i);
            DemoApp.addNewTask(task);
        }
//        DemoApp.startAllTask(getApplicationContext());
        DemoApp.startTaskSameTime(getApplicationContext());
    }

    @OnClick(R.id.btn_start_thread_again)
    public void onClickAgain() {
        DemoApp.stopTask();
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thread")
                .setMessage("Thread Start")
                .setPositiveButton("OK", null)
                .show();
    }


}
