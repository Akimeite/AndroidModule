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
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.djangoogle.banner.R;
import com.djangoogle.banner.event.PlayNextAdEvent;
import com.djangoogle.banner.model.AdResourceModel;
import com.djangoogle.player.impl.OnPlayListener;
import com.djangoogle.player.manager.AdPlayerManager;

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
					AdPlayerManager.getInstance().play();
					break;

				case AdResourceModel.TYPE_MIX:
					currentType = AdResourceModel.TYPE_MIX;
					//开始播放视频
					AdPlayerManager.getInstance().play();
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
				bannerViewHolder.acivBannerVideo.setVisibility(View.INVISIBLE);
				//加载广告图片
				loadImageAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder.acivBannerImage);
				break;

			//视频
			case AdResourceModel.TYPE_VIDEO:
				//设置缩略图和视频属性
				ConstraintLayout.LayoutParams singleVideoParams =
						new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
								ConstraintLayout.LayoutParams.MATCH_PARENT);
				bannerViewHolder.acivBannerVideo.setLayoutParams(singleVideoParams);
				bannerViewHolder.acivBannerVideo.setScaleType(ImageView.ScaleType.FIT_XY);
				bannerViewHolder.svBannerVideo.setLayoutParams(singleVideoParams);
				bannerViewHolder.acivBannerVideo.setVisibility(View.VISIBLE);
				bannerViewHolder.acivBannerImage.setVisibility(View.INVISIBLE);
				//加载视频广告
				loadVideoAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder, ScreenUtils.getScreenWidth(),
						ScreenUtils.getScreenHeight());
				break;

			//图片视频混合（此处按照16:9来计算）
			case AdResourceModel.TYPE_MIX:
				int imageId = bannerViewHolder.acivBannerImage.getId();
				int thumbnailId = bannerViewHolder.acivBannerVideo.getId();
				int videoId = bannerViewHolder.svBannerVideo.getId();
				int videoHeight = ScreenUtils.getScreenWidth() * 9 / 16;
				int imageHeight = ScreenUtils.getScreenHeight() - videoHeight;
				//设置图片宽高
				ConstraintLayout.LayoutParams mixImageParams =
						new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, imageHeight);
				bannerViewHolder.acivBannerImage.setLayoutParams(mixImageParams);
				bannerViewHolder.acivBannerImage.setScaleType(ImageView.ScaleType.FIT_XY);
				//设置缩略图和视频宽高
				ConstraintLayout.LayoutParams mixVideoParams =
						new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, videoHeight);
				bannerViewHolder.acivBannerVideo.setLayoutParams(mixVideoParams);
				bannerViewHolder.acivBannerVideo.setScaleType(ImageView.ScaleType.FIT_XY);
				bannerViewHolder.svBannerVideo.setLayoutParams(mixVideoParams);
				switch (mAdResourceList.get(bannerViewHolder.getAdapterPosition()).mixType) {
					//图片在上
					case AdResourceModel.MIX_TYPE_IMAGE_UP:
						//设置图片属性
						ConstraintSet mixImageUpConstraintSet = new ConstraintSet();
						mixImageUpConstraintSet.clone(bannerViewHolder.clBannerRoot);
						mixImageUpConstraintSet.connect(imageId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
						//设置缩略图和视频属性
						ConstraintSet mixVideoDownConstraintSet = new ConstraintSet();
						mixVideoDownConstraintSet.clone(bannerViewHolder.clBannerRoot);
						mixVideoDownConstraintSet.connect(thumbnailId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
								ConstraintSet.BOTTOM);
						mixVideoDownConstraintSet.connect(videoId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
						//应用设置
						mixImageUpConstraintSet.applyTo(bannerViewHolder.clBannerRoot);
						mixVideoDownConstraintSet.applyTo(bannerViewHolder.clBannerRoot);
						break;
					//视频在上
					case AdResourceModel.MIX_TYPE_VIDEO_UP:
						//设置缩略图和视频属性
						ConstraintSet mixVideoUpConstraintSet = new ConstraintSet();
						mixVideoUpConstraintSet.clone(bannerViewHolder.clBannerRoot);
						mixVideoUpConstraintSet.connect(thumbnailId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
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
				bannerViewHolder.acivBannerVideo.setVisibility(View.VISIBLE);
				bannerViewHolder.acivBannerImage.setVisibility(View.INVISIBLE);
				//加载图片广告
				loadImageAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder.acivBannerImage);
				//加载视频广告
				loadVideoAd(bannerViewHolder.getAdapterPosition(), bannerViewHolder, ScreenUtils.getScreenWidth(),
						videoHeight);
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
		AppCompatImageView acivBannerImage, acivBannerVideo;
		SurfaceView svBannerVideo;

		BannerViewHolder(@NonNull View itemView) {
			super(itemView);
			clBannerRoot = itemView.findViewById(R.id.clBannerRoot);
			svBannerVideo = itemView.findViewById(R.id.svBannerVideo);
			acivBannerImage = itemView.findViewById(R.id.acivBannerImage);
			acivBannerVideo = itemView.findViewById(R.id.acivBannerVideo);
		}
	}

	/**
	 * 替换数据
	 *
	 * @param adResourceList 广告资源
	 */
	public void replaceData(List<AdResourceModel> adResourceList) {
		//释放视频
		AdPlayerManager.getInstance().destroy();
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
	 * @param position         索引
	 * @param bannerViewHolder 轮播控件
	 * @param width            宽
	 * @param height           高
	 */
	private void loadVideoAd(int position, BannerViewHolder bannerViewHolder, int width, int height) {
		Glide.with(mContext)
		     .setDefaultRequestOptions(
				     new RequestOptions()
						     .frame(1000000)
						     .error(android.R.color.black)
						     .placeholder(android.R.color.black))
		     .load(mAdResourceList.get(position).videoPath)
		     .diskCacheStrategy(DiskCacheStrategy.DATA)//使用原图缓存
		     .dontAnimate()//取消动画
		     .into(bannerViewHolder.acivBannerVideo);
		String videoPath = mAdResourceList.get(position).videoPath;
		LogUtils.iTag("videoPath", "视频地址: " + videoPath);
//		AdPlayerManager.getInstance().destroy();
		AdPlayerManager.getInstance().initialize(mContext, bannerViewHolder.svBannerVideo);
		AdPlayerManager.getInstance().setLocalPath(videoPath);
		AdPlayerManager.getInstance().setSize(width, height);
		AdPlayerManager.getInstance().addOnPlayListener(new OnPlayListener() {
			@Override
			public void onPlaying() {
				if (View.VISIBLE == bannerViewHolder.acivBannerVideo.getVisibility()) {
					bannerViewHolder.acivBannerVideo.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onEnded() {
				if (1 == mAdResourceList.size() && (AdResourceModel.TYPE_VIDEO | AdResourceModel.TYPE_MIX) == mAdResourceList.get(0).type) {
					//仅一条广告且包含视频时循环播放
					AdPlayerManager.getInstance().play();
				} else {//播放下一条广告
					EventBus.getDefault().post(new PlayNextAdEvent(getNextIndex(position)));
				}
			}
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
				AdPlayerManager.getInstance().resume();
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
				AdPlayerManager.getInstance().pause();
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
		AdPlayerManager.getInstance().destroy();
	}
}
