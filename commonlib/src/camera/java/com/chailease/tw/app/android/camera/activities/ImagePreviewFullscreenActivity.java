package com.chailease.tw.app.android.camera.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.chailease.tw.app.android.commonlib.R;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImagePreviewFullscreenActivity extends AppCompatActivity {
    private static final String TAG = ImagePreviewFullscreenActivity.class.getSimpleName();
    public static final String EXTRA_PATH = "EXTRA_PATH";

    //region 可調屬性
    private final float MAX_SCALE = 5.0f;
    private final float MIN_SCALE = 1.0f;

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
    //endregion 可調屬性

    View mContentView;
    View mControlsView;
    ImageView imgScalable;
    ImageButton btnClose;

    private final Handler mHideHandler = new Handler();
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
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    // 預設原尺寸
    private float mScaleFactor = 1.0f;
    private boolean isScale = false;
    private float imgOriginalScale;
    private int imgOriginalWidth = 0, imgOriginalHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_preview_fullscreen);

        // UI
        mContentView = (View) findViewById(R.id.fullscreen_content);
        mControlsView = (View) findViewById(R.id.fullscreen_content_controls);
        imgScalable = (ImageView) findViewById(R.id.img_scalable);
        btnClose = (ImageButton) findViewById(R.id.btn_close);

        mVisible = true;

        final String path = getIntent().getStringExtra(EXTRA_PATH);
        imgScalable.setTag(path);

        // 偵測滾動跟點擊
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "onScroll = " + distanceX + " " + distanceY);
                if (mScaleFactor > 1) {
                    moveImage(distanceX, distanceY);
                    return true;
                } else {
                    moveImage(0, 0);
                    return false;
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp");
                toggle();
                return true;
            }
        });
        // 偵測縮放
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                isScale = true;
                float scaleFactor = detector.getScaleFactor();
                Log.d(TAG, "onScale mScaleFactor = " + mScaleFactor + " " + scaleFactor);
                mScaleFactor *= scaleFactor;

                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(MIN_SCALE, Math.min(mScaleFactor, MAX_SCALE));

                Log.d(TAG, "onScale mScaleFactor after = " + mScaleFactor);

                // Set the image matrix scale
                setImageScaleAlignCenter(mScaleFactor);
                return true;
            }
        });

        imgScalable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch " + event);
                boolean retVal = scaleGestureDetector.onTouchEvent(event);
                if (!isScale) {
                    retVal = gestureDetector.onTouchEvent(event) || retVal;
                } else {
                    isScale = false;
                }
                return retVal || imgScalable.onTouchEvent(event);
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 為了能取得view的大小，暫先用Runnable的方式
        mContentView.post(new Runnable() {
            @Override
            public void run() {
                BitmapWorkerTask task = new BitmapWorkerTask(imgScalable, mContentView.getWidth(), mContentView.getHeight());
                task.execute(path);
            }
        });
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
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
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

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;
        private int mReqWidth, mReqHeight;
        private String mPath;

        public BitmapWorkerTask(ImageView imageView, int reqWidth, int reqHeight) {
            mImageView = imageView;
            mReqWidth = reqWidth;
            mReqHeight = reqHeight;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            mPath = params[0];
            Log.d(TAG, "doInBackground " + mPath + " " + mReqWidth + " " + mReqHeight);
            final Bitmap bitmap = LocalImageLoader.decodeSampledBitmapFromFile(mPath, mReqWidth, mReqHeight);
            Log.d(TAG, "doInBackground bitmap = " + bitmap.getWidth() + " " + bitmap.getHeight());
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mPath.equals(mImageView.getTag())) {
                mImageView.setImageBitmap(bitmap);
                mImageView.setScaleType(ImageView.ScaleType.MATRIX);
                imgOriginalWidth = bitmap.getWidth();
                imgOriginalHeight = bitmap.getHeight();
                imgOriginalScale = Math.min((float) mImageView.getWidth() / imgOriginalWidth, (float) mImageView.getHeight() / imgOriginalHeight);
                setImageScaleAlignCenter(mScaleFactor);
            }
        }
    }

    // 利用matrix讓圖片置中縮放
    private void setImageScaleAlignCenter(float scaleFactor) {
        Matrix matrix = new Matrix(imgScalable.getImageMatrix());
        matrix.setScale(scaleFactor * imgOriginalScale, scaleFactor * imgOriginalScale);
        float centerX = (imgScalable.getWidth() - imgOriginalWidth * scaleFactor * imgOriginalScale) / 2;
        float centerY = (imgScalable.getHeight() - imgOriginalHeight * scaleFactor * imgOriginalScale) / 2;
        matrix.postTranslate(centerX, centerY);
        imgScalable.setImageMatrix(matrix);
    }

    private void moveImage(float distanceX, float distanceY) {
        Matrix matrix = new Matrix(imgScalable.getImageMatrix());
        matrix.postTranslate(-distanceX, -distanceY);
        imgScalable.setImageMatrix(matrix);
    }
}
