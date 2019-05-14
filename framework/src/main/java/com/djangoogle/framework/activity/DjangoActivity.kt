package com.djangoogle.framework.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.LogUtils
import com.djangoogle.framework.R
import com.djangoogle.framework.util.LoadingManager
import com.djangoogle.framework.util.NoDoubleClickUtils
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Activity基类
 * 所有Activity必须继承此类
 * Created by Djangoogle on 2018/10/11 10:21 with Android Studio.
 */
abstract class DjangoActivity : RxAppCompatActivity() {

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

	private var mUseBaseLayoutFlag = true//是否使用基础布局
	private var mNoDoubleClickFlag = true//是否使用防重复打开Activity

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
				//设置ButterKnife
				initButterKnife()
			}
			//返回键点击事件
			acibToolBarBackBtn.setOnClickListener { onBackPressed() }
		} else {
			if (0 != initLayout()) {//使用自定义布局
				setContentView(initLayout())
				//设置ButterKnife
				initButterKnife()
			}
		}
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

	/**
	 * 使用自定义布局
	 */
	protected fun useCustomLayout() {
		mUseBaseLayoutFlag = false
	}

	/**
	 * 设置ButterKnife
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
		LoadingManager.getInstance().show(supportFragmentManager)
	}

	/**
	 * 隐藏Loading
	 */
	protected fun hideLoading() {
		LoadingManager.getInstance().hide()
	}

	/**
	 * 打开Activity，并防止重复连续点击
	 *
	 * @param intent 意图
	 */
	override fun startActivity(intent: Intent) {
		if (mNoDoubleClickFlag && NoDoubleClickUtils.isDoubleClick()) {
			LogUtils.d("重复调用startActivity()，点击间隔时间不得小于" + NoDoubleClickUtils.INTERVAL + "ms")
			return
		}
		super.startActivity(intent)
	}

	/**
	 * 设置防重复打开Activity
	 *
	 * @param noDoubleClickFlag
	 */
	protected fun setNoDoubleClickFlag(noDoubleClickFlag: Boolean) {
		mNoDoubleClickFlag = noDoubleClickFlag
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onBaseMessageEvent(event: Any) {
	}
}
