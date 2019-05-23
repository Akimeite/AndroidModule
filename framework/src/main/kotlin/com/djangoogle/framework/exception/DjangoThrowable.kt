package com.djangoogle.framework.exception

/**
 * Created by Djangoogle on 2019/05/17 18:58 with Android Studio.
 */
class DjangoThrowable : Throwable {

	companion object {

		//未知错误
		const val UNKNOWN = 0x1000

		//解析错误
		const val PARSE_ERROR = 0x1001

		//网络错误
		const val CONNECT_ERROR = 0x1003

		//连接超时
		const val TIME_OUT = 0x1004

		//服务地址异常
		const val UNKNOWN_HOST = 0x1005

		//协议错误
		const val HTTP_ERROR = 0x1006
	}

	private var mCode: Int = UNKNOWN

	constructor(code: Int, message: String?) : super(message) {
		mCode = code
	}

	constructor(code: Int, message: String?, cause: Throwable?) : super(message, cause) {
		mCode = code
	}
}
