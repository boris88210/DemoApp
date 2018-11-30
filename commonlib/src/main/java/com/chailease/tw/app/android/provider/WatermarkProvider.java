package com.chailease.tw.app.android.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LruCache;

import com.chailease.tw.app.android.constants.SystemConstants;
import com.chailease.tw.app.android.utils.Cachable;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class WatermarkProvider implements Cachable {

    public static final int WARTERMARK_BASE_WIDTH   =   512;
    public static final int WARTERMARK_BASE_HEIGHT  =   512;
    public static final String WARTERMARK_SRC_ASSETS    = "watermark_512_512.png";

    // Use 1/8th of the available memory for this memory cache.
    private static final int cacheSize = SystemConstants.MAX_MEMORY / 8;
    private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static BitmapDrawable getWatermarkBitmapDrawable(Context context) throws IOException {

        Bitmap source = mMemoryCache.get(WARTERMARK_SRC_ASSETS);
        if (null == source) {
            AssetManager mngr = context.getAssets();
            // Create an input stream to read from the asset folder
            InputStream is=null;
            //Get the texture from the Android resource directory
            try {
                is = mngr.open(WARTERMARK_SRC_ASSETS);
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                mMemoryCache.put(WARTERMARK_SRC_ASSETS, bitmap);
                source = bitmap;
            } finally {
                if(null != is) is.close();
                is = null;
            }
        }
        //BitmapFactory is an Android graphics utility for images
        return convertToWatermarkDrawable(context, source);
    }
    private static BitmapDrawable convertToWatermarkDrawable(Context context, Bitmap source) {
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), source);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        //drawable.setAlpha(60);
        return drawable;
    }

    public static Drawable getWatermarkDrawable(Context context, String key) {
        Bitmap source = mMemoryCache.get(key);
        if (null != source) {
            return convertToWatermarkDrawable(context, source);
        }
        return null;
    }
    public static Drawable getWatermarkDrawable(Context context, int watermarkColorRid, String key) {
        Bitmap watermark = mMemoryCache.get(key);
        if (null == watermark) {
            renewWatermark(context, watermarkColorRid, key);
            watermark = mMemoryCache.get(key);
        }
        return convertToWatermarkDrawable(context, watermark);
    }

//    public static void renewWatermark(Context context, String content) {
//
//        TextView source = new TextView(context);
//        source.setText(content);
//        source.setDrawingCacheEnabled(true);
//        source.destroyDrawingCache();
//        source.buildDrawingCache();
//        int width =  512;
//        int height = 512;
//        Bitmap copy = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
//        int[] pixels = new int[width * height];
//        copy.setPixels(pixels, 0, width, 0, 0, width, height);
//        //copy.rotate
//        AssetManager mngr = context.getAssets();
//        //mngr.
//    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void renewWatermark(Context context, int watermarkColorRes, String text) {

        // DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        // int width = metrics.widthPixels;
        // int height = metrics.heightPixels;
        int width = WARTERMARK_BASE_WIDTH;
        int height = WARTERMARK_BASE_HEIGHT;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, conf);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(watermarkColorRes));
        paint.setTextSize((int) (200 * 1));

        // draw text to the Canvas center
        Rect bounds = new Rect();
        //bounds.set(1, 0, 1, 0);
        //bounds.offset(1, 1);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(90);  // default is 200.0
        //Toast.makeText(context, "Watermark Text.Size=" + paint.getTextSize(), Toast.LENGTH_LONG).show();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = bitmap.getWidth() / 2; //  (bitmap.getWidth() - bounds.width())/2;
        int y = bitmap.getHeight() / 2; // (bitmap.getHeight() + bounds.height())/2;

        canvas.drawColor(Color.WHITE);
        canvas.rotate(20);
        canvas.drawText(text, x * 1, y * 1, paint);
        mMemoryCache.put(text, bitmap);
    }

    @Override
    public void refresh() {
        if (null != mMemoryCache) {
            mMemoryCache.evictAll();
        }
    }

    @Override
    public synchronized void refresh(String key) {
        if (null != mMemoryCache && mMemoryCache.get(key)!=null) {
            mMemoryCache.remove(key);
        }
    }
}