package com.djangoogle.banner.model;

import java.io.Serializable;

/**
 * 广告资源实体类
 * Created by Djangoogle on 2019/03/28 10:19 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
public class AdResourceModel implements Serializable {

	public static final int TYPE_IMAGE = 0;
	public static final int TYPE_VIDEO = 1;
	public static final int TYPE_MIX = 2;

	public static final int MIX_TYPE_IMAGE_UP = 0;
	public static final int MIX_TYPE_VIDEO_UP = 1;

	private static final long serialVersionUID = 3964445966353004867L;

	//广告类型 0、图片 1、视频 2、图片视频混合
	public int type;
	//图片视频混合位置 0、图片在上 1、视频在上
	public int mixType;
	//图片广告地址
	public String imagePath;
	//图片切换间隔
	public long imageSwitchInterval;
	//视频广告地址
	public String videoPath;
}
