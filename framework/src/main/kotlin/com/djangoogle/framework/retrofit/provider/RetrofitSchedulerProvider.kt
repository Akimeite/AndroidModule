package com.djangoogle.framework.retrofit.provider

import com.djangoogle.framework.retrofit.impl.DjangoSchedulerProvider
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Djangoogle on 2019/05/17 19:23 with Android Studio.
 */
class RetrofitSchedulerProvider private constructor() : DjangoSchedulerProvider {

	companion object {
		val INSTANCE: RetrofitSchedulerProvider by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
			RetrofitSchedulerProvider()
		}
	}

	override fun computation(): Scheduler {
		return Schedulers.computation()
	}

	override fun io(): Scheduler {
		return Schedulers.io()
	}

	override fun ui(): Scheduler {
		return AndroidSchedulers.mainThread()
	}

	override fun <T, E> applyLifecycle(provider: LifecycleProvider<E>): ObservableTransformer<T, T> {
		return ObservableTransformer<T, T> { upstream: Observable<T> ->
			upstream.subscribeOn(io()).observeOn(ui()).bindToLifecycle(provider)
		}
	}

	override fun <T> applyUI(): ObservableTransformer<T, T> {
		return ObservableTransformer<T, T> { upstream: Observable<T> ->
			upstream.subscribeOn(io()).observeOn(ui())
		}
	}

	override fun <T> applyIO(): ObservableTransformer<T, T> {
		return ObservableTransformer<T, T> { upstream: Observable<T> ->
			upstream.subscribeOn(io()).observeOn(io())
		}
	}
}
