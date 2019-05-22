package com.djangoogle.arcsoft2x.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import java.io.IOException
import java.nio.ByteBuffer

class ImageUtil {

	companion object {

		/**
		 * Bitmap转化为ARGB数据，再转化为NV21数据
		 *
		 * @param src    传入的Bitmap，格式为[Bitmap.Config.ARGB_8888]
		 * @param width  NV21图像的宽度
		 * @param height NV21图像的高度
		 * @return nv21数据
		 */
		fun bitmapToNv21(src: Bitmap?, width: Int, height: Int): ByteArray? {
			if (src != null && src.width >= width && src.height >= height) {
				val argb = IntArray(width * height)
				src.getPixels(argb, 0, width, 0, 0, width, height)
				return argbToNv21(argb, width, height)
			} else {
				return null
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
		private fun argbToNv21(argb: IntArray, width: Int, height: Int): ByteArray {
			val frameSize = width * height
			var yIndex = 0
			var uvIndex = frameSize
			var index = 0
			val nv21 = ByteArray(width * height * 3 / 2)
			for (j in 0 until height) {
				for (i in 0 until width) {
					val R = argb[index] and 0xFF0000 shr 16
					val G = argb[index] and 0x00FF00 shr 8
					val B = argb[index] and 0x0000FF
					val Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
					val U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
					val V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128
					nv21[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
					if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.size - 2) {
						nv21[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
						nv21[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
					}

					++index
				}
			}
			return nv21
		}

		/**
		 * bitmap转化为bgr数据，格式为[Bitmap.Config.ARGB_8888]
		 *
		 * @param image 传入的bitmap
		 * @return bgr数据
		 */
		fun bitmapToBgr(image: Bitmap?): ByteArray? {
			if (image == null) {
				return null
			}
			val bytes = image.byteCount

			val buffer = ByteBuffer.allocate(bytes)
			image.copyPixelsToBuffer(buffer)
			val temp = buffer.array()
			val pixels = ByteArray(temp.size / 4 * 3)
			for (i in 0 until temp.size / 4) {
				pixels[i * 3] = temp[i * 4 + 2]
				pixels[i * 3 + 1] = temp[i * 4 + 1]
				pixels[i * 3 + 2] = temp[i * 4]
			}
			return pixels
		}

		/**
		 * 裁剪bitmap
		 *
		 * @param bitmap 传入的bitmap
		 * @param rect   需要被裁剪的区域
		 * @return 被裁剪后的bitmap
		 */
		fun imageClip(bitmap: Bitmap?, rect: Rect?): Bitmap? {
			return if (bitmap == null || rect == null || rect.isEmpty || bitmap.width < rect.right || bitmap.height < rect.bottom || rect.top < 0 || rect.bottom < 0 || rect.left < 0 || rect.right < 0) {
				null
			} else Bitmap.createBitmap(
				bitmap,
				rect.left,
				rect.top,
				rect.width(),
				rect.height(),
				null,
				false
			)
		}

		fun getBitmapFromUri(uri: Uri?, context: Context?): Bitmap? {
			if (uri == null || context == null) {
				return null
			}
			try {
				return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
			} catch (e: IOException) {
				e.printStackTrace()
				return null
			}

		}

		fun getRotateBitmap(b: Bitmap?, rotateDegree: Float): Bitmap? {
			if (b == null) {
				return null
			}
			val matrix = Matrix()
			matrix.postRotate(rotateDegree)
			return Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, false)
		}

		fun getScaleBitmap(bitmap: Bitmap?, rotateDegree: Float, newWidth: Int, newHeight: Int): Bitmap? {
			if (null == bitmap) {
				return null
			}
			val matrix = Matrix()
			val width = bitmap.width
			val height = bitmap.height
			val scaleWidth = newWidth.toFloat() / width
			val scaleHeight = newHeight.toFloat() / height
			matrix.postScale(scaleWidth, scaleHeight)//缩放
			matrix.postRotate(rotateDegree)//旋转
			matrix.setScale(-1f, 1f)//翻转
			//生成的翻转后的bitmap
			return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
		}

		/**
		 * 确保传给引擎的BGR24数据宽度为4的倍数
		 *
		 * @param bitmap 传入的bitmap
		 * @return 调整后的bitmap
		 */
		fun alignBitmapForBgr24(bitmap: Bitmap?): Bitmap? {
			var bitmap = bitmap
			if (bitmap == null || bitmap.width < 4) {
				return null
			}
			var width = bitmap.width
			val height = bitmap.height

			var needAdjust = false
			while (width % 4 != 0) {
				width--
				needAdjust = true
			}

			if (needAdjust) {
				bitmap = imageClip(bitmap, Rect(0, 0, width, height))
			}
			return bitmap
		}

		/**
		 * 确保传给引擎的NV21数据宽度为4的倍数，高为2的倍数
		 *
		 * @param bitmap 传入的bitmap
		 * @return 调整后的bitmap
		 */
		fun alignBitmapForNv21(bitmap: Bitmap?): Bitmap? {
			var bitmap = bitmap
			if (bitmap == null || bitmap.width < 4 || bitmap.height < 2) {
				return null
			}
			var width = bitmap.width
			var height = bitmap.height

			var needAdjust = false
			while (width % 4 != 0) {
				width--
				needAdjust = true
			}
			if (height % 2 != 0) {
				height--
				needAdjust = true
			}

			if (needAdjust) {
				bitmap = imageClip(bitmap, Rect(0, 0, width, height))
			}
			return bitmap
		}

		/**
		 * 水平翻转
		 *
		 * @param bitmap
		 * @return
		 */
		fun reverse(bitmap: Bitmap): Bitmap {
			val matrix = Matrix()
			matrix.setScale(-1f, 1f)
			val width = bitmap.width
			val height = bitmap.height
			//生成的翻转后的bitmap
			return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
		}

		/**
		 * 根据给定的宽和高进行拉伸
		 *
		 * @param origin    原图
		 * @param newWidth  新图的宽
		 * @param newHeight 新图的高
		 * @return new Bitmap
		 */
		fun scaleBitmap(origin: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
			if (origin == null) {
				return null
			}
			val height = origin.height
			val width = origin.width
			val scaleWidth = newWidth.toFloat() / width
			val scaleHeight = newHeight.toFloat() / height
			val matrix = Matrix()
			matrix.postScale(scaleWidth, scaleHeight)// 使用后乘
			return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
		}
	}
}
