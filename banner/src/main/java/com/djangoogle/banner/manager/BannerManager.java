package com.djangoogle.banner.manager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.djangoogle.banner.adapter.BannerAdapter;
import com.djangoogle.banner.event.PlayNextAdEvent;
import com.djangoogle.banner.model.AdResourceModel;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 轮播管理器
 * Created by Djangoogle on 2019/04/01 10:38 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
public class BannerManager {

	private static volatile BannerManager instance = null;

	public static BannerManager getInstance() {
		if (null == instance) {
			synchronized (BannerManager.class) {
				if (null == instance) {
					instance = new BannerManager();
				}
			}
		}
		return instance;
	}

	private Activity mActivity = null;
	private RecyclerView mBannerRecyclerView = null;
	private BannerAdapter mBannerAdapter = null;
	private LinearLayoutManager mLinearLayoutManager = null;

	/**
	 * 初始化
	 *
	 * @param activity     Activity对象
	 * @param recyclerView RecyclerView对象
	 */
	public void initialize(Activity activity, RecyclerView recyclerView) {
		//注册EventBus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		//设置视频渲染
		GSYVideoType.setRenderType(GSYVideoType.SUFRACE);
		//设置视频全屏拉伸显示
		GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
		//禁止滚动
		mLinearLayoutManager = new LinearLayoutManager(activity) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}

			@Override
			public boolean canScrollHorizontally() {
				return false;
			}
		};
		mActivity = activity;
		mBannerRecyclerView = recyclerView;
		mBannerRecyclerView.setLayoutManager(mLinearLayoutManager);
		mBannerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
				mBannerRecyclerView.post(() -> mBannerAdapter.setLastVisibleItemPosition(lastVisibleItemPosition));
			}
		});
	}

	/**
	 * 设置广告
	 *
	 * @param adResourceList 广告资源
	 */
	public void setUp(List<AdResourceModel> adResourceList) {
		mBannerAdapter = new BannerAdapter(mActivity);
		mBannerRecyclerView.setAdapter(mBannerAdapter);
		mBannerAdapter.replaceData(adResourceList);
	}

	/**
	 * 设置静音
	 *
	 * @param isMute 是否静音
	 */
	public void setMute(boolean isMute) {
		GSYVideoManager.instance().setNeedMute(isMute);
	}

	/**
	 * 恢复轮播
	 */
	public void resume() {
		if (null != mBannerAdapter) {
			mBannerAdapter.resume();
		}
	}

	/**
	 * 暂停轮播
	 */
	public void pause() {
		if (null != mBannerAdapter) {
			mBannerAdapter.pause();
		}
	}

	/**
	 * 释放资源
	 */
	public void destroy() {
		if (null != mBannerAdapter) {
			mBannerAdapter.destroy();
		}
		//注销EventBus
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
		mActivity = null;
	}

	/**
	 * 播放下一条广告
	 *
	 * @param playNextAdEvent 播放下一条广告实体类
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onPlayNextAdEvent(PlayNextAdEvent playNextAdEvent) {
		//播放下一条广告
		mLinearLayoutManager.scrollToPosition(playNextAdEvent.index);
	}
}
