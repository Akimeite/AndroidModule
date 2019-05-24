package com.djangoogle.arcsoft2x.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.hardware.Camera

/**
 * Created by Djangoogle on 2019/05/09 19:06 with Android Studio.
 */
object FaceUtil {

	/**
	 * @param ftRect                   FT人脸框
	 * @param previewWidth             相机预览的宽度
	 * @param previewHeight            相机预览高度
	 * @param canvasWidth              画布的宽度
	 * @param canvasHeight             画布的高度
	 * @param cameraDisplayOrientation 相机预览方向
	 * @param cameraId                 相机ID
	 * @param isMirror                 是否水平镜像显示（若相机是镜像显示的，设为true，用于纠正）
	 * @param mirrorHorizontal         为兼容部分设备使用，水平再次镜像
	 * @param mirrorVertical           为兼容部分设备使用，垂直再次镜像
	 * @return 调整后的需要被绘制到View上的rect
	 */
	fun adjustRect(
		ftRect: Rect?,
		previewWidth: Int,
		previewHeight: Int,
		canvasWidth: Int,
		canvasHeight: Int,
		cameraDisplayOrientation: Int,
		cameraId: Int,
		isMirror: Boolean,
		mirrorHorizontal: Boolean,
		mirrorVertical: Boolean
	): Rect? {
		if (ftRect == null) {
			return null
		}
		val rect = Rect(ftRect)

		val horizontalRatio: Float
		val verticalRatio: Float
		if (cameraDisplayOrientation % 180 == 0) {
			horizontalRatio = canvasWidth.toFloat() / previewWidth.toFloat()
			verticalRatio = canvasHeight.toFloat() / previewHeight.toFloat()
		} else {
			horizontalRatio = canvasHeight.toFloat() / previewWidth.toFloat()
			verticalRatio = canvasWidth.toFloat() / previewHeight.toFloat()
		}
		rect.left *= horizontalRatio.toInt()
		rect.right *= horizontalRatio.toInt()
		rect.top *= verticalRatio.toInt()
		rect.bottom *= verticalRatio.toInt()
		val newRect = Rect()
		when (cameraDisplayOrientation) {
			0 -> {
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.left = canvasWidth - rect.right
					newRect.right = canvasWidth - rect.left
				} else {
					newRect.left = rect.left
					newRect.right = rect.right
				}
				newRect.top = rect.top
				newRect.bottom = rect.bottom
			}
			90 -> {
				newRect.right = canvasWidth - rect.top
				newRect.left = canvasWidth - rect.bottom
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.top = canvasHeight - rect.right
					newRect.bottom = canvasHeight - rect.left
				} else {
					newRect.top = rect.left
					newRect.bottom = rect.right
				}
			}
			180 -> {
				newRect.top = canvasHeight - rect.bottom
				newRect.bottom = canvasHeight - rect.top
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.left = rect.left
					newRect.right = rect.right
				} else {
					newRect.left = canvasWidth - rect.right
					newRect.right = canvasWidth - rect.left
				}
			}
			270 -> {
				newRect.left = rect.top
				newRect.right = rect.bottom
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.top = rect.left
					newRect.bottom = rect.right
				} else {
					newRect.top = canvasHeight - rect.right
					newRect.bottom = canvasHeight - rect.left
				}
			}
			else -> {
			}
		}

		/**
		 * isMirror mirrorHorizontal finalIsMirrorHorizontal
		 * true         true                false
		 * false        false               false
		 * true         false               true
		 * false        true                true
		 *
		 * XOR
		 */
		if (isMirror xor mirrorHorizontal) {
			val left = newRect.left
			val right = newRect.right
			newRect.left = canvasWidth - right
			newRect.right = canvasWidth - left
		}
		if (mirrorVertical) {
			val top = newRect.top
			val bottom = newRect.bottom
			newRect.top = canvasHeight - bottom
			newRect.bottom = canvasHeight - top
		}
		return newRect
	}

	/**
	 * 获取人脸Bitmap
	 *
	 * @param bitmap 人脸图片
	 * @param rect   人脸矩形框
	 * @return 人脸Bitmap
	 */
	fun getFaceBitmap(bitmap: Bitmap, rect: Rect): Bitmap? {
		//抠人脸图
		val faceBitmap = BitmapUtil.imageClip(bitmap, rect)
		if (!bitmap.isRecycled) {
			bitmap.recycle()
		}
		if (null == faceBitmap) {
			return null
		}
		return faceBitmap
	}
}
