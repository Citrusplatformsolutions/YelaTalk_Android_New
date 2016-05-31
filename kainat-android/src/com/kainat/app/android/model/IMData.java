package com.kainat.app.android.model;

public final class IMData {

	public static final byte ONLINE = 1;
	public static final byte OFFLINE = 2;
	public static final byte AWAY = 3;
	public static final byte BUSY = 4;
	public static final byte INVISIBLE = 5;
	public static final byte LOGGING_IN = 6;
	public static final byte LOGIN_FAILED = 7;
	public static final byte ALREADY_CONFIGURE = 8;
	public static final byte NOT_LOGGED_IN = 9;
	public static final String START_TXT_YAHOO = "im:y:";
	public static final String START_TXT_MSN = "im:m:";
	public static final String START_TXT_ICQ = "im:i:";
	public static final String START_TXT_GTALK = "im:g:";
	public static final String START_TXT_AOL = "im:a:";

	public static final byte IM_YAHOO = 0;// 0;
	public static final byte IM_GTALK = 1;// 3;
	public static final byte IM_MSN = 2;// 1;
	public static final byte IM_AOL = 3;// 4;
	public static final byte IM_ICQ = 4;// 2;
	public static final byte IM_MAX = 5;

	public static final String[] MESSENGER_NAME = { "Yahoo", "GoogleTalk", "MSN", "AOL", "ICQ" };
	public static final String[] MESSENGER_NAME_FOR_SERVER = { "yahoo", "google_talk", "hotmail", "AOL", "ICQ" };
	public byte[] iStatusValue;
	public String[] mUserNames;
	public String[] mPasswords;
	public byte[] mCheckBoxData;
	public String[] mCustomMessages;
	private static IMData sSelf;
	private String sTickerText;
	private byte[] mLocalStatusValue = new byte[IM_MAX];

	public static IMData getInstance() {
		if (null == sSelf)
			sSelf = new IMData();
		return sSelf;
	}

	private IMData() {
		reset();
	}

	public void reset() {
		iStatusValue = new byte[] { OFFLINE, OFFLINE, OFFLINE, OFFLINE, OFFLINE };
		mUserNames = new String[] { "", "", "", "", "" };
		mPasswords = new String[] { "", "", "", "", "" };
		mCheckBoxData = new byte[] { 0, 0, 0, 0, 0 };
		mCustomMessages = new String[] { "", "", "", "", "" };
	}

	public boolean isOnline(int aIndex) {
		switch (iStatusValue[aIndex]) {
		case ONLINE:
		case AWAY:
		case BUSY:
		case INVISIBLE:
			return true;
		}
		return false;
	}

	public boolean isOnlineForSender(String aSender) {
		byte IMIndex = 0;
		if (-1 < aSender.indexOf(IMData.START_TXT_YAHOO))
			IMIndex = IM_YAHOO;
		else if (-1 < aSender.indexOf(IMData.START_TXT_MSN))
			IMIndex = IM_MSN;
		else if (-1 < aSender.indexOf(IMData.START_TXT_ICQ))
			IMIndex = IM_ICQ;
		else if (-1 < aSender.indexOf(IMData.START_TXT_GTALK))
			IMIndex = IM_GTALK;
		else if (-1 < aSender.indexOf(IMData.START_TXT_AOL))
			IMIndex = IM_AOL;

		return isOnline(IMIndex);
	}

	public boolean isAnyIMOnline() {
		for (int i = 0; i < iStatusValue.length; ++i) {
			switch (iStatusValue[i]) {
			case ONLINE:
			case AWAY:
			case BUSY:
			case INVISIBLE:
				return true;
			}
		}
		return false;
	}

	public void copyNewLocalStatus() {
		System.arraycopy(iStatusValue, 0, mLocalStatusValue, 0, iStatusValue.length);
	}

	public boolean isStatusChangedForTicker() {
		boolean isStatusChanged = false;
		StringBuffer buff;
		for (int i = 0; i < IM_MAX; ++i) {
			if (LOGGING_IN == mLocalStatusValue[i]) {
				if (isOnline(i)) {
					isStatusChanged = true;
					buff = new StringBuffer(MESSENGER_NAME[i]);
					buff.append(' ');
					buff.append("successfully logged in ");
					sTickerText = buff.toString();
					break;
				} else if (LOGIN_FAILED == iStatusValue[i]) {
					isStatusChanged = true;
					buff = new StringBuffer(MESSENGER_NAME[i]);
					buff.append(' ');
					buff.append("username or password incorrect");
					sTickerText = buff.toString();
					mPasswords[i] = ""; // Majid-10Sep08, If login fails, clear
					// the password field.
					mCheckBoxData[i] = 0;
					break;
				}
			}
		}
		if (isStatusChanged) {
			buff = null;
			System.arraycopy(iStatusValue, 0, mLocalStatusValue, 0, mLocalStatusValue.length);
			return true;
		}
		return false;
	}

	public String getTickerText() {
		return sTickerText;
	}

}
