package com.djangoogle.framework.widget

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djangoogle.framework.R
import com.djangoogle.framework.fragment.DjangoDialogFragment

/**
 * 基础等待层弹窗
 * Created by Djangoogle on 2018/10/24 09:33 with Android Studio.
 */
class DjangoLoading : DjangoDialogFragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NO_TITLE, R.style.django_loading)
	}

	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.dialog_loading_django, container, false)
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val djangoLoading = super.onCreateDialog(savedInstanceState)
		djangoLoading.setCancelable(false)
		djangoLoading.setCanceledOnTouchOutside(false)
		djangoLoading.setOnKeyListener { _, keyCode, _ -> KeyEvent.KEYCODE_BACK == keyCode }
		return djangoLoading
	}
}
