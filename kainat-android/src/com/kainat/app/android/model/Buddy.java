package com.kainat.app.android.model;

public class Buddy {

	public int id;
	public String name;
	public String realName;
	public String status;
	public String gender;
	public String media;
	public boolean isSelected;
	public boolean mIsOnline;

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("id=").append(id).append(", ");
		buffer.append("name=").append(name).append(", ");
		buffer.append("realName=").append(realName).append(", ");
		buffer.append("status=").append(status).append(", ");
		buffer.append("gender=").append(gender).append(", ");
		buffer.append("media=").append(media).append(", ");
		buffer.append("isSelected=").append(isSelected).append(", ");
		buffer.append("mIsOnline=").append(mIsOnline);
		return buffer.toString();
	}
}
