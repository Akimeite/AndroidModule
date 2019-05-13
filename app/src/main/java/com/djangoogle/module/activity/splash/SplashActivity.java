package com.djangoogle.module.activity.splash;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import com.blankj.utilcode.util.ToastUtils;
import com.djangoogle.module.R;
import com.djangoogle.module.activity.banner.BannerActivity;
import com.djangoogle.module.activity.base.BaseActivity;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.Arrays;
import java.util.List;

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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//申请权限
			requestPermissions();
		} else {
			//初始化
			init();
		}
	}

	@Override
	protected void initData() {}

	/**
	 * 申请权限
	 */
	private void requestPermissions() {
		XXPermissions.with(this)
		             .constantRequest()
		             .permission(Permission.Group.STORAGE)
		             .request(new OnPermission() {
			             @Override
			             public void hasPermission(List<String> granted, boolean isAll) {
				             //初始化
				             init();
			             }

			             @Override
			             public void noPermission(List<String> denied, boolean quick) {
				             String[] deniedArray = denied.toArray(new String[0]);
				             ToastUtils.showShort("权限" + Arrays.toString(deniedArray) + "被拒绝");
			             }
		             });
	}

	/**
	 * 初始化
	 */
	private void init() {
		new Handler().postDelayed(() -> {
			//延迟两秒打开轮播页
			startActivity(new Intent(mActivity, BannerActivity.class));
			finish();
		}, 2000L);
	}
}
