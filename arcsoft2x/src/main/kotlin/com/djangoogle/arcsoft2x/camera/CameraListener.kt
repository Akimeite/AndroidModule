package com.djangoogle.arcsoft2x.camera

import android.hardware.Camera

interface CameraListener {

	/**
	 * 当打开时执行
	 *
	 * @param camera             相机实例
	 * @param cameraId           相机ID
	 * @param displayOrientation 相机预览旋转角度
	 * @param isMirror           是否镜像显示
	 */
	fun onCameraOpened(camera: Camera, cameraId: Int, displayOrientation: Int, isMirror: Boolean)

	/**
	 * 预览数据回调
	 *
	 * @param nv21   预览数据
	 * @param camera 相机实例
	 */
	fun onPreview(nv21: ByteArray?, camera: Camera?)

	/**
	 * 当相机关闭时执行
	 */
	fun onCameraClosed()

	/**
	 * 当出现异常时执行
	 *
	 * @param e 相机相关异常
	 */
	fun onCameraError(e: Exception?)

	/**
	 * 属性变化时调用
	 *
	 * @param cameraID           相机ID
	 * @param displayOrientation 相机旋转方向
	 */
	fun onCameraConfigurationChanged(cameraID: Int, displayOrientation: Int)
}
