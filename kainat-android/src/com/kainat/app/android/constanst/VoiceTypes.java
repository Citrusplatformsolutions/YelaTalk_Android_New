package com.kainat.app.android.constanst;

public final class VoiceTypes {
	public static final byte QCP_FIXED_HALF_RATE = 0;
	public static final byte QCP_MAX_HALF_RATE = 1;
	public static final byte QCP_FIXED_FULL_RATE = 2;
	public static final byte QCP_MAX_FULL_RATE = 3;
	public static final byte PCM_FORMAT = 4;
	public static final byte MULAW_FORMAT = 5;
	public static final byte AMR_FORMAT = 6;
	public static final byte MS_GSM0610_FORMAT = 7;
	public static final byte GSM0610_FORMAT = 8;
	public static final byte LAST_CODEC = GSM0610_FORMAT + 1;
	public static final byte UNKNOWN_CODEC = LAST_CODEC + 1;

}
