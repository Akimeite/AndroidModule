package com.djangoogle.arcsoft2x.model;

import android.graphics.Rect;

import java.io.Serializable;

/**
 * 人脸信息实体类
 * Created by Djangoogle on 2018/12/21 09:52 with Android Studio.
 * © 2018 agesun® 安徽时旭智能科技有限公司™ All rights reserved.
 */
public class FaceInfoModel implements Serializable {

	private static final long serialVersionUID = -8216981078050487121L;

	private Rect rect;
	private int orient;
	private int age = 0;
	private String gender = "";
	private Face3DAngleModel face3DAngle;
	private String liveness = "";
	private String name = null;

	public FaceInfoModel() {}

	public FaceInfoModel(FaceInfoModel faceInfo) {
		if (null == faceInfo) {
			this.rect = new Rect();
			this.orient = 0;
		} else {
			this.rect = new Rect(faceInfo.getRect());
			this.orient = faceInfo.getOrient();
		}
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}

	public int getOrient() {
		return orient;
	}

	public void setOrient(int orient) {
		this.orient = orient;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Face3DAngleModel getFace3DAngle() {
		return face3DAngle;
	}

	public void setFace3DAngle(Face3DAngleModel face3DAngle) {
		this.face3DAngle = face3DAngle;
	}

	public String getLiveness() {
		return liveness;
	}

	public void setLiveness(String liveness) {
		this.liveness = liveness;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
