package com.djangoogle.banner.sample.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.djangoogle.banner.sample.loader.MediaLoader;
import com.agesun.base.application.AgesunBaseApp;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

/**
 * Created by Djangoogle on 2019/03/27 10:53 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
public class BannerApp extends AgesunBaseApp {

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//初始化相册
		initAlbum();
	}

	/**
	 * 初始化相册
	 */
	private void initAlbum() {
		Album.initialize(AlbumConfig.newBuilder(this)
		                            .setAlbumLoader(new MediaLoader())
		                            .setLocale(Locale.getDefault())
		                            .build());
	}
}
