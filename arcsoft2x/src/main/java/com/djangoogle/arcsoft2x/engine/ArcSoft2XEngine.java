package com.djangoogle.arcsoft2x.engine;

import android.content.Context;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.blankj.utilcode.util.LogUtils;
import com.djangoogle.arcsoft2x.constants.Gender;
import com.djangoogle.arcsoft2x.constants.Liveness;
import com.djangoogle.arcsoft2x.model.Face3DAngleModel;
import com.djangoogle.arcsoft2x.model.FaceFeatureModel;
import com.djangoogle.arcsoft2x.model.FaceInfoModel;
import com.djangoogle.arcsoft2x.util.TrackUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.djangoogle.arcsoft2x.constants.DetectCondition.AGE;
import static com.djangoogle.arcsoft2x.constants.DetectCondition.FACE3DANGLE;
import static com.djangoogle.arcsoft2x.constants.DetectCondition.FACE_RECOGNITION;
import static com.djangoogle.arcsoft2x.constants.DetectCondition.GENDER;
import static com.djangoogle.arcsoft2x.constants.DetectCondition.INIT_NONE;
import static com.djangoogle.arcsoft2x.constants.DetectCondition.LIVENESS;
import static com.djangoogle.arcsoft2x.constants.DetectCondition.PROCESS_NONE;
import static com.djangoogle.arcsoft2x.constants.DetectFaceOrient.ORIENT_0_ONLY;
import static com.djangoogle.arcsoft2x.constants.DetectFaceOrient.ORIENT_180_ONLY;
import static com.djangoogle.arcsoft2x.constants.DetectFaceOrient.ORIENT_270_ONLY;
import static com.djangoogle.arcsoft2x.constants.DetectFaceOrient.ORIENT_90_ONLY;
import static com.djangoogle.arcsoft2x.constants.DetectFaceSize.CUSTOM;
import static com.djangoogle.arcsoft2x.constants.DetectFaceSize.LARGE;
import static com.djangoogle.arcsoft2x.constants.DetectFaceSize.NORMAL;
import static com.djangoogle.arcsoft2x.constants.DetectFaceSize.SMALL;
import static com.djangoogle.arcsoft2x.constants.DetectFormat.BGR24;
import static com.djangoogle.arcsoft2x.constants.DetectFormat.NV21;
import static com.djangoogle.arcsoft2x.constants.DetectMode.IMAGE;
import static com.djangoogle.arcsoft2x.constants.DetectMode.VIDEO;
import static com.djangoogle.arcsoft2x.constants.Gender.FEMALE;
import static com.djangoogle.arcsoft2x.constants.Gender.MALE;
import static com.djangoogle.arcsoft2x.constants.Liveness.ALIVE;
import static com.djangoogle.arcsoft2x.constants.Liveness.NOT_ALIVE;

