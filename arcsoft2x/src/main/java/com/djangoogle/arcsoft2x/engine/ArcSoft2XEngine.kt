package com.djangoogle.arcsoft2x.engine

import android.content.Context
import com.arcsoft.face.*
import com.blankj.utilcode.util.LogUtils
import java.util.*

/**
 * 虹软单目算法引擎
 * Created by Djangoogle on 2019/04/08 17:42 with Android Studio.
 */
object ArcSoft2XEngine {

	const val COMPARE_SCORE = 0.8f

	private val TAG = ArcSoft2XEngine::class.java.simpleName

	//人脸检测尺寸
	//用于数值化表示的最小人脸尺寸，该尺寸代表人脸尺寸相对于图片长边的占比
	//video 模式有效值范围[2,16], Image 模式有效值范围[2,32]，多数情况下推荐值为 16
	//特殊情况下可根据具体场景下进行设置
	private const val DETECT_FACE_SCALE_VAL = 16

	//人脸检测最大数量
	private const val DETECT_FACE_MAX_NUM = 1

	//引擎初始化属性
	private const val ENGINE_MASK = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_FACE_RECOGNITION or FaceEngine.ASF_LIVENESS

	/**
	 * 激活虹软2.1算法引擎
	 *
	 * @param context 上下文
	 * @param appId   APP_ID
	 * @param sdkKey  SDK_KEY
	 * @return 成功/失败
	 */
	fun active(context: Context, appId: String, sdkKey: String): Boolean {
		LogUtils.iTag(TAG, "激活虹软2.1算法引擎")
		val faceEngine = FaceEngine()
		return when (val activeCode = faceEngine.active(context, appId, sdkKey)) {
			ErrorInfo.MOK, ErrorInfo.MERR_ASF_ALREADY_ACTIVATED -> {
				LogUtils.iTag(TAG, "虹软2.1算法引擎激活成功", activeCode)
				true
			}
			else -> {
				LogUtils.eTag(TAG, "虹软2.1算法引擎激活失败", activeCode)
				false
			}
		}
	}

	/**
	 * 获取图片引擎
	 *
	 * @param context                  上下文
	 * @param detectFaceOrientPriority 人脸检测方向
	 * @return 图片引擎
	 */
	fun getImageEngine(context: Context, detectFaceOrientPriority: Int): FaceEngine? {
		val faceEngine = FaceEngine()
		val faceEngineCode = faceEngine.init(
			context,
			FaceEngine.ASF_DETECT_MODE_IMAGE,
			detectFaceOrientPriority,
			DETECT_FACE_SCALE_VAL,
			DETECT_FACE_MAX_NUM,
			ENGINE_MASK
		)
		return if (ErrorInfo.MOK == faceEngineCode) {
			val versionInfo = VersionInfo()
			faceEngine.getVersion(versionInfo)
			LogUtils.iTag(TAG, "虹软2.1算法引擎初始化成功, 版本号: $versionInfo")
			faceEngine
		} else {
			LogUtils.eTag(TAG, "虹软2.1算法引擎初始化失败, 错误码: ", faceEngineCode)
			null
		}
	}

	/**
	 * 获取视频引擎
	 *
	 * @param context                  上下文
	 * @param detectFaceOrientPriority 人脸检测方向
	 * @return 图片引擎
	 */
	fun getVideoEngine(context: Context, detectFaceOrientPriority: Int): FaceEngine? {
		val faceEngine = FaceEngine()
		val faceEngineCode = faceEngine.init(
			context,
			FaceEngine.ASF_DETECT_MODE_VIDEO,
			detectFaceOrientPriority,
			DETECT_FACE_SCALE_VAL,
			DETECT_FACE_MAX_NUM,
			ENGINE_MASK
		)
		return if (ErrorInfo.MOK == faceEngineCode) {
			val versionInfo = VersionInfo()
			faceEngine.getVersion(versionInfo)
			LogUtils.iTag(TAG, "虹软2.1算法引擎初始化成功, 版本号: $versionInfo")
			faceEngine
		} else {
			LogUtils.eTag(TAG, "虹软2.1算法引擎初始化失败, 错误码: ", faceEngineCode)
			null
		}
	}

	/**
	 * 释放引擎
	 *
	 * @param faceEngine 算法引擎
	 */
	fun release(faceEngine: FaceEngine?) {
		try {
			if (null != faceEngine) {
				val faceEngineCode = faceEngine.unInit()
				LogUtils.iTag(TAG, "释放虹软2.1算法引擎", faceEngineCode)
			}
		} catch (e: Exception) {
			LogUtils.eTag(TAG, e.message)
		}
	}

	/**
	 * 根据条件处理人脸数据
	 *
	 * @param faceEngine 算法引擎
	 * @param data       流数据
	 * @param width      宽度
	 * @param height     高度
	 * @param format     处理格式
	 * @return 人脸信息
	 */
	fun processData(faceEngine: FaceEngine, data: ByteArray, width: Int, height: Int, format: Int): FaceInfo? {
		val faceInfoList = ArrayList<FaceInfo>()
		var code = faceEngine.detectFaces(data, width, height, format, faceInfoList)
		//检测人脸
		if (ErrorInfo.MOK != code || faceInfoList.isEmpty()) {
			LogUtils.eTag(TAG, "未检测到人脸", code)
			return null
		} else {
			LogUtils.eTag(TAG, "检测到人脸", code)
		}
		//检测人脸属性
		code = faceEngine.process(data, width, height, format, faceInfoList, FaceEngine.ASF_LIVENESS)
		return if (ErrorInfo.MOK == code) {
			if (faceInfoList.isNotEmpty()) {
				LogUtils.iTag(TAG, "检测人脸属性成功", code)
				faceInfoList[0]
			} else {
				LogUtils.eTag(TAG, "未检测到人脸属性", code)
				null
			}
		} else {
			LogUtils.eTag(TAG, "检测人脸属性失败", code)
			null
		}
	}

	/**
	 * 抽取特征数组
	 *
	 * @param faceEngine 算法引擎
	 * @param data       帧数据
	 * @param width      宽度
	 * @param height     高度
	 * @param format     处理格式
	 * @param faceInfo   人脸信息
	 * @return 特征数组
	 */
	fun extractFaceFeature(faceEngine: FaceEngine, data: ByteArray, width: Int, height: Int, format: Int, faceInfo: FaceInfo): ByteArray? {
		val faceFeature = FaceFeature()
		val extractFaceFeatureCode = faceEngine.extractFaceFeature(data, width, height, format, faceInfo, faceFeature)
		return if (ErrorInfo.MOK != extractFaceFeatureCode) null else faceFeature.featureData
	}

	/**
	 * 根据特征数组比对人脸
	 *
	 * @param faceEngine   算法引擎
	 * @param faceFeature1 特征数组1
	 * @param faceFeature2 特征数组2
	 * @return 比对分值
	 */
	fun compareFaceFeature(faceEngine: FaceEngine, faceFeature1: ByteArray, faceFeature2: ByteArray): Float {
		val faceSimilar = FaceSimilar()
		faceEngine.compareFaceFeature(FaceFeature(faceFeature1), FaceFeature(faceFeature2), faceSimilar)
		return faceSimilar.score
	}
}
