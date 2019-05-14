package com.djangoogle.framework.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.djangoogle.framework.R
import com.djangoogle.framework.glide.GlideApp

/**
 * Glide图片加载工具类
 * Created by Djangoogle on 2018/10/25 14:15 with Android Studio.
 */
class GlideUtils {

	companion object {

		/**
		 * 加载图片（有底图）
		 *
		 * @param context       上下文
		 * @param url           图片地址
		 * @param placeholderId 底图资源文件Id
		 * @param image         图片控件
		 */
		fun load(context: Context, url: Any, placeholderId: Int, image: ImageView) {
			GlideApp.with(context).load(url).placeholder(placeholderId).transition(DrawableTransitionOptions.withCrossFade()).into(image)
		}

		/**
		 * 加载图片（无底图）
		 *
		 * @param context 上下文
		 * @param url     图片地址
		 * @param image   图片控件
		 */
		fun load(context: Context, url: Any, image: ImageView) {
			GlideApp.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).into(image)
		}

		/**
		 * 居中加载图片
		 *
		 * @param context 上下文
		 * @param resId   图片资源文件Id
		 * @param image   图片控件
		 */
		fun load(context: Context, resId: Int, image: ImageView) {
			GlideApp.with(context).load(resId).centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(image)
		}

		/**
		 * 居中加载圆形图片
		 *
		 * @param context       上下文
		 * @param url           图片地址
		 * @param placeholderId 底图资源文件Id
		 * @param image         图片控件
		 */
		fun loadCircle(context: Context, url: Any, placeholderId: Int, image: ImageView) {
			GlideApp.with(context)
					.load(url)
					.placeholder(placeholderId)
					.circleCrop()
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(image)
		}

		/**
		 * 居中加载圆形图片
		 *
		 * @param context 上下文
		 * @param resId   图片资源文件Id
		 * @param image   图片控件
		 */
		fun loadCircle(context: Context, resId: Int, image: ImageView) {
			GlideApp.with(context).load(resId).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).into(image)
		}

		/**
		 * 居中加载圆形图片
		 *
		 * @param context 上下文
		 * @param url     图片地址
		 * @param image   图片控件
		 */
		fun loadCircle(context: Context, url: Any, image: ImageView) {
			GlideApp.with(context).load(url).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).into(image)
		}

		/**
		 * 截取视频第一帧
		 *
		 * @param context 上下文
		 * @param url     图片地址
		 * @param image   图片控件
		 */
		fun getFirstFrame(context: Context, url: Any, image: ImageView) {
			GlideApp.with(context).load(url).error(R.color.black).placeholder(R.color.black).frame(1000000L).into(image)
		}
	}
}
