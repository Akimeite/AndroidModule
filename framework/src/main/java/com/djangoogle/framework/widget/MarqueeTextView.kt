package com.djangoogle.framework.widget

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * 自定义支持跑马灯TextView
 * Created by Djangoogle on 2018/11/15 23:12 with Android Studio.
 */
class MarqueeTextView : AppCompatTextView {

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun isFocused(): Boolean {
		return true
	}
}
