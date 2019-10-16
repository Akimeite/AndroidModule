package com.djangoogle.framework.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.djangoogle.framework.manager.LoadingManager
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.trello.rxlifecycle3.components.support.RxFragment
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.functions.Consumer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Fragment基类
 * Created by Djangoogle on 2018/11/12 18:10 with Android Studio.
 */
@SuppressLint("CheckResult")
abstract class DjangoFragment : RxFragment() {

	//通用Activity
	protected lateinit var mActivity: Activity
	//界面是否初始化完毕
	private var isPrepared: Boolean = false
	//第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
	private var isFirstResume = true
	//Fragment是否首次可见
	private var isFirstVisible = true
	//Fragment是否首次不可见
	private var isFirstInvisible = true

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		mActivity = activity as FragmentActivity
	}

	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		val view: View = LayoutInflater.from(mActivity).inflate(initLayout(), null)
		//初始化ButterKnife
		initButterKnife(view)
		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		//设置界面
		initGUI()
		//设置事件
		initAction()
		//初始化准备状态
		initPrepare()
	}

	override fun onResume() {
		super.onResume()
		if (isFirstResume) {
			isFirstResume = false
			return
		}
		if (userVisibleHint) {
			onUserVisible()
		}
	}

	override fun onPause() {
		super.onPause()
		if (userVisibleHint) {
			onUserInvisible()
		}
	}

	override fun onStart() {
		super.onStart()
		//注册EventBus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this)
		}
	}

	override fun onStop() {
		super.onStop()
		//注销EventBus
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this)
		}
		hideLoading()
	}

	override fun setUserVisibleHint(isVisibleToUser: Boolean) {
		super.setUserVisibleHint(isVisibleToUser)
		if (isVisibleToUser) {
			if (isFirstVisible) {
				isFirstVisible = false
				//初始化准备状态
				initPrepare()
			} else {
				onUserVisible()
			}
		} else {
			if (isFirstInvisible) {
				isFirstInvisible = false
				onFirstUserInvisible()
			} else {
				onUserInvisible()
			}
		}
	}

	/**
	 * 初始化ButterKnife
	 */
	protected abstract fun initButterKnife(view: View)

	/**
	 * 设置布局
	 *
	 * @return 布局id
	 */
	protected abstract fun initLayout(): Int

	/**
	 * 设置界面
	 */
	protected abstract fun initGUI()

	/**
	 * 设置事件
	 */
	protected abstract fun initAction()

	/**
	 * 设置数据
	 */
	protected abstract fun initData()

	/**
	 * 按钮防重复点击
	 */
	protected fun singleClicks(view: View, onNext: Consumer<in Unit>) {
		view.clicks().throttleFirst(2L, TimeUnit.SECONDS).bindToLifecycle(this).subscribe(onNext)
	}

	/**
	 * 按钮可重复点击
	 */
	protected fun repeatClicks(view: View, onNext: Consumer<in Unit>) {
		view.clicks().bindToLifecycle(this).subscribe(onNext)
	}

	/**
	 * 按钮长按事件
	 */
	protected fun onLongClicks(view: View, onNext: Consumer<in Unit>) {
		view.longClicks().bindToLifecycle(this).subscribe(onNext)
	}

	/**
	 * 初始化准备状态
	 */
	@Synchronized
	protected fun initPrepare() {
		if (isPrepared) {
			onFirstUserVisible()
		} else {
			isPrepared = true
		}
	}

	/**
	 * 第一次fragment可见（进行初始化工作）
	 */
	protected fun onFirstUserVisible() {
		initData()
	}

	/**
	 * fragment可见（切换回来或者onResume）
	 */
	protected fun onUserVisible() {}

	/**
	 * 第一次fragment不可见（不建议在此处理事件）
	 */
	protected fun onFirstUserInvisible() {}

	/**
	 * fragment不可见（切换掉或者onPause）
	 */
	protected fun onUserInvisible() {}

	/**
	 * 显示Loading
	 */
	protected fun showLoading() {
		LoadingManager.INSTANCE.show(fragmentManager)
	}

	/**
	 * 隐藏Loading
	 */
	protected fun hideLoading() {
		LoadingManager.INSTANCE.hide()
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onBaseMessageEvent(event: Any) {
	}
}
