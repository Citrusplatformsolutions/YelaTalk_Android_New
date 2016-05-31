package com.kainat.app.android.inf;

public interface HttpSynchInf {
	public void onResponseSucess(Object respons,int requestForID);
	public void onResponseSucess(String respons,int requestForID);
	public void onResponseSucess(String respons,int requestForID, int subType,int totNewFeed);
	public void onError(String err);	
	public void onError(String err,int requestCode);
	public void onNotification(int requestCode, String sender, String msg);
	public void onNotificationThreadInbox(int requestCode, String sender, String msg);
}
