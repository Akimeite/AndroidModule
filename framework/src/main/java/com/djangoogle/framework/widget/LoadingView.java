package com.djangoogle.framework.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;

import com.djangoogle.framework.R;

/**
 * 基础等待层弹窗
 * Created by Djangoogle on 2018/10/24 09:33 with Android Studio.
 */
public class LoadingView extends AppCompatDialog {

	public LoadingView(Context context) {
		super(context, R.style.loading_view);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading_base);
	}
}
