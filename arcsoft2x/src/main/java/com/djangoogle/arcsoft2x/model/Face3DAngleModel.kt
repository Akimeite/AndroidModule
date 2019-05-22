package com.djangoogle.arcsoft2x.model

/**
 * 人脸角度实体类
 * Created by Djangoogle on 2018/12/21 09:53 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
class Face3DAngleModel {

	var yaw: Float = 0F
	var roll: Float = 0F
	var pitch: Float = 0F

	constructor()

	constructor(yaw: Float, roll: Float, pitch: Float) {
		this.yaw = yaw
		this.roll = roll
		this.pitch = pitch
	}
}
