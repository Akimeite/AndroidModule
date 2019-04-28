package com.djangoogle.framework.util;

/**
 * 防重复点击
 * Created by Djangoogle on 2018/10/11 10:31 with Android Studio.
 */
public class NoDoubleClickUtils {

	public static final long INTERVAL = 500L;
	private static long lastClickTime;

	public synchronized static boolean isDoubleClick() {
		long currentTime = System.currentTimeMillis();
		boolean isClick = currentTime - lastClickTime < INTERVAL;
		lastClickTime = currentTime;
		return isClick;
	}
}
