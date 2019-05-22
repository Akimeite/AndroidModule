package com.djangoogle.module.activity.splash

import android.content.Intent
import android.os.Build
import android.os.Handler
import com.djangoogle.module.R
import com.djangoogle.module.activity.base.BaseActivity
import com.djangoogle.module.activity.main.MainActivity

/**启动页
 * Created by Djangoogle on 2019/05/13 16:44 with Android Studio.
 */
class SplashActivity : BaseActivity() {

	override fun initLayout(): Int {
		return R.layout.activity_splash
	}

	override fun initGUI() {}

	override fun initAction() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//申请权限
			requestPermissions()
		} else {
			//初始化
			init()
		}
	}

	override fun initData() {}

	/**
	 * 申请权限
	 */
	private fun requestPermissions() {
//		XXPermissions.with(this)
//			.constantRequest()
//			.permission(*Permission.Group.STORAGE)
//			.request(object : OnPermission {
//				override fun hasPermission(granted: List<String>, isAll: Boolean) {
//					//初始化
//					init()
//				}
//
//				override fun noPermission(denied: List<String>, quick: Boolean) {
//					val deniedArray = denied.toTypedArray()
//					ToastUtils.showShort("权限" + Arrays.toString(deniedArray) + "被拒绝")
//				}
//			})
	}

	/**
	 * 初始化
	 */
	private fun init() {
		Handler().postDelayed({
			//延迟打开轮播页
			startActivity(Intent(mActivity, MainActivity::class.java))
			finish()
		}, 1000L)
	}
}