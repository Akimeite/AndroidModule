package com.djangoogle.module.application

import android.content.Context
import androidx.multidex.MultiDex
import com.djangoogle.banner.BannerEventBusIndex
import com.djangoogle.framework.FrameworkEventBusIndex
import com.djangoogle.framework.application.DjangoApp
import org.greenrobot.eventbus.EventBus

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
		EventBus.builder()
//			.addIndex(ArcSoft2XEventBusIndex())
			.addIndex(BannerEventBusIndex())
			.addIndex(FrameworkEventBusIndex())
			.installDefaultEventBus()
	}
}
