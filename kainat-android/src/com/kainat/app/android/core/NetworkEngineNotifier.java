package com.kainat.app.android.core;

public interface NetworkEngineNotifier {

	public static final byte API_NET_CONFIGUREED = 55;
	public static final byte API_NET_STARTED = 56;
	public static final byte API_DATA_EXECUTED = 57;
	public static final byte API_TRANSACTION_CANCELED = 58;
	public static final byte API_STATE_READING = 59;
	public static final byte API_STATE_WRITING = 60;

	public void networkAPINotification(int aRetCode, byte[] data, byte objectNo);

	public void networkStateChanged(int aNewState, String aStr);
	
	public void networkDataChange(int aNewState);
}