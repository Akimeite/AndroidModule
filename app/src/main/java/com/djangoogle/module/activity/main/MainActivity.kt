package com.djangoogle.module.activity.main

import android.content.Intent
import com.djangoogle.module.R
import com.djangoogle.module.activity.banner.BannerActivity
import com.djangoogle.module.activity.base.BaseActivity
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 主页
 * Created by Djangoogle on 2019/05/12 21:21 with Android Studio.
 */
class MainActivity : BaseActivity() {

	override fun initLayout(): Int {
		return R.layout.activity_main
	}

	override fun initGUI() {}

	override fun initAction() {
		singleClicks(acbMainBanner, Consumer { startActivity(Intent(this, BannerActivity::class.java)) })
		singleClicks(acbMainRetrofit, Consumer { })
	}

	override fun initData() {}
}