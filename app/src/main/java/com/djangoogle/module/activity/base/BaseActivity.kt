package com.djangoogle.module.activity.base

import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import com.djangoogle.framework.activity.DjangoActivity

/**
 * Created by Djangoogle on 2019/02/25 10:53 with Android Studio.
 */
abstract class BaseActivity : DjangoActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ablCommonToolBar.visibility = View.GONE
	}

	override fun initButterKnife() {
		ButterKnife.bind(this)
	}

	override fun onWindowFocusChanged(hasFocus: Boolean) {
		super.onWindowFocusChanged(hasFocus)
		//始终全屏显示页面
		if (hasFocus) {
			clBaseRootView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		}
	}
}
