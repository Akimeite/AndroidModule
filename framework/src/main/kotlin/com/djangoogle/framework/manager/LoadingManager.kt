package com.djangoogle.framework.manager

import android.support.v4.app.FragmentManager
import com.djangoogle.framework.widget.DjangoLoading

/**
 * 通用Loding
 * Created by Djangoogle on 2018/10/24 09:27 with Android Studio.
 */
class LoadingManager private constructor() {

	companion object {
		val INSTANCE: LoadingManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
			LoadingManager()
		}
	}

	private var lodingView: DjangoLoading? = null

	/**
	 * 显示
	 *
	 * @param fragmentManager FragmentManager
	 */
	fun show(fragmentManager: FragmentManager?) {
		hide()
		if (null == lodingView) {
			lodingView = DjangoLoading()
		}
		if (null != fragmentManager) {
			lodingView?.show(fragmentManager, LoadingManager::class.simpleName)
		}
	}

	/**
	 * 隐藏
	 */
	fun hide() {
		if (null != lodingView) {
			lodingView?.dismiss()
		}
		lodingView = null
	}
}
