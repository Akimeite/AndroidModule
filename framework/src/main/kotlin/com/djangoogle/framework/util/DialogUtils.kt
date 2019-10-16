package com.djangoogle.framework.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

/**
 * 弹出框工具类
 * Created by Djangoogle on 2018/10/18 11:20 with Android Studio.
 */
object DialogUtils {

	/**
	 * 显示弹出框
	 *
	 * @param context
	 * @param msg
	 * @param positiveListener
	 * @param negativeListener
	 */
	fun showSimpleDialog(
        context: Context,
        msg: String,
        positiveListener: DialogInterface.OnClickListener,
        negativeListener: DialogInterface.OnClickListener?
	) {
		AlertDialog.Builder(context).setMessage(msg)
            .setPositiveButton("确认", positiveListener)
            .setNegativeButton("取消", negativeListener)
            .create()
            .show()
	}

	/**
	 * 显示弹出框，取消键默认返回
	 *
	 * @param context
	 * @param msg
	 * @param listener
	 */
	fun showSimpleDialog(context: Context, msg: String, listener: DialogInterface.OnClickListener) {
		showSimpleDialog(context, msg, listener, null)
	}
}
