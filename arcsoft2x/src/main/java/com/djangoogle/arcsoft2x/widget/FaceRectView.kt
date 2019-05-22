package com.djangoogle.arcsoft2x.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View

import com.djangoogle.arcsoft2x.model.FaceInfoModel
import com.djangoogle.arcsoft2x.util.DrawHelper
import java.util.concurrent.CopyOnWriteArrayList

class FaceRectView : View {

	private val faceRectList = CopyOnWriteArrayList<FaceInfoModel>()

	constructor(context: Context?) : super(context, null)

	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		if (faceRectList.size > 0) {
			for (i in faceRectList.indices) {
				DrawHelper.drawFaceRect(canvas, faceRectList[i], Color.WHITE, 5)
			}
		}
	}

	fun clearFaceInfo() {
		faceRectList.clear()
		postInvalidate()
	}

	fun addFaceInfo(faceInfo: FaceInfoModel) {
		faceRectList.add(faceInfo)
		postInvalidate()
	}

	fun addFaceInfo(faceInfoList: List<FaceInfoModel>) {
		faceRectList.addAll(faceInfoList)
		postInvalidate()
	}
}