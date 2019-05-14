package com.djangoogle.framework.util

/**
 * 防重复点击
 * Created by Djangoogle on 2018/10/11 10:31 with Android Studio.
 */
class NoDoubleClickUtils {

	companion object {

		private var lastClickTime: Long = 0L

		@Synchronized
		fun isDoubleClick(interval: Long): Boolean {
			val currentTime = System.currentTimeMillis()
			val isClick = currentTime - lastClickTime < interval
			lastClickTime = currentTime
			return isClick
		}
	}
}
