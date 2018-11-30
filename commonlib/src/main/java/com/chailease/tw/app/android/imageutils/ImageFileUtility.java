package com.chailease.tw.app.android.imageutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.support.annotation.ColorInt;
import android.util.Log;

import com.chailease.tw.app.android.utils.LogUtility;

import static com.chailease.tw.app.android.commonlib.BuildConfig.LIB_ID;
import static com.chailease.tw.app.android.commonlib.BuildConfig.isLibTrace;

public class ImageFileUtility {

	public enum IMAGE_SCALE {
		OTHER,
		SCALE_16_9, SCALE_4_3,
		SCALE_2_1, SCALE_1_1
	}

	public static IMAGE_SCALE checkImageScale(File file) {
		return checkImageScale(file.getAbsolutePath());
	}
	public static IMAGE_SCALE checkImageScale(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		int width = options.outWidth;
		int height = options.outHeight;
		if (isLibTrace)
			LogUtility.debug(ImageFileUtility.class, "checkImageScale", " check file " + filePath + " >> width=" + width + ", height=" + height);
		if (height>width) {
			int tmp = width;
			width = height;
			height = tmp;
		}
		if (isLibTrace)
			LogUtility.debug(ImageFileUtility.class, "checkImageScale", " check file  >> width=" + width + ", height=" + height);
		if (width*3 == height*4) return IMAGE_SCALE.SCALE_4_3;
		else if (width*9 == height*16) return IMAGE_SCALE.SCALE_16_9;
		else if (width == height*2) return IMAGE_SCALE.SCALE_2_1;
		else if (width == height) return IMAGE_SCALE.SCALE_1_1;
		return IMAGE_SCALE.OTHER;
	}


	/**
	 * scaled: 
	 * @param source
	 * @param scaledWidth
	 * @param scaledHeight
	 * @return
	 */
	public static Bitmap resize(Bitmap source, float scaledWidth, float scaledHeight) {
		int width = source.getWidth();
		int height = source.getHeight();
		// 設定 transform matrix
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.postScale(scaledWidth, scaledHeight);
		// 產生縮圖後的 bitmap
		return Bitmap.createBitmap(source, 0, 0, width, height, scaleMatrix, true);
	}

