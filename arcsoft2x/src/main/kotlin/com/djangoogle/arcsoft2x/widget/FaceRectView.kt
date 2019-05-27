package com.djangoogle.arcsoft2x.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.djangoogle.arcsoft2x.util.DrawHelper
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Djangoogle on 2019/05/23 13:22 with Android Studio.
 */
class FaceRectView : View {

	var mRectList = CopyOnWriteArrayList<Rect>()
	var faceRectThickness = 5

	constructor(context: Context?) : super(context, null)

	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	override fun draw(canvas: Canvas?) {
		super.draw(canvas)
		if (mRectList.isNotEmpty()) {
			for (rect in mRectList) {
				DrawHelper.drawFaceRect(canvas, rect, Color.WHITE, faceRectThickness)
			}
		}
	}

	fun clearFaceInfo() {
		mRectList.clear()
		postInvalidate()
	}

	fun addFaceInfo(rect: Rect) {
		mRectList.add(rect)
		postInvalidate()
	}

	fun addFaceInfo(rectList: List<Rect>) {
		mRectList.addAll(rectList)
		postInvalidate()
	}
}