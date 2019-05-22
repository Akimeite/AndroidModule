package com.djangoogle.arcsoft2x.constants

/**
 * 检测人脸条件
 * Created by Djangoogle on 2018/12/20 10:24 with Android Studio.
 */
class DetectCondition {

	companion object {

		//无任何条件（初始化时）
		const val INIT_NONE = "initNone"
		//无任何条件（人脸识别时）
		const val PROCESS_NONE = "processNone"
		//人脸检测
		const val FACE_DETECT = "faceDetect"
		//人脸识别
		const val FACE_RECOGNITION = "faceRecognition"
		//年龄检测
		const val AGE = "age"
		//性别检测
		const val GENDER = "gender"
		//人脸三维角度检测
		const val FACE3DANGLE = "face3DAngle"
		//活体检测
		const val LIVENESS = "liveness"
	}
}
