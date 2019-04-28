package com.djangoogle.framework.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.LogUtils;
import com.djangoogle.framework.R;
import com.djangoogle.framework.util.LoadingManager;
import com.djangoogle.framework.util.NoDoubleClickUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Activity基类
 * 所有Activity必须继承此类
 * Created by Djangoogle on 2018/10/11 10:21 with Android Studio.
 */
public abstract class DjangoogleActivity extends RxAppCompatActivity {

	protected CoordinatorLayout clBaseRootView;//根View
	protected AppBarLayout ablCommonToolBar;//通用ToolBar根布局
	protected Toolbar tbCommon;//通用ToolBar
	protected AppCompatImageButton acibToolBarBackBtn;//返回键
	protected AppCompatImageView acivToolBarAvatar;//头像
	protected AppCompatTextView actvToolBarTitle;//标题
	protected AppCompatEditText acetToolBarInput;//输入框
	protected AppCompatTextView actvToolBarRightTextBtn;//右侧文字按钮
	protected AppCompatImageButton acibToolBarRightImgBtn;//右侧图标按钮
	protected FrameLayout flBaseBodyView;//bodyview
	protected FloatingActionButton fabBaseBottomRightBtn;//右下角浮动按钮
	protected FloatingActionButton fabBaseBottomLeftBtn;//左下角浮动按钮
	protected Activity mActivity;//通用Activity

