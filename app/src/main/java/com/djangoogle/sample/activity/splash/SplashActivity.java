package com.djangoogle.sample.activity.splash;

import android.content.Intent;
import android.os.Handler;

import com.blankj.utilcode.util.ToastUtils;
import com.djangoogle.sample.R;
import com.djangoogle.sample.activity.banner.BannerActivity;
import com.djangoogle.sample.activity.base.BaseActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

/**
 * 启动页
 * Created by Djangoogle on 2019/03/27 13:38 with Android Studio.
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
