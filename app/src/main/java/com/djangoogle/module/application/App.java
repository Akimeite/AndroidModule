package com.djangoogle.module.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.djangoogle.framework.application.DjangoApp;

/**
 * Created by Djangoogle on 2019/03/27 10:53 with Android Studio.
 */
public class App extends DjangoApp {

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
}
