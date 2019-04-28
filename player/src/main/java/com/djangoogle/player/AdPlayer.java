package com.djangoogle.player;

import android.content.Context;
import android.util.AttributeSet;

import com.agesun.media.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * 广告播放器
 * Created by Djangoogle on 2018/10/22 15:40 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
public class AdPlayer extends StandardGSYVideoPlayer {

	public AdPlayer(Context context, Boolean fullFlag) {
		super(context, fullFlag);
	}

	public AdPlayer(Context context) {
		super(context);
	}

	public AdPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public int getLayoutId() {
		return R.layout.ad_player;
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
}