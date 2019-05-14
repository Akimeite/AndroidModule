package com.djangoogle.framework.widget

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.LogUtils
import com.djangoogle.framework.R
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment

/**
 * 基础等待层弹窗
 * Created by Djangoogle on 2018/10/24 09:33 with Android Studio.
 */
class DjangoLoading : RxAppCompatDialogFragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NO_TITLE, R.style.django_loading)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.dialog_loading_base, container, false)
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val djangoLoading = super.onCreateDialog(savedInstanceState)
		djangoLoading.setCancelable(false)
		djangoLoading.setCanceledOnTouchOutside(false)
		djangoLoading.setOnKeyListener { _, keyCode, _ -> KeyEvent.KEYCODE_BACK == keyCode }
		LogUtils.dTag(DjangoLoading::class.java.simpleName, "创建")
		return djangoLoading
	}

	override fun show(manager: FragmentManager?, tag: String?) {
		super.show(manager, tag)
		LogUtils.dTag(DjangoLoading::class.java.simpleName, "显示")
	}

	override fun dismiss() {
		super.dismiss()
		LogUtils.dTag(DjangoLoading::class.java.simpleName, "隐藏")
	}

	override fun onDestroyView() {
		super.onDestroyView()
		LogUtils.dTag(DjangoLoading::class.java.simpleName, "销毁")
	}
}
