package com.djangoogle.framework.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.djangoogle.framework.R
import com.djangoogle.framework.manager.LoadingManager
import com.google.android.material.appbar.AppBarLayout
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_django.*
import kotlinx.android.synthetic.main.toolbar_django.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Activity基类
 * 所有Activity必须继承此类
 * Created by Djangoogle on 2018/10/11 10:21 with Android Studio.
 */
@SuppressLint("CheckResult")
abstract class DjangoActivity : RxAppCompatActivity() {

	protected lateinit var mDjangoRootView: CoordinatorLayout//根视图
	protected lateinit var mDjangoToolBarRootView: AppBarLayout//通用ToolBar根视图
	protected lateinit var mDjangoToolBar: Toolbar//通用ToolBar
	protected lateinit var mDjangoToolBarBackBtn: AppCompatImageButton//返回键
	protected lateinit var mDjangoToolBarAvatar: AppCompatImageView //头像
	protected lateinit var mDjangoToolBarTitle: AppCompatTextView//标题
	protected lateinit var mDjangoToolBarInput: AppCompatEditText//输入框
	protected lateinit var mDjangoToolBarRightTextBtn: AppCompatTextView//右侧文字按钮
	protected lateinit var mDjangoToolBarRightImgBtn: AppCompatImageButton//右侧图标按钮
	protected lateinit var mDjangoChildView: FrameLayout//子布局视图
	protected lateinit var mActivity: Activity//通用Activity

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mActivity = this
		setContentView(R.layout.activity_django)
		//设置布局
		mDjangoRootView = clDjangoRootView
		mDjangoToolBarRootView = ablDjangoToolBar
		mDjangoToolBar = tbDjangoToolbar
		mDjangoToolBarBackBtn = acibDjangoToolBarBackBtn
		mDjangoToolBarAvatar = acivDjangoToolBarAvatar
		mDjangoToolBarTitle = actvDjangoToolBarTitle
		mDjangoToolBarInput = acetDjangoToolBarInput
		mDjangoToolBarRightTextBtn = actvDjangoToolBarRightTextBtn
		mDjangoToolBarRightImgBtn = acibDjangoToolBarRightImgBtn
		mDjangoChildView = flDjangoChildView
		//添加子布局
		LayoutInflater.from(this).inflate(initLayout(), mDjangoChildView, true)
		//初始化ButterKnife
		initButterKnife()
		//返回键点击事件
		acibDjangoToolBarBackBtn.setOnClickListener { onBackPressed() }
		//修复修复安卓5497键盘bug
		KeyboardUtils.fixAndroidBug5497(this)
		//修复软键盘内存泄漏
		KeyboardUtils.fixSoftInputLeaks(this)
		//设置界面
		initGUI()
		//设置事件
		initAction()
		//设置数据
		initData()
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
	}

	override fun onDestroy() {
		super.onDestroy()
		//隐藏Loading
		hideLoading()
	}

	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
		if (MotionEvent.ACTION_DOWN == ev?.action) {
			//按下时隐藏键盘
			if (isShouldHideKeyboard(currentFocus, ev)) {
				val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
			}
		}
		return super.dispatchTouchEvent(ev)
	}

	/**
	 * 初始化ButterKnife
	 */
	protected abstract fun initButterKnife()

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
	protected fun singleClicks(view: View?, onNext: Consumer<in Unit>?) {
		view?.clicks()?.throttleFirst(2L, TimeUnit.SECONDS)?.bindToLifecycle(this)?.subscribe(onNext)
	}

	/**
	 * 按钮可重复点击
	 */
	protected fun repeatClicks(view: View?, onNext: Consumer<in Unit>?) {
		view?.clicks()?.bindToLifecycle(this)?.subscribe(onNext)
	}

	/**
	 * 按钮长按事件
	 */
	protected fun onLongClicks(view: View?, onNext: Consumer<in Unit>?) {
		view?.longClicks()?.bindToLifecycle(this)?.subscribe(onNext)
	}

	/**
	 * 显示标题栏
	 */
	protected fun showToolbar() {
		ablDjangoToolBar.visibility = View.VISIBLE
	}

	/**
	 * 隐藏返回键
	 */
	protected fun hideBackBtn() {
		acibDjangoToolBarBackBtn.visibility = View.GONE
	}

	/**
	 * 显示头像
	 */
	protected fun showAvatar() {
		acivDjangoToolBarAvatar.visibility = View.VISIBLE
	}

	/**
	 * 设置标题
	 *
	 * @param title 标题文字
	 */
	protected fun setTitle(title: String) {
		actvDjangoToolBarTitle.visibility = View.VISIBLE
		actvDjangoToolBarTitle.text = title
	}

	/**
	 * 设置输入框
	 *
	 * @param hint 提示文字
	 */
	protected fun setInput(hint: String) {
		acetDjangoToolBarInput.visibility = View.VISIBLE
		acetDjangoToolBarInput.hint = hint
	}

	/**
	 * 设置右侧文字按钮
	 *
	 * @param visibility 可见度
	 * @param text       按钮文字
	 * @param listener   点击事件
	 */
	protected fun setRightTextBtn(visibility: Int, text: String, listener: View.OnClickListener?) {
		actvDjangoToolBarRightTextBtn.visibility = visibility
		actvDjangoToolBarRightTextBtn.text = text
		if (null != listener) {
			actvDjangoToolBarRightTextBtn.setOnClickListener(listener)
		}
	}

	/**
	 * 设置右侧图标按钮
	 *
	 * @param resId    图标资源文件
	 * @param listener 点击事件
	 */
	protected fun setRightImgBtn(resId: Int, listener: View.OnClickListener?) {
		acibDjangoToolBarRightImgBtn.visibility = View.VISIBLE
		acibDjangoToolBarRightImgBtn.setImageResource(resId)
		if (null != listener) {
			acibDjangoToolBarRightImgBtn.setOnClickListener(listener)
		}
	}

	/**
	 * 显示Loading
	 */
	protected fun showLoading() {
		LoadingManager.INSTANCE.show(supportFragmentManager)
	}

	/**
	 * 隐藏Loading
	 */
	protected fun hideLoading() {
		LoadingManager.INSTANCE.hide()
	}

	/**
	 * 是否隐藏键盘
	 */
	private fun isShouldHideKeyboard(view: View?, event: MotionEvent): Boolean {
		if (view is EditText) {
			val outLocation: IntArray = intArrayOf(0, 0)
			view.getLocationInWindow(outLocation)
			val left = outLocation[0]
			val top = outLocation[1]
			val bottom = top + view.height
			val right = left + view.width
			return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
		}
		return false
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onBaseMessageEvent(event: Any) {
	}
}
