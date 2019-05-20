package com.djangoogle.framework.retrofit.exception

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.io.UnsupportedEncodingException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by Djangoogle on 2019/05/17 19:01 with Android Studio.
 */
class DjangoException {

	companion object {

		//未知错误
		private const val UNKNOWN = 0x1000

		//解析错误
		private const val PARSE_ERROR = 0x1001

		//网络错误
		private const val NETWORK_ERROR = 0x1002

		//协议错误
		private const val HTTP_ERROR = 0x1003

		fun handleException(e: Throwable): ApiException {
			val ex: ApiException
			when (e) {
				is JsonParseException, is JSONException, is ParseException -> {
					//解析错误
					ex = ApiException(PARSE_ERROR, e.message)
					return ex
				}
				is ConnectException -> {
					//网络错误
					ex = ApiException(NETWORK_ERROR, e.message)
					return ex
				}
				is UnknownHostException, is SocketTimeoutException -> {
					//连接错误
					ex = ApiException(NETWORK_ERROR, e.message)
					return ex
				}
				is UnsupportedEncodingException, is HttpException -> {
					//协议错误
					ex = ApiException(HTTP_ERROR, e.message)
					return ex
				}
				else -> {
					//未知错误
					ex = ApiException(UNKNOWN, e.message)
					return ex
				}
			}
		}
	}
}
