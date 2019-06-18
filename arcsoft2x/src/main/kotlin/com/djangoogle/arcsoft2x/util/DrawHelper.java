package com.djangoogle.arcsoft2x.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.djangoogle.arcsoft2x.widget.FaceRectView;

import java.util.List;

/**
 * 绘制人脸框帮助类，用于在{@link FaceRectView}上绘制矩形
 */
public class DrawHelper {

	private int previewWidth, previewHeight, canvasWidth, canvasHeight, cameraDisplayOrientation, cameraId;
	private boolean isMirror;

	public DrawHelper(int previewWidth, int previewHeight, int canvasWidth, int canvasHeight, int cameraDisplayOrientation, int cameraId,
	                  boolean isMirror) {
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.cameraDisplayOrientation = cameraDisplayOrientation;
		this.cameraId = cameraId;
		this.isMirror = isMirror;
	}

	/**
	 * 绘制数据信息到view上
	 *
	 * @param canvas            需要被绘制的view的canvas
	 * @param rect              人脸框
	 * @param color             绘制的颜色
	 * @param faceRectThickness 人脸框厚度
	 */
	public static void drawFaceRect(Canvas canvas, Rect rect, int color, int faceRectThickness) {
		if (null == canvas || null == rect) {
			return;
		}
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(faceRectThickness);
		paint.setColor(color);
		canvas.drawRect(rect, paint);
	}

	public void draw(FaceRectView faceRectView, List<Rect> rectList) {
		if (null == faceRectView) {
			return;
		}
		faceRectView.clearFaceInfo();
		if (null == rectList || rectList.isEmpty()) {
			return;
		}
		for (int i = 0; i < rectList.size(); i++) {
			rectList.set(i, RectUtil.adjustRect(rectList.get(i), previewWidth, previewHeight, canvasWidth, canvasHeight,
					cameraDisplayOrientation, cameraId, isMirror, false, false));
		}
		faceRectView.addFaceInfo(rectList);
	}

	public void setPreviewWidth(int previewWidth) {
		this.previewWidth = previewWidth;
	}

	public void setPreviewHeight(int previewHeight) {
		this.previewHeight = previewHeight;
	}

	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public void setCanvasHeight(int canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	public void setCameraDisplayOrientation(int cameraDisplayOrientation) {
		this.cameraDisplayOrientation = cameraDisplayOrientation;
	}

	public void setCameraId(int cameraId) {
		this.cameraId = cameraId;
	}

	public void setMirror(boolean mirror) {
		isMirror = mirror;
	}
}
