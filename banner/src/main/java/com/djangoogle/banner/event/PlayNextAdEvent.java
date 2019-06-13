package com.djangoogle.banner.event;

/**
 * 播放下一条广告实体类
 * Created by Djangoogle on 2019/03/30 18:11 with Android Studio.
 */
public class PlayNextAdEvent {

	public int index;

	public PlayNextAdEvent(int index) {
		this.index = index;
	}
}
