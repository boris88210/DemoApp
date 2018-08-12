package com.development.borissu.demoapp.activities.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
//Camera Device Callback
//Camera Session Callback


/*
1 使用TextureView作為預覽相機畫面的元件，並設定surfaceTextureListener
1-1 若TextureView is available 設定相機
設定相機:
    1. 取得目標相機id
    2. 取得預覽解析度
1-2 若TextureView is available 開啟相機
    取得相機危險權限

2 設定Camera state callback
2-1 onOpened,取得CameraDevice
2-2 onDisconnected,釋放資源
2-3 onError,釋放資源

3 onOpened中建立預覽

4 onResume中要開啟相機
  onPause中要關閉相機

5 建立執行緒，供以下使用
    開啟相機
    建立capture session

6 設定對焦

7 設定ImageReader

8 capture still image

9


 */

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CameraActivity extends BaseActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    //    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        mVisible = true;
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    //Camera


    private static final int REQUEST_CODE_CAMER = 1234;

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mState;

    @BindView(R.id.fullscreen_content)
    TextureView mPreview;
//    @BindView(R.id.gellery_recyclerview)
//    RecyclerView mRecyclerView;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private ImageView mPhotoCapturedImageView;
    private String GALLERY_LOCATION = "image gallery";
    private File mGalleryFolder;

    private Size mPreviewSize;
    private String mCameraId;

    private CameraDevice mCameraDevice;

    //定義相機各個狀態的callback
    private CameraDevice.StateCallback mCameraDeviceStateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.d("Camera", "Camera OnOpened");
                    mCameraDevice = camera;
                    createCameraPreviewSession();
