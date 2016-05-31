package com.kainat.app.android.bean;

public class UserAPI {

	private long userId;
	private String userName;
	private String name;
	private String thumbUrl;
	private String profileUrl;

	public UserAPI(long userId, String userName, String name, String thumbUrl,
			String profileUrl) {
		this.userId = userId;
		this.userName = userName;
		this.name = name;
		this.thumbUrl = thumbUrl;
		this.profileUrl = profileUrl;
	}

	public boolean equals(Object o) {
		if (o instanceof UserAPI) {
			UserAPI userAPI = (UserAPI) o;
			return this.userId == userAPI.userId;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return new Long(userId).hashCode();
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

}
