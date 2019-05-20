package com.djangoogle.framework.retrofit.transformer

import com.djangoogle.framework.retrofit.exception.ApiException
import com.djangoogle.framework.retrofit.exception.CustomException
import com.djangoogle.framework.retrofit.model.DjangoRequest
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function

/**
 * Created by Djangoogle on 2019/05/17 18:57 with Android Studio.
 */
class ResponseTransformer {

	companion object {

		fun <T> handleResult(): ObservableTransformer<DjangoRequest<T>, T> {
			return ObservableTransformer { upstream -> upstream.onErrorResumeNext(ErrorResumeFunction()).flatMap(ResponseFunction()) }
		}

		/**
		 * 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误等等。
		 *
		 * @param <T>
		 */
		private class ErrorResumeFunction<T> : Function<Throwable, ObservableSource<out DjangoRequest<T>>> {
			override fun apply(throwable: Throwable): ObservableSource<out DjangoRequest<T>> {
				return Observable.error<DjangoRequest<T>>(CustomException.handleException(throwable))
			}
		}

		/**
		 * 服务其返回的数据解析
		 * 正常服务器返回数据和服务器可能返回的exception
		 *
		 * @param <T>
		 */
		private class ResponseFunction<T> : Function<DjangoRequest<T>, ObservableSource<T>> {
			override fun apply(protocol: DjangoRequest<T>): ObservableSource<T> {
				val code = protocol.code
				val message = protocol.message
				return if (CustomException.SUCCESS == code) {
					Observable.just(protocol.data)
				} else {
					Observable.error(ApiException(code, message))
				}
			}
		}
	}
}
