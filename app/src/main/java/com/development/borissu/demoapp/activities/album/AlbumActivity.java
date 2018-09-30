package com.development.borissu.demoapp.activities.album;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.view.ActionMode;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import java.io.FileDescriptor;
import java.io.IOException;

import butterknife.BindView;

public class AlbumActivity extends BaseActivity {

    private static final int REQUEST_OPEN_RESULT_CODE = 123;

    @BindView(R.id.photoContainer)
    RelativeLayout photoContainer;

    @BindView(R.id.photo)
    ImageView photo;

    //客製化的View
    @BindView(R.id.pinchZoomImageView)
    PinchZoomImageView pinchZoomImageView;

    private Uri imageUri;
    private Animator animator;
    private int longAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(getApplicationContext(), "Long pressed", Toast.LENGTH_SHORT).show();
//                zoomImageFromThumb();
                pinchZoomPan();
                return true;
            }


        });

        //取得config的動畫時間
        longAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        Intent it = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        it.addCategory(Intent.CATEGORY_OPENABLE);
        it.setType("image/*");//Set an explicit MIME data type.
        startActivityForResult(it, REQUEST_OPEN_RESULT_CODE);


    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_OPEN_RESULT_CODE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                imageUri = resultData.getData();
                //使用以下方法會造成記憶體大量使用
//                try {
//                    Bitmap bitmap = getBitmapFromUri(uri);
//                    photo.setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                //使用第三方套件Glide，減少記憶體用量
//                Glide.with(this)
//                        .load(uri)
//                        .into(photo);


                Glide.with(this)
                        .load(imageUri)
                        .into(photo);
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return bitmap;
    }

    private void zoomImageFromThumb() {
        if (animator != null) {
            animator.cancel();
        }
        Glide.with(this)
                .load(imageUri)
                .into(pinchZoomImageView);

        Rect startBounds = new Rect();
        Rect finalBounds = new Rect();
        Point globalOffset = new Point();
        photo.getGlobalVisibleRect(startBounds);
        photoContainer.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() >
                (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;

        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = (float) startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;

        }

        photo.setAlpha(0f);
        pinchZoomImageView.setVisibility(View.VISIBLE);
        pinchZoomImageView.setPivotX(0f);
        pinchZoomImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(pinchZoomImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(pinchZoomImageView, View.X, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(pinchZoomImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(pinchZoomImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(longAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animator = null;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator = null;

            }
        });

        set.start();
        animator = set;

    }

    private void pinchZoomPan() {
        pinchZoomImageView.setImageUri(imageUri);
        photo.setAlpha(0.f);
        pinchZoomImageView.setVisibility(View.VISIBLE);

    }

}
