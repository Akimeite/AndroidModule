package com.djangoogle.arcsoft2x.util;

import android.content.Context;

import com.djangoogle.arcsoft2x.constants.DetectCondition;
import com.djangoogle.arcsoft2x.constants.DetectFaceOrient;
import com.djangoogle.arcsoft2x.constants.DetectFaceSize;
import com.djangoogle.arcsoft2x.constants.DetectFormat;
import com.djangoogle.arcsoft2x.constants.DetectMode;
import com.djangoogle.arcsoft2x.engine.ArcSoft2XEngine;

/**
 * Created by Djangoogle on 2019/05/21 16:53 with Android Studio.
 */
public class ArcSoft2XEngineUtil {

	/**
	 * 激活引擎
	 *
	 * @param context 上下文
	 * @param appId   APP_ID
	 * @param sdkKey  SDK_KEY
	 * @return 成功/失败
	 */
	public static boolean active(Context context, String appId, String sdkKey) {
		return new ArcSoft2XEngine(context).active(appId, sdkKey);
	}

	/**
	 * 获取抽取引擎
	 *
	 * @param context 上下文
	 * @return 抽取引擎
	 */
	public static ArcSoft2XEngine getExtractEngine(Context context) {
		ArcSoft2XEngine arcSoft2XEngine = new ArcSoft2XEngine(context);
		boolean result = arcSoft2XEngine.setDetectMode(DetectMode.IMAGE)
		                                .setDetectFaceOrient(DetectFaceOrient.ORIENT_ALL)
		                                .setDetectFaceSize(DetectFaceSize.NORMAL)
		                                .setDetectFaceMaxNum(1)
		                                .setDetectCondition(DetectCondition.FACE_DETECT,
				                                DetectCondition.FACE_RECOGNITION,
				                                DetectCondition.PROCESS_NONE)
		                                .setDetectFormat(DetectFormat.BGR24)
		                                .initialize();
		if (!result) {
			arcSoft2XEngine = null;
		}
		return arcSoft2XEngine;
	}

	/**
	 * 获取比对引擎
	 *
	 * @param context 上下文
	 * @return 比对引擎
	 */
	public static ArcSoft2XEngine getCompareEngine(Context context) {
		ArcSoft2XEngine arcSoft2XEngine = new ArcSoft2XEngine(context);
		boolean result = arcSoft2XEngine.setDetectMode(DetectMode.VIDEO)
		                                .setDetectFaceOrient(DetectFaceOrient.ORIENT_0_ONLY)
		                                .setDetectFaceSize(DetectFaceSize.NORMAL)
		                                .setDetectFaceMaxNum(1)
		                                .setDetectCondition(DetectCondition.FACE_DETECT,
				                                DetectCondition.FACE_RECOGNITION,
				                                DetectCondition.LIVENESS)
		                                .setDetectFormat(DetectFormat.NV21)
		                                .initialize();
		if (!result) {
			arcSoft2XEngine = null;
		}
		return arcSoft2XEngine;
	}
}
