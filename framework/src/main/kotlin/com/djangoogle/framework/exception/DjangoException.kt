package com.djangoogle.framework.exception

import android.net.ParseException
import com.djangoogle.framework.exception.DjangoThrowable.Companion.CONNECT_ERROR
import com.djangoogle.framework.exception.DjangoThrowable.Companion.HTTP_ERROR
import com.djangoogle.framework.exception.DjangoThrowable.Companion.PARSE_ERROR
import com.djangoogle.framework.exception.DjangoThrowable.Companion.TIME_OUT
import com.djangoogle.framework.exception.DjangoThrowable.Companion.UNKNOWN
import com.djangoogle.framework.exception.DjangoThrowable.Companion.UNKNOWN_HOST
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

		fun handleException(throwable: Throwable): Throwable {
			val djangoThrowable: DjangoThrowable
			when (throwable) {
				//解析错误
				is JsonParseException, is JSONException, is ParseException -> {
					djangoThrowable = DjangoThrowable(PARSE_ERROR, "解析错误", throwable)
					return djangoThrowable
				}
				//网络异常
				is ConnectException -> {
					djangoThrowable = DjangoThrowable(CONNECT_ERROR, "网络异常", throwable)
					return djangoThrowable
				}
				//连接超时
				is SocketTimeoutException -> {
					djangoThrowable = DjangoThrowable(TIME_OUT, "连接超时", throwable)
					return djangoThrowable
				}
				//服务地址异常
				is UnknownHostException -> {
					djangoThrowable = DjangoThrowable(UNKNOWN_HOST, "服务地址异常", throwable)
					return djangoThrowable
				}
				//协议错误
				is UnsupportedEncodingException, is HttpException -> {
					djangoThrowable = DjangoThrowable(HTTP_ERROR, "协议错误", throwable)
					return djangoThrowable
				}
				//未知错误
				else -> {
					djangoThrowable = DjangoThrowable(UNKNOWN, "未知错误", throwable)
					return djangoThrowable
				}
			}
		}
	}
}
