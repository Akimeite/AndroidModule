package com.djangoogle.banner.manager;

import android.content.Context;

import com.djangoogle.banner.adapter.BannerAdapter;
import com.djangoogle.banner.event.PlayNextAdEvent;
import com.djangoogle.banner.model.AdResourceModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 轮播管理器
 * Created by Djangoogle on 2019/04/01 10:38 with Android Studio.
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

	private RecyclerView mBannerRecyclerView = null;
	private BannerAdapter mBannerAdapter = null;
	private LinearLayoutManager mLinearLayoutManager = null;

	/**
	 * 初始化
	 *
	 * @param context      上下文
	 * @param recyclerView RecyclerView对象
	 */
	public void initialize(Context context, RecyclerView recyclerView) {
		//注册EventBus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		//禁止滚动
		mLinearLayoutManager = new LinearLayoutManager(context) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}

			@Override
			public boolean canScrollHorizontally() {
				return false;
			}
		};
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
		mBannerAdapter = new BannerAdapter();
		mBannerRecyclerView.setAdapter(mBannerAdapter);
	}

	/**
	 * 设置广告
	 *
	 * @param adResourceList 广告资源
	 */
	public void setUp(List<AdResourceModel> adResourceList) {
		mBannerAdapter.setNewData(adResourceList);
	}

	/**
	 * 设置音量
	 *
	 * @param volume 音量
	 */
	public void setVolume(@IntRange(from = 0, to = 15) int volume) {
		mBannerAdapter.setVolume(volume);
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
