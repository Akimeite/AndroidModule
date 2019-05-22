package com.djangoogle.arcsoft2x.model;

import java.io.Serializable;

/**
 * 人脸特征数组实体类
 * Created by Djangoogle on 2018/12/21 17:01 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
public class FaceFeatureModel implements Serializable {

	private static final long serialVersionUID = 8016552294968862729L;

	private byte[] featureData;

	public FaceFeatureModel() {}

	public FaceFeatureModel(byte[] featureData) {
		this.featureData = featureData;
	}

	public byte[] getFeatureData() {
		return featureData;
	}

	public void setFeatureData(byte[] featureData) {
		this.featureData = featureData;
	}
}
