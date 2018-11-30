package com.chailease.tw.app.android.camera.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.chailease.tw.app.android.commonlib.R;
import com.chailease.tw.app.android.camera.cameraview.AspectRatio;
import com.chailease.tw.app.android.camera.cameraview.CameraView;
import com.google.firebase.database.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class GoogleCameraViewActivity extends AppCompatActivity {
    protected static final String TAG = GoogleCameraViewActivity.class.getSimpleName();
    protected static final int REQUEST_CAMERA_PERMISSION = 123;

    public static final String EXTRA_FOLDER = "EXTRA_FOLDER";
    public final static String CAMERA_MODE = "CAMERA_MODE";
    public final static String FILE_NAMING_FORMAT = "FILE_NAMING_FORMAT";

    public static final int MODE_SINGLE_SHOT = 20201;
    public static final int MODE_MULTI_SHOT = 20202;

    protected static final int ORDER_FLASH_SWITCH = 0;
    protected static final int ORDER_CAMERA_SWITCH = 1;

    protected int cameraMode;
    protected String tempFilePath;
    protected String fileNamingFormat;

    //閃光定的控制變數，有四種選項
    protected static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,//閃光自動3
            CameraView.FLASH_OFF,//閃光關閉0
            CameraView.FLASH_ON,//閃光開啟1
//            CameraView.FLASH_TORCH,//火炬模式2
    };
    ////閃光燈控制圖示，順序對應四種控制變數，目前沒有火炬模式的圖案
    protected static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };
    //閃光燈控制名稱，順序對應四種控制變數
    protected static final String[] FLASH_TITLES = {
            "auto",
            "off",
            "on",
//            "torch",
    };
    //紀錄目前的閃光燈設定
    protected int mCurrentFlash;

    //日期格式
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

    //UI
    protected CameraView mCameraView;

    //相機callback
    protected CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        //拍攝相片
        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            int length = (data != null) ? data.length : 0;
            Log.d(TAG, "onPictureTaken " + length);
//            //提示已拍照
//            Toast.makeText(cameraView.getContext(), "picture take", Toast.LENGTH_SHORT)
//                    .show();
            //取得Handler，請Handler執行相片存檔
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    savePic(data);
                }
            });
        }

    };

    //Handler
    protected Handler mBackgroundHandler;

    public static final String LOG_LIFECYCLE = "LIFE_CYCLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設定layout
        setContentView(savedInstanceState);
        //取得cameraview，設定callback
        mCameraView = (CameraView) findViewById(R.id.camera_view);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }
        //設定Actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        //設定快門按紐
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_capture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.takePicture();
            }
        });
    }

    //可override自訂的layout
    protected void setContentView(Bundle bundle) {
        setContentView(R.layout.activity_google_camera_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //取得相機服務設定
        Intent intent = getIntent();
        cameraMode = intent.getIntExtra(CAMERA_MODE, MODE_SINGLE_SHOT);
        tempFilePath = intent.getStringExtra(EXTRA_FOLDER);
        fileNamingFormat = intent.getStringExtra(FILE_NAMING_FORMAT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //確認有權限，則啟動相機
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (cameraMode == MODE_MULTI_SHOT) {
            backToCaller();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加入閃光燈功能
        menu.add(Menu.NONE, Menu.NONE, ORDER_FLASH_SWITCH, "auto");
        MenuItem flashSwitch = menu.getItem(ORDER_FLASH_SWITCH);
        flashSwitch.setTitle(FLASH_TITLES[mCurrentFlash]);
        flashSwitch.setIcon(FLASH_ICONS[mCurrentFlash]);
        flashSwitch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //加入切換相機功能
//        menu.add(Menu.NONE, Menu.NONE, ORDER_CAMERA_SWITCH, "switch_camera");
//        MenuItem cameraSwitch = menu.getItem(ORDER_CAMERA_SWITCH);
//        cameraSwitch.setIcon(R.drawable.ic_switch_camera);
//        cameraSwitch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getOrder()) {
            case ORDER_FLASH_SWITCH:
                if (mCameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                return true;
            case ORDER_CAMERA_SWITCH:
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //show出相片比例選擇視窗
    private void showRatioDialog() {
        final Set<AspectRatio> ratios = mCameraView.getSupportedAspectRatios();
        final AspectRatio currentRatio = mCameraView.getAspectRatio();
        final AspectRatio[] aspectRatios = ratios.toArray(new AspectRatio[ratios.size()]);
        int selectedPos = 0;
        if (currentRatio != null) {
            for (; selectedPos < aspectRatios.length; selectedPos++) {
                if (currentRatio.equals(aspectRatios[selectedPos])) {
                    break;
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, aspectRatios),
                selectedPos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mCameraView != null) {
                            Toast.makeText(GoogleCameraViewActivity.this, aspectRatios[which].toString(), Toast.LENGTH_SHORT).show();
                            mCameraView.setAspectRatio(aspectRatios[which]);
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "無相機權限",
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    //取得handler
    protected Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    //相片存檔
    protected void savePic(byte[] data) {
        File tempFile = createOutputFile();
        if (tempFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(data);
            fos.close();
//            if (isCompileLog) {
//                Toast.makeText(getApplicationContext(), String.format("相片已存至 %s", tempFile.getAbsolutePath()), Toast.LENGTH_SHORT).show();
//            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        if (cameraMode == MODE_SINGLE_SHOT) {
            backToCaller();
        }


    }



    //建立一個檔案
    protected File getOutputMediaFile(String fileName) {
        File outputFolder = new File(getIntent().getStringExtra(EXTRA_FOLDER));
        File outputFile = new File(outputFolder, fileName);
        return outputFile;
    }

    //返回呼叫的Activity
    protected void backToCaller() {
        Intent it = new Intent();
        setResult(RESULT_OK, it);
        finish();
    }

    //依照不同模式，建立存檔路徑
    protected File createOutputFile() {
        File photoFile = null;
        if (null == fileNamingFormat) {
            photoFile = getOutputMediaFile(String.format("IMG_%s.jpg", simpleDateFormat.format(Calendar.getInstance().getTime())));
        } else {
            String fileName = setNamingRule();
            photoFile = new File(tempFilePath, fileName);
        }
        return photoFile;
    }

    //請override自訂的命名規則
    protected String setNamingRule() {
        return String.format(this.fileNamingFormat, System.currentTimeMillis());
    }
}
