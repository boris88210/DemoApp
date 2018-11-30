package com.chailease.tw.app.android.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

/**
 *
 */
public class ImageUtils {

    public static Bitmap getBitmap(Resources res, @DrawableRes int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        return BitmapFactory.decodeResource(res, resId, opt);
    }
}