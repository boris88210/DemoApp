package com.chailease.tw.app.android.camera.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class LocalImageLoader {
    private static final String TAG = LocalImageLoader.class.getSimpleName();
    private static LocalImageLoader instance;

    private LruCache<String, Bitmap> mMemoryCache;
    public static LocalImageLoader getInstance() {
        if (instance == null) {
            instance = new LocalImageLoader();
        }
        return instance;
    }

    private LocalImageLoader(){
        initMemoryCache();
    }

    //region 讀圖相關
    private void initMemoryCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void loadBitmap(String path, ImageView imageView, int reqWidth, int reqHeight) {
        Log.d(TAG, "loadBitmap: " + path + " request size: " + reqWidth + " " + reqHeight);
        imageView.setTag(path);
        final Bitmap bitmap = getBitmapFromMemCache(path);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(android.R.drawable.sym_def_app_icon);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, reqWidth, reqHeight);
            task.execute(path);
        }
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
            final Bitmap bitmap = decodeSampledBitmapFromFile(mPath, mReqWidth, mReqHeight);
            addBitmapToMemoryCache(mPath, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mPath.equals(mImageView.getTag())) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
    //endregion 讀圖相關

    //region utils
    public static Bitmap decodeSampledBitmapFromFile(String pathName,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        Log.d(TAG, "inSampleSize " + options.inSampleSize);
        Log.d(TAG, width + " " + height + " -- " + reqWidth + " " + reqHeight);
        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((height / inSampleSize) >= reqHeight
                    && (width / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    //endregion utils
}
