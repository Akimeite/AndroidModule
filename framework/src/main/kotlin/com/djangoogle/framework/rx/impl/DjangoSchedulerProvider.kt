package com.djangoogle.framework.rx.impl

import com.trello.rxlifecycle3.LifecycleProvider
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler

/**
 * Created by Djangoogle on 2019/05/17 19:21 with Android Studio.
 */
interface DjangoSchedulerProvider {

	fun computation(): Scheduler

	fun io(): Scheduler

	fun ui(): Scheduler

	fun <T, E> applyLifecycle(provider: LifecycleProvider<E>): ObservableTransformer<T, T>

	fun <T> applyUI(): ObservableTransformer<T, T>

	fun <T> applyIO(): ObservableTransformer<T, T>
}
