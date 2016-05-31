package com.kainat.app.android.model;

import com.kainat.app.android.util.format.Payload;

public class RTMessage {
	public static final byte MESSAGE_INBOX = 0;
	public static final byte MESSAGE_OUTBOX = 1;
	public static final byte MESSAGE_SENTBOX = 2;
	public byte mMessageType = MESSAGE_INBOX;
	public String mTime;
	public int mBitmap;
	public Payload mPayload;
	public String mRecepients;
	public boolean isSelected;
}
