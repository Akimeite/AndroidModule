package com.djangoogle.framework.service

import android.app.Service
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Djangoogle on 2019/05/22 17:34 with Android Studio.
 */
abstract class DjangoService : Service() {

	override fun onCreate() {
		super.onCreate()
		//注册EventBus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		//注销EventBus
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this)
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onBaseServiceEvent(event: Any) {
	}
}
