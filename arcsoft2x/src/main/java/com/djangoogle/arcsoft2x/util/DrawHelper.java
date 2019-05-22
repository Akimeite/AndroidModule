package com.djangoogle.arcsoft2x.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;

import com.djangoogle.arcsoft2x.model.FaceInfoModel;
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
	 * 绘制数据信息到view上，若 {@link FaceInfoModel#name} 不为null则绘制 {@link FaceInfoModel#name}
	 *
	 * @param canvas            需要被绘制的view的canvas
	 * @param drawInfo          绘制信息
	 * @param color             绘制的颜色
	 * @param faceRectThickness 人脸框厚度
	 */
	public static void drawFaceRect(Canvas canvas, FaceInfoModel drawInfo, int color, int faceRectThickness) {
		if (canvas == null || drawInfo == null) {
			return;
		}
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(faceRectThickness);
		paint.setColor(color);
		Rect rect = drawInfo.getRect();
		canvas.drawRect(rect, paint);

		if (null != drawInfo.getName()) {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setTextSize(rect.width() / 8);
			canvas.drawText(drawInfo.getName(), rect.left, rect.top - 10, paint);
		}
	}

	public void draw(FaceRectView faceRectView, List<FaceInfoModel> drawInfoList) {
		if (null == faceRectView) {
			return;
		}
		faceRectView.clearFaceInfo();
		if (null == drawInfoList || drawInfoList.isEmpty()) {
			return;
		}
		for (FaceInfoModel drawInfo : drawInfoList) {
			drawInfo.setRect(adjustRect(drawInfo.getRect(),
					previewWidth,
					previewHeight,
					canvasWidth,
					canvasHeight,
					cameraDisplayOrientation,
					cameraId,
					isMirror,
					false,
					false));
		}
		faceRectView.addFaceInfo(drawInfoList);
	}

	/**
	 * @param ftRect                   FT人脸框
	 * @param previewWidth             相机预览的宽度
	 * @param previewHeight            相机预览高度
	 * @param canvasWidth              画布的宽度
	 * @param canvasHeight             画布的高度
	 * @param cameraDisplayOrientation 相机预览方向
	 * @param cameraId                 相机ID
	 * @param isMirror                 是否水平镜像显示（若相机是镜像显示的，设为true，用于纠正）
	 * @param mirrorHorizontal         为兼容部分设备使用，水平再次镜像
	 * @param mirrorVertical           为兼容部分设备使用，垂直再次镜像
	 * @return 调整后的需要被绘制到View上的rect
	 */
	private Rect adjustRect(Rect ftRect, int previewWidth, int previewHeight, int canvasWidth, int canvasHeight,
	                        int cameraDisplayOrientation, int
			                        cameraId, boolean isMirror, boolean mirrorHorizontal, boolean mirrorVertical) {

		if (ftRect == null) {
			return null;
		}
		Rect rect = new Rect(ftRect);

		float horizontalRatio;
		float verticalRatio;
		if (cameraDisplayOrientation % 180 == 0) {
			horizontalRatio = (float) canvasWidth / (float) previewWidth;
			verticalRatio = (float) canvasHeight / (float) previewHeight;
		} else {
			horizontalRatio = (float) canvasHeight / (float) previewWidth;
			verticalRatio = (float) canvasWidth / (float) previewHeight;
		}
		rect.left *= horizontalRatio;
		rect.right *= horizontalRatio;
		rect.top *= verticalRatio;
		rect.bottom *= verticalRatio;
		Rect newRect = new Rect();
		switch (cameraDisplayOrientation) {
			case 0:
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.left = canvasWidth - rect.right;
					newRect.right = canvasWidth - rect.left;
				} else {
					newRect.left = rect.left;
					newRect.right = rect.right;
				}
				newRect.top = rect.top;
				newRect.bottom = rect.bottom;
				break;
			case 90:
				newRect.right = canvasWidth - rect.top;
				newRect.left = canvasWidth - rect.bottom;
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.top = canvasHeight - rect.right;
					newRect.bottom = canvasHeight - rect.left;
				} else {
					newRect.top = rect.left;
					newRect.bottom = rect.right;
				}
				break;
			case 180:
				newRect.top = canvasHeight - rect.bottom;
				newRect.bottom = canvasHeight - rect.top;
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.left = rect.left;
					newRect.right = rect.right;
				} else {
					newRect.left = canvasWidth - rect.right;
					newRect.right = canvasWidth - rect.left;
				}
				break;
			case 270:
				newRect.left = rect.top;
				newRect.right = rect.bottom;
				if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					newRect.top = rect.left;
					newRect.bottom = rect.right;
				} else {
					newRect.top = canvasHeight - rect.right;
					newRect.bottom = canvasHeight - rect.left;
				}
				break;
			default:
				break;
		}

		/**
		 * isMirror mirrorHorizontal finalIsMirrorHorizontal
		 * true         true                false
		 * false        false               false
		 * true         false               true
		 * false        true                true
		 *
		 * XOR
		 */
		if (isMirror ^ mirrorHorizontal) {
			int left = newRect.left;
			int right = newRect.right;
			newRect.left = canvasWidth - right;
			newRect.right = canvasWidth - left;
		}
		if (mirrorVertical) {
			int top = newRect.top;
			int bottom = newRect.bottom;
			newRect.top = canvasHeight - bottom;
			newRect.bottom = canvasHeight - top;
		}
		return newRect;
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
