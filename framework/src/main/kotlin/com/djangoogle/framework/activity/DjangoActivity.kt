package com.djangoogle.framework.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.djangoogle.framework.R
import com.djangoogle.framework.manager.LoadingManager
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Activity基类
 * 所有Activity必须继承此类
 * Created by Djangoogle on 2018/10/11 10:21 with Android Studio.
 */
abstract class DjangoActivity : RxAppCompatActivity() {

	companion object {

		private val TAG = DjangoActivity::class.simpleName
	}

	protected lateinit var clBaseRootView: CoordinatorLayout//根View
	protected lateinit var ablCommonToolBar: AppBarLayout//通用ToolBar根布局
	protected lateinit var tbCommon: Toolbar//通用ToolBar
	protected lateinit var acibToolBarBackBtn: AppCompatImageButton//返回键
	protected lateinit var acivToolBarAvatar: AppCompatImageView//头像
	protected lateinit var actvToolBarTitle: AppCompatTextView//标题
	protected lateinit var acetToolBarInput: AppCompatEditText//输入框
	protected lateinit var actvToolBarRightTextBtn: AppCompatTextView//右侧文字按钮
	protected lateinit var acibToolBarRightImgBtn: AppCompatImageButton//右侧图标按钮
	protected lateinit var flBaseBodyView: FrameLayout//bodyview
	protected lateinit var fabBaseBottomRightBtn: FloatingActionButton//右下角浮动按钮
	protected lateinit var fabBaseBottomLeftBtn: FloatingActionButton//左下角浮动按钮
	protected lateinit var mActivity: Activity//通用Activity
	protected var mUseBaseLayoutFlag = true//是否使用基础布局
	protected var mCompositeDisposable: CompositeDisposable = CompositeDisposable()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mActivity = this
		if (mUseBaseLayoutFlag) {//使用基础布局
			setContentView(R.layout.activity_base)
			//设置布局
			clBaseRootView = findViewById(R.id.clBaseRootView)
			ablCommonToolBar = findViewById(R.id.ablCommonToolBar)
			tbCommon = findViewById(R.id.tbCommon)
			acibToolBarBackBtn = findViewById(R.id.acibToolBarBackBtn)
			acivToolBarAvatar = findViewById(R.id.acivToolBarAvatar)
			actvToolBarTitle = findViewById(R.id.actvToolBarTitle)
			acetToolBarInput = findViewById(R.id.acetToolBarInput)
			actvToolBarRightTextBtn = findViewById(R.id.actvToolBarRightTextBtn)
			acibToolBarRightImgBtn = findViewById(R.id.acibToolBarRightImgBtn)
			flBaseBodyView = findViewById(R.id.flBaseBodyView)
			fabBaseBottomRightBtn = findViewById(R.id.fabBaseBottomRightBtn)
			fabBaseBottomLeftBtn = findViewById(R.id.fabBaseBottomLeftBtn)
			//添加子布局
			if (0 != initLayout()) {
				LayoutInflater.from(this).inflate(initLayout(), flBaseBodyView, true)
			}
			//返回键点击事件
			acibToolBarBackBtn.setOnClickListener { onBackPressed() }
		} else {
			if (0 != initLayout()) {//使用自定义布局
				setContentView(initLayout())
			}
		}
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
		mCompositeDisposable.clear()
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
		view?.clicks()?.throttleFirst(2L, TimeUnit.SECONDS)?.bindToLifecycle(this)?.subscribe(onNext)?.let { mCompositeDisposable.add(it) }
	}

	/**
	 * 按钮可重复点击
	 */
	protected fun repeatClicks(view: View?, onNext: Consumer<in Unit>?) {
		view?.clicks()?.bindToLifecycle(this)?.subscribe(onNext)?.let { mCompositeDisposable.add(it) }
	}

	/**
	 * 按钮长按事件
	 */
	protected fun onLongClicks(view: View?, onNext: Consumer<in Unit>?) {
		view?.longClicks()?.bindToLifecycle(this)?.subscribe(onNext)?.let { mCompositeDisposable.add(it) }
	}

	/**
	 * 隐藏标题栏
	 */
	protected fun hideToolbar() {
		ablCommonToolBar.visibility = View.GONE
	}

	/**
	 * 隐藏返回键
	 */
	protected fun hideBackBtn() {
		if (mUseBaseLayoutFlag) {
			acibToolBarBackBtn.visibility = View.GONE
		}
	}

	/**
	 * 显示头像
	 */
	protected fun showAvatar() {
		if (mUseBaseLayoutFlag) {
			acivToolBarAvatar.visibility = View.VISIBLE
		}
	}

	/**
	 * 设置标题
	 *
	 * @param title 标题文字
	 */
	protected fun setTitle(title: String) {
		if (mUseBaseLayoutFlag) {
			actvToolBarTitle.visibility = View.VISIBLE
			actvToolBarTitle.text = title
		}
	}

	/**
	 * 设置输入框
	 *
	 * @param hint 提示文字
	 */
	protected fun setInput(hint: String) {
		if (mUseBaseLayoutFlag) {
			acetToolBarInput.visibility = View.VISIBLE
			acetToolBarInput.hint = hint
		}
	}

	/**
	 * 设置右侧文字按钮
	 *
	 * @param visibility 可见度
	 * @param text       按钮文字
	 * @param listener   点击事件
	 */
	protected fun setRightTextBtn(visibility: Int, text: String, listener: View.OnClickListener?) {
		if (mUseBaseLayoutFlag) {
			actvToolBarRightTextBtn.visibility = visibility
			actvToolBarRightTextBtn.text = text
			if (null != listener) {
				actvToolBarRightTextBtn.setOnClickListener(listener)
			}
		}
	}

	/**
	 * 设置右侧图标按钮
	 *
	 * @param resId    图标资源文件
	 * @param listener 点击事件
	 */
	protected fun setRightImgBtn(resId: Int, listener: View.OnClickListener?) {
		if (mUseBaseLayoutFlag) {
			acibToolBarRightImgBtn.visibility = View.VISIBLE
			acibToolBarRightImgBtn.setImageResource(resId)
			if (null != listener) {
				acibToolBarRightImgBtn.setOnClickListener(listener)
			}
		}
	}

	/**
	 * 设置右下角浮动按钮
	 *
	 * @param resId    图标资源文件
	 * @param listener 点击事件
	 */
	protected fun setBottomRightFloatingBtn(resId: Int, listener: View.OnClickListener?) {
		if (mUseBaseLayoutFlag) {
			fabBaseBottomRightBtn.show()
			fabBaseBottomRightBtn.setImageResource(resId)
			if (null != listener) {
				fabBaseBottomRightBtn.setOnClickListener(listener)
			}
		}
	}

	/**
	 * 设置左下角浮动按钮
	 *
	 * @param resId    图标资源文件
	 * @param listener 点击事件
	 */
	protected fun setBottomLeftFloatingBtn(resId: Int, listener: View.OnClickListener?) {
		if (mUseBaseLayoutFlag) {
			fabBaseBottomLeftBtn.setImageResource(resId)
			if (null != listener) {
				fabBaseBottomLeftBtn.setOnClickListener(listener)
			}
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
