package com.development.borissu.demoapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.development.borissu.demoapp.asyncTask.ApiTask;
import com.development.borissu.demoapp.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class DemoApp extends Application {

    //todo:存放整個app共用的東西

    protected static List<ApiTask> taskList;

    protected static Context mContext;

    protected static int totalTask;
    protected static int finishedTask;

    protected static long startTime;

    protected static Activity currentActivity;

    public static void addNewTask(ApiTask task) {
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.add(task);
    }

    public static ApiTask getCurrentTask() {
        if (hasTask()) {
            return taskList.get(0);
        }
        return new ApiTask(-1);
    }

    public static void removeCurrentTask() {
        if (hasTask()) {
            taskList.remove(0);

        }
        if (hasTask()) {
            startAllTask(mContext);
        } else {
            Toast.makeText(mContext, "任務全部完成", Toast.LENGTH_SHORT).show();
        }
    }

    public static void clearTaskList() {
        if (hasTask()) {
            taskList.clear();
        }
    }

    public static boolean hasTask() {
        if (taskList != null && taskList.size() != 0) {
            return true;
        }
        return false;
    }

    public static void startAllTask(Context context) {
        mContext = context;
        if (hasTask()) {
            startTime = System.currentTimeMillis();
            finishedTask = 0;
            totalTask = taskList.size();
            taskList.get(0).execute();
        }
    }

    public static void stopTask() {
        if (hasTask()) {
            Log.i("AsyncTask", "stopTask, current task id: " + getCurrentTask().taskId);

            taskList.get(0).cancel(true);

            removeCurrentTask();
        }
    }

    public static void removeTaskById(int taskId) {
        if (!hasTask()) {
            return;
        }
        ApiTask removeTask = null;
        for (ApiTask task : taskList) {
            if (task.taskId == taskId) {
                removeTask = task;
                break;
            }
        }
        finishedTask += 1;

        taskList.remove(removeTask);
        if (!hasTask()) {

            if (mContext != null) {
                Toast.makeText(mContext, "全部任務完成", Toast.LENGTH_SHORT).show();
            }
        }
//        else {
//            startAllTask(mContext);
//        }
        if (currentActivity instanceof MainActivity) {
            MainActivity a = (MainActivity) currentActivity;
            a.updateApiProgress(finishedTask * 100 / totalTask);
            Log.i("Asynctask", "Task ID: " + taskId + ", total: " + totalTask + ", current: " + finishedTask);
            if (!hasTask()) {
                a.updateApiProgressTime(System.currentTimeMillis() - startTime);
            }
        }
    }

    public static void startTaskSameTime(Context context) {
        mContext = context;
        if (hasTask()) {
            startTime = System.currentTimeMillis();
            finishedTask = 0;
            totalTask = taskList.size();
            for (ApiTask task : taskList) {
//                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                task.execute();
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
                switch (activity.getLocalClassName()) {
                    case "MainActivty":
                        MainActivity a = (MainActivity) activity;
                        if (taskList != null && totalTask != 0) {
                            a.updateApiProgress(finishedTask * 100 / totalTask);
                        }
                        break;
                    default:
                        Log.i("AsyncTask", "Class name: " + activity.getClass().getSimpleName());
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
//                switch (activity.getLocalClassName()) {
//                    case "MainActivty":
//                        MainActivity a = (MainActivity) activity;
//                        if (totalTask != 0) {
//                            a.updateApiProgress(finishedTask * 100 / totalTask);
//                        }
//                        break;
//                    default:
//                        Log.i("AsyncTask", "Class name: " + activity.getClass().getSimpleName());
//                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


}
