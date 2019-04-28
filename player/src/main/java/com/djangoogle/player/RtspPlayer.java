package com.djangoogle.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.agesun.media.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import moe.codeest.enviews.ENDownloadView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Rtsp播放器
 * Created by Djangoogle on 2018/10/22 15:40 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
public class RtspPlayer extends StandardGSYVideoPlayer {

	private ProgressBar mLoading;

	public RtspPlayer(Context context, Boolean fullFlag) {
		super(context, fullFlag);
	}

	public RtspPlayer(Context context) {
		super(context);
	}

	public RtspPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void init(Context context) {
		super.init(context);
		mLoading = findViewById(R.id.loding);
		//针对Rtsp优化播放器配置
		initOption();
	}

	@Override
	public int getLayoutId() {
		return R.layout.rtsp_player;
	}

	@Override
	protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
		super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
		//不给触摸快进，如果需要，屏蔽下方代码即可
		mChangePosition = false;

		//不给触摸音量，如果需要，屏蔽下方代码即可
		mChangeVolume = false;

		//不给触摸亮度，如果需要，屏蔽下方代码即可
		mBrightness = false;
	}

	@Override
	protected void touchDoubleUp() {
		//不需要双击暂停
	}

	@Override
	protected void prepareVideo() {
		super.prepareVideo();
		mLoading.setVisibility(VISIBLE);
	}

	@Override
	public void startAfterPrepared() {
		super.startAfterPrepared();
		mLoading.setVisibility(GONE);
	}

	@Override
	protected void changeUiToPreparingShow() {
		Debuger.printfLog("changeUiToPreparingShow");

		setViewShowState(mTopContainer, VISIBLE);
		setViewShowState(mBottomContainer, VISIBLE);
		setViewShowState(mStartButton, INVISIBLE);
		setViewShowState(mLoadingProgressBar, VISIBLE);
		setViewShowState(mThumbImageViewLayout, VISIBLE);
		setViewShowState(mBottomProgressBar, INVISIBLE);
		setViewShowState(mLockScreen, GONE);

		if (mLoadingProgressBar instanceof ENDownloadView) {
			ENDownloadView enDownloadView = (ENDownloadView) mLoadingProgressBar;
			if (enDownloadView.getCurrentState() == ENDownloadView.STATE_PRE) {
				((ENDownloadView) mLoadingProgressBar).start();
			}
		}
	}

	@Override
	protected void changeUiToPlayingShow() {
		Debuger.printfLog("changeUiToPlayingShow");

		setViewShowState(mTopContainer, VISIBLE);
		setViewShowState(mBottomContainer, VISIBLE);
		setViewShowState(mStartButton, VISIBLE);
		setViewShowState(mLoadingProgressBar, INVISIBLE);
		setViewShowState(mThumbImageViewLayout, VISIBLE);
		setViewShowState(mBottomProgressBar, INVISIBLE);
		setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

		if (mLoadingProgressBar instanceof ENDownloadView) {
			((ENDownloadView) mLoadingProgressBar).reset();
		}
		updateStartImage();
	}

	/**
	 * 针对Rtsp优化播放器配置
	 */
	private void initOption() {
		VideoOptionModel videoOptionModel;
		List<VideoOptionModel> list = new ArrayList<>();
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//硬解码：1、打开，0、关闭
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 0);//软解码：1、打开，0、关闭
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video");//根据媒体类型来配置
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 1000);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);//无限读
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 30);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 1316);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 3);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "100");
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 8);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "auto_convert", 0);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "vol", 0);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "stimeout", 5000000);
		list.add(videoOptionModel);
		videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3000);
		list.add(videoOptionModel);
		GSYVideoManager.instance().setOptionModelList(list);
		//设置日志级别
		IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_WARN);
		//设置静音
		GSYVideoManager.instance().setNeedMute(true);
	}
}