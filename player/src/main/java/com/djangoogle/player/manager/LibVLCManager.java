package com.djangoogle.player.manager;

import android.content.Context;

import org.videolan.libvlc.LibVLC;

import java.util.ArrayList;

/**
 * VLC管理器
 * Created by Djangoogle on 2019/05/05 10:14 with Android Studio.
 */
public class LibVLCManager {
	private static volatile LibVLC instance = null;

	public static LibVLC getInstance(Context context, ArrayList<String> options) throws IllegalStateException {
		if (null == instance) {
			synchronized (LibVLCManager.class) {
				if (null == options) {
					instance = new LibVLC(context);
				} else {
					instance = new LibVLC(context, options);
				}
			}
		}
		return instance;
	}
}
