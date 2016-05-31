package com.kainat.app.android.bean;

import java.util.List;

public class ChannelPostAPIGetFeed extends SimpleFeed {

	private List<ChannelPostAPI> channelPostAPIList;
	private String nextUrl;
	private String prevUrl;

	public ChannelPostAPIGetFeed(String message, List<ChannelPostAPI> channelPostAPIList, String nextUrl, String prevUrl) {
		super(message);
		this.channelPostAPIList = channelPostAPIList;
		this.nextUrl = nextUrl;
		this.prevUrl = prevUrl;
	}

	public ChannelPostAPIGetFeed(YTError ytError, String message) {
		super(ytError, message);
	}

	public ChannelPostAPIGetFeed(YTError[] citrusErrors, String message) {
		super(citrusErrors, message);
	}

	public List<ChannelPostAPI> getChannelPostAPIList() {
		return channelPostAPIList;
	}

	public void setChannelPostAPIList(List<ChannelPostAPI> channelPostAPIList) {
		this.channelPostAPIList = channelPostAPIList;
	}

	public String getNextUrl() {
		return nextUrl;
	}

	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}

	public String getPrevUrl() {
		return prevUrl;
	}

	public void setPrevUrl(String prevUrl) {
		this.prevUrl = prevUrl;
	}

}
