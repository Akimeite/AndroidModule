package com.djangoogle.framework.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * 弹出框工具类
 * Created by Djangoogle on 2018/10/18 11:20 with Android Studio.
 */
public class DialogUtils {

	/**
	 * 显示弹出框
	 *
	 * @param context
	 * @param msg
	 * @param positiveListener
	 * @param negativeListener
	 */
	public static void showSimpleDialog(Context context, String msg, DialogInterface.OnClickListener positiveListener, DialogInterface
			.OnClickListener negativeListener) {
		new AlertDialog.Builder(context).setMessage(msg)
		                                .setPositiveButton("确认", positiveListener)
		                                .setNegativeButton("取消", negativeListener)
		                                .create()
		                                .show();
	}

	/**
	 * 显示弹出框，取消键默认返回
	 *
	 * @param context
	 * @param msg
	 * @param listener
	 */
	public static void showSimpleDialog(Context context, String msg, DialogInterface.OnClickListener listener) {
		showSimpleDialog(context, msg, listener, null);
	}
}
