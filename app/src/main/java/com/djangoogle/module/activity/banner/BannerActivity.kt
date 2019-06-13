package com.djangoogle.module.activity.banner

import android.annotation.SuppressLint
import android.os.Environment
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.djangoogle.banner.manager.BannerManager
import com.djangoogle.banner.model.AdResourceModel
import com.djangoogle.module.R
import com.djangoogle.module.activity.base.BaseActivity
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumFile
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_banner.*
import java.util.*

/**
 * 轮播页
 * Created by Djangoogle on 2019/03/27 13:37 with Android Studio.
 */
class BannerActivity : BaseActivity() {

	companion object {

		private const val IMAGE_CHANGE_INTERVAL = 5000L
	}

	override fun initLayout(): Int {
		return R.layout.activity_banner
	}

	override fun initGUI() {
		//初始化轮播器
		BannerManager.getInstance().initialize(this, rvBanner)
		//设置广告
		setUpAd()
	}

	@SuppressLint("CheckResult")
	override fun initAction() {
		singleClicks(rvBanner, Consumer {
			val path = Environment.getExternalStorageDirectory().absolutePath + "/ad/"
			val adResourceList = ArrayList<AdResourceModel>()
			var ad = AdResourceModel()
			ad.type = AdResourceModel.TYPE_VIDEO
			ad.videoPath = path + "2.mp4"
			adResourceList.add(ad)

			ad = AdResourceModel()
			ad.type = AdResourceModel.TYPE_IMAGE
			ad.imagePath = path + "6.jpg"
			ad.imageSwitchInterval = 3000L
			adResourceList.add(ad)

			BannerManager.getInstance().setUp(adResourceList)
		})
	}

	override fun initData() {}

	override fun onResume() {
		super.onResume()
		//恢复轮播
		BannerManager.getInstance().resume()
	}

	override fun onPause() {
		super.onPause()
		//暂停轮播
		BannerManager.getInstance().pause()
	}

	override fun onStop() {
		super.onStop()
		//暂停轮播
		BannerManager.getInstance().pause()
	}

	override fun onDestroy() {
		//销毁轮播
		BannerManager.getInstance().destroy()
		super.onDestroy()
	}

	/**
	 * 设置广告
	 */
	private fun setUpAd() {
		val path = PathUtils.getExternalStoragePath() + "/ad/"
		val adResourceList = ArrayList<AdResourceModel>()
		var ad = AdResourceModel()

		ad.type = AdResourceModel.TYPE_VIDEO
		ad.videoPath = path + "1.mp4"
		adResourceList.add(ad)
//
//		ad = AdResourceModel()
//		ad.type = AdResourceModel.TYPE_IMAGE
//		ad.imagePath = path + "2.png"
//		ad.imageSwitchInterval = 2000L
//		adResourceList.add(ad)

		ad = AdResourceModel()
		ad.type = AdResourceModel.TYPE_VIDEO
		ad.videoPath = path + "3.mp4"
		adResourceList.add(ad)

		//设置广告
		BannerManager.getInstance().setUp(adResourceList)
		//设置音量
		BannerManager.getInstance().setVolume(3)
	}

	/**
	 * 更新广告
	 */
	private fun updateAd() {
		Album.album(this)//Image and video mix options.
			.multipleChoice()//Multi-Mode, Single-Mode: singleChoice().
			.columnCount(3)//The number of columns in the page list.
			.selectCount(9)//Choose up to a few images.
			.camera(true)//Whether the camera appears in the Item.
			.cameraVideoQuality(1)//Video quality, [0, 1].
			.cameraVideoLimitDuration(java.lang.Long.MAX_VALUE)//The longest duration of the video is in milliseconds.
			.cameraVideoLimitBytes(500 * 1024L * 1024L)//Maximum size of the video, in bytes.
			.onResult { result ->
				if (result.isNotEmpty()) {
					val adResourceList = ArrayList<AdResourceModel>()
					albumFile@ for (albumFile in result) {
						val ad = AdResourceModel()
						when (albumFile.mediaType) {
							//图片资源
							AlbumFile.TYPE_IMAGE -> ad.type = AdResourceModel.TYPE_IMAGE
							//视频资源
							AlbumFile.TYPE_VIDEO -> ad.type = AdResourceModel.TYPE_VIDEO
							//未知资源
							else -> continue@albumFile
						}
						ad.imagePath = albumFile.path
						ad.imageSwitchInterval = IMAGE_CHANGE_INTERVAL
						adResourceList.add(ad)
					}
					//设置广告
					BannerManager.getInstance().setUp(adResourceList)
				}
			}
			.onCancel { ToastUtils.showShort("取消广告更新") }
			.start()
	}
}
