package com.kainat.app.android.model;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

public final class PhoneContact {
	public String name;
	public Uri thumbnailUri ;
	public List<String> phoneNumber;
	public List<String> email;
	public String mobileno;
	//public long id;

	{
		phoneNumber = new ArrayList<String>();
		email = new ArrayList<String>();
	}

//	public PhoneContact(long id) {
//		this.id = id;
//	}
public void  setmobileno(String mnumber){
	this.mobileno = mnumber;
}
public String getMobileno(){
	return mobileno;
}
	public PhoneContact(String name, long id) {
		this.name = name;
		//this.id = id;
	}

	public PhoneContact() {
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("PhoneContact[ name=" + name + "]");
		return buffer.toString();
	}

	public String convertInFormat(String _deLimiter) {
		if (null != name && 0 < name.length()) {
			StringBuilder buf = new StringBuilder("{\"c\":[");
			buf.append('\"');
			buf.append(this.name);
			buf.append('\"');
			buf.append(_deLimiter);
			buf.append("\"\"");
			buf.append(_deLimiter);
			if (null != this.phoneNumber && !this.phoneNumber.isEmpty()) {
				for (String number : this.phoneNumber) {
					buf.append('\"');
					buf.append(number);
					buf.append('\"');
					buf.append(_deLimiter);//added ny nag it thro exception on setver if any contact have two contact
				}
//				buf.deleteCharAt(buf.length()-1);
//				buf.deleteCharAt(buf.length()-1);
//				buf.append(_deLimiter);//commented by nag
			} else {
				buf.append("\"\"");
				buf.append(_deLimiter);
			}
			//Email address
			if (null != this.email && !this.email.isEmpty()) {
				for (String address : this.email) {
					buf.append('\"');
					buf.append(address);
					buf.append('\"');
					buf.append(_deLimiter);
				}
//				buf.deleteCharAt(buf.length()-1);
//				buf.deleteCharAt(buf.length()-1);
				//buf.append(_deLimiter);
			} else{
				buf.append("\"\"");
				buf.append(_deLimiter);
			}
			if(buf.toString().lastIndexOf(_deLimiter) != -1)
			{
				buf.deleteCharAt(buf.length()-1);
				buf.deleteCharAt(buf.length()-1);
			}
			buf.append("]}");
			return buf.toString();
		} else {
			return "";
		}
	}
}