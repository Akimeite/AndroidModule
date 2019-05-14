package com.djangoogle.framework.util;

import android.support.v4.app.FragmentManager;

import com.blankj.utilcode.util.LogUtils;
import com.djangoogle.framework.widget.DjangoLoading;

/**
 * 通用Loding
 * Created by Djangoogle on 2018/10/24 09:27 with Android Studio.
 */
public class LoadingManager {

	private static LoadingManager instance = null;

	private DjangoLoading lodingView = null;

	public static LoadingManager getInstance() {
		if (null == instance) {
			synchronized (LoadingManager.class) {
				if (null == instance) {
					instance = new LoadingManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 显示
	 *
	 * @param fragmentManager FragmentManager
	 */
	public void show(FragmentManager fragmentManager) {
		try {
			if (null != fragmentManager) {
				if (null == lodingView) {
					lodingView = new DjangoLoading();
				}
				lodingView.show(fragmentManager, LoadingManager.class.getSimpleName());
			}
		} catch (Exception e) {
			LogUtils.e(e);
		}
	}

	/**
	 * 隐藏
	 */
	public void hide() {
		try {
			if (null != lodingView) {
				lodingView.dismiss();
			}
		} catch (Exception e) {
			LogUtils.e(e);
		}
	}
}
