package com.djangoogle.framework.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djangoogle.framework.widget.LoadingView;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Fragment基类
 * Created by Djangoogle on 2018/11/12 18:10 with Android Studio.
 */
public abstract class DjangoogleFragment extends RxFragment {

	//通用Activity
	protected Activity mActivity;
	//通用Loading
	protected LoadingView mLoading;
	//界面是否初始化完毕
	private boolean isPrepared;
	//第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
	private boolean isFirstResume = true;
	//Fragment是否首次可见
	private boolean isFirstVisible = true;
	//Fragment是否首次不可见
	private boolean isFirstInvisible = true;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//初始化布局
		if (0 != initLayout()) {
			View view = LayoutInflater.from(mActivity).inflate(initLayout(), null);
			//设置ButterKnife
			initButterKnife(view);
			return view;
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//初始化Loading
		mLoading = new LoadingView(mActivity);
		//点击屏幕不能关闭加载框
		mLoading.setCanceledOnTouchOutside(false);
		//点击返回键不能关闭加载框
		mLoading.setCancelable(false);
		//设置界面
		initGUI();
		//设置事件
		initAction();
		//初始化准备状态
		initPrepare();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isFirstResume) {
			isFirstResume = false;
			return;
		}
		if (getUserVisibleHint()) {
			onUserVisible();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getUserVisibleHint()) {
			onUserInvisible();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		//注册EventBus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		//注销EventBus
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
		hideLoading();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (isFirstVisible) {
				isFirstVisible = false;
				//初始化准备状态
				initPrepare();
			} else {
				onUserVisible();
			}
		} else {
			if (isFirstInvisible) {
				isFirstInvisible = false;
				onFirstUserInvisible();
			} else {
				onUserInvisible();
			}
		}
	}

	/**
	 * 设置ButterKnife
	 */
	protected abstract void initButterKnife(View view);

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
	 * 初始化准备状态
	 */
	public synchronized void initPrepare() {
		if (isPrepared) {
			onFirstUserVisible();
		} else {
			isPrepared = true;
		}
	}

	/**
	 * 第一次fragment可见（进行初始化工作）
	 */
	public void onFirstUserVisible() {
		initData();
	}

	/**
	 * fragment可见（切换回来或者onResume）
	 */
	public void onUserVisible() {}

	/**
	 * 第一次fragment不可见（不建议在此处理事件）
	 */
	public void onFirstUserInvisible() {}

	/**
	 * fragment不可见（切换掉或者onPause）
	 */
	public void onUserInvisible() {}

	/**
	 * 显示Loading
	 */
	protected void showLoading() {
		mLoading.show();
	}

	/**
	 * 隐藏Loading
	 */
	protected void hideLoading() {
		mLoading.dismiss();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBaseMessageEvent(Object event) {}
}