	private static BitmapFactory.Options getBitmapOptions(){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		//options.inSampleSize = scale;
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options,true);
		} catch (Exception e) {
			Log.w(LIB_ID, "getBitmapOptions --> change inNativeAlloc:"+e.getMessage());
		}
		return options;
	}

	public static Bitmap decodeImageUri(final Context context, Intent data) throws FileNotFoundException {
		InputStream ist = context.getContentResolver()
							.openInputStream(data.getData());
		Bitmap bitmap = BitmapFactory.decodeStream(ist);
		return bitmap;
	}

	/**
	 * 將圖檔路徑轉成 Bitmap
	 * @param filePath
	 * @return
	 */
	public static Bitmap decodeImageFile(String filePath) {
		Bitmap bitmap = null;
		if(filePath != null) {
			BitmapFactory.Options options = getBitmapOptions(); 
			bitmap = BitmapFactory.decodeFile(filePath, options);				
		}
		return bitmap;
	}
	/**
	 * <b>scale:</b> If set to a value > 1, requests the decoder to subsample the original image, returning a smaller image to save memory. The sample size is the number of pixels in either dimension that correspond to a single pixel in the decoded bitmap. For example, inSampleSize == 4 returns an image that is 1/4 the width/height of the original, and 1/16 the number of pixels. Any value <= 1 is treated the same as 1. Note: the decoder uses a final value based on powers of 2, any other value will be rounded down to the nearest power of 2.
	 * @param filePath
	 * @param scale
	 * @return
	 */
	public static Bitmap decodeImageFile(String filePath, short scale) {
		Bitmap bitmap = null;
		if(filePath != null) {
			BitmapFactory.Options options = getBitmapOptions(); 
			options.inJustDecodeBounds = false;
			//options.inSampleSize = 10;   //width，hight設為原來的十分一
			options.inSampleSize = scale;
			bitmap = BitmapFactory.decodeFile(filePath, options);				
		}
		return bitmap;
	}

	/**
	 * <b>maxSize:</b> 依比例高或寬縮小到哪個尺寸(pixel)
	 * @param imagePath
	 * @param maxSize
	 * @return
	 */
	public static Bitmap decodeImageFile (String imagePath, int maxSize) {
		return decodeImageFile(imagePath, maxSize, maxSize);
	}
	/**
	 * 依比例高或寬縮小到接近指定個尺寸(pixel)
	 * @param imagePath
	 * @param maxSizeH
	 * @param maxSizeW
	 * @return
	 */
	public static Bitmap decodeImageFile (String imagePath, int maxSizeH, int maxSizeW) {
		//Only decode image size. Not whole image
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, option);
		boolean wh = maxSizeW >= maxSizeH;
		//The new size to decode to 
		int NEW_SIZE_H=maxSizeH;
		int NEW_SIZE_W=maxSizeW;

		int oWidth = option.outWidth;
		int oHeight = option.outHeight;
		int width=oWidth;
		int height=oHeight;
		if (wh && width<height) {
			NEW_SIZE_H=maxSizeW;
			NEW_SIZE_W=maxSizeH;
		} else if (!wh && width>height) {
			NEW_SIZE_H=maxSizeW;
			NEW_SIZE_W=maxSizeH;
		}
		if (isLibTrace)
			LogUtility.debug(ImageFileUtility.class, "decodeImageFile", " --> max.width=" + NEW_SIZE_W + ", max.height=" + NEW_SIZE_H + ";  width:" + width + ", height:" + height + "; file:" + imagePath);
		int scale=1;
		int chgBase = 2;
		while(true){
			if((width)<=NEW_SIZE_W && (height)<=NEW_SIZE_H)
				break;
			width = oWidth/chgBase;
			height = oHeight/chgBase;
			scale++;
			chgBase++;
		}
		if (isLibTrace)
			LogUtility.debug(ImageFileUtility.class, "decodeImageFile", " --> max.width=" + NEW_SIZE_W + ", max.height=" + NEW_SIZE_H + ";  width:" + width + "; height:" + height + "; scale:" + scale);
		//Decode again with inSampleSize
		option = getBitmapOptions();
		option.inSampleSize=scale;
		Bitmap decode = BitmapFactory.decodeFile(imagePath, option);
		if (isLibTrace)
			LogUtility.debug(ImageFileUtility.class, "decodeImageFile", " --> width:" + decode.getWidth() + "; height:" + decode.getHeight() + "; scale:" + scale);
		width = decode.getWidth();
		height = decode.getHeight();
		if ((width)<=NEW_SIZE_W && (height)<=NEW_SIZE_H) {
			return decode;
		}

		scale = Math.min((oWidth/NEW_SIZE_W),(oHeight/NEW_SIZE_H));
		option = getBitmapOptions();
		option.inSampleSize= (scale<=1) ? 2 : scale;
		decode = BitmapFactory.decodeFile(imagePath, option);

		if (isLibTrace)
			Log.d(LIB_ID, "decodeImageFile --> width:" + decode.getWidth() + "; height:" + decode.getHeight() + "; scale:" + scale);
		return decode;
	}
	/**
	 * 原圖灰階
	 * @param src
	 * @return
	 */
	public static Bitmap grayImage(Bitmap src) {
		return grayImage(src, 24);
	}
	/**
	 * 原圖灰階
	 * @param src
	 * @return
	 */
	public static Bitmap grayImage(Bitmap src, int iAlpha) {
		int width = src.getWidth();
		int height = src.getHeight();

		int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

		src.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << iAlpha; 
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey& 0x00FF0000 ) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}

	/**
	 * 將原始圖檔轉成JPEG
	 * @param source
	 * @param targetPath
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File saveAsJPEG(Bitmap source, File targetPath, String fileName) throws IOException {
		return saveAsJPEG(source, targetPath, fileName, 100);
	}
	/**
	 * 將原始圖檔轉成JPEG
	 * @param source
	 * @param targetPath
	 * @param fileName
	 * @param quality
	 * @return
	 * @throws IOException
	 */
	public static File saveAsJPEG(Bitmap source, File targetPath, String fileName, int quality) throws IOException {
		// 壓成 JPEG 存檔
		String jpg = (fileName.indexOf(".")!=-1 ? fileName.substring(0, fileName.indexOf(".")) : fileName) + ".jpg";
		File target = new File(targetPath, jpg);
		if (!target.exists() || target.isDirectory()) {
			target.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(target);
		try {
			source.compress(CompressFormat.JPEG, quality, fos);
		} finally {
			fos.close();
		}
		return target;
	}
	/**
	 * 將原始圖檔轉成JPEG imagepicker
	 * @param source
	 * @param targetPath
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File saveAsJPEG(Bitmap source, String targetPath, String fileName) throws IOException {
		return saveAsJPEG(source, targetPath, fileName, 100);
	}
	public static File saveAsJPEG(Bitmap source, String targetPath, String fileName, int quality) throws IOException {
		// 壓成 JPEG 存檔
		String jpg = (fileName.indexOf(".")!=-1 ? fileName.substring(0, fileName.indexOf(".")) : fileName) + ".jpg";
		File target = new File(targetPath, jpg);
		FileOutputStream fos = new FileOutputStream(target);

		source.compress(CompressFormat.JPEG, quality, fos);
		fos.close();
		return target;
	}

	public static int imageRotation(File source) {
		
		if (null == source) return 0;
		try {
			ExifInterface exif = new ExifInterface(source.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			
			if (orientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }
			if (orientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; }
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
			
			return 0;
		} catch (IOException e) {
			Log.w(LIB_ID, "imageRotation --> source:" + source.getAbsolutePath(), e);
		}
		
		return 0;
	}
	
	public static int imageRotation(String sourcePath) {

		if (null == sourcePath) return 0;
		try {
			ExifInterface exif = new ExifInterface(sourcePath);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			if (orientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }
			if (orientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; }
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
			return 0;
		} catch (IOException e) {
			Log.w(LIB_ID, "imageRotation --> source:" + sourcePath, e);
		}
		return 0;
	}

	public static Bitmap imageCanvas(File source, Bitmap bitmap) {
		int orientation = imageRotation(source);
		int rotate = 0;
		switch(orientation) {
		case 270:	//	8
			rotate-=90;
		case 180:	//	3
			rotate=orientation;
		case 90:	//	6
			rotate=orientation;
		}
		Bitmap cameraBmp = null;
		bitmap = bitmap==null ? decodeImageFile(source.getAbsolutePath()) : bitmap;
		if (rotate != 0) {
//			Canvas canvas = new Canvas(bitmap);
//			canvas.rotate(rotate);
			cameraBmp = ThumbnailUtils.extractThumbnail(bitmap
					, bitmap.getWidth(), bitmap.getHeight());
			// NOTE incredibly useful trick for cropping/resizing square
			// http://stackoverflow.com/a/17733530/294884
			Matrix m = new Matrix();
			
			m.postRotate(rotate);
			cameraBmp = Bitmap.createBitmap(cameraBmp,
					0, 0, cameraBmp.getWidth(), cameraBmp.getHeight(),
					m, true);

			try {
				if (!bitmap.isRecycled()) bitmap.recycle();
			} catch (Exception e) {
				Log.w(LIB_ID, "imageCanvas --> source:" + source.getAbsolutePath(), e);
			}
		} else {
			cameraBmp = bitmap;
		}
		return cameraBmp;
	}
	public static File imageCanvas(File source, boolean saveAsNew) {
		int orientation = imageRotation(source);
		int rotate = 0;
		File canvasFile = source;
		switch(orientation) {
		case 270:	//	8
			rotate-=90;
		case 180:	//	3
			rotate=orientation;
		case 90:	//	6
			rotate=orientation;
		}
		
		if (rotate != 0) {
			Bitmap bitmap = decodeImageFile(source.getAbsolutePath());
//			Canvas canvas = new Canvas(bitmap);
//			canvas.rotate(rotate);
			String targetName = source.getName();
			if (saveAsNew) {
				targetName = targetName.substring(0, targetName.lastIndexOf(".")) + "_" + rotate + ".jpg";
			}
			//	the other way
			Bitmap cameraBmp = ThumbnailUtils.extractThumbnail(bitmap
					, bitmap.getWidth(), bitmap.getHeight());
			// NOTE incredibly useful trick for cropping/resizing square
			// http://stackoverflow.com/a/17733530/294884
			Matrix m = new Matrix();
			
			m.postRotate(rotate);
			cameraBmp = Bitmap.createBitmap(cameraBmp,
					0, 0, cameraBmp.getWidth(), cameraBmp.getHeight(),
					m, true);

			try {
				canvasFile = saveAsJPEG(cameraBmp, source.getParentFile(), targetName);
				if (!bitmap.isRecycled()) bitmap.recycle();
				if (!cameraBmp.isRecycled()) cameraBmp.recycle();
			} catch (IOException e) {
				Log.w(LIB_ID, "imageCanvas --> source:" + source.getAbsolutePath(), e);
			}
		}
		return canvasFile;
	}

	enum WATERMARK_LOC_MODE {
		CORNER_BR, CORNER_BL, CORNER_TR, CORNER_TL,
		CENTER
	}
	static WATERMARK_LOC_MODE watermarkLoc = WATERMARK_LOC_MODE.CENTER;
	static int wm_text_offset_w = 0;
	static int wm_text_offset_h = 0;
	public static void changeWatermarkLocMode(WATERMARK_LOC_MODE loc) {
		watermarkLoc = loc;
	}
	public static void changeTextWatermarkOffset(int w, int h) {
		if (w > 0) wm_text_offset_w = 0-w;
		else wm_text_offset_w = w;
		if (h > 0) wm_text_offset_h = 0-h;
		else wm_text_offset_h = h;
	}

	final static Object colorLocker = new Object();
	@ColorInt static int watermarkColor = Color.RED;
	static float custWatermarkTextSize = 0;
	static float watermarkAlphaFloat = 1f;
	static int watermarkAlphaInt = 255;
	static boolean watermarkTextUnderLine = false;
	public static void changeWatermarkColor(@ColorInt int color) {
		watermarkColor = color;
	}
	public static void changeWatermarkTextSize(float textPixelSize) {
		if (textPixelSize<=0) return;
		custWatermarkTextSize = textPixelSize;
	}
	public static void changeWatermarkAlpha(float alpha) {
		if (alpha <= 1f && alpha >= 0f) {
			watermarkAlphaFloat = alpha;
			watermarkAlphaInt = convertAlpha(alpha);
		}
	}
	public static void changeWatermarkAlpha(int alpha) {
		if (alpha <= 255 && alpha >= 0)
			watermarkAlphaInt = alpha;
	}
	public static void setWatermarkTextUnderLine(boolean underLine) {
		watermarkTextUnderLine = underLine;
	}
	static int convertAlpha(float alpha) {
		if (alpha <= 0f) return 0;
		else if (alpha >= 1f) return 255;
		int cv = (int) (255 * alpha);
		return cv;
	}
	public static Bitmap makeWatermark(Bitmap src, String watermark, @ColorInt int color) {
		if (color != watermarkColor) {
			synchronized (colorLocker) {
				@ColorInt int orgColor = watermarkColor;
				try {
					watermarkColor = color;
					Bitmap rs = makeWatermark(src, watermark);
					return rs;
				} finally {
					watermarkColor = orgColor;
				}
			}
		}
		return makeWatermark(src, watermark);
	}
	static int checkWaterWidthStart(int srcWidth, int wmWidth, WATERMARK_LOC_MODE wl) {
		if (wmWidth>0 && srcWidth>wmWidth) {    //  watermark by image
			switch (wl) {
				case CENTER:
					return (srcWidth-wmWidth)/2;
				case CORNER_BL:
				case CORNER_TL:
					return 0;
				case CORNER_BR:
				case CORNER_TR:
					return srcWidth/2;
			}
		} else if (wmWidth<=0){     //      watermark by text
			switch(wl) {
				case CENTER:
					return srcWidth/2+wmWidth;    //  the offset of width
				case CORNER_BL:
				case CORNER_TL:
					return 0;
				case CORNER_BR:
				case CORNER_TR:
					return srcWidth/2-wmWidth;    //  the offset of width
			}
		}
		return 0;
	}
	static int checkWaterHeightStart(int srcHeight, int wmHeight, WATERMARK_LOC_MODE wl) {
		if (srcHeight>0 && srcHeight>wmHeight) {    //  watermark by image
			switch (wl) {
				case CENTER:
					return (srcHeight-wmHeight)/2;
				case CORNER_TL:
				case CORNER_TR:
					return 0;
				case CORNER_BL:
				case CORNER_BR:
					return srcHeight/2;
			}
		} else if (wmHeight<=0){    //  watermark by text
			switch(wl) {
				case CENTER:
					return srcHeight/2+wmHeight;    //  the offset of width
				case CORNER_TL:
				case CORNER_TR:
					return 0;
				case CORNER_BL:
				case CORNER_BR:
					return srcHeight/2-wmHeight;    //  the offset of width
			}
		}
		return 0;
	}
	public static Bitmap makeWatermark(Bitmap src, Object watermark) {

		if(src == null) {
			return null;
		}

		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();

		//create the new blank bitmap
		Bitmap newmap = Bitmap.createBitmap(srcWidth,srcHeight, Bitmap.Config.ARGB_8888); //  創建一個新的和src長度寬度一樣的點陣圖

		Canvas cv = new Canvas(newmap);
		cv.drawBitmap(src, 0, 0,null);  //  在0,0座標開始畫入src
		if (watermark instanceof Bitmap) {
			Bitmap wmap = (Bitmap) watermark;
			int waterWidth = checkWaterWidthStart(srcWidth, wmap.getWidth(), watermarkLoc);
			int waterHeight = checkWaterHeightStart(srcHeight, wmap.getHeight(), watermarkLoc);
			cv.drawBitmap(wmap, waterWidth, waterHeight, null);  //  在src的右下解畫入浮水印圖片
		} else if (watermark instanceof String) {
			if (isLibTrace)
				Log.d(LIB_ID, "makeWatermark --> start to mark");
			String text = watermark.toString();
			Paint paint = new Paint();
			paint.setColor(watermarkColor);
			paint.setAlpha(watermarkAlphaInt);
			int textHeight = 0;
			int textWidth = 0;
			if (custWatermarkTextSize >0 ) {
				paint.setTextSize(custWatermarkTextSize);   //  the default size of text maybe in 12
				Rect bounds = new Rect();
				paint.getTextBounds(text, 0, text.length(), bounds);
				textHeight = bounds.height();
				textWidth = bounds.width();
				wm_text_offset_w = textWidth;
				if (wm_text_offset_w > srcWidth) wm_text_offset_w = srcWidth-(textWidth/text.length());
			}
			paint.setAntiAlias(true);
			paint.setUnderlineText(watermarkTextUnderLine);
			int waterWidth = checkWaterWidthStart(srcWidth, wm_text_offset_w, watermarkLoc);
			int waterHeight = checkWaterHeightStart(srcHeight, wm_text_offset_h, watermarkLoc);
			if (isLibTrace)
				Log.d(LIB_ID, "makeWatermark --> mark as: text size=" + paint.getTextSize()
						+ ", custom text size=" + custWatermarkTextSize
						+ ", alpha=" + paint.getAlpha()
						+ ", start width=" + waterWidth + ", start height=" + waterHeight
						+ ", with text=" + text
						+ "; src width=" + srcWidth + ", src height=" + srcHeight
						+ "; text.width=" + textWidth + ", text.height=" + textHeight);
			cv.drawText(text, waterWidth, waterHeight, paint);  //  這是畫入浮水印文字，在畫文字時，需要指定    paint
			if (isLibTrace)
				Log.d(LIB_ID, "makeWatermark --> end of mark");
		}
		int toCount = cv.save(Canvas.ALL_SAVE_FLAG);  //  保存
		cv.restoreToCount(toCount);
		//cv.restore();   //  存儲

		if (isLibTrace)
			Log.d(LIB_ID, "makeWatermark --> finish watermark");

		return newmap;
	}

}
