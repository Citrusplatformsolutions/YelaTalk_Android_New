package com.kainat.app.android.bean;

public class GroupEventBean {

	public String id ;
	public String title ;
	public String totalCount ;
	public String groupname ;
	public String comProfileUrl ;
	public String hosterUsername ;
	public String hosterThumbUr ;
	public String hosterProfileUrl ;
	public String hosterDisplayName ;
	public String description ;
	public String startDate ;
	public String endDate ;
	public String pictureUrl ;
	public String thumbPictureUrl ;
	public String audioUrl ;
	public String nextUrl ;
	public String prevUrl ;
	public String tracking;	
	public String seo ;
	public long insertTime ;
	public int more ;
	
	public String messageInfo1;	
	public String messageInfo2;	
	public String button_name;	
	public String button_action;
	public String messageAlert1;	
	public String messageAlert2;
	
	public String ownerUsername;
	public String ownerThumbUrl;
	public String ownerProfileUrl;
	public String ownerDisplayName;
	
	public String startTime;
	public String endTime;
	
	@Override
	public String toString() {
		return "[id : "+id 
		+"][insertTime : "+insertTime+"]["
		+"][title : "+title+"]["
		+"][messageInfo1 : "+messageInfo1+"]["
		+"][messageInfo2 : "+messageInfo2+"]["
		+"][button_name : "+button_name+"]["
		+"][button_action : "+button_action+"]["
		+"][messageAlert1 : "+messageAlert1+"]["
		+"][messageAlert2 : "+messageAlert2+"]["
		+"][startTime : "+startTime+"]["
		+"][endTime : "+endTime+"]["
		+"[totalCount : "+totalCount+"]["
		+"[groupname : "+groupname+"]["
		+"[comProfileUrl : "+comProfileUrl+"]["
		+"[hosterUsername : "+hosterUsername+"]["
		+"[hosterThumbUr : "+hosterThumbUr+"]["
		+"[hosterDisplayName : "+hosterDisplayName+"]["
		+"[description : "+description+"]["
		+"[startDate : "+startDate+"]["
		+"[endDate : "+endDate+"]["
		+"[pictureUrl : "+pictureUrl+"]["
		+"[thumbPictureUrl : "+thumbPictureUrl+"]["
		+"[audioUrl : "+audioUrl+"]["
		+"[nextUrl : "+nextUrl+"]["
		+"[tracking : "+tracking+"]["
		+"[prevUrl : "+prevUrl+"]["
		;
	}
}
