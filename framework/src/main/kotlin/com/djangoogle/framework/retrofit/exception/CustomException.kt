package com.djangoogle.framework.retrofit.exception

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by Djangoogle on 2019/05/17 19:01 with Android Studio.
 */
class CustomException {

	companion object {

		//操作成功
		const val SUCCESS = 1000

		//操作失败
		const val ERROR = 1001

		//参数错误
		const val PARAMETER_ERROR = 1002

		//未知错误
		const val UNKNOWN = 0x1000

		//解析错误
		const val PARSE_ERROR = 0x1001

		//网络错误
		const val NETWORK_ERROR = 0x1002

		//协议错误
		const val HTTP_ERROR = 0x1003

		fun handleException(e: Throwable): ApiException {
			val ex: ApiException
			if (e is JsonParseException || e is JSONException || e is ParseException) {
				//解析错误
				ex = ApiException(PARSE_ERROR, e.message)
				return ex
			} else if (e is ConnectException) {
				//网络错误
				ex = ApiException(NETWORK_ERROR, e.message)
				return ex
			} else if (e is UnknownHostException || e is SocketTimeoutException) {
				//连接错误
				ex = ApiException(NETWORK_ERROR, e.message)
				return ex
			} else {
				//未知错误
				ex = ApiException(UNKNOWN, e.message)
				return ex
			}
		}
	}
}
