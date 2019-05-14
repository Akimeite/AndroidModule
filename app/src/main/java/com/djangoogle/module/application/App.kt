package com.djangoogle.module.application

import android.content.Context
import android.support.multidex.MultiDex
import com.djangoogle.framework.application.DjangoApp
import com.djangoogle.module.manager.EventBusManager

/**
 * Created by Djangoogle on 2019/03/27 10:53 with Android Studio.
 */
class App : DjangoApp() {

	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(base)
		MultiDex.install(this)
	}

	override fun onCreate() {
		super.onCreate()
		//初始化EventBus索引
		EventBusManager.instance.initialize()
	}
}
