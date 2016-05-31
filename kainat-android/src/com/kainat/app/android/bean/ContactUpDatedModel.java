package com.kainat.app.android.bean;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class ContactUpDatedModel {

	@SerializedName(value = "emailUserBaseMap")
	public Map<String, UserDetail> emailUserBaseMap;

	@SerializedName(value = "mobileNumberUserBaseMap")
	public Map<String, UserDetail> mobileNumberUserBaseMap;

	ContactUpDatedModel() {
		super();
	}

	public static class UserDetail {

		@SerializedName(value = "userName")
		public String userName = null;
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getiSipAddress() {
			return iSipAddress;
		}
		public void setiSipAddress(String iSipAddress) {
			this.iSipAddress = iSipAddress;
		}
		public String getiState() {
			return iState;
		}
		public void setiState(String iState) {
			this.iState = iState;
		}
		public long getiUserId() {
			return iUserId;
		}
		public void setiUserId(long iUserId) {
			this.iUserId = iUserId;
		}
		public String getPresenceStatus() {
			return presenceStatus;
		}
		public void setPresenceStatus(String presenceStatus) {
			this.presenceStatus = presenceStatus;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMobileNumber() {
			return mobileNumber;
		}
		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}
		public String getAssignedMDN() {
			return assignedMDN;
		}
		public void setAssignedMDN(String assignedMDN) {
			this.assignedMDN = assignedMDN;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getProfileUrl() {
			return profileUrl;
		}
		public void setProfileUrl(String profileUrl) {
			this.profileUrl = profileUrl;
		}
		@SerializedName("sipAddress")
		public String iSipAddress = null;

		@SerializedName("state")
		public String iState = null;

		@SerializedName("userId")
		public long iUserId;

		@SerializedName("presenceStatus")
		public String presenceStatus = null;
		@SerializedName("type")
		public String type = null;
		@SerializedName("name")
		public String name = null;
		@SerializedName("mobileNumber")
		public String mobileNumber = null;
		@SerializedName("assignedMDN")
		public String assignedMDN = null;
		@SerializedName("email")
		public String email = null;
		@SerializedName("profileUrl")
		public String profileUrl = null;
		@SerializedName("gender")
		public String gender = null;
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
	}


}
