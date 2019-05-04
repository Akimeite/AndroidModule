package com.djangoogle.player.ad;

import android.content.Context;
import android.util.AttributeSet;

import org.videolan.libvlc.media.VideoView;

/**
 * 广告播放器
 * Created by Djangoogle on 2018/10/22 15:40 with Android Studio.
 */
public class AdPlayer extends VideoView {

	public AdPlayer(Context context) {
		super(context);
	}

	public AdPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AdPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	static {
		System.loadLibrary("vlc");
		System.loadLibrary("vlcjni");
	}
}