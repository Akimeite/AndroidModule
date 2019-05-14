package com.djangoogle.framework.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djangoogle.framework.manager.LoadingManager
import com.trello.rxlifecycle2.components.support.RxFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Fragment基类
 * Created by Djangoogle on 2018/11/12 18:10 with Android Studio.
 */
abstract class DjangoFragment : RxFragment() {

	//通用Activity
	protected var mActivity: Activity? = null
	//界面是否初始化完毕
	private var isPrepared: Boolean = false
	//第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
	private var isFirstResume = true
	//Fragment是否首次可见
	private var isFirstVisible = true
	//Fragment是否首次不可见
	private var isFirstInvisible = true

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mActivity = activity
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		//初始化布局
		if (0 != initLayout()) {
			return LayoutInflater.from(mActivity).inflate(initLayout(), null)
		}
		return super.onCreateView(inflater, container, savedInstanceState)
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
