package com.kainat.app.android.util;

public class ChannelRefreshInfo  
{
	public String channelName;
	public String channelID;
	public int newMessageCount;
	
	public ChannelRefreshInfo(){
		
	}
	public ChannelRefreshInfo(String channelName, String channelID, int newMessageCount){
		this.channelName = channelName;
		this.channelID = channelID;
		this.newMessageCount = newMessageCount;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getChannelID() {
		return channelID;
	}
	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}
	public int getNewMessageCount() {
		return newMessageCount;
	}
	public void setNewMessageCount(int newMessageCount) {
		this.newMessageCount = newMessageCount;
	}
}
