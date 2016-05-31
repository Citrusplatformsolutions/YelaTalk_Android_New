package com.kainat.app.android.model;

public final class OutboxMessage extends RTMessage {
	public int mTransactionId;
	public int mOperation;
	public int mStatus = 100;
}
