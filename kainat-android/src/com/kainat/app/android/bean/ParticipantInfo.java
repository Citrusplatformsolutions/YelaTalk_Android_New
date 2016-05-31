package com.kainat.app.android.bean;

public class ParticipantInfo {
	public String firstName;
	public String lastName;
	public String userId;
	public String userName;
	public String profileImageUrl;
	public String profileUrl;
	public boolean isSender;
	public String cid;
    
	public String getName(){
		if((firstName!=null && firstName.length()>0 ) && (lastName!=null && lastName.length()>0))	
			return firstName.toUpperCase() ;
		else
			return userName.toUpperCase();
	}
    @Override
	public String toString() {
		String s = "[firstName :"+firstName+"]\n[" +
				"[lastName :"+lastName+"]\n[" +
				"[userId :"+userId+"]\n[" +
				"[userName :"+userName+"]\n[" +
				"[profileImageUrl :"+profileImageUrl+"]\n[" +
				"[profileUrl :"+profileUrl+"]\n[" +
				"[isSender :"+isSender+"]\n[" 
				 ;
				
		return s;
	}
	
}