/**
 * 虹软单目算法引擎
 * Created by Djangoogle on 2019/04/08 17:42 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
public class ArcSoft2XEngine {

	public static final float SCORE = 0.8F;

	private static final String TAG = ArcSoft2XEngine.class.getSimpleName();

	private Context mContext;
	private FaceEngine mFaceEngine;
	private String mDetectMode;
	private String mDetectFaceOrient;
	private String mDetectFaceSize;
	private int[] mCustomDetectFaceSize = new int[1];
	private int mDetectFaceMaxNum;
	private String[] mDetectConditions;
	private String mDetectFormat;

	/**
	 * 构造方法
	 *
	 * @param context 上下文
	 */
	public ArcSoft2XEngine(Context context) {
		mContext = context;
	}

	/**
	 * 激活虹软2.1算法引擎
	 *
	 * @param appId  APP_ID
	 * @param sdkKey SDK_KEY
	 * @return 成功/失败
	 */
	public boolean active(String appId, String sdkKey) {
		LogUtils.iTag(TAG, "激活虹软2.1算法引擎");
		mFaceEngine = new FaceEngine();
		int activeCode = mFaceEngine.active(mContext, appId, sdkKey);
		switch (activeCode) {
			case ErrorInfo.MOK:
			case ErrorInfo.MERR_ASF_ALREADY_ACTIVATED:
				LogUtils.iTag(TAG, "虹软2.1算法引擎激活成功", activeCode);
				return true;

			default:
				LogUtils.eTag(TAG, "虹软2.1算法引擎激活失败", activeCode);
				return false;
		}
	}

	/**
	 * 初始化引擎
	 */
	public boolean initialize() {
		LogUtils.iTag(TAG, "初始化虹软2.1算法引擎");
		if (null == mFaceEngine) {
			mFaceEngine = new FaceEngine();
		}
		//检测模式
		long detectMode;
		switch (mDetectMode) {
			case IMAGE:
				detectMode = FaceEngine.ASF_DETECT_MODE_IMAGE;
				break;

			case VIDEO:
				detectMode = FaceEngine.ASF_DETECT_MODE_VIDEO;
				break;

			default:
				detectMode = FaceEngine.ASF_DETECT_MODE_IMAGE;
				break;
		}
		//检测方向
		int detectFaceOrient;
		switch (mDetectFaceOrient) {
			case ORIENT_0_ONLY:
				detectFaceOrient = FaceEngine.ASF_OP_0_ONLY;
				break;

			case ORIENT_90_ONLY:
				detectFaceOrient = FaceEngine.ASF_OP_90_ONLY;
				break;

			case ORIENT_180_ONLY:
				detectFaceOrient = FaceEngine.ASF_OP_180_ONLY;
				break;

			case ORIENT_270_ONLY:
				detectFaceOrient = FaceEngine.ASF_OP_270_ONLY;
				break;

			default:
				detectFaceOrient = FaceEngine.ASF_OP_0_HIGHER_EXT;
				break;
		}
		//人脸检测尺寸
		//用于数值化表示的最小人脸尺寸，该尺寸代表人脸尺寸相对于图片长边的占比
		//video 模式有效值范围[2,16], Image 模式有效值范围[2,32]，多数情况下推荐值为 16
		//特殊情况下可根据具体场景下进行设置
		int detectFaceScale;
		switch (mDetectFaceSize) {
			//小
			case SMALL:
				detectFaceScale = 2;
				break;

			//普通
			case NORMAL:
				detectFaceScale = 16;
				break;

			//大
			case LARGE:
				if (FaceEngine.ASF_DETECT_MODE_IMAGE == detectMode) {
					detectFaceScale = 32;
				} else {
					detectFaceScale = 16;
				}
				break;

			//自定义
			case CUSTOM:
				detectFaceScale = mCustomDetectFaceSize[0];
				break;

			default:
				detectFaceScale = 16;
				break;
		}
		int arcSoft2Code = mFaceEngine.init(mContext, detectMode, detectFaceOrient, detectFaceScale, mDetectFaceMaxNum, getInitMask());
		if (ErrorInfo.MOK == arcSoft2Code) {
			VersionInfo versionInfo = new VersionInfo();
			mFaceEngine.getVersion(versionInfo);
			LogUtils.iTag(TAG, "虹软2.1算法引擎初始化成功，版本号：" + versionInfo);
			return true;
		} else {
			LogUtils.eTag(TAG, "虹软2.1算法引擎初始化失败", arcSoft2Code);
			return false;
		}
	}

	public void release() {
		LogUtils.iTag(TAG, "释放虹软2.1算法引擎");
		try {
			if (null != mFaceEngine) {
				mFaceEngine.unInit();
				mFaceEngine = null;
			}
		} catch (Exception e) {
			LogUtils.eTag(TAG, e.getMessage());
		}
	}

	/**
	 * 根据条件处理人脸数据
	 */
	public List<FaceInfoModel> processData(byte[] data, int width, int height) {
		List<FaceInfo> faceInfoList = new ArrayList<>();
		int arcSoft2Code = mFaceEngine.detectFaces(data, width, height, getFormat(), faceInfoList);
		if (ErrorInfo.MOK != arcSoft2Code || faceInfoList.isEmpty()) {
			LogUtils.eTag(TAG, "未检测到人脸", arcSoft2Code);
			return null;
		}
		//活体检测只支持一个人脸，所以只保留最大的人脸
		if (hasCondition(LIVENESS)) {
			TrackUtil.keepMaxFace(faceInfoList);
		}
		arcSoft2Code = mFaceEngine.process(data, width, height, getFormat(), faceInfoList, getProcessMask());
		if (ErrorInfo.MOK == arcSoft2Code) {
			List<FaceInfoModel> faceInfoModelList = origin2Model(faceInfoList);
			if (null != faceInfoModelList && !faceInfoModelList.isEmpty()) {
				LogUtils.iTag(TAG, "检测到人脸", arcSoft2Code);
				return faceInfoModelList;
			} else {
				LogUtils.eTag(TAG, "未检测到人脸", arcSoft2Code);
				return null;
			}
		} else {
			LogUtils.eTag(TAG, "未检测到人脸", arcSoft2Code);
			return null;
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
	public List<FaceFeatureModel> extractFaceFeature(byte[] data, int width, int height, List<FaceInfoModel> faceInfoModelList) {
		List<FaceInfo> faceInfoList = model2Origin(faceInfoModelList);
		FaceFeature[] faceFeatures = new FaceFeature[faceInfoList.size()];
		int[] extractFaceFeatureCodes = new int[faceInfoList.size()];
		List<FaceFeatureModel> faceFeatureModelList = new ArrayList<>();
		for (int i = 0; i < faceInfoList.size(); i++) {
			faceFeatures[i] = new FaceFeature();
			extractFaceFeatureCodes[i] = mFaceEngine.extractFaceFeature(data, width, height, getFormat(), faceInfoList.get(i),
					faceFeatures[i]);
			FaceFeatureModel faceFeatureModel = new FaceFeatureModel(ErrorInfo.MOK != extractFaceFeatureCodes[i] ? null :
					faceFeatures[i].getFeatureData());
			faceFeatureModelList.add(faceFeatureModel);
		}
		return faceFeatureModelList;
	}

	/**
	 * 根据特征数组比对人脸
	 *
	 * @param faceFeature1 特征数组1
	 * @param faceFeature2 特征数组2
	 * @return 比对分值
	 */
	public float compareFaceFeature(FaceFeatureModel faceFeature1, FaceFeatureModel faceFeature2) {
		FaceFeature faceFeature01 = new FaceFeature(faceFeature1.getFeatureData());
		FaceFeature faceFeature02 = new FaceFeature(faceFeature2.getFeatureData());
		FaceSimilar faceSimilar = new FaceSimilar();
		mFaceEngine.compareFaceFeature(faceFeature01, faceFeature02, faceSimilar);
		return faceSimilar.getScore();
	}

	/**
	 * 获取人脸检测格式
	 *
	 * @return 人脸检测格式
	 */
	private int getFormat() {
		int format;
		switch (mDetectFormat) {
			case NV21:
				format = FaceEngine.CP_PAF_NV21;
				break;

			case BGR24:
				format = FaceEngine.CP_PAF_BGR24;
				break;

			default:
				format = FaceEngine.CP_PAF_NV21;
				break;
		}
		return format;
	}

	/**
	 * 解析并生成引擎初始化条件
	 *
	 * @return 引擎初始化条件
	 */
	private int getInitMask() {
		int initMask;
		if (hasCondition(INIT_NONE)) {
			initMask = FaceEngine.ASF_NONE;
		} else {
			initMask = FaceEngine.ASF_FACE_DETECT;
			if (hasCondition(FACE_RECOGNITION)) {
				initMask = initMask | FaceEngine.ASF_FACE_RECOGNITION;
			}
			if (hasCondition(AGE)) {
				initMask = initMask | FaceEngine.ASF_AGE;
			}
			if (hasCondition(GENDER)) {
				initMask = initMask | FaceEngine.ASF_GENDER;
			}
			if (hasCondition(FACE3DANGLE)) {
				initMask = initMask | FaceEngine.ASF_FACE3DANGLE;
			}
			if (hasCondition(LIVENESS)) {
				initMask = initMask | FaceEngine.ASF_LIVENESS;
			}
		}
		return initMask;
	}

	/**
	 * 解析并生成人脸检测条件
	 *
	 * @return 人脸检测条件
	 */
	private int getProcessMask() {
		int processMask = -1;
		if (hasCondition(PROCESS_NONE)) {
			processMask = FaceEngine.ASF_NONE;
		} else {
			if (hasCondition(AGE)) {
				processMask = FaceEngine.ASF_AGE;
			}
			if (hasCondition(GENDER)) {
				if (-1 == processMask) {
					processMask = FaceEngine.ASF_GENDER;
				} else {
					processMask = processMask | FaceEngine.ASF_GENDER;
				}
			}
			if (hasCondition(FACE3DANGLE)) {
				if (-1 == processMask) {
					processMask = FaceEngine.ASF_FACE3DANGLE;
				} else {
					processMask = processMask | FaceEngine.ASF_FACE3DANGLE;
				}
			}
			if (hasCondition(LIVENESS)) {
				if (-1 == processMask) {
					processMask = FaceEngine.ASF_LIVENESS;
				} else {
					processMask = processMask | FaceEngine.ASF_LIVENESS;
				}
			}
		}
		return processMask;
	}

	/**
	 * 将原始数据转换为封装数据
	 *
	 * @param faceInfoList 原始数据
	 * @return 封装数据
	 */
	private List<FaceInfoModel> origin2Model(List<FaceInfo> faceInfoList) {
		List<FaceInfoModel> faceInfoModelList = new ArrayList<>();
		List<AgeInfo> ageInfoList = new ArrayList<>();
		List<GenderInfo> genderInfoList = new ArrayList<>();
		List<Face3DAngle> face3DAngleList = new ArrayList<>();
		List<LivenessInfo> livenessInfoList = new ArrayList<>();
		//年龄信息结果
		if (hasCondition(AGE)) {
			int ageCode = mFaceEngine.getAge(ageInfoList);
			if (ErrorInfo.MOK != ageCode) {
				LogUtils.eTag(TAG, "未检测到年龄信息", ageCode);
				return null;
			}
		}
		//性别信息结果
		if (hasCondition(GENDER)) {
			int genderCode = mFaceEngine.getGender(genderInfoList);
			if (ErrorInfo.MOK != genderCode) {
				LogUtils.eTag(TAG, "未检测到性别信息", genderCode);
				return null;
			}
		}
		//人脸三维角度结果
		if (hasCondition(FACE3DANGLE)) {
			int face3DAngleCode = mFaceEngine.getFace3DAngle(face3DAngleList);
			if (ErrorInfo.MOK != face3DAngleCode) {
				LogUtils.eTag(TAG, "未检测到人脸三维角度信息", face3DAngleCode);
				return null;
			}
		}
		//活体检测结果
		if (hasCondition(LIVENESS)) {
			int livenessCode = mFaceEngine.getLiveness(livenessInfoList);
			if (ErrorInfo.MOK != livenessCode) {
				LogUtils.eTag(TAG, "未检测到活体信息", livenessCode);
				return null;
			}
		}
		//组装人脸数据
		for (int i = 0; i < faceInfoList.size(); i++) {
			FaceInfoModel faceInfoModel = new FaceInfoModel();
			//设置人脸框
			faceInfoModel.setRect(faceInfoList.get(i).getRect());
			//设置方向
			faceInfoModel.setOrient(faceInfoList.get(i).getOrient());
			//年龄信息
			if (hasCondition(AGE)) {
				faceInfoModel.setAge(ageInfoList.get(i).getAge());
			}
			//判断性别
			String gender;
			if (hasCondition(GENDER)) {
				switch (genderInfoList.get(i).getGender()) {
					//男性
					case GenderInfo.MALE:
						gender = MALE;
						break;

					//女性
					case GenderInfo.FEMALE:
						gender = FEMALE;
						break;

					//未知
					default:
						gender = Gender.UNKNOWN;
						break;
				}
				faceInfoModel.setGender(gender);
			}
			//人脸角度
			if (hasCondition(FACE3DANGLE)) {
				Face3DAngleModel face3DAngleModel = new Face3DAngleModel(face3DAngleList.get(i).getYaw(),
						face3DAngleList.get(i).getRoll(),
						face3DAngleList.get(i).getPitch());
				faceInfoModel.setFace3DAngle(face3DAngleModel);
			}
			//判断活体
			String liveness;
			if (hasCondition(LIVENESS)) {
				switch (livenessInfoList.get(i).getLiveness()) {
					//男性
					case LivenessInfo.ALIVE:
						liveness = ALIVE;
						break;

					//女性
					case LivenessInfo.NOT_ALIVE:
						liveness = NOT_ALIVE;
						break;

					//未知
					default:
						liveness = Liveness.UNKNOWN;
						break;
				}
				faceInfoModel.setLiveness(liveness);
			}
			faceInfoModelList.add(faceInfoModel);
		}
		return faceInfoModelList;
	}

	/**
	 * 将原始数据转换为封装数据
	 *
	 * @param faceInfoModelList 封装数据
	 * @return 原始数据
	 */
	private List<FaceInfo> model2Origin(List<FaceInfoModel> faceInfoModelList) {
		List<FaceInfo> faceInfoList = new ArrayList<>();
		for (int i = 0; i < faceInfoModelList.size(); i++) {
			FaceInfo faceInfo = new FaceInfo();
			faceInfo.setRect(faceInfoModelList.get(i).getRect());
			faceInfo.setOrient(faceInfoModelList.get(i).getOrient());
			faceInfoList.add(faceInfo);
		}
		return faceInfoList;
	}

	/**
	 * 判断是否包含指定条件
	 *
	 * @param condition 条件
	 * @return 是否包含该条件
	 */
	private boolean hasCondition(String condition) {
		return Arrays.asList(mDetectConditions).contains(condition);
	}

	/**
	 * 设置检测模式
	 *
	 * @param detectMode 图片/视频
	 * @return 引擎对象
	 */
	public ArcSoft2XEngine setDetectMode(String detectMode) {
		mDetectMode = detectMode;
		return this;
	}

	/**
	 * 设置检测方向
	 *
	 * @param detectFaceOrient 0°/90°/180°/270°/全方位
	 * @return 引擎对象
	 */
	public ArcSoft2XEngine setDetectFaceOrient(String detectFaceOrient) {
		mDetectFaceOrient = detectFaceOrient;
		return this;
	}

	/**
	 * 设置检测人脸大小
	 *
	 * @param detectFaceSize       人脸大小类型
	 * @param customDetectFaceSize 自定义人脸大小
	 * @return 引擎对象
	 */
	public ArcSoft2XEngine setDetectFaceSize(String detectFaceSize, int... customDetectFaceSize) {
		mDetectFaceSize = detectFaceSize;
		mCustomDetectFaceSize = customDetectFaceSize;
		return this;
	}

	/**
	 * 设置同时检测的最大人脸数
	 *
	 * @param detectFaceMaxNum 虹软2.1有效值范围[1,50]
	 * @return 引擎对象
	 */
	public ArcSoft2XEngine setDetectFaceMaxNum(Integer detectFaceMaxNum) {
		mDetectFaceMaxNum = detectFaceMaxNum;
		return this;
	}

	/**
	 * 设置人脸检测条件
	 *
	 * @param detectConditions 人脸检测条件
	 * @return 引擎对象
	 */
	public ArcSoft2XEngine setDetectCondition(String... detectConditions) {
		mDetectConditions = detectConditions;
		return this;
	}

	/**
	 * 设置人脸检测格式
	 *
	 * @param detectFormat 人脸检测格式
	 * @return 引擎对象
	 */
	public ArcSoft2XEngine setDetectFormat(String detectFormat) {
		mDetectFormat = detectFormat;
		return this;
	}
}
