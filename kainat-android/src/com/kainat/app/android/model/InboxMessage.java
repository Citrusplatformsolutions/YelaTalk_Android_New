package com.kainat.app.android.model;

import com.kainat.app.android.core.BusinessProxy;


public class InboxMessage extends RTMessage {

	public String mId;
	public String mRTMLMsgID;
	public String mSenderName;
	public String displayText="";
	public int mStatus;
	public int mDRM;
	public int mNotification;

	public InboxMessage clone() {
		InboxMessage message = new InboxMessage();
		message.mId = this.mId;
		message.mRTMLMsgID = this.mRTMLMsgID;
		message.mSenderName = this.mSenderName;
		message.mStatus = this.mStatus;
		message.mDRM = this.mDRM;
		message.mNotification = this.mNotification;
		message.mTime = this.mTime;
		message.mBitmap = this.mBitmap;
		message.mRecepients = this.mRecepients;
		message.displayText=this.displayText;
		return message;
	}

	public InboxMessage copyAndGetPayload() {
		InboxMessage message = clone();
		message.mPayload = BusinessProxy.sSelf.getPayloadForInboxMessage(message.mId);
		return message;
	}
}
