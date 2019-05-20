package com.djangoogle.framework.retrofit.exception

/**
 * Created by Djangoogle on 2019/05/17 18:58 with Android Studio.
 */
class ApiException : Exception {

	var code: Int = -1
	var displayMessage: String? = ""

	constructor(code: Int, displayMessage: String?) {
		this.code = code
		this.displayMessage = displayMessage
	}

	constructor(code: Int, message: String, displayMessage: String?) : super(message) {
		this.code = code
		this.displayMessage = displayMessage
	}
}
