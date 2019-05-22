package com.djangoogle.arcsoft2x.engine

import android.content.Context
import com.arcsoft.face.*
import com.blankj.utilcode.util.LogUtils
import com.djangoogle.arcsoft2x.constants.*
import com.djangoogle.arcsoft2x.model.Face3DAngleModel
import com.djangoogle.arcsoft2x.model.FaceFeatureModel
import com.djangoogle.arcsoft2x.model.FaceInfoModel
import com.djangoogle.arcsoft2x.util.TrackUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 虹软单目算法引擎
 * Created by Djangoogle on 2019/04/08 17:42 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
class ArcSoft2XEngine(private val mContext: Context) {

	companion object {

		const val SCORE = 0.8f

		private val TAG = ArcSoft2XEngine::class.java.simpleName
	}

	private var mFaceEngine: FaceEngine? = null
	private lateinit var mDetectMode: String
	private lateinit var mDetectFaceOrient: String
	private lateinit var mDetectFaceSize: String
	private var mCustomDetectFaceSize = IntArray(1)
	private var mDetectFaceMaxNum: Int = 1
	private lateinit var mDetectConditions: Array<out String>
	private lateinit var mDetectFormat: String

	/**
	 * 获取人脸检测格式
	 *
	 * @return 人脸检测格式
	 */
	private val format: Int
		get() {
			return when (mDetectFormat) {
				DetectFormat.NV21 -> FaceEngine.CP_PAF_NV21
				DetectFormat.BGR24 -> FaceEngine.CP_PAF_BGR24
				else -> FaceEngine.CP_PAF_NV21
			}
		}

	/**
	 * 解析并生成引擎初始化条件
	 *
	 * @return 引擎初始化条件
	 */
	private val initMask: Int
		get() {
			var initMask: Int
			if (hasCondition(DetectCondition.INIT_NONE)) {
				initMask = FaceEngine.ASF_NONE
			} else {
				initMask = FaceEngine.ASF_FACE_DETECT
				if (hasCondition(DetectCondition.FACE_RECOGNITION)) {
					initMask = initMask or FaceEngine.ASF_FACE_RECOGNITION
				}
				if (hasCondition(DetectCondition.AGE)) {
					initMask = initMask or FaceEngine.ASF_AGE
				}
				if (hasCondition(DetectCondition.GENDER)) {
					initMask = initMask or FaceEngine.ASF_GENDER
				}
				if (hasCondition(DetectCondition.FACE3DANGLE)) {
					initMask = initMask or FaceEngine.ASF_FACE3DANGLE
				}
				if (hasCondition(DetectCondition.LIVENESS)) {
					initMask = initMask or FaceEngine.ASF_LIVENESS
				}
			}
			return initMask
		}

	/**
	 * 解析并生成人脸检测条件
	 *
	 * @return 人脸检测条件
	 */
	private val processMask: Int
		get() {
			var processMask = -1
			if (hasCondition(DetectCondition.PROCESS_NONE)) {
				processMask = FaceEngine.ASF_NONE
			} else {
				if (hasCondition(DetectCondition.AGE)) {
					processMask = FaceEngine.ASF_AGE
				}
				if (hasCondition(DetectCondition.GENDER)) {
					processMask = if (-1 == processMask) FaceEngine.ASF_GENDER else processMask or FaceEngine.ASF_GENDER
				}
				if (hasCondition(DetectCondition.FACE3DANGLE)) {
					processMask = if (-1 == processMask) FaceEngine.ASF_FACE3DANGLE else processMask or FaceEngine.ASF_FACE3DANGLE
				}
				if (hasCondition(DetectCondition.LIVENESS)) {
					processMask = if (-1 == processMask) FaceEngine.ASF_LIVENESS else processMask or FaceEngine.ASF_LIVENESS
				}
			}
			return processMask
		}

