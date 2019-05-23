package com.djangoogle.arcsoft2x.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Camera

import com.djangoogle.arcsoft2x.widget.FaceRectView

/**
 * 绘制人脸框帮助类，用于在[FaceRectView]上绘制矩形
 * Created by Djangoogle on 2019/04/08 17:42 with Android Studio.
 */
class DrawHelper(
	private var previewWidth: Int,
	private var previewHeight: Int,
	private var canvasWidth: Int,
	private var canvasHeight: Int,
	private var cameraDisplayOrientation: Int,
	private var cameraId: Int,
	private var isMirror: Boolean
) {

	companion object {

		/**
		 * 绘制数据信息到view上
		 *
		 * @param canvas            需要被绘制的view的canvas
		 * @param rect              人脸框
		 * @param color             绘制的颜色
		 * @param faceRectThickness 人脸框厚度
		 */
		fun drawFaceRect(canvas: Canvas?, rect: Rect?, color: Int, faceRectThickness: Int) {
			if (null == canvas || null == rect) {
				return
			}
			val paint = Paint()
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = faceRectThickness.toFloat()
			paint.color = color
			canvas.drawRect(rect, paint)
		}
	}

	fun draw(faceRectView: FaceRectView?, rectList: List<Rect>?) {
		if (null == faceRectView) {
			return
		}
		faceRectView.clearFaceInfo()
		if (null == rectList || rectList.isEmpty()) {
			return
		}
		val newRectList = ArrayList<Rect>()
		for (rect in rectList) {
			adjustRect(
				rect,
				previewWidth,
				previewHeight,
				canvasWidth,
				canvasHeight,
				cameraDisplayOrientation,
				cameraId,
				isMirror,
				mirrorHorizontal = false,
				mirrorVertical = false
			)?.let { newRectList.add(it) }
		}
		faceRectView.addFaceInfo(newRectList)
	}

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
	private fun adjustRect(
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

	fun setPreviewWidth(previewWidth: Int) {
		this.previewWidth = previewWidth
	}

	fun setPreviewHeight(previewHeight: Int) {
		this.previewHeight = previewHeight
	}

	fun setCanvasWidth(canvasWidth: Int) {
		this.canvasWidth = canvasWidth
	}

	fun setCanvasHeight(canvasHeight: Int) {
		this.canvasHeight = canvasHeight
	}

	fun setCameraDisplayOrientation(cameraDisplayOrientation: Int) {
		this.cameraDisplayOrientation = cameraDisplayOrientation
	}

	fun setCameraId(cameraId: Int) {
		this.cameraId = cameraId
	}

	fun setMirror(mirror: Boolean) {
		isMirror = mirror
	}
}
