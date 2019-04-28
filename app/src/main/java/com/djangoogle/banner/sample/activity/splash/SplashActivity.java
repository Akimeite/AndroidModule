package com.djangoogle.banner.sample.activity.splash;

import android.content.Intent;
import android.os.Handler;

import com.agesun.banner.sample.R;
import com.djangoogle.banner.sample.activity.banner.BannerActivity;
import com.djangoogle.banner.sample.activity.base.BaseActivity;
import com.blankj.utilcode.util.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

/**
 * 启动页
 * Created by Djangoogle on 2019/03/27 13:38 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
public class SplashActivity extends BaseActivity {

	@Override
	protected int initLayout() {
		return R.layout.activity_splash;
	}

	@Override
	protected void initGUI() {}

	@Override
	protected void initAction() {
		//申请权限
		requestPermissions();
	}

	@Override
	protected void initData() {}

	/**
	 * 申请权限
	 */
	private void requestPermissions() {
		AndPermission.with(this)
		             .runtime()
		             .permission(Permission.Group.STORAGE)
		             .onGranted(data -> new Handler().postDelayed(() -> {
			             //延迟两秒打开轮播页
			             startActivity(new Intent(mActivity, BannerActivity.class));
			             finish();
		             }, 2000L))
		             .onDenied(data -> ToastUtils.showShort("申请权限被拒绝"))
		             .start();
	}
}
