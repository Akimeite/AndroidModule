package com.djangoogle.framework.reactivex.impl

import com.trello.rxlifecycle3.LifecycleProvider
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import java.util.concurrent.Executor

/**
 * Created by Djangoogle on 2019/05/17 19:21 with Android Studio.
 */
interface DjangoSchedulerProvider {

	fun computation(): Scheduler

	fun io(): Scheduler

	fun trampoline(): Scheduler

	fun newThread(): Scheduler

	fun single(): Scheduler

	fun from(executor: Executor): Scheduler

	fun ui(): Scheduler

	fun <T, E> applyLifecycle(provider: LifecycleProvider<E>): ObservableTransformer<T, T>

	fun <T> applyUI(): ObservableTransformer<T, T>

	fun <T> applyIO(): ObservableTransformer<T, T>
}
