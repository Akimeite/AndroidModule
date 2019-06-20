package com.djangoogle.arcsoft2x.engine

import android.content.Context
import android.util.Log
import com.arcsoft.face.*
import com.djangoogle.arcsoft2x.model.FaceInfoResult
import java.util.*

/**
 * 虹软单目算法引擎
 * Created by Djangoogle on 2019/04/08 17:42 with Android Studio.
 */
object ArcSoft2XEngine {

	private val TAG = ArcSoft2XEngine::class.java.simpleName

	//人脸检测尺寸
	//用于数值化表示的最小人脸尺寸，该尺寸代表人脸尺寸相对于图片长边的占比
	//video 模式有效值范围[2,16], Image 模式有效值范围[2,32]，多数情况下推荐值为 16
	//特殊情况下可根据具体场景下进行设置
	private const val IMAGE_DETECT_FACE_SCALE_VAL = 32
	private const val VIDEO_DETECT_FACE_SCALE_VAL = 16

	//人脸检测最大数量
	private const val DETECT_FACE_MAX_NUM = 1

	//图片引擎初始化属性
	private const val IMAGE_ENGINE_MASK = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_FACE_RECOGNITION or FaceEngine.ASF_NONE

	//视频引擎初始化属性
	private const val VIDEO_ENGINE_MASK = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_FACE_RECOGNITION or FaceEngine.ASF_LIVENESS

	/**
	 * 激活虹软2.X算法引擎
	 *
	 * @param context 上下文
	 * @param appId   APP_ID
	 * @param sdkKey  SDK_KEY
	 * @return 成功/失败
	 */
	fun active(context: Context, appId: String, sdkKey: String): Boolean {
		Log.i(TAG, "开始激活虹软2.X算法引擎")
		val faceEngine = FaceEngine()
		return when (val activeCode = faceEngine.active(context, appId, sdkKey)) {
			ErrorInfo.MOK, ErrorInfo.MERR_ASF_ALREADY_ACTIVATED -> {
				Log.i(TAG, "虹软2.X算法引擎激活成功, 返回码: $activeCode")
				true
			}
			else -> {
				Log.i(TAG, "虹软2.X算法引擎激活失败, 返回码: $activeCode")
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
			IMAGE_DETECT_FACE_SCALE_VAL,
			DETECT_FACE_MAX_NUM,
			IMAGE_ENGINE_MASK
		)
		return if (ErrorInfo.MOK == faceEngineCode) {
			val versionInfo = VersionInfo()
			faceEngine.getVersion(versionInfo)
			Log.i(TAG, "虹软2.X算法引擎初始化成功, 版本号: $versionInfo")
			faceEngine
		} else {
			Log.i(TAG, "虹软2.X算法引擎初始化失败, 返回码: $faceEngineCode")
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
			VIDEO_DETECT_FACE_SCALE_VAL,
			DETECT_FACE_MAX_NUM,
			VIDEO_ENGINE_MASK
		)
		return if (ErrorInfo.MOK == faceEngineCode) {
			val versionInfo = VersionInfo()
			faceEngine.getVersion(versionInfo)
			Log.i(TAG, "虹软2.X算法引擎初始化成功, 版本号: $versionInfo")
			faceEngine
		} else {
			Log.i(TAG, "虹软2.X算法引擎初始化失败, 返回码: $faceEngineCode")
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
				Log.i(TAG, "释放虹软2.X算法引擎, 返回码: $faceEngineCode")
			}
		} catch (e: Exception) {
			Log.e(TAG, e.message.toString())
		}
	}

	/**
	 * 处理图片数据
	 *
	 * @param imageEngine 算法引擎
	 * @param data        图片数据
	 * @param width       宽度
	 * @param height      高度
	 * @return 人脸信息
	 */
	fun processImage(imageEngine: FaceEngine, data: ByteArray, width: Int, height: Int, fotmat: Int): FaceInfoResult {
		val faceInfoList = ArrayList<FaceInfo>()
		val code = imageEngine.detectFaces(data, width, height, fotmat, faceInfoList)
		//检测人脸
		return if (ErrorInfo.MOK != code || faceInfoList.isEmpty()) {
			FaceInfoResult(code, "未检测到人脸", false, null, null)
		} else {
			FaceInfoResult(code, "检测到人脸", false, faceInfoList[0], null)
		}
	}

	/**
	 * 处理视频数据
	 *
	 * @param videoEngine 视频引擎
	 * @param nv21        视频数据
	 * @param width       宽度
	 * @param height      高度
	 * @param liveness    是否检测活体
	 * @return 人脸信息
	 */
	fun processVideo(videoEngine: FaceEngine, nv21: ByteArray, width: Int, height: Int, liveness: Boolean): FaceInfoResult {
		val faceInfoList = ArrayList<FaceInfo>()
		var code = videoEngine.detectFaces(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList)
		//检测人脸
		return when {
			ErrorInfo.MOK != code || faceInfoList.isEmpty() -> FaceInfoResult(code, "未检测到人脸", false, null, nv21)
			//不检测活体
			!liveness -> FaceInfoResult(code, "检测到人脸", false, faceInfoList[0], nv21)
			//活体检测
			else -> {
				code = videoEngine.process(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList, FaceEngine.ASF_LIVENESS)
				when {
					ErrorInfo.MOK != code || faceInfoList.isEmpty() -> FaceInfoResult(code, "未检测到活体人脸", false, null, nv21)
					else -> {
						val livenessInfoList = ArrayList<LivenessInfo>()
						code = videoEngine.getLiveness(livenessInfoList)
						when {
							ErrorInfo.MOK != code || livenessInfoList.isEmpty() -> FaceInfoResult(code, "未检测到活体信息", false, null, nv21)
							else -> FaceInfoResult(
								code,
								"活体检测成功",
								LivenessInfo.ALIVE == livenessInfoList[0].liveness,
								faceInfoList[0],
								nv21
							)
						}
					}
				}
			}
		}
	}

	/**
	 * 抽取特征数组
	 *
	 * @param imageEngine 图片引擎
	 * @param data        帧数据
	 * @param width       宽度
	 * @param height      高度
	 * @param faceInfo    人脸信息
	 * @param format      数据格式
	 * @return 特征数组
	 */
	fun extractFeature(imageEngine: FaceEngine, data: ByteArray?, width: Int, height: Int, faceInfo: FaceInfo?, format: Int): ByteArray? {
		val faceFeature = FaceFeature()
		val extractFaceFeatureCode = imageEngine.extractFaceFeature(data, width, height, format, faceInfo, faceFeature)
		return if (ErrorInfo.MOK != extractFaceFeatureCode) null else faceFeature.featureData
	}

	/**
	 * 根据特征数组比对人脸
	 *
	 * @param imageEngine  图片引擎
	 * @param faceFeature1 特征数组1
	 * @param faceFeature2 特征数组2
	 * @return 比对分值
	 */
	fun compareFaceFeature(imageEngine: FaceEngine, faceFeature1: ByteArray, faceFeature2: ByteArray): Float {
		val faceSimilar = FaceSimilar()
		imageEngine.compareFaceFeature(FaceFeature(faceFeature1), FaceFeature(faceFeature2), faceSimilar)
		return faceSimilar.score
	}
}
