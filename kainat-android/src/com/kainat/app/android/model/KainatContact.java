package com.kainat.app.android.model;

import java.io.Serializable;
import java.util.Vector;

import com.google.gson.annotations.SerializedName;

public class KainatContact implements Serializable{

String gender;
	public String getGender() {
	return gender;
}
	
	String mobileNumber;

public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	byte channelValue;
	public byte getIsInChannel() {
		return channelValue;
	}
	public void setChannelValue(byte value) {
		this.channelValue = value;
	}

public void setGender(String gender) {
	this.gender = gender;
}

	boolean selected ;
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String userName;
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String userId;
	public String name;
	
	public String realName;

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@SerializedName(value = "mobileNumberList")
	Vector<String> phone ;
	public Vector<String> getPhone() {
		return phone;
	}

	public void setPhone(Vector<String> phone) {
		this.phone = phone;
	}

	public Vector<String> getEmail() {
		return email;
	}

	public void setEmail(Vector<String> email) {
		this.email = email;
	}
	@SerializedName(value = "emailList")
	Vector<String> email ;

}
