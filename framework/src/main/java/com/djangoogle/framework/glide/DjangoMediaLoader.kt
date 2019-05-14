package com.djangoogle.framework.glide

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader

/**
 * Created by Djangoogle on 2019/04/28 09:42 with Android Studio.
 */
class DjangoMediaLoader : AlbumLoader {

	override fun load(imageView: ImageView, albumFile: AlbumFile) {
		load(imageView, albumFile.path)
	}

	override fun load(imageView: ImageView, url: String) {
		Glide.with(imageView.context).load(url).transition(DrawableTransitionOptions.withCrossFade()).into(imageView)
	}
}
