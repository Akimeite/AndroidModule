package com.djangoogle.framework.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.LogUtils
import com.trello.rxlifecycle3.components.support.RxDialogFragment

/**
 * Created by Djangoogle on 2019/05/20 10:24 with Android Studio.
 */
open class DjangoDialogFragment : RxDialogFragment() {

	companion object {

		private val TAG = DjangoDialogFragment::class.simpleName
	}

	//通用Activity
	protected lateinit var mActivity: Activity

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		LogUtils.dTag(TAG, "onAttach")
		mActivity = activity as FragmentActivity
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		LogUtils.dTag(TAG, "onCreateDialog")
		return super.onCreateDialog(savedInstanceState)
	}

	override fun show(manager: FragmentManager?, tag: String?) {
		super.show(manager, tag)
		LogUtils.dTag(TAG, "show")
	}

	override fun dismiss() {
		super.dismiss()
		LogUtils.dTag(TAG, "dismiss")
	}

	override fun onDetach() {
		super.onDetach()
		LogUtils.dTag(TAG, "onDetach")
	}
}
