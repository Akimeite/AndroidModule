package com.djangoogle.arcsoft2x.model

import android.graphics.Rect

/**
 * 人脸信息实体类
 * Created by Djangoogle on 2018/12/21 09:52 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
class FaceInfoModel {

	var rect: Rect? = null
	var orient: Int = 0
	var age = 0
	var gender = ""
	var face3DAngle: Face3DAngleModel? = null
	var liveness = ""
	var name: String? = null

	constructor()

	constructor(faceInfo: FaceInfoModel?) {
		if (null == faceInfo) {
			this.rect = Rect()
			this.orient = 0
		} else {
			this.rect = Rect(faceInfo.rect)
			this.orient = faceInfo.orient
		}
	}
}
