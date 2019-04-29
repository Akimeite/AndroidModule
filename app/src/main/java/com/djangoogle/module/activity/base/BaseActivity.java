package com.djangoogle.module.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.djangoogle.framework.activity.DjangoogleActivity;

import butterknife.ButterKnife;

/**
 * Created by Djangoogle on 2019/02/25 10:53 with Android Studio.
 */
public abstract class BaseActivity extends DjangoogleActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ablCommonToolBar.setVisibility(View.GONE);
	}

	@Override
	protected void initButterKnife() {
		ButterKnife.bind(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		//始终全屏显示页面
		if (hasFocus) {
			clBaseRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View
					.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View
					.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
}
