package com.chailease.tw.app.android.imageutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import static com.chailease.tw.app.android.commonlib.BuildConfig.LIB_ID;
import static com.chailease.tw.app.android.commonlib.BuildConfig.isLibTrace;

/**
 * Created by Gil on 07/06/2014.
 */
public class ImageInternalFetcher extends ImageResizer {

	Context mContext;

	public ImageInternalFetcher(Context context, int imageWidth, int imageHeight) {
		super(context, imageWidth, imageHeight);
		init(context);
	}

	public ImageInternalFetcher(Context context, int imageSize) {
		super(context, imageSize);
		init(context);
	}

	private void init(Context context){
		mContext = context;
	}

	protected Bitmap processBitmap(Uri uri){
		Bitmap newBitmap = decodeSampledBitmapFromFile(mSampleMode, uri.getPath(), mImageWidth, mImageHeight, getImageCache());
		if (isLibTrace)
			Log.d(LIB_ID, "ImageInternalFetcher:processBitmap  ==> the new-bitmap width:" + newBitmap.getWidth() + ", height:" + newBitmap.getHeight());
		return newBitmap;
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		return processBitmap((Uri)data);
	}
}
