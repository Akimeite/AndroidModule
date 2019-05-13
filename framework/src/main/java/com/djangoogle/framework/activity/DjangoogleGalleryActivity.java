package com.djangoogle.framework.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.djangoogle.framework.R;
import com.djangoogle.framework.constants.DjangoogleConstants;
import com.djangoogle.framework.util.DjangoogleUtils;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

/**
 * 相册选取基类
 * Created by Djangoogle on 2018/10/17 13:13 with Android Studio.
 */
public abstract class DjangoogleGalleryActivity extends DjangoogleActivity {

	protected String mBaseMediaPath;

	protected EasyPopup mTakePhotoPopup;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBaseMediaPath = DjangoogleUtils.getExternalFilesDir(this, DjangoogleConstants.PATH_MEDIA);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mTakePhotoPopup) {
			mTakePhotoPopup.dismiss();
		}
	}

	/**
	 * 在屏幕底部弹出选择框并指定任意ViewGroup背景变暗
	 *
	 * @param viewGroup     指定的View
	 * @param mimeType      媒体类型 MimeType.ofAll().ofImage().ofVideo().ofAudio()
	 * @param maxPickNumber 最多可以选择的媒体数量
	 */
	protected void popupGallery(ViewGroup viewGroup, int mimeType, int maxPickNumber) {
		mTakePhotoPopup = EasyPopup.create()
		                           .setContentView(getMActivity(), R.layout.popup_take_photo_base)
		                           .setWidth(ScreenUtils.getScreenWidth())
		                           .setHeight(ConvertUtils.dp2px(120.5F))
		                           .setAnimationStyle(R.style.bottomPopupAnim)
		                           .setBackgroundDimEnable(true)//允许背景变暗
		                           .setDimValue(0.5F);//变暗的透明度(0-1)，0为完全透明
		if (null != viewGroup) {
			mTakePhotoPopup.setDimView(viewGroup);//指定任意ViewGroup背景变暗
		}
		mTakePhotoPopup.apply();
		//拍摄点击事件
		mTakePhotoPopup.findViewById(R.id.tvPopupTakePhoto).setOnClickListener(v -> {
			mTakePhotoPopup.dismiss();
			ToastUtils.showShort("拍摄点击事件");
		});
		//相册点击事件
		mTakePhotoPopup.findViewById(R.id.tvPopupSelectPhoto).setOnClickListener(v -> {
			mTakePhotoPopup.dismiss();
			ToastUtils.showShort("相册点击事件");
		});
		//弹出相册框
		mTakePhotoPopup.showAtAnchorView(getWindow().getDecorView()
		                                            .findViewById(android.R.id.content), YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);
	}

	/**
	 * 在屏幕底部弹出选择框
	 *
	 * @param mimeType      媒体类型 MimeType.ofAll().ofImage().ofVideo().ofAudio()
	 * @param maxPickNumber 最多可以选择的媒体数量
	 */
	protected void popupGallery(int mimeType, int maxPickNumber) {
		popupGallery(null, mimeType, maxPickNumber);
	}
}
