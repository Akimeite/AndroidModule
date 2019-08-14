package com.djangoogle.banner.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

/**
 * Created by Djangoogle on 2019/08/14 10:53 with Android Studio.
 */
public class FullScreenVideoView extends VideoView {

	public FullScreenVideoView(Context context) {
		super(context);
	}

	public FullScreenVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
	}
}
