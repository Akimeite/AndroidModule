package com.djangoogle.arcsoft2x.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Bitmap处理工具
 * Created by Djangoogle on 2019/05/24 08:25 with Android Studio.
 */
public class BitmapUtil {

	private static final int VALUE_FOR_4_ALIGN = 0b11;
	private static final int VALUE_FOR_2_ALIGN = 0b01;

	/**
	 * Bitmap转化为ARGB数据，再转化为NV21数据
	 *
	 * @param src    传入的Bitmap，格式为{@link Bitmap.Config#ARGB_8888}
	 * @param width  NV21图像的宽度
	 * @param height NV21图像的高度
	 * @return nv21数据
	 */
	public static byte[] bitmapToNv21(Bitmap src, int width, int height) {
		if (src != null && src.getWidth() >= width && src.getHeight() >= height) {
			int[] argb = new int[width * height];
			src.getPixels(argb, 0, width, 0, 0, width, height);
			return argbToNv21(argb, width, height);
		} else {
			return null;
		}
	}

	/**
	 * ARGB数据转化为NV21数据
	 *
	 * @param argb   argb数据
	 * @param width  宽度
	 * @param height 高度
	 * @return nv21数据
	 */
	private static byte[] argbToNv21(int[] argb, int width, int height) {
		int frameSize = width * height;
		int yIndex = 0;
		int uvIndex = frameSize;
		int index = 0;
		byte[] nv21 = new byte[width * height * 3 / 2];
		for (int j = 0; j < height; ++j) {
			for (int i = 0; i < width; ++i) {
				int R = (argb[index] & 0xFF0000) >> 16;
				int G = (argb[index] & 0x00FF00) >> 8;
				int B = argb[index] & 0x0000FF;
				int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
				int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
				int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
				nv21[yIndex++] = (byte) (Y < 0 ? 0 : (Y > 255 ? 255 : Y));
				if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.length - 2) {
					nv21[uvIndex++] = (byte) (V < 0 ? 0 : (V > 255 ? 255 : V));
					nv21[uvIndex++] = (byte) (U < 0 ? 0 : (U > 255 ? 255 : U));
				}

				++index;
			}
		}
		return nv21;
	}

	/**
	 * bitmap转化为bgr数据，格式为{@link Bitmap.Config#ARGB_8888}
	 *
	 * @param image 传入的bitmap
	 * @return bgr数据
	 */
	public static byte[] bitmapToBgr(Bitmap image) {
		if (image == null) {
			return null;
		}
		int bytes = image.getByteCount();

		ByteBuffer buffer = ByteBuffer.allocate(bytes);
		image.copyPixelsToBuffer(buffer);
		byte[] temp = buffer.array();
		byte[] pixels = new byte[(temp.length / 4) * 3];
		for (int i = 0; i < temp.length / 4; i++) {
			pixels[i * 3] = temp[i * 4 + 2];
			pixels[i * 3 + 1] = temp[i * 4 + 1];
			pixels[i * 3 + 2] = temp[i * 4];
		}
		return pixels;
	}

	public static Bitmap getBitmapFromUri(Uri uri, Context context) {
		if (uri == null || context == null) {
			return null;
		}
		try {
			return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 裁剪bitmap
	 *
	 * @param bitmap Bitmap
	 * @param rect   需要被裁剪的区域
	 * @return 裁剪后的bitmap
	 */
	public static Bitmap imageClip(Bitmap bitmap, Rect rect) {
		if (null == bitmap || null == rect || rect.isEmpty() || bitmap.getWidth() < rect.right || bitmap.getHeight() < rect.bottom || rect.top < 0 || rect.bottom < 0 || rect.left < 0 || rect.right < 0) {
			return null;
		}
		return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height(), null, false);
	}

	/**
	 * 旋转bitmap
	 *
	 * @param bitmap       Bitmap
	 * @param rotateDegree 角度
	 * @return 旋转后的bitmap
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, float rotateDegree) {
		if (null == bitmap) {
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateDegree);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
	}

	/**
	 * 水平翻转bitmap
	 *
	 * @param bitmap Bitmap
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
	 * @param bitmap    Bitmap
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

	/**
	 * 确保传给引擎的BGR24数据宽度为4的倍数
	 *
	 * @param bitmap 传入的bitmap
	 * @return 调整后的bitmap
	 */
	public static Bitmap alignBitmapForBgr24(Bitmap bitmap) {
		if (null == bitmap || bitmap.getWidth() < 4) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		boolean needAdjust = false;

		//保证宽度是4的倍数
		if ((width & VALUE_FOR_4_ALIGN) != 0) {
			width &= ~VALUE_FOR_4_ALIGN;
			needAdjust = true;
		}

		if (needAdjust) {
			bitmap = imageClip(bitmap, new Rect(0, 0, width, height));
		}
		return bitmap;
	}

	/**
	 * 确保传给引擎的NV21数据宽度为4的倍数，高为2的倍数
	 *
	 * @param bitmap 传入的bitmap
	 * @return 调整后的bitmap
	 */
	public static Bitmap alignBitmapForNv21(Bitmap bitmap) {
		if (null == bitmap || bitmap.getWidth() < 4 || bitmap.getHeight() < 2) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		boolean needAdjust = false;
		//保证宽度是4的倍数
		if ((width & VALUE_FOR_4_ALIGN) != 0) {
			width &= ~VALUE_FOR_4_ALIGN;
			needAdjust = true;
		}

		//保证高度是2的倍数
		if ((height & VALUE_FOR_2_ALIGN) != 0) {
			height--;
			needAdjust = true;
		}

		if (needAdjust) {
			bitmap = imageClip(bitmap, new Rect(0, 0, width, height));
		}
		return bitmap;
	}
}
