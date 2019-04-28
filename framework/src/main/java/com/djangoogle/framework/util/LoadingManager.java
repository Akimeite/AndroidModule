package com.djangoogle.framework.util;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.djangoogle.framework.widget.LoadingView;

/**
 * 通用Loding
 * Created by Djangoogle on 2018/10/24 09:27 with Android Studio.
 */
public class LoadingManager {

	private static LoadingManager instance = null;

	private LoadingView lodingView = null;

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
	 * @param context 上下文
	 */
	public void show(Context context) {
		if (null != context) {
			try {
				if (null == lodingView) {
					lodingView = new LoadingView(context);
				}
				lodingView.setCanceledOnTouchOutside(false);//点击屏幕不能关闭加载框
				lodingView.setCancelable(false);//点击返回键不能关闭加载框
				lodingView.show();
			} catch (Exception e) {
				LogUtils.e(e);
			}
		}
	}

	/**
	 * 隐藏
	 */
	public void hide() {
		if (null != lodingView) {
			try {
				lodingView.dismiss();
			} catch (Exception e) {
				LogUtils.e(e);
			}
		}
	}
}
