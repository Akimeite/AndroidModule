package com.djangoogle.banner.sample.loader;

import android.widget.ImageView;

import com.agesun.banner.sample.R;
import com.agesun.base.glide.GlideApp;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

/**
 * 媒体加载器
 * Created by Djangoogle on 2019/03/28 08:11 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
public class MediaLoader implements AlbumLoader {

	@Override
	public void load(ImageView imageView, AlbumFile albumFile) {
		load(imageView, albumFile.getPath());
	}

	@Override
	public void load(ImageView imageView, String url) {
		GlideApp.with(imageView.getContext())
		        .load(url)
		        .error(R.mipmap.placeholder)
		        .placeholder(R.mipmap.placeholder)
		        .transition(DrawableTransitionOptions.withCrossFade())
		        .into(imageView);
	}
}
