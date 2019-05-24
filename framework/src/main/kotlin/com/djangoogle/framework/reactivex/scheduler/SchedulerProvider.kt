package com.djangoogle.framework.reactivex.scheduler

import com.djangoogle.framework.reactivex.impl.DjangoSchedulerProvider
import com.trello.rxlifecycle3.LifecycleProvider
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

/**
 * Created by Djangoogle on 2019/05/17 19:23 with Android Studio.
 */
class SchedulerProvider private constructor() : DjangoSchedulerProvider {

	companion object {
		val INSTANCE: SchedulerProvider by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
			SchedulerProvider()
		}
	}

	override fun computation(): Scheduler {
		return Schedulers.computation()
	}

	override fun io(): Scheduler {
		return Schedulers.io()
	}

	override fun trampoline(): Scheduler {
		return Schedulers.trampoline()
	}

	override fun newThread(): Scheduler {
		return Schedulers.newThread()
	}

	override fun single(): Scheduler {
		return Schedulers.single()
	}

	override fun from(executor: Executor): Scheduler {
		return Schedulers.from(executor)
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
