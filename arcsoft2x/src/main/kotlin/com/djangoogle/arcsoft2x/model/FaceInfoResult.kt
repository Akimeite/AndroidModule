package com.djangoogle.arcsoft2x.model

import android.graphics.Rect
import com.arcsoft.face.FaceInfo

/**
 * Created by Djangoogle on 2019/05/23 17:28 with Android Studio.
 */
class FaceInfoResult(
		var code: Int,
		var message: String,
		var liveness: Boolean,
		var faceInfo: FaceInfo?,
		var nv21: ByteArray?
) {

	var rect: Rect? = null
}
