package com.djangoogle.arcsoft2x.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtil {
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

	/**
	 * 裁剪bitmap
	 *
	 * @param bitmap 传入的bitmap
	 * @param rect   需要被裁剪的区域
	 * @return 被裁剪后的bitmap
	 */
	public static Bitmap imageClip(Bitmap bitmap, Rect rect) {
		if (bitmap == null || rect == null || rect.isEmpty() || bitmap.getWidth() < rect.right || bitmap.getHeight() < rect.bottom || rect.top < 0 || rect.bottom < 0 || rect.left < 0 || rect.right < 0) {
			return null;
		}
		return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height(), null, false);
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

	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
		if (b == null) {
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateDegree);
		return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
	}

	public static Bitmap getScaleBitmap(Bitmap bitmap, float rotateDegree, int newWidth, int newHeight) {
		if (null == bitmap) {
			return null;
		}
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		matrix.postScale(scaleWidth, scaleHeight);//缩放
		matrix.postRotate(rotateDegree);//旋转
		matrix.setScale(-1, 1);//翻转
		//生成的翻转后的bitmap
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
	}

	/**
	 * 确保传给引擎的BGR24数据宽度为4的倍数
	 *
	 * @param bitmap 传入的bitmap
	 * @return 调整后的bitmap
	 */
	public static Bitmap alignBitmapForBgr24(Bitmap bitmap) {
		if (bitmap == null || bitmap.getWidth() < 4) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		boolean needAdjust = false;
		while (width % 4 != 0) {
			width--;
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
		if (bitmap == null || bitmap.getWidth() < 4 || bitmap.getHeight() < 2) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		boolean needAdjust = false;
		while (width % 4 != 0) {
			width--;
			needAdjust = true;
		}
		if (height % 2 != 0) {
			height--;
			needAdjust = true;
		}

		if (needAdjust) {
			bitmap = imageClip(bitmap, new Rect(0, 0, width, height));
		}
		return bitmap;
	}

	/**
	 * 水平翻转
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap reverse(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.setScale(-1, 1);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		//生成的翻转后的bitmap
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
	}

	/**
	 * 根据给定的宽和高进行拉伸
	 *
	 * @param origin    原图
	 * @param newWidth  新图的宽
	 * @param newHeight 新图的高
	 * @return new Bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
		if (origin == null) {
			return null;
		}
		int height = origin.getHeight();
		int width = origin.getWidth();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
		return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
	}
}
