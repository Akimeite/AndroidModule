package com.djangoogle.banner.sample.activity.banner;

import android.os.Environment;
import android.support.v7.widget.RecyclerView;

import com.agesun.banner.sample.R;
import com.blankj.utilcode.util.ToastUtils;
import com.djangoogle.banner.manager.BannerManager;
import com.djangoogle.banner.model.AdResourceModel;
import com.djangoogle.banner.sample.activity.base.BaseActivity;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 轮播页
 * Created by Djangoogle on 2019/03/27 13:37 with Android Studio.
 */
public class BannerActivity extends BaseActivity {

	private static final long IMAGE_CHANGE_INTERVAL = 5000L;

	@BindView(R.id.rvBanner) RecyclerView rvBanner;

	@Override
	protected int initLayout() {
		return R.layout.activity_banner;
	}

	@Override
	protected void initGUI() {
		//初始化轮播器
		BannerManager.getInstance().initialize(this, rvBanner);
		//设置广告
		setUpAd();
//		setBottomRightFloatingBtn(R.mipmap.add, v -> {
//			//更新广告
//			updateAd();
//		});
	}

	@Override
	protected void initAction() {}

	@Override
	protected void initData() {}

	@Override
	protected void onResume() {
		super.onResume();
		//恢复轮播
		BannerManager.getInstance().resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//暂停轮播
		BannerManager.getInstance().pause();
	}

	@Override
	protected void onDestroy() {
		//销毁轮播
		BannerManager.getInstance().destroy();
		super.onDestroy();
	}

	/**
	 * 设置广告
	 */
	private void setUpAd() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ad/";
		List<AdResourceModel> adResourceList = new ArrayList<>();
		AdResourceModel ad;

//		ad = new AdResourceModel();
//		ad.type = AdResourceModel.TYPE_MIX;
//		ad.mixType = AdResourceModel.MIX_TYPE_IMAGE_UP;
//		ad.imagePath = path + "7.jpg";
//		ad.videoPath = path + "6.mp4";
//		adResourceList.add(ad);

		ad = new AdResourceModel();
		ad.type = AdResourceModel.TYPE_VIDEO;
		ad.videoPath = path + "2.mp4";
		adResourceList.add(ad);

//		ad = new AdResourceModel();
//		ad.type = AdResourceModel.TYPE_VIDEO;
//		ad.videoPath = path + "5.mp4";
//		adResourceList.add(ad);
//
//		ad = new AdResourceModel();
//		ad.type = AdResourceModel.TYPE_IMAGE;
//		ad.imagePath = path + "3.jpg";
//		ad.imageSwitchInterval = 6000L;
//		adResourceList.add(ad);
//
//		ad = new AdResourceModel();
//		ad.type = AdResourceModel.TYPE_MIX;
//		ad.mixType = AdResourceModel.MIX_TYPE_VIDEO_UP;
//		ad.imagePath = path + "0.png";
//		ad.videoPath = path + "8.mp4";
//		adResourceList.add(ad);

		//设置广告
		BannerManager.getInstance().setUp(adResourceList);
	}

	/**
	 * 更新广告
	 */
	private void updateAd() {
		Album.album(this)//Image and video mix options.
		     .multipleChoice()//Multi-Mode, Single-Mode: singleChoice().
		     .columnCount(3)//The number of columns in the page list.
		     .selectCount(9)//Choose up to a few images.
		     .camera(true)//Whether the camera appears in the Item.
		     .cameraVideoQuality(1)//Video quality, [0, 1].
		     .cameraVideoLimitDuration(Long.MAX_VALUE)//The longest duration of the video is in milliseconds.
		     .cameraVideoLimitBytes(500 * 1024L * 1024L)//Maximum size of the video, in bytes.
		     .onResult(result -> {
			     if (!result.isEmpty()) {
				     List<AdResourceModel> adResourceList = new ArrayList<>();
				     for (AlbumFile albumFile : result) {
					     AdResourceModel ad = new AdResourceModel();
					     switch (albumFile.getMediaType()) {
						     //图片资源
						     case AlbumFile.TYPE_IMAGE:
							     ad.type = AdResourceModel.TYPE_IMAGE;
							     break;
						     //视频资源
						     case AlbumFile.TYPE_VIDEO:
							     ad.type = AdResourceModel.TYPE_VIDEO;
							     break;
						     //未知资源
						     default:
							     continue;
					     }
					     ad.imagePath = albumFile.getPath();
					     ad.imageSwitchInterval = IMAGE_CHANGE_INTERVAL;
					     adResourceList.add(ad);
				     }
				     //设置广告
				     BannerManager.getInstance().setUp(adResourceList);
			     }
		     })
		     .onCancel(result -> ToastUtils.showShort("取消广告更新"))
		     .start();
	}

	@OnClick(R.id.acb)
	public void onClick() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ad/";
		List<AdResourceModel> adResourceList = new ArrayList<>();
		AdResourceModel ad;

		ad = new AdResourceModel();
		ad.type = AdResourceModel.TYPE_IMAGE;
		ad.imagePath = path + "1.png";
		ad.imageSwitchInterval = 3000L;
		adResourceList.add(ad);

		ad = new AdResourceModel();
		ad.type = AdResourceModel.TYPE_IMAGE;
		ad.imagePath = path + "6.png";
		ad.imageSwitchInterval = 3000L;
		adResourceList.add(ad);

		BannerManager.getInstance().setUp(adResourceList);
	}
}