//                    Toast.makeText(getApplicationContext(), "Camera Opened!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    Log.d("Camera", "Camera OnDisconnected");

                    //失去連線的時候釋放資源
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.d("Camera", "Camera OnError");

                    //發生錯誤的時候釋放資源
                    camera.close();
                    mCameraDevice = null;
                }
            };

    //攝影請求
    private CaptureRequest mPreviewCaptureRequest;
    private CaptureRequest.Builder mPreviewCaptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW://預覽狀態
                    //不做任何事
                    break;
                case STATE_WAIT_LOCK://對焦狀態
                    Integer afState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (afState == CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED) {
//                        unLockFocus();
//                        Log.d("Camera", "Focus Lock Success!");
//                        Toast.makeText(getApplicationContext(), "對焦成功", Toast.LENGTH_SHORT).show();
                        //拍照
                        captureStillImage();
                    }
                    break;
            }
        }

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.d("Camera", "Focus Lock Fail!");

            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        }
    };

    //執行緒
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    //相片檔案
    private String mImageFileLocation = "";
    private static File mImageFile;
    private ImageReader mImagereader;
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage()));
                }
            };


    private static class ImageSaver implements Runnable {

        private final Image mImage;

        private ImageSaver(Image image) {
            mImage = image;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);

            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(mImageFile);
                fileOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        openBackgroundThread();

        if (mPreview.isAvailable()) {
            setupCamera(mPreview.getWidth(), mPreview.getHeight());
            openCamera();
        } else {
            mPreview.setSurfaceTextureListener(listener);
        }

    }

    @Override
    protected void onPause() {

        closeCamera();

        closeBackgroundThread();

        super.onPause();
    }

    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {

        //SurfaceTextureListener
        @SuppressLint("MissingPermission")
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            openCamera();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    //Camera
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            //取得主要相機id
            for (String cameraId : cameraManager.getCameraIdList()) {

                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                //todo:多鏡頭是否適用?

                //跳過前鏡頭
                if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                //取得相機支援可輸出格式，
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                Size largestImageSize = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new Comparator<Size>() {
                            @Override
                            public int compare(Size lhs, Size rhs) {
                                return Long.signum(lhs.getWidth() * lhs.getHeight() -
                                        rhs.getWidth() * rhs.getHeight());
                            }
                        }
                );
                mImagereader = ImageReader.newInstance(largestImageSize.getWidth(),
                        largestImageSize.getHeight(),
                        ImageFormat.JPEG,
                        1);
                mImagereader.setOnImageAvailableListener(mOnImageAvailableListener,
                        mBackgroundHandler);

                //取得相容於輸出格式的尺寸list，再取得最小
                mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                Log.d("Camera", "Preferred Preview size : (" + mPreviewSize.getWidth() + ", " + mPreviewSize.getHeight() + ")");
                //紀錄主鏡頭id
                mCameraId = cameraId;

                return;

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 取得最佳(最小)預覽尺寸
     *
     * @param mapSizes
     * @param width
     * @param height
     * @return
     */
    private Size getPreferredPreviewSize(Size[] mapSizes, int width, int height) {
        //建立可使用Size List
        List<Size> collectorSizes = new ArrayList<>();

        //從map中取得能用的尺寸
        for (Size option : mapSizes) {
            if (width > height) {//橫向
                if (option.getWidth() >= width &&
                        option.getHeight() >= height) {//輸出解析度 > 螢幕解析度
                    collectorSizes.add(option);

                }
            } else {//直向
                if (option.getWidth() >= height &&
                        option.getHeight() >= width) {
                    collectorSizes.add(option);
                }
            }
        }

        //若有合用的輸出，則回傳排序後 像素面積最小的
        if (collectorSizes.size() > 0) {
            //回傳
            return Collections.min(collectorSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {

                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        //若無則回傳空List
        return mapSizes[0];
    }

    //開啟相機，必須取得危險權限
    private void openCamera() {
        Log.d("Camera", "open camera");

        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);

        //檢查權限
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //請求權限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMER);
        } else {
            //有取得權限的情況
            try {
                //開始camera source
//                    cameraSource.start(cameraPreview.getHolder());
                manager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.e("Camera", "openCamera Error");
            }
        }
        Toast.makeText(this, "已有權限", Toast.LENGTH_SHORT).show();
    }


    //關閉camera釋放資源
    private void closeCamera() {
        Log.d("Camera", "close camera");
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mImagereader != null) {
            mImagereader.close();
            mImagereader = null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    openCamera();

                } else {

                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //建立預覽Session
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = mPreview.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            mPreviewCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImagereader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (mCameraDevice == null) {
                                return;
                            }
                            try {
                                mPreviewCaptureRequest = mPreviewCaptureRequestBuilder.build();
                                mCameraCaptureSession = session;
                                mCameraCaptureSession.setRepeatingRequest(
                                        mPreviewCaptureRequest,
                                        mSessionCaptureCallback,
                                        mBackgroundHandler
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "create camera session failed", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e("Camera", "createCameraPreviewSession Error");

        }
    }

    //開啟相機執行緒
    private void openBackgroundThread() {
        Log.d("Camera", "open camera thread");

        mBackgroundThread = new HandlerThread("Camera2 background thread");
        mBackgroundThread.start();

        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());


    }

    //關閉相機執行緒
    private void closeBackgroundThread() {
        Log.d("Camera", "close camera thread");

        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //拍照
    @OnClick(R.id.btn_take_photo)
    public void takePhoto(View view) {
        try {
            mImageFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lockFocus();
    }

    //建立存擋路徑
    private void createImageGallery() {
        // 路徑:/storage/emulated/0/Pictures
        // File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // 路徑:/storage/emulated/0
        // File storageDirectory = Environment.getExternalStorageDirectory();
        // 路徑:/data/user/0/com.development.borissu.demoapp.debug/files
        File storageDirectory = this.getFilesDir();

        Log.d("Camera", "External Storage Public Directory: " + storageDirectory.getPath());
//        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
//        if (!mGalleryFolder.exists()) {
//            mGalleryFolder.mkdirs();
//        }

    }

    //建立影像檔案
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";

        File image = File.createTempFile(imageFileName, ".jpg", mGalleryFolder);
        mImageFileLocation = image.getAbsolutePath();

        return image;

    }

    void setReducedImageSize() {
        int targetImageViewWidth = mPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);


    }

    //鎖定焦距
    private void lockFocus() {
        try {
            mState = STATE_WAIT_LOCK;
            mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_START);
            mCameraCaptureSession.capture(mPreviewCaptureRequestBuilder.build(),
                    mSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //解除鎖定焦距
    private void unLockFocus() {
        try {
            mState = STATE_PREVIEW;
            mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
            mCameraCaptureSession.capture(mPreviewCaptureRequestBuilder.build(),
                    mSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //捕捉靜止圖像
    private void captureStillImage() {
        try {

            CaptureRequest.Builder captureStillBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            captureStillBuilder.addTarget(mImagereader.getSurface());

            //WindowManager: 與畫面溝通的interface
            //Display: 畫面資訊
            //getRotation: 取得現在畫面選轉的角度
            int rotation = getWindowManager().getDefaultDisplay().getRotation();

            captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    ORIENTATIONS.get(rotation));

            CameraCaptureSession.CaptureCallback captureCallback =
                    new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                       @NonNull CaptureRequest request,
                                                       @NonNull TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);

                            Toast.makeText(getApplicationContext(),
                                    "Image Captured!", Toast.LENGTH_SHORT).show();

                            unLockFocus();

                        }
                    };
            mCameraCaptureSession.capture(
                    captureStillBuilder.build(), captureCallback, null
            );

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
