package com.djangoogle.framework.application

import android.app.Application
import com.blankj.utilcode.util.*
import com.djangoogle.framework.glide.DjangoMediaLoader
import com.djangoogle.framework.manager.LruBitmapCacheManager
import com.djangoogle.framework.retrofit.manager.RetrofitManager
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
		//初始化设备信息
		initDeviceInfo()
		//初始化MMKV
		MMKV.initialize(this)
		//初始化数据库管理工具
		LitePal.initialize(this)
		//初始化Retrofit
		RetrofitManager.INSTANCE.initialize(this)
		//初始化相册
		initAlbum()
		//初始化Bitmap内存缓存管理器
		LruBitmapCacheManager.INSTANCE.initialize()
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
		Album.initialize(
			AlbumConfig.newBuilder(this)
				.setAlbumLoader(DjangoMediaLoader())
				.setLocale(Locale.getDefault())
				.build()
		)
	}

	/**
	 * 初始化设备信息
	 */
	private fun initDeviceInfo() {
		val sbDebugInfo = StringBuilder()
		sbDebugInfo.append("App是否是debug版本: ").append(if (AppUtils.isAppDebug()) "是" else "否").append("\n")
		sbDebugInfo.append("App是否是系统应用: ").append(if (AppUtils.isAppSystem()) "是" else "否").append("\n")
		sbDebugInfo.append("App包名: ").append(AppUtils.getAppPackageName()).append("\n")
		sbDebugInfo.append("App名称: ").append(AppUtils.getAppName()).append("\n")
		sbDebugInfo.append("App路径: ").append(AppUtils.getAppPath()).append("\n")
		sbDebugInfo.append("App版本号: ").append(AppUtils.getAppVersionName()).append("\n")
		sbDebugInfo.append("App版本码: ").append(AppUtils.getAppVersionCode()).append("\n")
		sbDebugInfo.append("App签名SHA1值: ").append(AppUtils.getAppSignatureSHA1()).append("\n")
		sbDebugInfo.append("App签名SHA256值: ").append(AppUtils.getAppSignatureSHA256()).append("\n")
		sbDebugInfo.append("App签名MD5值: ").append(AppUtils.getAppSignatureMD5()).append("\n")
		sbDebugInfo.append("设备是否root: ").append(if (DeviceUtils.isDeviceRooted()) "是" else "否").append("\n")
		sbDebugInfo.append("设备adb是否可用: ").append(if (DeviceUtils.isAdbEnabled()) "是" else "否").append("\n")
		sbDebugInfo.append("设备系统版本号: ").append(DeviceUtils.getSDKVersionName()).append("\n")
		sbDebugInfo.append("设备系统版本码: ").append(DeviceUtils.getSDKVersionCode()).append("\n")
		sbDebugInfo.append("设备AndroidID: ").append(DeviceUtils.getAndroidID()).append("\n")
		sbDebugInfo.append("设备MAC地址: ").append(DeviceUtils.getMacAddress()).append("\n")
		sbDebugInfo.append("设备厂商: ").append(DeviceUtils.getManufacturer()).append("\n")
		sbDebugInfo.append("设备型号: ").append(DeviceUtils.getModel()).append("\n")
		sbDebugInfo.append("设备ABIs: ").append(DeviceUtils.getABIs()?.contentToString()).append("\n")
		sbDebugInfo.append("设备屏幕宽度: ").append(ScreenUtils.getScreenWidth()).append("px").append("\n")
		sbDebugInfo.append("设备屏幕高度: ").append(ScreenUtils.getScreenHeight()).append("px").append("\n")
		sbDebugInfo.append("设备状态栏高度: ").append(BarUtils.getStatusBarHeight()).append("px").append("\n")
		if (BarUtils.isSupportNavBar()) sbDebugInfo.append("设备导航栏高度: ").append(BarUtils.getNavBarHeight()).append("px").append("\n")
		sbDebugInfo.append("设备屏幕密度: ").append(ScreenUtils.getScreenDensity()).append("\n")
		sbDebugInfo.append("设备屏幕密度DPI: ").append(ScreenUtils.getScreenDensityDpi()).append("\n")
		sbDebugInfo.append("启动时间: ").append(TimeUtils.getNowString()).append("\n")
		LogUtils.iTag("BasicInfo", sbDebugInfo.toString())
		sbDebugInfo.clear()
	}
}