	/**
	 * 激活虹软2.1算法引擎
	 *
	 * @param appId  APP_ID
	 * @param sdkKey SDK_KEY
	 * @return 成功/失败
	 */
	fun active(appId: String, sdkKey: String): Boolean {
		LogUtils.iTag(TAG, "激活虹软2.1算法引擎")
		mFaceEngine = FaceEngine()
		return when (val activeCode = mFaceEngine?.active(mContext, appId, sdkKey)) {
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
	 * 初始化引擎
	 */
	fun initialize(): Boolean {
		//注册EventBus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this)
		}
		LogUtils.iTag(TAG, "初始化虹软2.1算法引擎")
		mFaceEngine = FaceEngine()
		//检测模式
		val detectMode: Long = when (mDetectMode) {
			DetectMode.IMAGE -> FaceEngine.ASF_DETECT_MODE_IMAGE
			DetectMode.VIDEO -> FaceEngine.ASF_DETECT_MODE_VIDEO
			else -> FaceEngine.ASF_DETECT_MODE_IMAGE
		}
		//检测方向
		val detectFaceOrient: Int = when (mDetectFaceOrient) {
			DetectFaceOrient.ORIENT_0_ONLY -> FaceEngine.ASF_OP_0_ONLY
			DetectFaceOrient.ORIENT_90_ONLY -> FaceEngine.ASF_OP_90_ONLY
			DetectFaceOrient.ORIENT_180_ONLY -> FaceEngine.ASF_OP_180_ONLY
			DetectFaceOrient.ORIENT_270_ONLY -> FaceEngine.ASF_OP_270_ONLY
			else -> FaceEngine.ASF_OP_0_HIGHER_EXT
		}
		//人脸检测尺寸
		//用于数值化表示的最小人脸尺寸，该尺寸代表人脸尺寸相对于图片长边的占比
		//video 模式有效值范围[2,16], Image 模式有效值范围[2,32]，多数情况下推荐值为 16
		//特殊情况下可根据具体场景下进行设置
		val detectFaceScale: Int
		detectFaceScale = when (mDetectFaceSize) {
			//小
			DetectFaceSize.SMALL -> 2
			//普通
			DetectFaceSize.NORMAL -> 16
			//大
			DetectFaceSize.LARGE -> if (FaceEngine.ASF_DETECT_MODE_IMAGE == detectMode) 32 else 16
			//自定义
			DetectFaceSize.CUSTOM -> mCustomDetectFaceSize[0]
			else -> 16
		}
		val arcSoft2Code = mFaceEngine?.init(mContext, detectMode, detectFaceOrient, detectFaceScale, mDetectFaceMaxNum, initMask)
		return if (ErrorInfo.MOK == arcSoft2Code) {
			val versionInfo = VersionInfo()
			mFaceEngine?.getVersion(versionInfo)
			LogUtils.iTag(TAG, "虹软2.1算法引擎初始化成功，版本号：$versionInfo")
			true
		} else {
			LogUtils.eTag(TAG, "虹软2.1算法引擎初始化失败", arcSoft2Code)
			false
		}
	}

	fun release() {
		LogUtils.iTag(TAG, "释放虹软2.1算法引擎")
		try {
			if (null != mFaceEngine) {
				mFaceEngine?.unInit()
				mFaceEngine = null
			}
		} catch (e: Exception) {
			LogUtils.eTag(TAG, e.message)
		} finally {
			//注销EventBus
			if (EventBus.getDefault().isRegistered(this)) {
				EventBus.getDefault().unregister(this)
			}
		}
	}

	/**
	 * 根据条件处理人脸数据
	 */
	fun processData(data: ByteArray, width: Int, height: Int): List<FaceInfoModel>? {
		val faceInfoList = ArrayList<FaceInfo>()
		var arcSoft2Code = mFaceEngine?.detectFaces(data, width, height, format, faceInfoList)
		if (ErrorInfo.MOK != arcSoft2Code || faceInfoList.isEmpty()) {
			LogUtils.eTag(TAG, "未检测到人脸", arcSoft2Code)
			return null
		}
		//活体检测只支持一个人脸，所以只保留最大的人脸
		if (hasCondition(DetectCondition.LIVENESS)) {
			TrackUtil.keepMaxFace(faceInfoList)
		}
		arcSoft2Code = mFaceEngine?.process(data, width, height, format, faceInfoList, processMask)
		return if (ErrorInfo.MOK == arcSoft2Code) {
			val faceInfoModelList = origin2Model(faceInfoList)
			if (null != faceInfoModelList && faceInfoModelList.isNotEmpty()) {
				LogUtils.iTag(TAG, "检测到人脸", arcSoft2Code)
				faceInfoModelList
			} else {
				LogUtils.eTag(TAG, "未检测到人脸", arcSoft2Code)
				null
			}
		} else {
			LogUtils.eTag(TAG, "未检测到人脸", arcSoft2Code)
			null
		}
	}

	/**
	 * 提取特征数组
	 *
	 * @param data              帧数据
	 * @param width             宽度
	 * @param height            高度
	 * @param faceInfoModelList 人脸信息集合
	 * @return 特征数组集合
	 */
	fun extractFaceFeature(data: ByteArray, width: Int, height: Int, faceInfoModelList: List<FaceInfoModel>): List<FaceFeatureModel> {
		val faceInfoList = model2Origin(faceInfoModelList)
		val faceFeatures = ArrayList<FaceFeature>(faceInfoList.size)
		val extractFaceFeatureCodes = ArrayList<Int>(faceInfoList.size)
		val faceFeatureModelList = ArrayList<FaceFeatureModel>()
		for (i in faceInfoList.indices) {
			faceFeatures[i] = FaceFeature()
			extractFaceFeatureCodes[i] = mFaceEngine!!.extractFaceFeature(data, width, height, format, faceInfoList[i], faceFeatures[i])
			val faceFeatureModel =
				if (ErrorInfo.MOK != extractFaceFeatureCodes[i]) FaceFeatureModel() else FaceFeatureModel(faceFeatures[i].featureData)
			faceFeatureModelList.add(faceFeatureModel)
		}
		return faceFeatureModelList
	}

	/**
	 * 根据特征数组比对人脸
	 *
	 * @param faceFeature1 特征数组1
	 * @param faceFeature2 特征数组2
	 * @return 比对分值
	 */
	fun compareFaceFeature(faceFeature1: FaceFeatureModel, faceFeature2: FaceFeatureModel): Float {
		val faceFeature01 = FaceFeature(faceFeature1.featureData)
		val faceFeature02 = FaceFeature(faceFeature2.featureData)
		val faceSimilar = FaceSimilar()
		mFaceEngine?.compareFaceFeature(faceFeature01, faceFeature02, faceSimilar)
		return faceSimilar.score
	}

	/**
	 * 将原始数据转换为封装数据
	 *
	 * @param faceInfoList 原始数据
	 * @return 封装数据
	 */
	private fun origin2Model(faceInfoList: List<FaceInfo>): List<FaceInfoModel>? {
		val faceInfoModelList = ArrayList<FaceInfoModel>()
		val ageInfoList = ArrayList<AgeInfo>()
		val genderInfoList = ArrayList<GenderInfo>()
		val face3DAngleList = ArrayList<Face3DAngle>()
		val livenessInfoList = ArrayList<LivenessInfo>()
		//年龄信息结果
		if (hasCondition(DetectCondition.AGE)) {
			val ageCode = mFaceEngine?.getAge(ageInfoList)
			if (ErrorInfo.MOK != ageCode) {
				LogUtils.eTag(TAG, "未检测到年龄信息", ageCode)
				return null
			}
		}
		//性别信息结果
		if (hasCondition(DetectCondition.GENDER)) {
			val genderCode = mFaceEngine?.getGender(genderInfoList)
			if (ErrorInfo.MOK != genderCode) {
				LogUtils.eTag(TAG, "未检测到性别信息", genderCode)
				return null
			}
		}
		//人脸三维角度结果
		if (hasCondition(DetectCondition.FACE3DANGLE)) {
			val face3DAngleCode = mFaceEngine?.getFace3DAngle(face3DAngleList)
			if (ErrorInfo.MOK != face3DAngleCode) {
				LogUtils.eTag(TAG, "未检测到人脸三维角度信息", face3DAngleCode)
				return null
			}
		}
		//活体检测结果
		if (hasCondition(DetectCondition.LIVENESS)) {
			val livenessCode = mFaceEngine?.getLiveness(livenessInfoList)
			if (ErrorInfo.MOK != livenessCode) {
				LogUtils.eTag(TAG, "未检测到活体信息", livenessCode)
				return null
			}
		}
		//组装人脸数据
		for (i in faceInfoList.indices) {
			val faceInfoModel = FaceInfoModel()
			//设置人脸框
			faceInfoModel.rect = faceInfoList[i].rect
			//设置方向
			faceInfoModel.orient = faceInfoList[i].orient
			//年龄信息
			if (hasCondition(DetectCondition.AGE)) {
				faceInfoModel.age = ageInfoList[i].age
			}
			//判断性别
			val gender: String
			if (hasCondition(DetectCondition.GENDER)) {
				gender = when (genderInfoList[i].gender) {
					//男性
					GenderInfo.MALE -> Gender.MALE
					//女性
					GenderInfo.FEMALE -> Gender.FEMALE
					//未知
					else -> Gender.UNKNOWN
				}
				faceInfoModel.gender = gender
			}
			//人脸角度
			if (hasCondition(DetectCondition.FACE3DANGLE)) {
				val face3DAngleModel = Face3DAngleModel(
					face3DAngleList[i].yaw,
					face3DAngleList[i].roll,
					face3DAngleList[i].pitch
				)
				faceInfoModel.face3DAngle = face3DAngleModel
			}
			//判断活体
			val liveness: String
			if (hasCondition(DetectCondition.LIVENESS)) {
				liveness = when (livenessInfoList[i].liveness) {
					//男性
					LivenessInfo.ALIVE -> Liveness.ALIVE
					//女性
					LivenessInfo.NOT_ALIVE -> Liveness.NOT_ALIVE
					//未知
					else -> Liveness.UNKNOWN
				}
				faceInfoModel.liveness = liveness
			}
			faceInfoModelList.add(faceInfoModel)
		}
		return faceInfoModelList
	}

	/**
	 * 将原始数据转换为封装数据
	 *
	 * @param faceInfoModelList 封装数据
	 * @return 原始数据
	 */
	private fun model2Origin(faceInfoModelList: List<FaceInfoModel>): List<FaceInfo> {
		val faceInfoList = ArrayList<FaceInfo>()
		for (i in faceInfoModelList.indices) {
			val faceInfo = FaceInfo()
			faceInfo.rect = faceInfoModelList[i].rect
			faceInfo.orient = faceInfoModelList[i].orient
			faceInfoList.add(faceInfo)
		}
		return faceInfoList
	}

	/**
	 * 判断是否包含指定条件
	 *
	 * @param condition 条件
	 * @return 是否包含该条件
	 */
	private fun hasCondition(condition: String): Boolean {
		return mDetectConditions.contains(condition)
	}

	/**
	 * 设置检测模式
	 *
	 * @param detectMode 图片/视频
	 * @return 引擎对象
	 */
	fun setDetectMode(detectMode: String): ArcSoft2XEngine {
		mDetectMode = detectMode
		return this
	}

	/**
	 * 设置检测方向
	 *
	 * @param detectFaceOrient 0°/90°/180°/270°/全方位
	 * @return 引擎对象
	 */
	fun setDetectFaceOrient(detectFaceOrient: String): ArcSoft2XEngine {
		mDetectFaceOrient = detectFaceOrient
		return this
	}

	/**
	 * 设置检测人脸大小
	 *
	 * @param detectFaceSize       人脸大小类型
	 * @param customDetectFaceSize 自定义人脸大小
	 * @return 引擎对象
	 */
	fun setDetectFaceSize(detectFaceSize: String, vararg customDetectFaceSize: Int): ArcSoft2XEngine {
		mDetectFaceSize = detectFaceSize
		mCustomDetectFaceSize = customDetectFaceSize
		return this
	}

	/**
	 * 设置同时检测的最大人脸数
	 *
	 * @param detectFaceMaxNum 虹软2.1有效值范围[1,50]
	 * @return 引擎对象
	 */
	fun setDetectFaceMaxNum(detectFaceMaxNum: Int): ArcSoft2XEngine {
		mDetectFaceMaxNum = detectFaceMaxNum
		return this
	}

	/**
	 * 设置人脸检测条件
	 *
	 * @param detectConditions 人脸检测条件
	 * @return 引擎对象
	 */
	fun setDetectCondition(vararg detectConditions: String): ArcSoft2XEngine {
		mDetectConditions = detectConditions
		return this
	}

	/**
	 * 设置人脸检测格式
	 *
	 * @param detectFormat 人脸检测格式
	 * @return 引擎对象
	 */
	fun setDetectFormat(detectFormat: String): ArcSoft2XEngine {
		mDetectFormat = detectFormat
		return this
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onArcSoft2XEngineEvent(event: Any) {
	}
}
