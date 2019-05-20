package com.djangoogle.framework.retrofit.model

/**
 * Created by Djangoogle on 2019/05/20 08:25 with Android Studio.
 */
abstract class DjangoRequest<T> {

	var code = -1
	var message = ""
	var data: T? = null
}
