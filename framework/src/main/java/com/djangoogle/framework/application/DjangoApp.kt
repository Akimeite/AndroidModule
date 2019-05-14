package com.djangoogle.framework.application

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.djangoogle.framework.glide.DjangoMediaLoader
import com.djangoogle.framework.util.LruBitmapCacheUtil
import com.tencent.mmkv.MMKV
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import org.litepal.LitePal
import java.util.*

/**
 * 自定义基础Application
 * Created by Djangoogle on 2018/10/10 17:46 with Android Studio.
 */
open class DjangoApp : Application() {

	override fun onCreate() {
		super.onCreate()
		//初始化通用工具类
		Utils.init(this)
		//初始化日志管理器
		initLogUtils()
		//初始化MMKV
		MMKV.initialize(this)
		//初始化数据库管理工具
		LitePal.initialize(this)
		//初始化相册
		initAlbum()
		//初始化Bitmap内存缓存管理器
		LruBitmapCacheUtil.getInstance().initialize()
	}

	/**
	 * 初始化日志管理器
	 */
	private fun initLogUtils() {
		//关闭头部信息
		LogUtils.getConfig().isLogHeadSwitch = false
		//关闭边框
		LogUtils.getConfig().setBorderSwitch(false)
	}

	/**
	 * 初始化相册
	 */
	private fun initAlbum() {
		Album.initialize(AlbumConfig.newBuilder(this)
				.setAlbumLoader(DjangoMediaLoader())
				.setLocale(Locale.getDefault())
				.build())
	}
}
