package com.djangoogle.module.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import com.djangoogle.module.R
import com.djangoogle.module.activity.banner.BannerActivity
import com.djangoogle.module.activity.base.BaseActivity
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

/**
 * 主页
 * Created by Djangoogle on 2019/05/12 21:21 with Android Studio.
 */
class MainActivity : BaseActivity() {

	override fun initLayout(): Int {
		return R.layout.activity_main
	}

	override fun initGUI() {}

	@SuppressLint("CheckResult")
	override fun initAction() {
		acbMainBanner.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe { startActivity(Intent(this, BannerActivity::class.java)) }
	}

	override fun initData() {}
}