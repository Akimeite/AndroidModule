package com.djangoogle.banner.event;

import java.io.Serializable;

/**
 * 播放下一条广告实体类
 * Created by Djangoogle on 2019/03/30 18:11 with Android Studio.
 * © 2019 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
public class PlayNextAdEvent implements Serializable {

	private static final long serialVersionUID = -1448494636253135983L;

	public int index;

	public PlayNextAdEvent(int index) {
		this.index = index;
	}
}