package com.djangoogle.player.manager;

import android.content.Context;
import android.view.SurfaceView;

import com.blankj.utilcode.util.LogUtils;
import com.djangoogle.player.impl.OnPlayListener;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

/**
 * 广告播放器
 * Created by Djangoogle on 2018/10/22 15:40 with Android Studio.
 */
public class AdPlayerManager {

	private LibVLC mLibVLC;
	private IVLCVout mIVLCVout;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mSurfaceView;
	private OnPlayListener mOnPlayListener;
	private long mTotalTime = 0;
	private int mVideoWidth, mVideoHight;

	private static volatile AdPlayerManager instance = null;

	public static AdPlayerManager getInstance() {
		if (null == instance) {
			synchronized (AdPlayerManager.class) {
				if (null == instance) {
					instance = new AdPlayerManager();
				}
			}
		}
		return instance;
	}

	static {
		System.loadLibrary("vlc");
		System.loadLibrary("vlcjni");
	}

	private IVLCVout.OnNewVideoLayoutListener mOnNewVideoLayoutListener = new IVLCVout.OnNewVideoLayoutListener() {
		@Override
		public void onNewVideoLayout(IVLCVout ivlcVout, int i, int i1, int i2, int i3, int i4, int i5) {
			try {
				//获取播放时长，长宽
				mTotalTime = mMediaPlayer.getLength();
				mVideoWidth = i;
				mVideoHight = i1;
				LogUtils.i("视频尺寸: " + mVideoWidth + ":" + mVideoHight, "播放时长: " + mTotalTime);
			} catch (Exception e) {
				LogUtils.e(e);
			}
		}
	};

	private MediaPlayer.EventListener mEventListener = new MediaPlayer.EventListener() {
		@Override
		public void onEvent(MediaPlayer.Event event) {
			try {
				if (event.getTimeChanged() == 0 || mTotalTime == 0 || event.getTimeChanged() > mTotalTime) {
					return;
				}
				//开始播放
				if (Media.State.Playing == mMediaPlayer.getPlayerState()) {
					mOnPlayListener.onPlaying();
				}
				//播放结束
				if (Media.State.Ended == mMediaPlayer.getPlayerState()) {
					mMediaPlayer.stop();
					mOnPlayListener.onEnded();
				}
			} catch (Exception e) {
				LogUtils.e(e);
			}
		}
	};

	/**
	 * 初始化
	 *
	 * @param context     上下文
	 * @param surfaceView 播放器画布
	 */
	public void initialize(Context context, SurfaceView surfaceView) {
		mSurfaceView = surfaceView;
		mLibVLC = LibVLCManager.getInstance(context, null);
		mMediaPlayer = new MediaPlayer(mLibVLC);
		mIVLCVout = mMediaPlayer.getVLCVout();
		mIVLCVout.setVideoView(surfaceView);
		mIVLCVout.attachViews(mOnNewVideoLayoutListener);
	}

	/**
	 * 设置本地视频路径
	 *
	 * @param path 本地视频路径
	 */
	public void setLocalPath(String path) {
		Media media = new Media(mLibVLC, path);
		mMediaPlayer.setMedia(media);
		mMediaPlayer.setEventListener(mEventListener);
	}

	/**
	 * 设置视频尺寸
	 *
	 * @param width  宽
	 * @param height 高
	 */
	public void setSize(int width, int height) {
		mIVLCVout.setWindowSize(width, height);
		mMediaPlayer.setAspectRatio(width + ":" + height);
		mMediaPlayer.setScale(0F);
	}

	/**
	 * 开始播放
	 */
	public void play() {
		mMediaPlayer.play();
	}

	/**
	 * 添加播放完毕监听
	 *
	 * @param onPlayListener 播放完毕监听
	 */
	public void addOnPlayListener(OnPlayListener onPlayListener) {
		mOnPlayListener = onPlayListener;
	}

	/**
	 * 暂停播放
	 */
	public void pause() {
		if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			mMediaPlayer.setEventListener(null);
		}
		if (null != mIVLCVout) {
			mIVLCVout.detachViews();
		}
	}

	/**
	 * 恢复播放
	 */
	public void resume() {
		mIVLCVout.setVideoView(mSurfaceView);
		mIVLCVout.attachViews(mOnNewVideoLayoutListener);
		if (null != mMediaPlayer) {
			mMediaPlayer.setEventListener(mEventListener);
		}
	}

	/**
	 * 释放播放器
	 */
	public void destroy() {
		try {
			pause();
			if (null != mMediaPlayer) {
				mMediaPlayer.release();
			}
		} catch (Exception e) {
			LogUtils.e(e);
		}
	}
}