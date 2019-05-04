//package com.djangoogle.banner.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.WindowManager;
//
//import com.djangoogle.banner.impl.OnVideoCompletionListener;
//import com.djangoogle.player.AdPlayer;
//import com.shuyu.gsyvideoplayer.GSYVideoManager;
//import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
//import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
//import com.shuyu.gsyvideoplayer.utils.Debuger;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import moe.codeest.enviews.ENDownloadView;
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//
//public class BannerAdPlayer extends AdPlayer implements OnVideoCompletionListener {
//
//	private OnVideoCompletionListener mOnVideoCompletionListener;
//
//	public BannerAdPlayer(Context context, Boolean fullFlag) {
//		super(context, fullFlag);
//	}
//
//	public BannerAdPlayer(Context context) {
//		super(context);
//	}
//
//	public BannerAdPlayer(Context context, AttributeSet attrs) {
//		super(context, attrs);
//	}
//
//	@Override
//	protected void init(Context context) {
//		super.init(context);
//		//优化播放器配置
//		initOption();
//	}
//
//	@Override
//	protected void changeUiToPreparingShow() {
//		Debuger.printfLog("changeUiToPreparingShow");
//
//		setViewShowState(mTopContainer, VISIBLE);
//		setViewShowState(mBottomContainer, VISIBLE);
//		setViewShowState(mStartButton, INVISIBLE);
//		setViewShowState(mLoadingProgressBar, VISIBLE);
//		setViewShowState(mThumbImageViewLayout, VISIBLE);
//		setViewShowState(mBottomProgressBar, INVISIBLE);
//		setViewShowState(mLockScreen, GONE);
//
//		if (mLoadingProgressBar instanceof ENDownloadView) {
//			ENDownloadView enDownloadView = (ENDownloadView) mLoadingProgressBar;
//			if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
//				((ENDownloadView) mLoadingProgressBar).start();
//			}
//		}
//	}
//
//	@Override
//	protected void changeUiToPlayingShow() {
//		Debuger.printfLog("changeUiToPlayingShow");
//
//		setViewShowState(mTopContainer, VISIBLE);
//		setViewShowState(mBottomContainer, VISIBLE);
//		setViewShowState(mStartButton, VISIBLE);
//		setViewShowState(mLoadingProgressBar, INVISIBLE);
//		setViewShowState(mThumbImageViewLayout, VISIBLE);
//		setViewShowState(mBottomProgressBar, INVISIBLE);
//		setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
//
//		if (mLoadingProgressBar instanceof ENDownloadView) {
//			((ENDownloadView) mLoadingProgressBar).reset();
//		}
//		updateStartImage();
//	}
//
//	/**
//	 * 优化播放器配置
//	 */
//	private void initOption() {
//		VideoOptionModel videoOptionModel;
//		List<VideoOptionModel> list = new ArrayList<>();
//		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);//硬解码：1、打开，0、关闭
//		list.add(videoOptionModel);
//		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 1);//软解码：1、打开，0、关闭
//		list.add(videoOptionModel);
//		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 50);//降低倍数
//		list.add(videoOptionModel);
//		GSYVideoManager.instance().setOptionModelList(list);
//		//设置日志级别
//		IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_WARN);
//	}
//
//	/**
//	 * 设置播放完毕监听
//	 *
//	 * @param listener
//	 */
//	public void setOnCompletionListener(OnVideoCompletionListener listener) {
//		mOnVideoCompletionListener = listener;
//	}
//
//	@Override
//	public void onAutoCompletion() {
//		super.onAutoCompletion();
//		if (null != mOnVideoCompletionListener) {
//			mOnVideoCompletionListener.onCompletion();
//		}
//	}
//
//	@Override
//	public void onCompletion() {
//		cancelDismissControlViewTimer();
//
//		mSaveChangeViewTIme = 0;
//		mCurrentPosition = 0;
//
//		if (mTextureViewContainer.getChildCount() > 0) {
//			mTextureViewContainer.removeAllViews();
//		}
//
//		if (!mIfCurrentIsFullscreen) {
//			getGSYVideoManager().setListener(null);
//			getGSYVideoManager().setLastListener(null);
//		}
//		getGSYVideoManager().setCurrentVideoHeight(0);
//		getGSYVideoManager().setCurrentVideoWidth(0);
//
//		mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
//		((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//		releaseNetWorkState();
//	}
//}
