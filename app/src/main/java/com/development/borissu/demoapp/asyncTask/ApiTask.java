package com.development.borissu.demoapp.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.development.borissu.demoapp.DemoApp;

public class ApiTask extends AsyncTask<String, Integer, Boolean> {
    public int taskId;
    public int progress;

    public ApiTask(int taskId) {//建構子
        super();
        this.taskId = taskId;
    }

    @Override
    protected void onPreExecute() {//任務前
        super.onPreExecute();

        Log.i("AsyncTask", "onPreExecute ID: " + taskId);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        Log.i("AsyncTask", "Task ID: " + taskId + ", onPreExecute");
        for (int i = 0; i < 8; i++) {
            try {
                Thread.sleep(500);
                publishProgress(progress += 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                onPostExecute(false);
            }
            if (isCancelled()) {
                break;
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.i("AsyncTask", "Task ID:" + taskId + ", onProgressUpdate..." + values[0]);

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.i("AsyncTask", "Task ID: " + taskId + ", onPostExecute");
//        if (DemoApp.getCurrentTask().taskId == taskId) {
//            DemoApp.removeCurrentTask();
//        }

        DemoApp.removeTaskById(taskId);

        if (!DemoApp.hasTask()) {
            Log.i("AsyncTask", "Mission Complete!");
            return;
        }
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
    }

    @Override
    protected void onCancelled() {//任務取消
        super.onCancelled();
    }
}