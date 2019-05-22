package com.djangoogle.arcsoft2x.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.djangoogle.arcsoft2x.model.FaceInfoModel;
import com.djangoogle.arcsoft2x.util.DrawHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FaceRectView extends View {

	private CopyOnWriteArrayList<FaceInfoModel> faceRectList = new CopyOnWriteArrayList<>();

	public FaceRectView(Context context) {
		this(context, null);
	}

	public FaceRectView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (faceRectList != null && faceRectList.size() > 0) {
			for (int i = 0; i < faceRectList.size(); i++) {
				DrawHelper.drawFaceRect(canvas, faceRectList.get(i), Color.WHITE, 5);
			}
		}
	}

	public void clearFaceInfo() {
		faceRectList.clear();
		postInvalidate();
	}

	public void addFaceInfo(FaceInfoModel faceInfo) {
		faceRectList.add(faceInfo);
		postInvalidate();
	}

	public void addFaceInfo(List<FaceInfoModel> faceInfoList) {
		faceRectList.addAll(faceInfoList);
		postInvalidate();
	}
}