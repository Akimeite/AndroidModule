package com.djangoogle.arcsoft2x.util

import android.graphics.Rect

import com.arcsoft.face.FaceInfo

/**
 * 人脸处理工具
 * Created by Djangoogle on 2019/04/08 17:42 with Android Studio.
 */
object TrackUtil {

	fun isSameFace(faceSimilarity: Float, rect1: Rect, rect2: Rect): Boolean {
		val left = Math.max(rect1.left, rect2.left)
		val top = Math.max(rect1.top, rect2.top)
		val right = Math.min(rect1.right, rect2.right)
		val bottom = Math.min(rect1.bottom, rect2.bottom)
		val innerArea = (right - left) * (bottom - top)
		return left < right && top < bottom && rect2.width().toFloat() * rect2.height().toFloat() * faceSimilarity <= innerArea && rect1.width().toFloat() * rect1.height().toFloat() * faceSimilarity <= innerArea
	}

	/**
	 * 保留最大人脸
	 */
	fun keepMaxFace(faceInfoList: ArrayList<FaceInfo>?) {
		if (null == faceInfoList || faceInfoList.size <= 1) {
			return
		}
		var maxFaceInfo = faceInfoList[0]
		for (faceInfo in faceInfoList) {
			if (faceInfo.rect.width() > maxFaceInfo.rect.width()) {
				maxFaceInfo = faceInfo
			}
		}
		faceInfoList.clear()
		faceInfoList.add(maxFaceInfo)
	}
}
