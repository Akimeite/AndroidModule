package com.djangoogle.arcsoft2x.util

import android.graphics.Rect
import com.arcsoft.face.FaceInfo

class TrackUtil {

	companion object {

		fun isSameFace(fSimilarity: Float, rect1: Rect, rect2: Rect): Boolean {
			val left = Math.max(rect1.left, rect2.left)
			val top = Math.max(rect1.top, rect2.top)
			val right = Math.min(rect1.right, rect2.right)
			val bottom = Math.min(rect1.bottom, rect2.bottom)
			val innerArea = (right - left) * (bottom - top)
			return left < right && top < bottom && rect2.width().toFloat() * rect2.height().toFloat() * fSimilarity <= innerArea && rect1.width().toFloat() * rect1.height().toFloat() * fSimilarity <= innerArea
		}

		fun keepMaxFace(ftFaceList: MutableList<FaceInfo>?) {
			if (ftFaceList == null || ftFaceList.size <= 1) {
				return
			}
			var maxFaceInfo = ftFaceList[0]
			for (faceInfo in ftFaceList) {
				if (faceInfo.rect.width() > maxFaceInfo.rect.width()) {
					maxFaceInfo = faceInfo
				}
			}
			ftFaceList.clear()
			ftFaceList.add(maxFaceInfo)
		}
	}
}
