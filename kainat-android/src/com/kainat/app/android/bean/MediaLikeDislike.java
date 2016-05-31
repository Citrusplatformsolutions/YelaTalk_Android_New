package com.kainat.app.android.bean;

public class MediaLikeDislike {
	public String userId;
	public String userName ;
	public String opDateTime ;
	public long timestamp ;
	public String profileUrl ;
	public String userThumbUrl ;
	public String userDisplayName ;
	public String nextUrl ;
	public String prevUrl ;
	public String refreshUrl ;
	public String order ;
	
	@Override
	public String toString() {
		
		return "[userId : "+userId+"]["
				+"[userName : "+userName+"]["
				+"[userThumbUrl : "+userThumbUrl+"]["
				+"[profileUrl : "+profileUrl+"]["
				;
	}
}
