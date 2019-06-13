package com.djangoogle.player.manager;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.djangoogle.player.impl.OnPlayListener;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import androidx.annotation.IntRange;

/**
 * VLC播放器
 * Created by Djangoogle on 2018/10/22 15:40 with Android Studio.
 */
public class VLCManager {

	private static final String TAG = VLCManager.class.getSimpleName();

	private static volatile VLCManager instance = null;

	public static VLCManager getInstance() {
		if (null == instance) {
			synchronized (VLCManager.class) {
				if (null == instance) {
					instance = new VLCManager();
				}
			}
		}
		return instance;
	}

	static {
		System.loadLibrary("vlc");
		System.loadLibrary("vlcjni");
	}

	private LibVLC mLibVLC;
	private IVLCVout mIVLCVout;
	private Media mMedia;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mSurfaceView;
	private OnPlayListener mOnPlayListener;
	private long mTotalTime = 0;

	private IVLCVout.OnNewVideoLayoutListener mOnNewVideoLayoutListener = new IVLCVout.OnNewVideoLayoutListener() {
		@Override
		public void onNewVideoLayout(IVLCVout ivlcVout, int i, int i1, int i2, int i3, int i4, int i5) {
			try {
				//获取播放时长，长宽
				mTotalTime = mMediaPlayer.getLength();
				Log.i(TAG, "视频尺寸: " + i + "×" + i1 + ", 播放时长: " + mTotalTime);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e.getCause());
			}
		}
	};

	private MediaPlayer.EventListener mEventListener = new MediaPlayer.EventListener() {
		@Override
		public void onEvent(MediaPlayer.Event event) {
			try {
				if (0 == event.getTimeChanged() || 0 == mTotalTime || event.getTimeChanged() > mTotalTime) {
					return;
				}
				//开始播放
				if (Media.State.Playing == mMediaPlayer.getPlayerState() && null != mOnPlayListener) {
					mOnPlayListener.onPlaying();
					return;
				}
				//播放结束
				if (Media.State.Ended == mMediaPlayer.getPlayerState() && null != mOnPlayListener) {
					mOnPlayListener.onEnded();
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e.getCause());
			}
		}
	};

	/**
	 * 初始化
	 *
	 * @param context 上下文
	 */
	public void initialize(Context context) {
		mLibVLC = LibVLCManager.getInstance(context, null);
		mMediaPlayer = new MediaPlayer(mLibVLC);
		mIVLCVout = mMediaPlayer.getVLCVout();
	}

	/**
	 * 设置画布
	 *
	 * @param surfaceView 播放器画布
	 */
	public void setView(SurfaceView surfaceView) {
		mSurfaceView = surfaceView;
		mIVLCVout.setVideoView(surfaceView);
		mIVLCVout.attachViews(mOnNewVideoLayoutListener);
	}

	/**
	 * 设置本地视频路径
	 *
	 * @param path 本地视频路径
	 */
	public void setLocalPath(String path) {
		mMedia = new Media(mLibVLC, path);
		mMediaPlayer.setMedia(mMedia);
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
	 * 添加播放完毕监听
	 *
	 * @param onPlayListener 播放完毕监听
	 */
	public void addOnPlayListener(OnPlayListener onPlayListener) {
		mOnPlayListener = onPlayListener;
	}

	/**
	 * 设置音量
	 *
	 * @param volume 音量
	 */
	public void setVolume(@IntRange(from = 0, to = 15) int volume) {
		if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
			mMediaPlayer.setVolume(volume);
		}
	}

	/**
	 * 开始播放
	 */
	public void play() {
		if (null != mMediaPlayer) {
			mMediaPlayer.play();
		}
	}

	/**
	 * 暂停播放
	 */
	public void pause() {
		if (null != mMediaPlayer) {
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
		if (null != mIVLCVout) {
			mIVLCVout.setVideoView(mSurfaceView);
			mIVLCVout.attachViews(mOnNewVideoLayoutListener);
		}
		if (null != mMediaPlayer) {
			mMediaPlayer.setEventListener(mEventListener);
		}
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		if (null != mMediaPlayer) {
			mMediaPlayer.stop();
			mMediaPlayer.setEventListener(null);
		}
		if (null != mIVLCVout) {
			mIVLCVout.detachViews();
		}
	}

	/**
	 * 释放播放器
	 */
	public void destroy() {
		try {
			//停止播放
			stop();
			if (null != mMediaPlayer) {
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			if (null != mMedia) {
				mMedia.release();
				mMedia = null;
			}
			if (null != mLibVLC) {
				mLibVLC.release();
				mLibVLC = null;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e.getCause());
		}
	}
}