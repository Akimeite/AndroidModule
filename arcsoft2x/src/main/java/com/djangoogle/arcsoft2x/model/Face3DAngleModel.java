package com.djangoogle.arcsoft2x.model;

import java.io.Serializable;

/**
 * 人脸角度实体类
 * Created by Djangoogle on 2018/12/21 09:53 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
public class Face3DAngleModel implements Serializable {

	private static final long serialVersionUID = 1638240184539060676L;

	private float yaw;
	private float roll;
	private float pitch;

	public Face3DAngleModel() {}

	public Face3DAngleModel(float yaw, float roll, float pitch) {
		this.yaw = yaw;
		this.roll = roll;
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
