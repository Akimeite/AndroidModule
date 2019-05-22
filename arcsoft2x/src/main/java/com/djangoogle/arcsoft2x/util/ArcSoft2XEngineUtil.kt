package com.djangoogle.arcsoft2x.util

import android.content.Context
import com.djangoogle.arcsoft2x.constants.*
import com.djangoogle.arcsoft2x.engine.ArcSoft2XEngine

/**
 * Created by Djangoogle on 2019/05/21 16:53 with Android Studio.
 */
class ArcSoft2XEngineUtil {

	companion object {

		/**
		 * 激活引擎
		 *
		 * @param context 上下文
		 * @param appId   APP_ID
		 * @param sdkKey  SDK_KEY
		 * @return 成功/失败
		 */
		fun active(context: Context, appId: String, sdkKey: String): Boolean {
			return ArcSoft2XEngine(context).active(appId, sdkKey)
		}

		/**
		 * 获取抽取引擎
		 *
		 * @param context 上下文
		 * @return 抽取引擎
		 */
		fun getExtractEngine(context: Context): ArcSoft2XEngine? {
			var arcSoft2XEngine: ArcSoft2XEngine?
			arcSoft2XEngine = ArcSoft2XEngine(context)
			val result = arcSoft2XEngine.setDetectMode(DetectMode.IMAGE)
				.setDetectFaceOrient(DetectFaceOrient.ORIENT_ALL)
				.setDetectFaceSize(DetectFaceSize.NORMAL)
				.setDetectFaceMaxNum(1)
				.setDetectCondition(
					DetectCondition.FACE_DETECT,
					DetectCondition.FACE_RECOGNITION,
					DetectCondition.PROCESS_NONE
				)
				.setDetectFormat(DetectFormat.BGR24)
				.initialize()
			if (!result) {
				arcSoft2XEngine = null
			}
			return arcSoft2XEngine
		}

		/**
		 * 获取比对引擎
		 *
		 * @param context 上下文
		 * @return 比对引擎
		 */
		fun getCompareEngine(context: Context): ArcSoft2XEngine? {
			var arcSoft2XEngine: ArcSoft2XEngine?
			arcSoft2XEngine = ArcSoft2XEngine(context)
			val result = arcSoft2XEngine.setDetectMode(DetectMode.VIDEO)
				.setDetectFaceOrient(DetectFaceOrient.ORIENT_0_ONLY)
				.setDetectFaceSize(DetectFaceSize.NORMAL)
				.setDetectFaceMaxNum(1)
				.setDetectCondition(
					DetectCondition.FACE_DETECT,
					DetectCondition.FACE_RECOGNITION,
					DetectCondition.LIVENESS
				)
				.setDetectFormat(DetectFormat.NV21)
				.initialize()
			if (!result) {
				arcSoft2XEngine = null
			}
			return arcSoft2XEngine
		}
	}
}
