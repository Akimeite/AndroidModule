package com.djangoogle.module.application

import android.content.Context
import androidx.multidex.MultiDex
import com.djangoogle.framework.application.DjangoApp

/**
 * Created by Djangoogle on 2019/03/27 10:53 with Android Studio.
 */
class App : DjangoApp() {

	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(base)
		MultiDex.install(this)
	}

}
