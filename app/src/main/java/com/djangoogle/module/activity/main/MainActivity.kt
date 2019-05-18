package com.djangoogle.module.activity.main

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.djangoogle.module.R
import com.djangoogle.module.activity.banner.BannerActivity
import com.djangoogle.module.activity.base.BaseActivity
import com.djangoogle.module.network.Network
import com.djangoogle.module.network.ZhuangbiImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
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
		singleClicks(acbMainRetrofit, Consumer { testRetrofit() })
	}

	override fun initData() {}

	private fun testRetrofit() {
		Network.getZhuangbiApi()
			.search("1")
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(Consumer<List<ZhuangbiImage>> { images ->
				ToastUtils.showShort("请求到" + images.size)
			}, Consumer<Throwable> {
				ToastUtils.showShort(it.message)
			})
	}
}