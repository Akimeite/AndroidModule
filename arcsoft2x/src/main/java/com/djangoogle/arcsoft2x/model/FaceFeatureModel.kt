package com.djangoogle.arcsoft2x.model

/**
 * 人脸特征数组实体类
 * Created by Djangoogle on 2018/12/21 17:01 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
class FaceFeatureModel {

	var featureData: ByteArray? = null

	constructor()

	constructor(featureData: ByteArray) {
		this.featureData = featureData
	}
}
