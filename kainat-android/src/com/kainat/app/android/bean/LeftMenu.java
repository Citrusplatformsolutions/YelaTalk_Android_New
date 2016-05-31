package com.kainat.app.android.bean;

import java.util.Vector;

public class LeftMenu {
	public boolean isgrid;
	public boolean isUserDetails;
	public String caption; 
	public String thumb;
	public String action;
	
	public String userThumbUrl; 
	public String userProfileUrl;
	public String userDisplayName;
	public String userStatus;
	
	public Vector<LeftMenu> gridMenu = new Vector<LeftMenu>() ;
	
	@Override
	public String toString() {
		return "[isgrid : "+isgrid 
		+"][caption : "+caption+"]["
		+"[thumb : "+thumb+"]["
		+"[action : "+action+"]["	
		+"[isUserDetails : "+isUserDetails+"]["
		+"[userThumbUrl : "+userThumbUrl+"]["
		+"[userProfileUrl : "+userProfileUrl+"]["
		+"[userDisplayName : "+userDisplayName+"]["
		+"[userStatus : "+userStatus+"]["
		
		+"[gridMenu : "+gridMenu.toString()+"]["
		;
	}
}
