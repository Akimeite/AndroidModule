package com.djangoogle.framework.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 自定义支持跑马灯TextView
 * Created by Djangoogle on 2018/11/15 23:12 with Android Studio.
 */
class MarqueeTextView : AppCompatTextView {

	constructor(context: Context) : super(context, null)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	override fun isFocused(): Boolean {
		return true
	}
}