	private boolean mUseBaseLayoutFlag = true;//是否使用基础布局
	private boolean mNoDoubleClickFlag = true;//是否使用防重复打开Activity

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		if (mUseBaseLayoutFlag) {//使用基础布局
			setContentView(R.layout.activity_base);
			//设置布局
			clBaseRootView = findViewById(R.id.clBaseRootView);
			ablCommonToolBar = findViewById(R.id.ablCommonToolBar);
			tbCommon = findViewById(R.id.tbCommon);
			acibToolBarBackBtn = findViewById(R.id.acibToolBarBackBtn);
			acivToolBarAvatar = findViewById(R.id.acivToolBarAvatar);
			actvToolBarTitle = findViewById(R.id.actvToolBarTitle);
			acetToolBarInput = findViewById(R.id.acetToolBarInput);
			actvToolBarRightTextBtn = findViewById(R.id.actvToolBarRightTextBtn);
			acibToolBarRightImgBtn = findViewById(R.id.acibToolBarRightImgBtn);
			flBaseBodyView = findViewById(R.id.flBaseBodyView);
			fabBaseBottomRightBtn = findViewById(R.id.fabBaseBottomRightBtn);
			fabBaseBottomLeftBtn = findViewById(R.id.fabBaseBottomLeftBtn);
			//添加子布局
			if (0 != initLayout()) {
				LayoutInflater.from(this).inflate(initLayout(), flBaseBodyView, true);
				//设置ButterKnife
				initButterKnife();
			}
			//返回键点击事件
			acibToolBarBackBtn.setOnClickListener(v -> onBackPressed());
		} else {
			if (0 != initLayout()) {//使用自定义布局
				setContentView(initLayout());
				//设置ButterKnife
				initButterKnife();
			}
		}
		//设置界面
		initGUI();
		//设置事件
		initAction();
		//设置数据
		initData();
	}

	@Override
	protected void onStart() {
		super.onStart();
		//注册EventBus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		//注销EventBus
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
		//隐藏Loading
		hideLoading();
	}

	/**
	 * 使用自定义布局
	 */
	protected void useCustomLayout() {
		mUseBaseLayoutFlag = false;
	}

	/**
	 * 设置ButterKnife
	 */
	protected abstract void initButterKnife();

	/**
	 * 设置布局
	 *
	 * @return 布局id
	 */
	protected abstract int initLayout();

	/**
	 * 设置界面
	 */
	protected abstract void initGUI();

	/**
	 * 设置事件
	 */
	protected abstract void initAction();

	/**
	 * 设置数据
	 */
	protected abstract void initData();

	/**
	 * 隐藏返回键
	 */
	protected void hideBackBtn() {
		if (mUseBaseLayoutFlag) {
			acibToolBarBackBtn.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示头像
	 */
	protected void showAvatar() {
		if (mUseBaseLayoutFlag) {
			acivToolBarAvatar.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置标题
	 *
	 * @param title 标题文字
	 */
	protected void setTitle(String title) {
		if (mUseBaseLayoutFlag) {
			actvToolBarTitle.setVisibility(View.VISIBLE);
			actvToolBarTitle.setText(title);
		}
	}

	/**
	 * 设置输入框
	 *
	 * @param hint 提示文字
	 */
	protected void setInput(String hint) {
		if (mUseBaseLayoutFlag) {
			acetToolBarInput.setVisibility(View.VISIBLE);
			acetToolBarInput.setHint(hint);
		}
	}

	/**
	 * 设置右侧文字按钮
	 *
	 * @param visibility 可见度
	 * @param text       按钮文字
	 * @param listener   点击事件
	 */
	protected void setRightTextBtn(int visibility, String text, View.OnClickListener listener) {
		if (mUseBaseLayoutFlag) {
			actvToolBarRightTextBtn.setVisibility(visibility);
			actvToolBarRightTextBtn.setText(text);
			if (null != listener) {
				actvToolBarRightTextBtn.setOnClickListener(listener);
			}
		}
	}

	/**
	 * 设置右侧图标按钮
	 *
	 * @param resId    图标资源文件
	 * @param listener 点击事件
	 */
	protected void setRightImgBtn(int resId, View.OnClickListener listener) {
		if (mUseBaseLayoutFlag) {
			acibToolBarRightImgBtn.setVisibility(View.VISIBLE);
			acibToolBarRightImgBtn.setImageResource(resId);
			if (null != listener) {
				acibToolBarRightImgBtn.setOnClickListener(listener);
			}
		}
	}

	/**
	 * 设置右下角浮动按钮
	 *
	 * @param resId    图标资源文件
	 * @param listener 点击事件
	 */
	protected void setBottomRightFloatingBtn(int resId, View.OnClickListener listener) {
		if (mUseBaseLayoutFlag) {
			fabBaseBottomRightBtn.show();
			fabBaseBottomRightBtn.setImageResource(resId);
			if (null != listener) {
				fabBaseBottomRightBtn.setOnClickListener(listener);
			}
		}
	}

	/**
	 * 设置左下角浮动按钮
	 *
	 * @param resId    图标资源文件
	 * @param listener 点击事件
	 */
	protected void setBottomLeftFloatingBtn(int resId, View.OnClickListener listener) {
		if (mUseBaseLayoutFlag) {
			fabBaseBottomLeftBtn.setImageResource(resId);
			if (null != listener) {
				fabBaseBottomLeftBtn.setOnClickListener(listener);
			}
		}
	}

	/**
	 * 显示Loading
	 */
	protected void showLoading() {
		LoadingManager.getInstance().show(mActivity);
	}

	/**
	 * 隐藏Loading
	 */
	protected void hideLoading() {
		LoadingManager.getInstance().hide();
	}

	/**
	 * 打开Activity，并防止重复连续点击
	 *
	 * @param intent 意图
	 */
	public void startActivity(Intent intent) {
		if (mNoDoubleClickFlag && NoDoubleClickUtils.isDoubleClick()) {
			LogUtils.d("重复调用startActivity()，点击间隔时间不得小于" + NoDoubleClickUtils.INTERVAL + "ms");
			return;
		}
		super.startActivity(intent);
	}

	/**
	 * 设置防重复打开Activity
	 *
	 * @param noDoubleClickFlag
	 */
	public void setNoDoubleClickFlag(boolean noDoubleClickFlag) {
		mNoDoubleClickFlag = noDoubleClickFlag;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBaseMessageEvent(Object event) {}
}
