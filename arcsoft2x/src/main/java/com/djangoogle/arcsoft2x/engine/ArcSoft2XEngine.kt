package com.djangoogle.arcsoft2x.engine

import android.content.Context
import com.arcsoft.face.*
import com.blankj.utilcode.util.LogUtils
import com.djangoogle.arcsoft2x.model.FaceInfoResult
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

	//图片引擎初始化属性
	private const val IMAGE_ENGINE_MASK = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_FACE_RECOGNITION

	//视频引擎初始化属性
	private const val VIDEO_ENGINE_MASK = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_FACE_RECOGNITION or FaceEngine.ASF_LIVENESS

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
			IMAGE_ENGINE_MASK
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
			VIDEO_ENGINE_MASK
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
	 * 处理图片数据
	 *
	 * @param faceEngine 算法引擎
	 * @param data       流数据
	 * @param width      宽度
	 * @param height     高度
	 * @return 人脸信息
	 */
	fun processImage(faceEngine: FaceEngine, data: ByteArray, width: Int, height: Int): FaceInfoResult {
		val faceInfoList = ArrayList<FaceInfo>()
		var code = faceEngine.detectFaces(data, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList)
		//检测人脸
		if (ErrorInfo.MOK != code) {
			return FaceInfoResult(code, "检测人脸失败", false, null)
		}
		if (faceInfoList.isEmpty()) {
			return FaceInfoResult(code, "未检测到人脸", false, null)
		}
		//检测人脸属性
		code = faceEngine.process(data, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList, FaceEngine.ASF_NONE)
		return if (ErrorInfo.MOK != code) {
			FaceInfoResult(code, "检测人脸属性失败", false, null)
		} else {
			if (faceInfoList.isEmpty()) {
				FaceInfoResult(code, "未检测到人脸属性", false, null)
			} else {
				FaceInfoResult(code, "检测人脸属性成功", false, faceInfoList[0])
			}
		}
	}

	/**
	 * 处理视频数据
	 *
	 * @param faceEngine 算法引擎
	 * @param data       流数据
	 * @param width      宽度
	 * @param height     高度
	 * @param liveness   是否检测活体
	 * @return 人脸信息
	 */
	fun processVideo(faceEngine: FaceEngine, data: ByteArray, width: Int, height: Int, liveness: Boolean): FaceInfoResult {
		val faceInfoList = ArrayList<FaceInfo>()
		var code = faceEngine.detectFaces(data, width, height, FaceEngine.CP_PAF_NV21, faceInfoList)
		//检测人脸
		if (ErrorInfo.MOK != code) {
			return FaceInfoResult(code, "检测人脸失败", false, null)
		}
		if (faceInfoList.isEmpty()) {
			return FaceInfoResult(code, "未检测到人脸", false, null)
		}
		//检测人脸属性
		code = faceEngine.process(
			data,
			width,
			height,
			FaceEngine.CP_PAF_NV21,
			faceInfoList,
			if (liveness) FaceEngine.ASF_LIVENESS else FaceEngine.ASF_NONE
		)
		if (ErrorInfo.MOK != code) {
			return FaceInfoResult(code, "检测人脸属性失败", false, null)
		}
		return if (faceInfoList.isEmpty()) {
			FaceInfoResult(code, "未检测到人脸属性", false, null)
		} else {
			if (liveness) {
				//活体检测
				val livenessInfoList = ArrayList<LivenessInfo>()
				val livenessCode = faceEngine.getLiveness(livenessInfoList)
				if (ErrorInfo.MOK != livenessCode) {
					FaceInfoResult(livenessCode, "检测人脸属性失败", false, null)
				} else {
					if (livenessInfoList.isEmpty()) {
						FaceInfoResult(code, "未检测到活体信息", false, null)
					} else {
						FaceInfoResult(code, "检测人脸属性成功", LivenessInfo.ALIVE == livenessInfoList[0].liveness, faceInfoList[0])
					}
				}
			} else {
				FaceInfoResult(code, "检测人脸属性成功", false, faceInfoList[0])
			}
		}
	}

	/**
	 * 抽取图片特征数组
	 *
	 * @param faceEngine 算法引擎
	 * @param data       帧数据
	 * @param width      宽度
	 * @param height     高度
	 * @param faceInfo   人脸信息
	 * @return 特征数组
	 */
	fun extractImageFeature(faceEngine: FaceEngine, data: ByteArray, width: Int, height: Int, faceInfo: FaceInfo): ByteArray? {
		val faceFeature = FaceFeature()
		val extractFaceFeatureCode = faceEngine.extractFaceFeature(data, width, height, FaceEngine.CP_PAF_BGR24, faceInfo, faceFeature)
		return if (ErrorInfo.MOK != extractFaceFeatureCode) null else faceFeature.featureData
	}

	/**
	 * 抽取视频特征数组
	 *
	 * @param faceEngine 算法引擎
	 * @param data       帧数据
	 * @param width      宽度
	 * @param height     高度
	 * @param faceInfo   人脸信息
	 * @return 特征数组
	 */
	fun extractVideoFeature(faceEngine: FaceEngine, data: ByteArray, width: Int, height: Int, faceInfo: FaceInfo): ByteArray? {
		val faceFeature = FaceFeature()
		val extractFaceFeatureCode = faceEngine.extractFaceFeature(data, width, height, FaceEngine.CP_PAF_NV21, faceInfo, faceFeature)
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
