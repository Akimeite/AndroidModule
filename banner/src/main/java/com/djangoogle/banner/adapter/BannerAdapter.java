package com.djangoogle.banner.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.agesun.banner.R;
import com.djangoogle.banner.event.PlayNextAdEvent;
import com.djangoogle.banner.model.AdResourceModel;
import com.djangoogle.banner.widget.BannerAdPlayer;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Djangoogle on 2019/03/29 21:51 with Android Studio.
 * © 2019 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

	private Context mContext;
	private List<AdResourceModel> mAdResourceList = new ArrayList<>();
	private int mLastVisibleItemPosition = -1, mLastVisibleItemPositionCount = 0;
	private Disposable mAdPlayDisposable = null;
	private int currentType = -1;

	public BannerAdapter(Context context) {
		mContext = context;
	}

	@NonNull
	@Override
	public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner, viewGroup, false);
		return new BannerViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull List<Object> payloads) {
		if (payloads.isEmpty()) {
			onBindViewHolder(holder, position);
		} else {
			switch (mAdResourceList.get(holder.getAdapterPosition()).type) {
				case AdResourceModel.TYPE_IMAGE:
					currentType = AdResourceModel.TYPE_IMAGE;
					//当前Item处于最上层
					if (mLastVisibleItemPosition == holder.getAdapterPosition()) {
						//开始图片广告计时任务
						startImageAdTimerTask();
					}
					break;

				case AdResourceModel.TYPE_VIDEO:
					currentType = AdResourceModel.TYPE_VIDEO;
					//开始播放视频
					holder.bapBannerVideo.startPlayLogic();
					break;

				case AdResourceModel.TYPE_MIX:
					currentType = AdResourceModel.TYPE_MIX;
					//开始播放视频
					holder.bapBannerVideo.startPlayLogic();
					break;

				default:
					currentType = -1;
					break;
			}
		}
	}

	@Override
	public void onBindViewHolder(@NonNull BannerViewHolder bannerViewHolder, int i) {
		switch (mAdResourceList.get(bannerViewHolder.getAdapterPosition()).type) {
			//图片
			case AdResourceModel.TYPE_IMAGE:
				//设置图片属性
				ConstraintLayout.LayoutParams singleImageParams =
						new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
								ConstraintLayout.LayoutParams.MATCH_PARENT);
				bannerViewHolder.acivBannerImage.setLayoutParams(singleImageParams);
				bannerViewHolder.acivBannerImage.setScaleType(ImageView.ScaleType.FIT_XY);
				bannerViewHolder.acivBannerImage.setVisibility(View.VISIBLE);
				bannerViewHolder.bapBannerVideo.setVisibility(View.INVISIBLE);
				//加载广告图片
				loadImageAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder.acivBannerImage);
				break;

			//视频
			case AdResourceModel.TYPE_VIDEO:
				//设置视频属性
				ConstraintLayout.LayoutParams singleVideoParams =
						new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
								ConstraintLayout.LayoutParams.MATCH_PARENT);
				bannerViewHolder.bapBannerVideo.setLayoutParams(singleVideoParams);
				bannerViewHolder.bapBannerVideo.setVisibility(View.VISIBLE);
				bannerViewHolder.acivBannerImage.setVisibility(View.INVISIBLE);
				//加载视频广告
				loadVideoAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder.bapBannerVideo);
				break;

			//图片视频混合（此处按照16:9来计算）
			case AdResourceModel.TYPE_MIX:
				int imageId = bannerViewHolder.acivBannerImage.getId();
				int videoId = bannerViewHolder.bapBannerVideo.getId();
				int videoHeight = ScreenUtils.getScreenWidth() * 9 / 16;
				int imageHeight = ScreenUtils.getScreenHeight() - videoHeight;
				//设置图片宽高
				ConstraintLayout.LayoutParams mixImageParams =
						new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, imageHeight);
				bannerViewHolder.acivBannerImage.setLayoutParams(mixImageParams);
				bannerViewHolder.acivBannerImage.setScaleType(ImageView.ScaleType.FIT_XY);
				//设置视频宽高
				ConstraintLayout.LayoutParams mixVideoParams =
						new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, videoHeight);
				bannerViewHolder.bapBannerVideo.setLayoutParams(mixVideoParams);
				switch (mAdResourceList.get(bannerViewHolder.getAdapterPosition()).mixType) {
					//图片在上
					case AdResourceModel.MIX_TYPE_IMAGE_UP:
						//设置图片属性
						ConstraintSet mixImageUpConstraintSet = new ConstraintSet();
						mixImageUpConstraintSet.clone(bannerViewHolder.clBannerRoot);
						mixImageUpConstraintSet.connect(imageId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
						//设置视频属性
						ConstraintSet mixVideoDownConstraintSet = new ConstraintSet();
						mixVideoDownConstraintSet.clone(bannerViewHolder.clBannerRoot);
						mixVideoDownConstraintSet.connect(videoId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
						//应用设置
						mixImageUpConstraintSet.applyTo(bannerViewHolder.clBannerRoot);
						mixVideoDownConstraintSet.applyTo(bannerViewHolder.clBannerRoot);
						break;
					//视频在上
					case AdResourceModel.MIX_TYPE_VIDEO_UP:
						//设置视频属性
						ConstraintSet mixVideoUpConstraintSet = new ConstraintSet();
						mixVideoUpConstraintSet.clone(bannerViewHolder.clBannerRoot);
						mixVideoUpConstraintSet.connect(videoId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
						//设置图片属性
						ConstraintSet mixImageDownConstraintSet = new ConstraintSet();
						mixImageDownConstraintSet.clone(bannerViewHolder.clBannerRoot);
						mixImageDownConstraintSet.connect(imageId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
						//应用设置
						mixVideoUpConstraintSet.applyTo(bannerViewHolder.clBannerRoot);
						mixImageDownConstraintSet.applyTo(bannerViewHolder.clBannerRoot);
						break;
					//未知类型
					default:
						//播放下一条广告
						EventBus.getDefault().post(new PlayNextAdEvent(getNextIndex(bannerViewHolder.getAdapterPosition())));
						return;
				}
				bannerViewHolder.acivBannerImage.setVisibility(View.VISIBLE);
				bannerViewHolder.bapBannerVideo.setVisibility(View.VISIBLE);
				//加载图片广告
				loadImageAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder.acivBannerImage);
				//加载视频广告
				loadVideoAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder.bapBannerVideo);
				break;

			default:
				break;
		}
	}

	@Override
	public int getItemCount() {
		return mAdResourceList.size();
	}

	class BannerViewHolder extends RecyclerView.ViewHolder {

		ConstraintLayout clBannerRoot;
		AppCompatImageView acivBannerImage;
		BannerAdPlayer bapBannerVideo;

		BannerViewHolder(@NonNull View itemView) {
			super(itemView);
			clBannerRoot = itemView.findViewById(R.id.clBannerRoot);
			acivBannerImage = itemView.findViewById(R.id.acivBannerImage);
			bapBannerVideo = itemView.findViewById(R.id.bapBannerVideo);
		}
	}

	/**
	 * 替换数据
	 *
	 * @param adResourceList 广告资源
	 */
	public void replaceData(List<AdResourceModel> adResourceList) {
		//释放视频
		GSYVideoManager.releaseAllVideos();
		mAdResourceList.clear();
		mAdResourceList.addAll(adResourceList);
		notifyDataSetChanged();
	}

	/**
	 * 设置当前Item索引
	 *
	 * @param lastVisibleItemPosition 当前Item索引
	 */
	public void setLastVisibleItemPosition(int lastVisibleItemPosition) {
		if (mLastVisibleItemPosition != lastVisibleItemPosition) {
			mLastVisibleItemPosition = lastVisibleItemPosition;
			mLastVisibleItemPositionCount = 1;
		} else {
			mLastVisibleItemPositionCount++;
		}
		//过滤重复Item
		if (mLastVisibleItemPositionCount > 1) {
			return;
		}
		//局部刷新
		notifyItemChanged(lastVisibleItemPosition, "lastVisibleItemPosition");
	}

	/**
	 * 获取下一个广告的索引
	 *
	 * @param currentPosition 当前位置
	 */
	private int getNextIndex(int currentPosition) {
		int index;
		//索引大于等于列表长度时归零
		if (currentPosition + 1 >= mAdResourceList.size()) {
			index = 0;
		} else {
			//索引自增
			index = currentPosition + 1;
		}
		return index;
	}

	/**
	 * 开始图片广告计时任务
	 */
	private void startImageAdTimerTask() {
		Observable.timer(mAdResourceList.get(mLastVisibleItemPosition).imageSwitchInterval, TimeUnit.MILLISECONDS)
		          .subscribeOn(AndroidSchedulers.mainThread())
		          .subscribe(new Observer<Long>() {
			          @Override
			          public void onSubscribe(Disposable d) {
				          mAdPlayDisposable = d;
			          }

			          @Override
			          public void onNext(Long aLong) {
				          //播放下一条广告
				          EventBus.getDefault().post(new PlayNextAdEvent(getNextIndex(mLastVisibleItemPosition)));
			          }

			          @Override
			          public void onError(Throwable e) {}

			          @Override
			          public void onComplete() {}
		          });
	}

	/**
	 * 加载图片广告
	 *
	 * @param position           索引
	 * @param appCompatImageView 图片控件
	 */
	private void loadImageAd(int position, AppCompatImageView appCompatImageView) {
		String imagePath = mAdResourceList.get(position).imagePath;
		LogUtils.iTag("imagePath", "图片地址: " + imagePath);
		Glide.with(mContext)
		     .load(imagePath)
		     .listener(new RequestListener<Drawable>() {
			     //图片加载失败
			     @Override
			     public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
				     //播放下一条广告
				     EventBus.getDefault().post(new PlayNextAdEvent(getNextIndex(position)));
				     return false;
			     }

			     @Override
			     public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource,
			                                    boolean isFirstResource) {
				     return false;
			     }
		     })
		     .diskCacheStrategy(DiskCacheStrategy.DATA)
		     .dontAnimate()
		     .into(appCompatImageView);
	}

	/**
	 * 加载视频广告
	 *
	 * @param position       索引
	 * @param bannerAdPlayer 视频控件
	 */
	private void loadVideoAd(int position, BannerAdPlayer bannerAdPlayer) {
		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		Glide.with(mContext)
		     .setDefaultRequestOptions(
				     new RequestOptions()
						     .frame(1000000)
						     .error(android.R.color.black)
						     .placeholder(android.R.color.black))
		     .load(mAdResourceList.get(position).videoPath)
		     .diskCacheStrategy(DiskCacheStrategy.DATA)//使用原图缓存
		     .dontAnimate()//取消动画
		     .into(imageView);
		bannerAdPlayer.setThumbImageView(imageView);
		String videoPath = mAdResourceList.get(position).videoPath;
		LogUtils.iTag("videoPath", "视频地址: " + videoPath);
		bannerAdPlayer.setUp(videoPath, true, "");
		bannerAdPlayer.setLooping(1 == mAdResourceList.size() && AdResourceModel.TYPE_VIDEO == mAdResourceList.get(0).type);
		bannerAdPlayer.setOnCompletionListener(() -> {
			//播放下一条广告
			EventBus.getDefault().post(new PlayNextAdEvent(getNextIndex(position)));
		});
	}

	/**
	 * 恢复轮播
	 */
	public void resume() {
		switch (currentType) {
			case AdResourceModel.TYPE_IMAGE:
				//重新开始播放当前图片广告
				if (mLastVisibleItemPosition >= 0) {
					//开始图片广告计时任务
					startImageAdTimerTask();
				}
				break;

			case AdResourceModel.TYPE_VIDEO:
			case AdResourceModel.TYPE_MIX:
				GSYVideoManager.onResume();
				break;

			default:
				break;
		}
	}

	/**
	 * 暂停轮播
	 */
	public void pause() {
		switch (currentType) {
			case AdResourceModel.TYPE_IMAGE:
				if (null != mAdPlayDisposable && !mAdPlayDisposable.isDisposed()) {
					mAdPlayDisposable.dispose();
				}
				break;

			case AdResourceModel.TYPE_VIDEO:
			case AdResourceModel.TYPE_MIX:
				GSYVideoManager.onPause();
				break;

			default:
				break;
		}
	}

	/**
	 * 释放资源
	 */
	public void destroy() {
		if (null != mAdPlayDisposable && !mAdPlayDisposable.isDisposed()) {
			mAdPlayDisposable.dispose();
		}
		GSYVideoManager.releaseAllVideos();
	}
}
