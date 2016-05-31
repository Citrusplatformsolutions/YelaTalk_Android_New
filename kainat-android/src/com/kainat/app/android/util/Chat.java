package com.kainat.app.android.util;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.kainat.app.android.util.format.Payload;

public class Chat implements Serializable{
	public static final byte UNREAD_MESSAGE = 0;
	public static final byte READ_MESSAGE = 1;
	public String userName;
	public String timestamp = "";
	public Payload payload;
	public byte readUnread = 0;
	transient public Bitmap[] mChattersImage = new Bitmap[2];
	
	public Chat() {
	}

	public Chat(String username, Payload payload,String timestamp) {
		this.userName = username;
		this.timestamp = timestamp;
		this.payload = payload;
	}
}