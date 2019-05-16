package com.djangoogle.module.manager

import com.djangoogle.banner.BannerEventBusIndex
import com.djangoogle.framework.FrameworkEventBusIndex
import org.greenrobot.eventbus.EventBus

/**
 * Created by Djangoogle on 2019/05/14 11:32 with Android Studio.
 */
class EventBusManager private constructor() {

	companion object {
		val INSTANCE: EventBusManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
			EventBusManager()
		}
	}

	var mEventBus: EventBus? = EventBus.getDefault()
		private set

	fun initialize() {
		mEventBus = EventBus.builder().addIndex(BannerEventBusIndex()).addIndex(FrameworkEventBusIndex()).build()
	}
}