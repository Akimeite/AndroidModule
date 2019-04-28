package com.djangoogle.framework.glide;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

/**
 * Created by Djangoogle on 2019/04/28 09:42 with Android Studio.
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
		        .transition(DrawableTransitionOptions.withCrossFade())
		        .into(imageView);
	}
}
