package com.djangoogle.arcsoft2x.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Bitmap处理工具
 * Created by Djangoogle on 2019/05/24 08:25 with Android Studio.
 */
public class BitmapUtil {

	/**
	 * 从URI获取bitmap
	 *
	 * @param uri     Uri
	 * @param context 上下文
	 * @return bitmap
	 */
	public static Bitmap getBitmapFromUri(Uri uri, Context context) {
		if (null == uri || null == context) {
			return null;
		}
		try {
			return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
		} catch (IOException e) {
			Log.e("getBitmapFromUri", e.getMessage(), e.getCause());
			return null;
		}
	}

	/**
	 * 水平翻转bitmap
	 *
	 * @param bitmap bitmap
	 * @return 水平翻转后的bitmap
	 */
	public static Bitmap reverseBitmap(Bitmap bitmap) {
		if (null == bitmap) {
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.setScale(-1, 1);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		//生成的翻转后的bitmap
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
	}

	/**
	 * 缩放bitmap
	 *
	 * @param bitmap    bitmap
	 * @param newWidth  新宽度
	 * @param newHeight 新高度
	 * @return 缩放后的bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		if (null == bitmap) {
			return null;
		}
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
	}
}
