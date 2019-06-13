package com.djangoogle.module.activity.splash

import android.content.Intent
import android.os.Build
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.djangoogle.module.R
import com.djangoogle.module.activity.base.BaseActivity
import com.djangoogle.module.activity.main.MainActivity
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

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
		PermissionUtils.permission(PermissionConstants.STORAGE).callback(object : PermissionUtils.FullCallback {
			override fun onGranted(permissionsGranted: MutableList<String>?) {
				//初始化
				init()
			}

			override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
				val deniedForeverArray = permissionsDeniedForever?.toTypedArray()
				val deniedArray = permissionsDenied?.toTypedArray()
				ToastUtils.showShort("权限" + Arrays.toString(deniedForeverArray) + "被永久拒绝\n权限" + Arrays.toString(deniedArray) + "被拒绝")
			}
		}).request()
	}

	/**
	 * 初始化
	 */
	private fun init() {
		Observable.timer(1L, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).bindToLifecycle(this).subscribe(object : Observer<Long> {
			override fun onSubscribe(d: Disposable) {}

			override fun onNext(aLong: Long) {
				//延迟打开轮播页
				startActivity(Intent(mActivity, MainActivity::class.java))
				finish()
			}

			override fun onError(e: Throwable) {
				//延迟打开轮播页
				startActivity(Intent(mActivity, MainActivity::class.java))
				finish()
			}

			override fun onComplete() {}
		})
	}
}