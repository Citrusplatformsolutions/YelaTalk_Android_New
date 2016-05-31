package com.kainat.app.android.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;

import com.kainat.app.android.YelatalkApplication;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.model.IMData;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;

public class FrameEngine {

	private static final String TAG = FrameEngine.class.getSimpleName();

	public static FrameEngine sSelf;
	private int mOtsFrameLen;
	private int mBitmap;
	boolean log=false;
	private Hashtable<String, String> mScriptTable;

	private FrameEngine() {
		sSelf = this;
	}

	public synchronized static void createInstance() {
		if (null == sSelf) {
			new FrameEngine();
		}
	}

	public int parse(ResponseObject aResObj) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parse--Entry: DataLength to Parse = " + aResObj.getResponseData().length);
		int frameOffset = 0;
		int bitmap = 0;
		try {
			// POINTING FRAME LEN [4 byte] ...PARSE FRAME LEN
			int len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parse--FRAME LENGTH = " + len);
			// SKIP 4 BYTEs TO POINT BITMAP[4 byte]
			frameOffset += 4;
			bitmap = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parse-- BITMAP VALUE = " + bitmap);
			if (bitmap == 0)
				return Constants.ERROR_INVALID_RESPONSE;
			// SKIP 4 BYTEs TO POINT Message Type[1 byte]
			frameOffset += 4;
			if ((bitmap & 0x01) > 0) { // MSGTYPE_BIT_MASK
				bitmap &= ~1; // removing the last bit mean to say the msg type
				// bit
//				System.out.println("----------------aResObj.mResponseData[frameOffset++]----"+aResObj.mResponseData[frameOffset+1]);
				switch (aResObj.mResponseData[frameOffset++]) {
				case 1: // REG MESSAGE TYPE
					parseRegResponse(aResObj, bitmap, frameOffset);
					break;
				case 4: // Inbox
				case 2: // Vtov
				case 5: // Search
				case 6: // Search
					parseVoiceResponse(aResObj, bitmap, frameOffset);
					break;
				default:
					return Constants.ERROR_INVALID_RESPONSE;
				}
			} else {
				return Constants.ERROR_INVALID_RESPONSE;
			}
		} catch (OutOfMemoryError me) {
			System.gc();
			if (Logger.ENABLE)
				Logger.error(TAG, "--parse-- Out of memory error calling GC", me);
			return Constants.ERROR_OUT_OF_MEMORY;
		} catch (Throwable ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parse-- " + ex, ex);
			return Constants.ERROR_PARSE_ERROR;
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parse--Exit:");
		return Constants.ERROR_NONE;
	}
//2,4,5,6,8,9,10,12,15
	private void parseRegResponse(ResponseObject aResObj, int bitmap, int frameOffset) throws Throwable {
		if (Constants.FRAME_TYPE_GET_EXTENDED_PROFILE == aResObj.getSentOp()) {
			parseRegResponseForOtherProfile(aResObj, bitmap, frameOffset);
			return;
		}
		int bitmapOffSet = 1;
		int len = 0;
		int tempInt = 0;
		SettingData profile = SettingData.sSelf;
		String tempStr;
		String[] strArr;
		while (bitmap > 0) {
			switch (bitmap & (1 << bitmapOffSet)) {
			case 1 << 1: // MSG_STATUS_BIT_MASK:
				// Skip 1 byte to point src number length
				aResObj.setResponseCode(aResObj.mResponseData[frameOffset++]);
				// System.out.println("Server response code - "+aResObj.getResponseCode());
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse-- RESPONSE STATUS = " + aResObj.getResponseCode());
				break;
			case 1 << 2:// SRC_NUM_BIT_MASK:
				len = aResObj.mResponseData[frameOffset++];
				// tempStr = new
				// String(aResObj.iResponseData,frameOffset,len,
				// CommonConstants.ENC);
				// if(Flags.LOG_ENABLE)
				// Logger.verbose("Frame::parseRegResponse():[INFO]: SRC NUMBER = "
				// + tempStr, OtsLogger.INFO);
				frameOffset += len;
				break;
			case 1 << 3: // MSG_COUNT_BIT_MASK:
				// Fetch Message Count
				aResObj.setMessageCount(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4));
				//System.out.println("=============MESSAGE COUNT====================" + aResObj.getMessageCount());
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--Message Count = " + aResObj.getMessageCount());
				frameOffset += 4;
				break;
			case 1 << 4: // TRANS_ID_BIT_MASK:
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--GOT TRANS ID = " + tempInt + " : SENT TRANS ID = " + aResObj.getSentTransactionId());
				frameOffset += 4;
				break;
			case 1 << 5: // LAST_TRANS_ID_BIT_MASK:
				aResObj.setLastTransactionId(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4));
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--LAST TRANSID = " + aResObj.getLastTransactionId());
				frameOffset += 4;
				break;
			case 1 << 6: // USER_ID_BIT_MASK:
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
				//System.out.println("UID-"+tempInt);
				if (tempInt > 0)
					BusinessProxy.sSelf.setUserId(tempInt);
				else
					throw new Exception("UserID found = 0000");
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse-- USERID = " + BusinessProxy.sSelf.getUserId());
				frameOffset += 4;
				break;
			case 1 << 7:
				// frameOffset = parseBuddyList(aResObj, frameOffset);
				frameOffset = parseBuddyList(aResObj, frameOffset, aResObj.getSentOp());
				break;
			case 1 << 8: // Client Id
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
				if (tempInt > 0)
					BusinessProxy.sSelf.setClientId(tempInt);
				else
					throw new Exception("Device-ID 0000");
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse-- CLIENT ID = " + BusinessProxy.sSelf.getClientId());
				frameOffset += 4;
				break;
			case 1 << 9: // upgrade type[SOFT/HARD]
				aResObj.setUpgradeType(aResObj.mResponseData[frameOffset++]);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--UPGRADE TYPE = " + aResObj.getUpgradeType());
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
				frameOffset += 2;
				aResObj.setUpgradeURL(new String(aResObj.mResponseData, frameOffset, len, Constants.ENC));
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse-- Upgrade URL = " + aResObj.getUpgradeURL());
				frameOffset += len;
				break;
			case 1 << 10:
				frameOffset = parseGroupList(aResObj, frameOffset);
				break;
			case 1 << 11://This for the special Notification message coming from the server.
				//Here complete payload will come for the messages.

				//frameOffset = parseSpecialMessages(aResObj, frameOffset, true);
				if(Logger.ENABLE)
				System.out.println("Something is wrong- 1 << 11 bit is on");
				break;
			case 1 << 12: // messageUpdateTimeStamp
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				// length of messageUpdateTimeStamp
				// String str = new
				// String(aResObj.iResponseData,frameOffset,len,
				// CommonConstants.ENC);
				frameOffset += len;
				break;
			case 1 << 13: // Suggested User Names
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
				frameOffset += 2;
				aResObj.setSuggestedNames(new String(aResObj.mResponseData, frameOffset, len, Constants.ENC));
				frameOffset += len;
				// Dummy Test
				// if(aResObj.iSuggestedNames != null &&
				// aResObj.iSuggestedNames.length() > 0)
				// SplashUi.names = aResObj.iSuggestedNames;
				// else
				// {
				// String name =
				// UiController.iUiController.iSettingInfo.iUserName;
				// SplashUi.names = name + "1981" + "::" + name + "0109" +
				// "::" + name + "8484";
				// }
				break;
			case 1 << 14:
				frameOffset = parseIMSettingList(aResObj, frameOffset);
				break;
			case 1 << 15:
				// Read No. of Keys in 1 Byte
				len = aResObj.mResponseData[frameOffset++];
				int keyLen = 0;
				String key = null;
				String value = null;
				aResObj.mKeyValues = new Hashtable<String, String>(len);
				//Loop here for the number of Keys
				for (int i = 0; i < len; i++) {
					//read length ok Key in 1 Byte
					keyLen = aResObj.mResponseData[frameOffset++];
					//read actual key
					key = new String(aResObj.mResponseData, frameOffset, keyLen);
					frameOffset += keyLen;
					//read length of value in 4 bytes
					tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
					frameOffset += 4;
					//read actual value
					value = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
					frameOffset += tempInt;
					aResObj.mKeyValues.put(key, value);
					if (key.equalsIgnoreCase("username")) {
						profile.setUserName(value);
					}
					if (key.equalsIgnoreCase("mob")) {
						profile.setNumber(value);
					}
					if (key.equalsIgnoreCase("firstname")) {
						profile.setFirstName(value);
						profile.setLoginFirstName(value);
					}
					if (key.equalsIgnoreCase("lastname")) {
						profile.setLastName(value);
						profile.setLoginLastName(value);
					}
					//fetch Add components Here - advturl, advtKey, userParam
				     else if (key.equalsIgnoreCase("advturl")) {
				      BusinessProxy.sSelf.advertisementData.clear();
				      BusinessProxy.sSelf.advertisementData.put(key, value);
				     }
				     else if (key.equalsIgnoreCase("advtKey")) {
				      BusinessProxy.sSelf.advertisementData.put(key, value);
				     }
				     else if (key.equalsIgnoreCase("userParam")) {
				      BusinessProxy.sSelf.advertisementData.put(key, value);
				     }					
//				     else if (key.equalsIgnoreCase("animationListURL")) {//Animation List URL
//				    	 BusinessProxy.sSelf.animationListURL = value;
//				     }					
//				     else if (key.equalsIgnoreCase("singleAnimationURL")) {//Single Animation URL
//				    	 BusinessProxy.sSelf.singleAnimationURL = value;
//				     }					
				     else if (key.equalsIgnoreCase("mediacat")) {
				    	 if(value != null && value.trim().length() > 0)
				    	 {
				    		 if(value.lastIndexOf("|^") == value.length() - 2)
				    			 value = value.substring(0, value.lastIndexOf("|^"));
				    		 BusinessProxy.sSelf.mediaCategories = Utilities.split(new StringBuffer(value), "|^");
				    	 }
				     }					
				     else if (key.equalsIgnoreCase("mediacatids")) {
				    	 if(value != null && value.trim().length() > 0)
				    	 {
				    		 if(value.lastIndexOf("|^") == value.length() - 2)
				    			 value = value.substring(0, value.lastIndexOf("|^"));
				    		 BusinessProxy.sSelf.mediaCategoryIDs = Utilities.split(new StringBuffer(value), "|^");
				    	 }
				     }					
				     else if (key.equalsIgnoreCase("commcat")) {
				    	 if(value != null && value.trim().length() > 0)
				    	 {
				    		 if(value.lastIndexOf("|^") == value.length() - 2)
				    			 value = value.substring(0, value.lastIndexOf("|^"));
				    		 BusinessProxy.sSelf.communityCategories = Utilities.split(new StringBuffer(value), "|^");
				    	 }
				     }	
//					System.out.println("-------frame-------key-------"+key);
//				      System.out.println("-------value-------value-------"+value);
					if(Logger.ENABLE){
//				      System.out.println("-------frame-------key-------"+key);
//				      System.out.println("-------value-------value-------"+value);
					}
				}
				break;
			case 1 << 16: // USER NAME
				len = aResObj.mResponseData[frameOffset++];
				profile.setUserName(new String(aResObj.mResponseData, frameOffset, len));
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--USER NAME = " + profile.getUserName());
				frameOffset += len;
				break;
			case 1 << 17: // password
				len = aResObj.mResponseData[frameOffset++];
				profile.setPassword(new String(aResObj.mResponseData, frameOffset, len));
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--PASSWORD = " + profile.getPassword());
				frameOffset += len;
				break;
			case 1 << 18: // email
				len = aResObj.mResponseData[frameOffset++];
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--EMAIL len = " + Integer.toString(len));
				tempStr = new String(aResObj.mResponseData, frameOffset, len);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]: EMAIL = " + tempStr);
				if (tempStr != null && tempStr.length() > 0)
					profile.setEmail(tempStr.split(":")[0]);
				frameOffset += len;
				break;
			case 1 << 19: // name
				len = aResObj.mResponseData[frameOffset++];
				tempStr = new String(aResObj.mResponseData, frameOffset, len);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]: NAME OF USER = " + tempStr);
				strArr = tempStr.split(":");
				profile.setGender(2);
				if (strArr[0].trim().equals("M"))
					profile.setGender(1);
				profile.setFirstName(strArr[1].trim());
				if (strArr.length > 2)
					profile.setLastName(strArr[2].trim());
				if (strArr.length > 3) {
					profile.setNameEditable(strArr[3].trim().equalsIgnoreCase("editable"));
				}
				frameOffset += len;
				break;
			case 1 << 20: // Address
				len = aResObj.mResponseData[frameOffset++];
				tempStr = new String(aResObj.mResponseData, frameOffset, len);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]: Address = " + tempStr);
				strArr = tempStr.split(":");
				for (int i = 0; i < strArr.length; i++) {
					switch (i) {
					case 0:
						profile.setAddress(strArr[i].equals("null") ? "" : strArr[i].trim());
						break;
					case 1:
						profile.setCity(strArr[i].equals("null") ? "" : strArr[i].trim());
						break;
					case 2:
						profile.setState(strArr[i].equals("null") ? "" : strArr[i].trim());
						break;
					case 3:
						profile.setZip(strArr[i].equals("null") ? "" : strArr[i].trim());
						break;
					case 4:
						profile.setCountry(strArr[i].equals("null") ? "" : strArr[i].trim());
						break;
					}
				}
				frameOffset += len;
				break;
			case 1 << 21: // client age
				// profile.iAge = aResObj.mResponseData[frameOffset]-1;
				// //older version less than 151
				// frameOffset += 1;
				len = aResObj.mResponseData[frameOffset++]; // read len in 1
				// byte
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]: len of Birthday String= " + len);
				profile.setBirthDate(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]:: Birthday String = " + profile.getBirthDate());
				break;
			case 1 << 22: // carrier
				//Now this bit is used for the School Information - 
				len = aResObj.mResponseData[frameOffset++];
				profile.setCarrier(new String(aResObj.mResponseData, frameOffset, len));
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]: Carrier = " + profile.getCarrier());
				frameOffset += len;
				break;
			case 1 << 23: // notification
				profile.setNotification(aResObj.mResponseData[frameOffset++]);
				break;
			case 1 << 24: // order of message
				// profile.iMsgRecOrder = aResObj.mResponseData[frameOffset];
				frameOffset += 1;
				break;
			case 1 << 25: // phone number
				len = aResObj.mResponseData[frameOffset++];
				tempStr = new String(aResObj.mResponseData, frameOffset, len);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse()--[INFO]: PHONE NUMBERS = " + tempStr);
				strArr = tempStr.split(":");
				profile.setNumber((strArr[0].equals("null")) ? "" : strArr[0]);
				frameOffset += len;
				break;
			case 1 << 26:
				profile.setCopyMessageTo(aResObj.mResponseData[frameOffset++]);
				break;
			case 1 << 27: // Community Operation
				profile.setCommunityMessagePreference(aResObj.mResponseData[frameOffset++]);
				// community operation value in 1 byte.
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO] - community message preference = " + profile.getCommunityMessagePreference());
				break;
			case 1 << 28: // auto Response
				String autoreply = null;
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
				// length of Auto Response
				frameOffset += 2;
				if (len > 0) {
					String str = new String(aResObj.mResponseData, frameOffset, len);
					profile.setAutoReplyMessage(str.substring(str.indexOf(":") + 1));
					if (str.length() > 0 && str.indexOf(":") != -1)
						autoreply = str.substring(0, str.indexOf(":"));
					if (autoreply != null && autoreply.equalsIgnoreCase("ON"))
						profile.setAutoReply(true);
					else
						profile.setAutoReply(false);
					frameOffset += len;
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseRegResponse--[INFO]:: auto Response String = " + str);
					str = null;
				}
				break;
			case 1 << 29: // Voice And Picture.
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse-- [INFO] - For voice and picture.");
				InboxTblObject inboxData = new InboxTblObject(InboxTblObject.INBOX);
				frameOffset = parseForAttachment(aResObj, frameOffset, inboxData);
				aResObj.setData(inboxData);
				break;
			case 1 << 30: // PAYLOAD_BIT_MASK:
//				if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
//					parseInboxMessages(aResObj, frameOffset, true);
//				else
//					parseInboxMessages(aResObj, frameOffset, false);
				if(Logger.ENABLE)
			    System.out.println("SOMETHING IS WRONG - 1 << 30 BIT ON");
				break;
			default:
				break;
			}// switch (bitmap &&)
			bitmap = bitmap & ~(1 << bitmapOffSet);
			bitmapOffSet++;
		}// End while(bitmap)
	}

	private void parseRegResponseForOtherProfile(ResponseObject aResObj, int bitmap, int frameOffset) throws Throwable {
		int bitmapOffSet = 1;
		int len = 0;
		int tempInt = 0;
		SettingData profile = SettingData.sSelf;
		while (bitmap > 0) {
			switch (bitmap & (1 << bitmapOffSet)) {
			case 1 << 1: // MSG_STATUS_BIT_MASK:
				// Skip 1 byte to point src number length
				aResObj.setResponseCode(aResObj.mResponseData[frameOffset++]);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse():[INFO]: RESPONSE STATUS = " + aResObj.getResponseCode());
				break;
			case 1 << 2:// SRC_NUM_BIT_MASK:
				len = aResObj.mResponseData[frameOffset++];
				frameOffset += len;
				break;
			case 1 << 3: // MSG_COUNT_BIT_MASK:
				// Fetch Message Count
				aResObj.setMessageCount(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4));
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]: Message Count = " + aResObj.getMessageCount());
				frameOffset += 4;
				break;
			case 1 << 4: // TRANS_ID_BIT_MASK:
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseRegResponse--[INFO]:: GOT TRANS ID = " + tempInt + " : SENT TRANS ID = " + aResObj.getSentTransactionId());
				frameOffset += 4;
				break;
			case 1 << 5: // RELATION SHIP STATUS
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setRelationshipStatus(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 6: // SEXUAL ORIENTATION
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setSexualOrientation(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 7: // LOOKING FOR
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setLookingFor(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 8: // EDUCATION
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setEducation(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 9: // INTERESTS
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setInterest(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 10: // FAVOURITE MOVIE
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setFavoriteMovie(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 11: // FAVOURITE MUSIC
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setFavoriteMusic(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 12:// OCCUPATION
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setOccupation(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			case 1 << 13:// Language
				len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
				profile.setLanguage(new String(aResObj.mResponseData, frameOffset, len));
				frameOffset += len;
				break;
			default:
				break;
			}
			bitmap = bitmap & ~(1 << bitmapOffSet);
			bitmapOffSet++;
		}// End while
	}

	private int parseGroupList(ResponseObject aResObj, int frameOffset) throws Exception {
		switch (aResObj.getSentOp()) {
		case Constants.FRAME_TYPE_GET_GROUP_DETAILS:
			int len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
			frameOffset += 2;
			DataModel.sSelf.storeObject(DMKeys.RETRIEVED_GROUP_DETAILS, new String(aResObj.mResponseData, frameOffset, len, Constants.ENC));
			frameOffset += len;
			return frameOffset;
		default:
			int tempInt = aResObj.mResponseData[frameOffset++]; // time
			// stamp len
			BusinessProxy.sSelf.mGroupTimeStamp = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseGroupList()--[INFO]: GROUP TS = " + BusinessProxy.sSelf.mGroupTimeStamp);
			frameOffset += tempInt;
			BusinessProxy.sSelf.setGroupCountOnServer(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2));
			frameOffset += 2;
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseGroupList():[INFO]: GROUP TOT COUNT = " + BusinessProxy.sSelf.getGroupCountOnServer());
			int aCountToParse = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
			frameOffset += 2;
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseGroupList--[INFO]: GROUP CURRENT COUNT = " + aCountToParse);
			
				DataModel.sSelf.storeObject(DMKeys.TOT_COMMUNITE,new Integer(aCountToParse));
			if (aCountToParse == 0)
				return frameOffset;
			aResObj.setGroupObject(new InboxTblObject(InboxTblObject.GROUP_LIST));
			String str = null;
			String[] strBuf = null;
			int i = 0;
			for (; i < aCountToParse; i++) {
				try {
					//tempInt = aResObj.iResponseData[frameOffset++];
					//Get Length in 2 Bytes
					tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
					frameOffset += 2;
					str = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
					frameOffset += tempInt;
					if (i < Constants.MAX_CONTACT_LIST_SIZE) {
						strBuf = str.split(";");
						//ID OF GROUP
						aResObj.getGroupObject().insertField(0, new Integer(Integer.parseInt(strBuf[0])), InboxTblObject.INBOX_FIELD_MSG_ID);
						// NAME OF THE COMMUNITY
						aResObj.getGroupObject().insertField(0, strBuf[1], InboxTblObject.INBOX_FIELD_NAME);
						// ROLE IN COMM 2-OWNER/1-ADMIN/0-MEMBER
						aResObj.getGroupObject().insertField(0, strBuf[2], InboxTblObject.INBOX_FIELD_NUMBER);
						if (4 < strBuf.length) {
							aResObj.getGroupObject().insertField(0, strBuf[3] + ";" + strBuf[4], InboxTblObject.INBOX_FIELD_STATUS);

							// STORE COMM MSG ON/OFF
						} else {
							aResObj.getGroupObject().insertField(0, "", InboxTblObject.INBOX_FIELD_STATUS);
							// STORE COMM MSG ON/OFF
						}
					}
					str = null;
				} catch (Exception e) {
				}
			}
			aResObj.getGroupObject().setItemCount(i);
			return frameOffset;
		}
	}

	private int parseIMBuddyList(ResponseObject aResObj, int frameOffset) throws Exception {
		// NOW IM AND BUDDY TIME STAMP IS SAME
		int tempInt = aResObj.mResponseData[frameOffset++]; // time stamp len
		BusinessProxy.sSelf.mBuddyTimeStamp = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseIMBuddyList--[FINE]: IM BUDDY TS = " + BusinessProxy.sSelf.mBuddyTimeStamp);
		frameOffset += tempInt;
		// UiController.iUiController.iTotBuddyCountAtServer =
		// Utilities.bytesToInt(aResObj.mResponseData, frameOffset,
		// 2);
		tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
		frameOffset += 2;
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseIMBuddyList():[INFO]: IM BUDDY TOT COUNT = " + tempInt);
		int aCountToParse = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
		frameOffset += 2;
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseIMBuddyList--[INFO]: IM BUDDY CURRENT COUNT = " + aCountToParse);
		if (aCountToParse == 0)
			return frameOffset;
		aResObj.setIMBuddyObject(new InboxTblObject(InboxTblObject.IM_LIST));
		String str = null;
		String[] strBuf = null;
		int messengerCount = 0;
		for (int loop = 0; loop < aCountToParse; loop++) {
			tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
			frameOffset += 2;
			str = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseIMBuddyList - Buddy information - " + str);
			frameOffset += tempInt;
			strBuf = str.split(";");
			// like
			// 12354;busy;im:m:vishal@hotmail.com;vishal@hotmail.com;custom_message
			// IM BUDDY ID
			aResObj.getIMBuddyObject().insertField(0, new Integer(Integer.parseInt(strBuf[0])), InboxTblObject.INBOX_FIELD_MSG_ID);
			// READING STATUS ONLINE/INVISIBLE/BUSY/AWAY/CUSTOM
			aResObj.getIMBuddyObject().insertField(0, strBuf[1], InboxTblObject.INBOX_FIELD_STATUS);
			// ACTUAL NAME OF THE MESSENGER BUDDY
			aResObj.getIMBuddyObject().insertField(0, strBuf[2], InboxTblObject.INBOX_FIELD_NUMBER);
			// FRIENDLY NAME OF THE MESSENGER BUDDY
			aResObj.getIMBuddyObject().insertField(0, strBuf[3], InboxTblObject.INBOX_FIELD_NAME);
			if (strBuf.length > 4)
				aResObj.getIMBuddyObject().insertField(0, strBuf[4], InboxTblObject.INBOX_FIELD_EMAIL);
			else
				aResObj.getIMBuddyObject().insertField(0, "", InboxTblObject.INBOX_FIELD_EMAIL);

			++messengerCount;
			str = null;
			strBuf = null;
		}
		// No 'MORE' Concept in Buddy list
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--ParseIMBuddyList--[INFO] - count found = " + messengerCount);
		if (Logger.ENABLE) {
			String status = "";
			for (int i = 0; i < IMData.IM_MAX; i++)
				status += BusinessProxy.sSelf.mMessengerData.iStatusValue[i] + " ";
			Logger.verbose(TAG, "--parseIMBuddyList[INFO]-- STATUS DATA = " + status);
		}
		aResObj.getIMBuddyObject().setItemCount(messengerCount);
		return frameOffset;
	}

	/**
	 * This method is made just to parse the buddy list of IM
	 */
	private int parseBuddyList(ResponseObject aResObj, int frameOffset, int frameType) throws Exception {
		int tempInt = aResObj.mResponseData[frameOffset++]; // time stamp len
		BusinessProxy.sSelf.mBuddyTimeStamp = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseBuddyList()--[FINE]: BUDDY TS = " + BusinessProxy.sSelf.mBuddyTimeStamp);
		frameOffset += tempInt;
		BusinessProxy.sSelf.setBuddyCountOnServer(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2));
		frameOffset += 2;
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseBuddyList()--[INFO]: BUDDY TOT COUNT = " + BusinessProxy.sSelf.getBuddyCountOnServer());
		int aCountToParse = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
		frameOffset += 2;
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseBuddyList--[INFO]: BUDDY CURRENT COUNT = " + aCountToParse);
		if (aCountToParse == 0)
			return frameOffset;
		aResObj.setBuddyObject(new InboxTblObject(InboxTblObject.BUDDY_LIST));
		String str = null;
		String[] strBuf = null;
		int buddyCount = 0;
		for (int loop = 0; loop < aCountToParse; loop++) {
			try {
				//                if (frameType == CommonConstants.FRAME_TYPE_BUDDY_GET_OFFLINE)
				//                {
				//                    tempInt = CoreController.iSelf.bytesToInt(aResObj.iResponseData, frameOffset, 2);
				//                    frameOffset += 2;
				//                }
				//                else
				//                    tempInt = aResObj.iResponseData[frameOffset++];
				//Get Length in 2 Bytes
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
				frameOffset += 2;

				str = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
//				System.out.println("@@@@@@@@@@@@@@@@@@@@BUDDY DATA@@@@@@@@@@@@@@@@@@@" + str);
				frameOffset += tempInt;
				if (loop < Constants.MAX_CONTACT_LIST_SIZE) {
					strBuf = str.split(";");
					aResObj.getBuddyObject().insertField(0, new Integer(Integer.parseInt(strBuf[0])), InboxTblObject.INBOX_FIELD_MSG_ID);
					// READING ONLINE/OFFLINE/BOOKMARK ETC. STATUS
					aResObj.getBuddyObject().insertField(0, strBuf[1].trim(), InboxTblObject.INBOX_FIELD_STATUS);
					// ACTUAL NAME OF THE BUDDY
					aResObj.getBuddyObject().insertField(0, strBuf[2].trim(), InboxTblObject.INBOX_FIELD_NUMBER);
					// FRIENDLY NAME OF THE BUDDY
					aResObj.getBuddyObject().insertField(0, strBuf[2].trim(), InboxTblObject.INBOX_FIELD_NAME);
					//					aResObj.getBuddyObject().insertField(0, strBuf[3], InboxTblObject.INBOX_FIELD_NAME);
					//Logger.verbose(TAG, "Buddy Name - " + strBuf[3]);
					// READING MALE/FEMALE
					if (5 < strBuf.length) {
						if (strBuf.length > 8 && strBuf[8].trim().length() > 0)
							aResObj.getBuddyObject().insertField(0, strBuf[5].trim() + ";" + strBuf[8].trim(), InboxTblObject.INBOX_FIELD_MSG_DRM);
						else
							aResObj.getBuddyObject().insertField(0, strBuf[5].trim(), InboxTblObject.INBOX_FIELD_MSG_DRM);
					} else
						aResObj.getBuddyObject().insertField(0, "", InboxTblObject.INBOX_FIELD_MSG_DRM);
					//					if (6 < strBuf.length)
					//						aResObj.getBuddyObject().insertField(0, strBuf[6], InboxTblObject.INBOX_FIELD_MSG_DRM);
					//					else
					//						aResObj.getBuddyObject().insertField(0, "", InboxTblObject.INBOX_FIELD_MSG_DRM);
					// READING MEDIA V+P, V, P
					if (6 < strBuf.length) {
						if (7 < strBuf.length && strBuf[7].trim().length() > 0)
							aResObj.getBuddyObject().insertField(0, strBuf[6].trim() + ";" + strBuf[7].trim(), InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
						else
							aResObj.getBuddyObject().insertField(0, strBuf[6].trim(), InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
					} else
						aResObj.getBuddyObject().insertField(0, "", InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
					//					if (7 < strBuf.length) {
					//						if (8 < strBuf.length && strBuf[8].trim().length() > 0)
					//							aResObj.getBuddyObject().insertField(0, strBuf[7] + ";" + strBuf[8], InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
					//						else
					//							aResObj.getBuddyObject().insertField(0, strBuf[7], InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
					//					} else
					//						aResObj.getBuddyObject().insertField(0, "", InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
					++buddyCount;
				}
				str = null;
				strBuf = null;
			} catch (Exception e) {
				if (Logger.ENABLE)
					Logger.error(TAG, "--parseBuddyList()::[ERROR] -- BUDDY PARSING ERROR" + e, e);
			}
		}
		// No 'MORE' Concept in Buddy list
		aResObj.getBuddyObject().setItemCount(buddyCount);
		return frameOffset;
	}

	private int parseIMSettingList(ResponseObject aResObj, int frameOffset) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseIMSetting--[FINE]: ENTRY");
		int countToParse = aResObj.mResponseData[frameOffset++]; // Count for IM
		if (countToParse == 0)
			return frameOffset;
		// CoreController.iSelf.IMStr = new String[aCountToParse];
		int tempInt;
		String tempStr;
		String[] imData;
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--parseIMSettingList--[INFO]: GROUP CURRENT COUNT = " + countToParse);

		for (int i = 0; i < countToParse; i++) {
			try {
				// Read 2 Bytes for Len of Each Record.
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
				frameOffset += 2;
				// VALUE IS LIKE
				// IM_TYPE;user_name;password;status;custom_message;remember_me(true/false);auto_login(true/false)
				tempStr = new String(aResObj.mResponseData, frameOffset, tempInt, Constants.ENC);
				imData = tempStr.split(";");
				for (int loop = 0; loop < IMData.IM_MAX; loop++) {
					if (IMData.MESSENGER_NAME_FOR_SERVER[loop].equals(imData[0])) {
						BusinessProxy.sSelf.mMessengerData.mUserNames[loop] = imData[1];
						BusinessProxy.sSelf.mMessengerData.mPasswords[loop] = imData[2];
						BusinessProxy.sSelf.mMessengerData.iStatusValue[loop] = Byte.parseByte(imData[3]);
						BusinessProxy.sSelf.mMessengerData.mCustomMessages[loop] = imData[4];

						if (imData[6].equals("true"))
							BusinessProxy.sSelf.mMessengerData.mCheckBoxData[loop] |= 1 << 1;
						else
							BusinessProxy.sSelf.mMessengerData.mCheckBoxData[loop] &= ~(1 << 1);
						BusinessProxy.sSelf.mMessengerData.mCheckBoxData[loop] |= 1 << 2;
						break;
					}
				}
				// strBuf = UiController.iUiController.split(str, ';');
				frameOffset += tempInt;
			} catch (Exception e) {
				if (Logger.ENABLE)
					Logger.error(TAG, "--parseIMSettingList--[ERROR]-" + e, e);
			}
		}
		BusinessProxy.sSelf.mMessengerData.copyNewLocalStatus();
		return frameOffset;
	}

	private void parseVoiceResponse(ResponseObject aResObj, int bitmap, int frameOffset) throws Throwable, OutOfMemoryError {
		try {
			int bitmapOffSet = 1;
			int len;
			int tempInt;
			while (bitmap > 0) {
				switch (bitmap & (1 << bitmapOffSet)) {
				case 1 << 1: // MSG_STATUS_BIT_MASK:
					// skip 1 byte to point src number length
					aResObj.setResponseCode(aResObj.mResponseData[frameOffset++]);
					//System.out.println("Server response code - "+aResObj.getResponseCode());
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]: RESPONSE STATUS = " + aResObj.getResponseCode());
					break;
				case 1 << 2:// SRC_NUM_BIT_MASK:
					len = aResObj.mResponseData[frameOffset++];
					// String tempStr = new
					// String(aResObj.mResponseData,frameOffset,len,
					// CommonConstants.ENC);
					// if(Logger.ENABLE)
					// Logger.verbose(TAG,
					// "Frame::parseVoiceResponse():[INFO]: SRC NUMBER = "
					// + tempStr, OtsLogger.INFO);
					frameOffset += len;
					break;
				case 1 << 3: // MSG_COUNT_BIT_MASK:
					// Fetch Message Count
					aResObj.setMessageCount(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4));
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]: Message Count = " + aResObj.getMessageCount());
					frameOffset += 4;
					break;
				case 1 << 4: // TRANS_ID_BIT_MASK:
					tempInt = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4);
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]:: GOT TRANS ID = " + tempInt + " : SENT TRANS ID = " + aResObj.getSentTransactionId());
					frameOffset += 4;
					break;
				case 1 << 5: // LAST_TRANS_ID_BIT_MASK:
					aResObj.setLastTransactionId(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4));
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]: LAST TRANSID = " + aResObj.getLastTransactionId());
					frameOffset += 4;
					break;
				case 1 << 6: // USER_ID_BIT_MASK:
					
					BusinessProxy.sSelf.setUserId(Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 4));
				
					FeedTask.writeToFile(" usere id change: "+BusinessProxy.sSelf.getUserId()) ;
					FeedTask.writeToFile(" usere id change getResponseCode : "+aResObj.getResponseCode()) ;
					FeedTask.writeToFile(" usere id change getSentOp : "+aResObj.getSentOp()) ;
					FeedTask.writeToFile(" usere id change getSentTransactionId : "+aResObj.getSentTransactionId()) ;
				
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]: USERID = " + BusinessProxy.sSelf.getUserId());
					frameOffset += 4;
					break;
				case 1 << 7:
					// frameOffset = parseBuddyList(aResObj, frameOffset);
					frameOffset = parseBuddyList(aResObj, frameOffset, aResObj.getSentOp());
					break;
				case 1 << 9:
					// upgrade type[SOFT/HARD]
					aResObj.setUpgradeType(aResObj.mResponseData[frameOffset++]);
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]: UPGRADE TYPE = " + aResObj.getUpgradeType());
					len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
					frameOffset += 2;
					aResObj.setUpgradeURL(new String(aResObj.mResponseData, frameOffset, len, Constants.ENC));
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]: URL = " + aResObj.getUpgradeURL());
					frameOffset += len;
					break;
				case 1 << 10: // Group bit is set and start parsing
					frameOffset = parseGroupList(aResObj, frameOffset);
					break;
				case 1 << 11:
					// frameOffset = parseGroupMemberList(aResObj,
					// frameOffset);
					break;
				case 1 << 12:
					// length of messageUpdateTimeStamp
					// String str = new
					// String(aResObj.mResponseData,frameOffset,len,
					// Constants.ENC);
					// if(Logger.ENABLE)
					// Logger.verbose(TAG,
					// "--parseVoiceResponse()[INFO] - messageUpdateTimeStamp = "
					// +str,OtsLogger.INFO);
					len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);
					frameOffset += len;
					break;
			/*	case 1 << 13: // PARSE IM BUDDIES INFORMATION
					// frameOffset = parseIMBuddyList(aResObj, frameOffset);
					switch (aResObj.getSentOp()) {
					case Constants.FRAME_TYPE_BUDDY_GET_OFFLINE:
						frameOffset = parseBuddyList(aResObj, frameOffset, aResObj.getSentOp());
						break;
					default:
						frameOffset = parseIMBuddyList(aResObj, frameOffset);
						break;
					}
					break;*/
			     case 1 << 13: //PARSE IM BUDDIES INFORMATION //new changed for emer
                     //frameOffset = parseIMBuddyList(aResObj, frameOffset);
//                     switch (aResObj.iSentOp)
//                     {
//                         case CommonConstants.FRAME_TYPE_BUDDY_GET_OFFLINE:
//                             frameOffset = parseBuddyList(aResObj, frameOffset, aResObj.iSentOp);
//                             break;
//                         default:
//                             frameOffset = parseIMBuddyList(aResObj, frameOffset);
//                             break;
//                     }
                     len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
                     frameOffset += 2;
//                     aResObj.iSuggestedNames = new String(aResObj.mResponseData, frameOffset, len, Constants.ENC);
                     aResObj.setSuggestedNames(new String(aResObj.mResponseData, frameOffset, len, Constants.ENC));
                     frameOffset += len;
                     break;
				case 1 << 15:// CHECK FOR IM SETTING HERE IF IM SETING
					// RECIEVED.
					frameOffset = parseIMSettingList(aResObj, frameOffset);
					break;
				case 1 << 16:// CHECK FOR STAT CONTROL FROM SERVER
					//
					len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
					frameOffset += 2;
					aResObj.setClientControl(new String(aResObj.mResponseData, frameOffset, len, Constants.ENC));
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse():[INFO]: Stat control string = " + aResObj.getClientControl());
					frameOffset += len;
					break;
				 case 1 << 17://new changed for emer
                     //this is special value from server
                     //2 - Log OFF
                     //3 - Log off and delete all DB's
                     //4 - Reset Message ID to null
                     //5 - Reset Message ID to given value
                     aResObj.setiSpecialValues(aResObj.mResponseData[frameOffset++]);
                     if (aResObj.getiSpecialValues() == 5)
                     {
                         len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset, 2);
                         frameOffset += 2;
                         BusinessProxy.sSelf.setTopMessageIdOnClient(new String(aResObj.mResponseData, frameOffset, len, Constants.ENC));
                         frameOffset += len;
                     }
                     break;
				case 1 << 24: // PARSE HEART BEAT OF THE IM
					tempInt = aResObj.mResponseData[frameOffset++];
					if (0 == tempInt)
						break;
					if (0 < ((1 << 0) & tempInt)) // MSN
					{
						if (IMData.LOGGING_IN == BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_MSN]) {
							if (IMData.OFFLINE != aResObj.mResponseData[frameOffset])
								BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_MSN] = aResObj.mResponseData[frameOffset];
						} else
							BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_MSN] = aResObj.mResponseData[frameOffset];
						++frameOffset;
					}
					if (0 < ((1 << 1) & tempInt)) // YAHOO
					{
						if (IMData.LOGGING_IN == BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_YAHOO]) {
							if (IMData.OFFLINE != aResObj.mResponseData[frameOffset])
								BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_YAHOO] = aResObj.mResponseData[frameOffset];
						} else
							BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_YAHOO] = aResObj.mResponseData[frameOffset];
						++frameOffset;
					}
					if (0 < ((1 << 2) & tempInt)) // AOL
					{
						if (IMData.LOGGING_IN == BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_AOL]) {
							if (IMData.OFFLINE != aResObj.mResponseData[frameOffset])
								BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_AOL] = aResObj.mResponseData[frameOffset];
						} else
							BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_AOL] = aResObj.mResponseData[frameOffset];
						++frameOffset;
					}
					if (0 < ((1 << 3) & tempInt)) // ICQ
					{
						if (IMData.LOGGING_IN == BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_ICQ]) {
							if (IMData.OFFLINE != aResObj.mResponseData[frameOffset])
								BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_ICQ] = aResObj.mResponseData[frameOffset];
						} else
							BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_ICQ] = aResObj.mResponseData[frameOffset];
						++frameOffset;
					}
					if (0 < ((1 << 4) & tempInt)) // GTALK
					{
						if (IMData.LOGGING_IN == BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_GTALK]) {
							if (IMData.OFFLINE != aResObj.mResponseData[frameOffset])
								BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_GTALK] = aResObj.mResponseData[frameOffset];
						} else
							BusinessProxy.sSelf.mMessengerData.iStatusValue[IMData.IM_GTALK] = aResObj.mResponseData[frameOffset];
						++frameOffset;
					}
					break;
				case 1 << 28: // 28th bit is checked for auto response
					len = Utilities.bytesToInt(aResObj.mResponseData, frameOffset++, 1);// length of messageUpdateTimeStamp
					String str = null;
					if (len > 0) {
						str = new String(aResObj.mResponseData, frameOffset, len, Constants.ENC);
						frameOffset += len;
						if (str.equals("ON"))
							SettingData.sSelf.setAutoReply(true);
						else
							SettingData.sSelf.setAutoReply(false);
					}
					if (Logger.ENABLE)
						Logger.verbose(TAG, "--parseVoiceResponse()[INFO] - Auto Response Value = " + str);
					str = null;
					break;
				case 1 << 30: // PAYLOAD_BIT_MASK:
					switch (aResObj.getSentOp()) {
					case Constants.FRAME_TYPE_SEARCH_OTS_BUDDY:
						parseSearchResult(aResObj, frameOffset);
						break;
					case Constants.FRAME_TYPE_PHCONTACTS_INVITE:
						parseInboxMessages(aResObj, frameOffset, true);
						break;
					case Constants.FRAME_TYPE_BOOKMARK:
					case Constants.FRAME_TYPE_INBOX_MORE:
						parseInboxMessages(aResObj, frameOffset, false);
						break;
					case Constants.FRAME_TYPE_GET_GROUP_DETAILS:
						InboxTblObject inboxData = new InboxTblObject(InboxTblObject.INBOX);
						frameOffset = parseForAttachment(aResObj, frameOffset, inboxData);
						aResObj.setData(inboxData);
						break;
					default:
						parseInboxMessages(aResObj, frameOffset, true);
						break;
					}
					break;
				default:
					break;
				}// switch (bitmap &&)
				bitmap = bitmap & ~(1 << bitmapOffSet);
				bitmapOffSet++;
			}// end while(bitmap)
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseVoiceResponse():[ERROR]:" + me, me);
			throw me;
		}
	}

	private void parseSearchResult(ResponseObject aResObj, int aOffset) throws Throwable, OutOfMemoryError {
		try {
			int tempInt;
			int bitmap;
			String tempStr;
			int count = aResObj.getMessageCount();
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseSearchResult():[INFO]---MESSAGE COUNT TO PARSE = " + count);
			InboxTblObject inboxData = new InboxTblObject(InboxTblObject.INBOX);
			int i = 0;
			for (; i < count; i++) {
				// check for the bits 1, 2, 3 for status, picture, voice

				// GET MESSAGE ID FOR i
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				inboxData.appendField(new Integer(tempInt), InboxTblObject.INBOX_FIELD_MSG_ID);
				aOffset += 4; // pointing to message status

				// GET MESSAGE STATUS FOR i
				bitmap = aResObj.mResponseData[aOffset++];
				inboxData.appendField(new Integer(bitmap), InboxTblObject.INBOX_FIELD_STATUS);

				//Fetch Length of user name
				// tempInt = aResObj.iResponseData[aOffset++];
				//Get Length in 2 Bytes
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 2);
				aOffset += 2;
				// Fetch USER NAME
				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				inboxData.appendField(tempStr, InboxTblObject.INBOX_FIELD_NUMBER);
				aOffset += tempInt;
				inboxData.appendField(tempStr, InboxTblObject.INBOX_FIELD_NAME);
				// Fetch length of Name field
				//				tempInt = aResObj.mResponseData[aOffset++];
				//				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				//				inboxData.appendField(tempStr, InboxTblObject.INBOX_FIELD_NAME);
				//				aOffset += tempInt;
			}
			inboxData.setItemCount(i);
			aResObj.setData(inboxData);
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseSearchResult():[ERROR]:" + me, me);
			throw me;
		}
	}

	//parse special forced  messages
	private int parseSpecialMessages(ResponseObject aResObj, int aOffset, boolean aIsPayload) throws Throwable, OutOfMemoryError {
		try {
			int tempInt;
			String tempStr;
			int count = 1;//aResObj.getMessageCount();
//			InboxActivity.totmsg = count;
			if (count > Constants.INBOX_MAX_REC_SIZE)
				count = Constants.INBOX_MAX_REC_SIZE;
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseSpecialMessages():[INFO]---MESSAGE COUNT TO PARSE = " + count);
			InboxTblObject inboxData = new InboxTblObject(InboxTblObject.INBOX);
			int i = 0;
			int drm;
			String messageID;
			for (; i < count; i++) {
				//GET LENGHT OF MESSAGE id IN 2 BYTES
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 2);
				aOffset += 2;
				messageID = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				if (Logger.ENABLE) {
					Logger.verbose(TAG, "Frame::parseSpecialMessages():[INFO]---MESSAGE ID OF MESSAGE " + i + " - " + messageID);
				}
				inboxData.insertField(0, messageID, InboxTblObject.INBOX_FIELD_MSG_ID);
				aOffset += tempInt;

				// GET MESSAGE ID FOR i
				//				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				//				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_MSG_ID);
				//				aOffset += 4; // pointing to message status

				// DRM FLAGS READ
				drm = tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_MSG_DRM);

				aOffset += 4;
				// NOTIFICATION FLAGS READ
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
				aOffset += 4;

				// GET MESSAGE STATUS FOR i
				tempInt = aResObj.mResponseData[aOffset++];
				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_STATUS);

				// Fetch length of from field
				tempInt = aResObj.mResponseData[aOffset++];

				// Fetch From Field
				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				inboxData.insertField(0, tempStr, InboxTblObject.INBOX_FIELD_NAME);
				aOffset += tempInt; // pointing to length of To ph numbers

				// Fetch Length of Userid / phone number /emailid
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 2);
				aOffset += 2;
				// pointing to userid/phone number/emailid
				// Fetch userid/phone Number/emailid
				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				inboxData.insertField(0, tempStr, InboxTblObject.INBOX_FIELD_NUMBER);

				aOffset += tempInt; // pointing to length of time
				// Fetch Length of time
				tempInt = aResObj.mResponseData[aOffset++];
				// Fetch Time
				//2011-05-14 04:57:22.0
				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				BusinessProxy.sSelf.mInboxTimeStamp = tempStr;
				//				System.out.println("Inbox Time:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + tempStr);
				//				if (tempStr.indexOf(".0") != -1)
				//					tempStr = tempStr.substring(0, tempStr.indexOf(".0"));
				//				if (0 == (drm & InboxTblObject.DRM_SYSTEM_MSG))
				//					tempStr = Utilities.convertTime(tempStr);
				//				System.out.println("Converted Inbox Time:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + tempStr);
				inboxData.insertField(0, tempStr, InboxTblObject.INBOX_FIELD_TIME);
				aOffset += tempInt; // pointing to payload/next message id
				if (aIsPayload) {
					aOffset = parseForAttachment(aResObj, aOffset, inboxData);
					if (aOffset == -1) {
						if (Logger.ENABLE)
							Logger.warning(TAG, "--parseSpecialMessages():[ERROR]--PAYLOAD PARSE ERROR");
						throw new Exception("Failed to parse payload");
					}
				} else {
					inboxData.mBitmap.add(0, new Integer(Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4)));
					aOffset += 4;
				}
			}
			inboxData.setItemCount(i);
			BusinessProxy.sSelf.mSearchResult = inboxData;

			if (inboxData == null || inboxData.getItemCount() == 0) {
				//no more message
				//sho
				///InboxActivity.totmsg = 0 ;
			} else {
				//makeToast("Incomeing more message:"+inboxData.getItemCount());
				///InboxActivity.totmsg = inboxData.getItemCount() ;
			}
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseSpecialMessages():[ERROR]:" + me, me);
			throw me;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseSpecialMessages():[ERROR]: " + ex, ex);
		}
		return aOffset;
	}

	private void parseInboxMessages(ResponseObject aResObj, int aOffset, boolean aIsPayload) throws Throwable, OutOfMemoryError {
		try {
			int tempInt;
			String tempStr;
			int count = aResObj.getMessageCount();
//			InboxActivity.totmsg = count;
			if (count > Constants.INBOX_MAX_REC_SIZE)
				count = Constants.INBOX_MAX_REC_SIZE;
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseInboxMessages():[INFO]---MESSAGE COUNT TO PARSE = " + count);
			InboxTblObject inboxData = new InboxTblObject(InboxTblObject.INBOX);
			int i = 0;
			int drm;
			String messageID;
			for (; i < count; i++) {
				//GET LENGHT OF MESSAGE id IN 2 BYTES
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 2);
				aOffset += 2;
				messageID = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				if (Logger.ENABLE) {
					Logger.verbose(TAG, "Frame::parseInboxMessages():[INFO]---MESSAGE ID OF MESSAGE " + i + " - " + messageID);
				}
				inboxData.insertField(0, messageID, InboxTblObject.INBOX_FIELD_MSG_ID);
				aOffset += tempInt;
				
				
				// GET MESSAGE ID FOR i
				//				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				//				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_MSG_ID);
				//				aOffset += 4; // pointing to message status

				// DRM FLAGS READ
				drm = tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_MSG_DRM);
//				System.out.println("INBOX_FIELD_MSG_DRM : "+tempInt);
				aOffset += 4;
				// NOTIFICATION FLAGS READ
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				//System.out.println("INBOX_FIELD_MSG_NOTIFICATION : "+tempInt);
				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
				aOffset += 4;

				// GET MESSAGE STATUS FOR i
				tempInt = aResObj.mResponseData[aOffset++];
				inboxData.insertField(0, new Integer(tempInt), InboxTblObject.INBOX_FIELD_STATUS);

				// Fetch length of from field
				tempInt = aResObj.mResponseData[aOffset++];

				// Fetch From Field
				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
//				System.out.println("===============MESSAGE ID AT============" + i + "==== MESSGAE ID:" + messageID + "===FROM==" + tempStr);
				inboxData.insertField(0, tempStr, InboxTblObject.INBOX_FIELD_NAME);
				aOffset += tempInt; // pointing to length of To ph numbers
//				System.out.println("INBOX_FIELD_NAME : "+tempStr);
				// Fetch Length of Userid / phone number /emailid
				tempInt = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 2);
				aOffset += 2;
				// pointing to userid/phone number/emailid
				// Fetch userid/phone Number/emailid
				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				inboxData.insertField(0, tempStr, InboxTblObject.INBOX_FIELD_NUMBER);
//				System.out.println("INBOX_FIELD_NUMBER : "+tempStr);
				aOffset += tempInt; // pointing to length of time
				// Fetch Length of time
				tempInt = aResObj.mResponseData[aOffset++];
				// Fetch Time
				//2011-05-14 04:57:22.0
				tempStr = new String(aResObj.mResponseData, aOffset, tempInt, Constants.ENC);
				//				System.out.println("=================TIME FOR Message======================" + tempStr + "============AT INDEX===========" + i);
				BusinessProxy.sSelf.mInboxTimeStamp = tempStr;
				//System.out.println("Inbox Time:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + tempStr);
				//				if (tempStr.indexOf(".0") != -1)
				//					tempStr = tempStr.substring(0, tempStr.indexOf(".0"));
				//				if (0 == (drm & InboxTblObject.DRM_SYSTEM_MSG))
				//					tempStr = Utilities.convertTime(tempStr);
				//				System.out.println("Converted Inbox Time:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + tempStr);
				inboxData.insertField(0, tempStr, InboxTblObject.INBOX_FIELD_TIME);
				aOffset += tempInt; // pointing to payload/next message id
				if (aIsPayload) {
					aOffset = parseForAttachment(aResObj, aOffset, inboxData);
					if (aOffset == -1) {
						if (Logger.ENABLE)
							Logger.warning(TAG, "--parseInboxMessages():[ERROR]--PAYLOAD PARSE ERROR");
						throw new Exception("Failed to parse payload");
					}
				} else {
					inboxData.mBitmap.add(0, new Integer(Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4)));
					aOffset += 4;
				}
			}
			inboxData.setItemCount(i);
			aResObj.setData(inboxData);

			if (inboxData == null || inboxData.getItemCount() == 0) {
				//no more message
				//sho
				///InboxActivity.totmsg = 0 ;
			} else {
				//makeToast("Incomeing more message:"+inboxData.getItemCount());
				///InboxActivity.totmsg = inboxData.getItemCount() ;
			}
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseInboxMessages():[ERROR]:" + me, me);
			throw me;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseInboxMessages():[ERROR]: " + ex, ex);
		}
	}

	private int parseForAttachment(ResponseObject aResObj, int aOffset, InboxTblObject aInboxData) throws OutOfMemoryError, Throwable {
		try {
			int payloadCount;
			int tempInt;
			byte paloadType;
			// GET PAYLOAD COUNT
			payloadCount = aResObj.mResponseData[aOffset];
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseForAttachment():[INFO] --Payload Count = " + payloadCount);
			Payload payloadData = new Payload();
			aOffset += 1; // pointing to payload type
			for (int i = 0; i < payloadCount; i++) {
				// GET PAYLOAD TYPE
				paloadType = aResObj.mResponseData[aOffset++];

				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseForAttachment():[INFO] --Payload Type = " + paloadType);
				// GET ATTACHMENT COUNT
				tempInt = aResObj.mResponseData[aOffset++];
				if (Logger.ENABLE)
					Logger.verbose(TAG, "--parseForAttachment():[INFO] -- Attachment Count = " + tempInt + " For " + paloadType);
				switch (paloadType) {
				case Payload.PAYLOAD_TYPE_VOICE:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VOICE;
					payloadData.mVoice = new byte[tempInt][];
					payloadData.mVoiceType = new byte[tempInt];
					aOffset = parseAttachment(aResObj, aOffset, payloadData.mVoice, payloadData.mVoiceType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_TEXT:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
					payloadData.mText = new byte[tempInt][];
					payloadData.mTextType = new byte[tempInt];
					aOffset = parseAttachment(aResObj, aOffset, payloadData.mText, payloadData.mTextType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_PICTURE:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_PICTURE;
					payloadData.mPicture = new byte[tempInt][];
					payloadData.mPictureType = new byte[tempInt];
					aOffset = parseAttachment(aResObj, aOffset, payloadData.mPicture, payloadData.mPictureType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_RTML:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_RTML;
					payloadData.mRTML = new byte[tempInt][];
					payloadData.mRTMLType = new byte[tempInt];
					aOffset = parseAttachment(aResObj, aOffset, payloadData.mRTML, payloadData.mRTMLType, tempInt);
				//	System.out.println("RTML Message ->"+new String(payloadData.mRTML[0], 0, payloadData.mRTML[0].length));
					break;
				case Payload.PAYLOAD_TYPE_VIDEO:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VIDEO;
					payloadData.mVideo = new byte[tempInt][];
					payloadData.mVideoType = new byte[tempInt];
					aOffset = parseAttachment(aResObj, aOffset, payloadData.mVideo, payloadData.mVideoType, tempInt);
					break;
				}
			}
			aInboxData.mPayload.add(0, payloadData);
			aInboxData.mBitmap.add(0, new Integer(payloadData.mPayloadTypeBitmap));
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseForAttachment():[ERROR]:" + me, me);
			throw me;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseForAttachment():[ERROR]: " + ex, ex);
			throw ex;
		}
		return aOffset;
	}

	private int parseAttachment(ResponseObject aResObj, int aOffset, byte[][] aAttachment, byte[] aAttachmentType, int aCount) throws OutOfMemoryError, Throwable {
		int len = 0;
		try {
			for (int j = 0; j < aCount; j++) {
				aAttachmentType[j] = aResObj.mResponseData[aOffset++];
				len = Utilities.bytesToInt(aResObj.mResponseData, aOffset, 4);
				aOffset += 4;
				// Attachment
				aAttachment[j] = new byte[len];
				System.arraycopy(aResObj.mResponseData, aOffset, aAttachment[j], 0, len);
				aOffset += len;
			}
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseAttachment():[ERROR]:" + me, me);
			throw me;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseAttachment():[ERROR]:" + ex, ex);
		}
		return aOffset;
	}

	public int createSettingFrame(OutboxTblObject aRequest) {
		try {
			if (aRequest == null)
				return Constants.ERROR_NULL_POINTER;
			SettingData settingInfo = (SettingData) aRequest.mRequestInfo;
			if (settingInfo == null)
				return Constants.ERROR_NULL_POINTER;
			StringBuffer frameStr = (new StringBuffer()).append("MSGTYPE");
			frameStr.append(Constants.EQUAL);
			frameStr.append("REG");
			frameStr.append(Constants.FIELD_SEPARATOR); // mesage type REG

			frameStr.append("SRCPHONENUM");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.mSrcNumber);
			frameStr.append(Constants.FIELD_SEPARATOR); // src number

			// PLATFORM
			frameStr.append("PLATFORM");
			frameStr.append(Constants.EQUAL);
			frameStr.append(YelatalkApplication.clientProperty.getProperty(ClientProperty.PLATFORM_NAME));
			frameStr.append(Constants.FIELD_SEPARATOR);

			// GMT Offset - Gets Default timezone
			frameStr.append("GMT_OFFSET");
			frameStr.append(Constants.EQUAL);
			TimeZone time = TimeZone.getDefault();
//			TimeZone tz = TimeZone.getDefault();
			String gmt1=TimeZone.getTimeZone(time.getID()).getDisplayName(false,TimeZone.SHORT);
//			String gmt2=TimeZone.getTimeZone(time.getID()).getDisplayName(false,TimeZone.LONG); 
//			System.out.println("TimeZone : "+gmt1+"\t"+gmt2);
			frameStr.append(gmt1);//time.getID());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// VERSION
			frameStr.append("CLIENTVER");
			frameStr.append(Constants.EQUAL);
			frameStr.append(YelatalkApplication.clientProperty.getProperty(ClientProperty.CLIENT_VERSION));
			frameStr.append(Constants.FIELD_SEPARATOR);

			// USER_NAME
			if (settingInfo.getUserName() != null && settingInfo.getUserName().length() > 0) {
				frameStr.append("USERNAME");
				frameStr.append(Constants.EQUAL);
				frameStr.append(settingInfo.getUserName());
				frameStr.append(Constants.FIELD_SEPARATOR);
			}
			// PASSWORD
			if (Constants.FRAME_TYPE_CHECK_AVAILABILITY != aRequest.mOp[0]) {
				frameStr.append("PASSWORD");
				frameStr.append(Constants.EQUAL);
				frameStr.append(settingInfo.getPassword());
				frameStr.append(Constants.FIELD_SEPARATOR);
			}

			// Login as invisible
			if (aRequest.mLoginAsInvisible == 1) {// 1 means login as invisible
				frameStr.append("INVISIBLE");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mLoginAsInvisible);
				frameStr.append(Constants.FIELD_SEPARATOR);
			}

			if (Constants.FRAME_TYPE_LOGIN == aRequest.mOp[0]) {
				//EMAIL-ID
				if (null != settingInfo.getEmail()) {
					frameStr.append("EMAILID");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getEmail());
					frameStr.append(Constants.FIELD_SEPARATOR);
				}

				//Phone Number
				if (null != settingInfo.getNumber() ) {
					frameStr.append("PHONENUM");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getNumber());
					frameStr.append(Constants.FIELD_SEPARATOR);
				}
			}

			// Activation code
			if (Constants.FRAME_TYPE_ACTIVATE_CODE == aRequest.mOp[0]) {
				frameStr.append("ACTIVATIONCODE");
				frameStr.append(Constants.EQUAL);
				frameStr.append(settingInfo.getActivationCode());
				frameStr.append(Constants.FIELD_SEPARATOR);
			}

			// Transaction Id
			frameStr.append("TRANSACTIONID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(aRequest.mIdList[0]);
			frameStr.append(Constants.FIELD_SEPARATOR);

			frameStr.append("IMEI");
			frameStr.append(Constants.EQUAL);
			frameStr.append(Utilities.getPhoneIMEINumberWithPushReg());
			frameStr.append(Constants.FIELD_SEPARATOR);

			frameStr.append("IMSI");
			frameStr.append(Constants.EQUAL);
			frameStr.append(Utilities.sPhoneModel);
			aRequest.mApi[0] = Constants.EXECUTE_VOICE;

			// VENDORNAME
			if (Constants.FRAME_TYPE_LOGOFF != aRequest.mOp[0]) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("VENDORNAME");
				frameStr.append(Constants.EQUAL);
				// ADD VENDORNAME
				frameStr.append(YelatalkApplication.clientProperty.getProperty(ClientProperty.VENDOR_NAME));
				frameStr.append(';');
				// ADD DISTRIBUTOR NAME
				frameStr.append(YelatalkApplication.clientProperty.getProperty(ClientProperty.DISTRIBUTOR_NAME));
			}
			if (Constants.FRAME_TYPE_LOGIN == aRequest.mOp[0] || Constants.FRAME_TYPE_SIGNUP == aRequest.mOp[0]) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("LANGUAGE");
				frameStr.append(Constants.EQUAL);
				frameStr.append(Utilities.sPhoneLanguage);
			}
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("OPERATION");
			frameStr.append(Constants.EQUAL);
			frameStr.append(aRequest.mMessageOp);
			/*
			 * if(aRequest.iOp[0] == Constants.FRAME_TYPE_LOGIN) { frameStr.append(Constants.FIELD_SEPARATOR); frameStr.append("BUDDY_TS"); frameStr.append(Constants.EQUAL);
			 * frameStr.append(CoreController.iSelf.iBuddyTimeStamp); } else /
			 */
			if (Constants.FRAME_TYPE_UPDATE_PROFILE == aRequest.mOp[0]) {
				// EMAIL-ID
				if (null != settingInfo.getEmail()) {
//					SettingData.sSelf.setEmailVerified(false) ;
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("EMAILID");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getEmail());
				}

				// PHONE-NUMBERs
				if (null != settingInfo.getNumber()) {
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("PHONENUM");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getNumber());
				}
				/**
				 * GOING TO WRITE A WRONG CODE. Actually there is no field in setting table object to send the new password so sending new password on the Carrier variable.
				 */
				if (null != settingInfo.getCarrier()) {
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("PASSWORD_UPDATE");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getCarrier());
				}
			} else if (aRequest.mOp[0] == Constants.FRAME_TYPE_SIGNUP) {
				// EMAIL-ID
				if (settingInfo.getEmail() != null) {
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("EMAILID");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getEmail());
				}
				// NAME
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("NAME");
				frameStr.append(Constants.EQUAL);
				frameStr.append(settingInfo.getGender());
				frameStr.append(':');
				if (null != settingInfo.getFirstName())
					frameStr.append(settingInfo.getFirstName());
				else
					frameStr.append(' ');
				frameStr.append(':');
				if (null != settingInfo.getLastName())
					frameStr.append(settingInfo.getLastName());
				else
					frameStr.append(' ');

				// PHONE-NUMBERs
				// in 741 version not need to send location info in registration
				if (null != settingInfo.getNumber()){
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("PHONENUM");
				frameStr.append(Constants.EQUAL);
				if (null != settingInfo.getNumber())
					frameStr.append(settingInfo.getNumber());
				else
					frameStr.append(' ');
				frameStr.append(": : : : ");
				}
				// ADDRESS
				// in 741 version not need to send location info in registration
				if (null != settingInfo.getCity() || null != settingInfo.getState() || null != settingInfo.getZip() || null != settingInfo.getCountry() ){
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("HOMEADD");
				frameStr.append(Constants.EQUAL);
				frameStr.append(" ");
				frameStr.append(':');
				if (null != settingInfo.getCity())
					frameStr.append(settingInfo.getCity());
//				else
//					frameStr.append(' ');
				frameStr.append(':');
				if (null != settingInfo.getState())
					frameStr.append(settingInfo.getState());
//				else
//					frameStr.append(' ');
				frameStr.append(':');
				if (null != settingInfo.getZip())
					frameStr.append(settingInfo.getZip());
//				else
//					frameStr.append(' ');
				frameStr.append(':');
				if (null != settingInfo.getCountry())
					frameStr.append(settingInfo.getCountry());
//				else
//					frameStr.append(' ');
				}

				// BIRTHDAY
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("BIRTHDAY");
				frameStr.append(Constants.EQUAL);
				if (null != settingInfo.getBirthDate())
					frameStr.append(settingInfo.getBirthDate());
				else
					frameStr.append(' ');

				if (Constants.FRAME_TYPE_SIGNUP != aRequest.mOp[0]) {
					// NOTIFICATION
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("NOTIFICATIONS");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getDelPrefWhenOffline());

					// COPY MESSAGE TO SPECIFIC LIKE EMAIL
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("COPYMESSAGETO");
					frameStr.append(Constants.EQUAL);
					frameStr.append(settingInfo.getCopyMessageTo());

					// VIEW PROFILE ENUM(YES, NO)
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("VIEW_PROFILE");
					frameStr.append(Constants.EQUAL);
					frameStr.append(0);
				}
			} else if (Constants.FRAME_TYPE_FORGOT_PASSWORD == aRequest.mOp[0]) {
				// PHONE-NUMBER
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("PHONENUM");
				frameStr.append(Constants.EQUAL);
				frameStr.append(settingInfo.getNumber());
			}
			aRequest.mRetryCount[0] = 0;
			// Logger.verbose(TAG, "--createSettingFrame():[INFO] FRAME = " +
			// frameStr.toString());
			aRequest.mHeader[0] = frameStr.toString();
//			System.out.println("REG Header -- "+aRequest.mHeader[0]);
//			System.out.println("==FRAME==" + frameStr.toString());
			frameStr = null;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--createSettingFrame():[ERROR] --" + ex, ex);
			return Constants.ERROR_CREATE_FRAME;
		}
		return Constants.ERROR_NONE;
	}

	public int createVtovFrame(OutboxTblObject aRequest) {
		try {
			if (aRequest == null)
				return Constants.ERROR_NULL_POINTER;
			aRequest.mApi[0] = Constants.EXECUTE_VOICE;
			int destCount = aRequest.mDestContacts.length;

			// MSG TYPE
			StringBuffer frameStr = new StringBuffer();
			frameStr.append("MSGTYPE");
			frameStr.append(Constants.EQUAL);
			frameStr.append("VTOV");
			frameStr.append(Constants.FIELD_SEPARATOR);

			// srcnumber
			frameStr.append("SRCPHONENUM");
			frameStr.append(Constants.EQUAL);
			//frameStr.append(BusinessProxy.sSelf.mSrcNumber);
			frameStr.append(BusinessProxy.sSelf.getUserId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// MESSAGE OP
			frameStr.append("OPERATION");
			frameStr.append(Constants.EQUAL);
			frameStr.append(aRequest.mMessageOp);
			frameStr.append(Constants.FIELD_SEPARATOR);

			// USER ID
			frameStr.append("USERID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.getUserId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// DESTINATION NUMBER COUNT
			frameStr.append("DESTPHONECOUNT");
			frameStr.append(Constants.EQUAL);

			// DESTINATION PHONES

			// check needs to be added here for rtml message.
			for (int tempInt = 0; tempInt < destCount; tempInt++) {
				frameStr.append(aRequest.mDestContacts[tempInt]);
				if (tempInt < (destCount - 1)) {
					frameStr.append(';');
				}
			}

			// MESSAGE-ID
			if (aRequest.mMessageId != null) {
				//if (aRequest.mMessageId > 0) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("MESSAGEID");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mMessageId);
			}

			// TRANSACTION ID
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("TRANSACTIONID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(aRequest.mIdList[0]);

			// DEVICE-ID
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("CLIENTID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.getClientId());

			// BUDDY_TS
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("BUDDY_TS");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.mBuddyTimeStamp);

			// GROUP_TS
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("GROUP_TS");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.mGroupTimeStamp);

			// top message id
			//			frameStr.append(Constants.FIELD_SEPARATOR);
			//			frameStr.append("TOPMSGID");
			//			frameStr.append(Constants.EQUAL);
			//			frameStr.append(BusinessProxy.sSelf.getTopMessageIdOnClient());
			if (BusinessProxy.sSelf.getTopMessageIdOnClient() != null) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("TOPMSGID");
				frameStr.append(Constants.EQUAL);
				frameStr.append(BusinessProxy.sSelf.getTopMessageIdOnClient());
			}

			//Inbox Time Stamp
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("INBOX_TS");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.mInboxTimeStamp);

			if (DataModel.sSelf.getBoolean(DMKeys.UPLOAD_PRIVATE_MEDIA)) {
				// for upload private media - UPLOADPRIVATEMEDIA.
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("UPLOADPRIVATEMEDIA");
				frameStr.append(Constants.EQUAL);
				frameStr.append(InboxTblObject.DRM_PRIVATE_MSG);
			}
			if (aRequest.mAutoPlay > 0) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("AUTOPLAY");
				frameStr.append(Constants.EQUAL);
				frameStr.append(InboxTblObject.DRM_AUTOPLAY);
				if (aRequest.mExtraCommand != null && aRequest.mExtraCommand.length() > 0) {
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("EXTRACOMMAND");
					frameStr.append(Constants.EQUAL);
					frameStr.append(aRequest.mExtraCommand);
				}
			}
			if (aRequest.mChatMessageType > 0) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("SPECIALMSG");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mChatMessageType);
			}
			if (aRequest.mRTAnimationMessage > 0) {
				//for RT Animation Command
				if (aRequest.mExtraCommand != null && aRequest.mExtraCommand.length() > 0) {
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("EXTRACOMMAND");
					frameStr.append(Constants.EQUAL);
					frameStr.append(aRequest.mExtraCommand);
				}
				
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("SPECIALMSG");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mChatMessageType);
			}

			aRequest.mRetryCount[0] = 0;
			aRequest.mHeader[0] = frameStr.toString();
//			System.out.println("V2V Header -- " + aRequest.mHeader[0]);
			frameStr = null;
		} catch (Exception e) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--createVtovFrame():[ERROR] --" + e, e);
			return Constants.ERROR_CREATE_FRAME;
		}
		return Constants.ERROR_NONE;
	}

	// ** CHANGE replace with vtov frame
	// ------------------------------------------------------------------------------

	public int createBookmarkFrame(OutboxTblObject aRequest) {
		try {
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--createInboxFrame():Entry:");
			if (aRequest == null)
				return Constants.ERROR_NULL_POINTER;
//			aRequest.mApi[0] = Constants.EXECUTE_VOICE;
			// MSG TYPE
			StringBuffer frameStr = new StringBuffer();
			frameStr.append("MSGTYPE");
			frameStr.append(Constants.EQUAL);
			frameStr.append("INBOX" );
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("OPERATION");
			frameStr.append(Constants.EQUAL);
			frameStr.append(aRequest.mMessageOp);//get
			frameStr.append(Constants.FIELD_SEPARATOR);


			// SOURCE PHONE NUMBER
			frameStr.append("SRCPHONENUM");
			frameStr.append(Constants.EQUAL);
			//frameStr.append(BusinessProxy.sSelf.mSrcNumber);
			frameStr.append(BusinessProxy.sSelf.getUserId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// DEVICE-ID
			frameStr.append("CLIENTID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.getClientId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// user id
			frameStr.append("USERID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.getUserId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			aRequest.mRetryCount[0] = 0;
			aRequest.mHeader[0] = frameStr.toString();
			//System.out.println("=====Inbox Header -- " + aRequest.mHeader[0]);
			frameStr = null;
		} catch (Exception e) {
			e.printStackTrace();
			if (Logger.ENABLE)
				Logger.error(TAG, "--createInboxFrame():[ERROR] --" + e, e);
			return Constants.ERROR_CREATE_FRAME;
		}
		return Constants.ERROR_NONE;
	}
	public int createInboxFrame(OutboxTblObject aRequest) {
		try {
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--createInboxFrame():Entry:");
			if (aRequest == null)
				return Constants.ERROR_NULL_POINTER;
			aRequest.mApi[0] = Constants.EXECUTE_VOICE;
			// MSG TYPE
			StringBuffer frameStr = new StringBuffer();
			frameStr.append("MSGTYPE");
			frameStr.append(Constants.EQUAL);
			frameStr.append("INBOX");
			frameStr.append(Constants.FIELD_SEPARATOR);
			frameStr.append("OPERATION");
			frameStr.append(Constants.EQUAL);
			frameStr.append(aRequest.mMessageOp);
			frameStr.append(Constants.FIELD_SEPARATOR);

			// get Top message ID
			//Top Message ID will be send in case of Refresh and other operations
			switch (aRequest.mMessageOp) {
			
//			case Constants.MSG_OP_GET:
			case Constants.MSG_OP_NEWMSG:
			case Constants.MSG_OP_SET:
			case Constants.MSG_OP_DEL:
			case Constants.MSG_OP_DEL_ALL:
			case Constants.MSG_OP_FWD:
			case Constants.MSG_OP_REPLY:
			case Constants.MSG_OP_VOICE_NEW:
				if (BusinessProxy.sSelf.getTopMessageIdOnClient() != null) {
					frameStr.append("TOPMSGID");
					frameStr.append(Constants.EQUAL);
					frameStr.append(BusinessProxy.sSelf.getTopMessageIdOnClient());
					frameStr.append(Constants.FIELD_SEPARATOR);
				}
				break;
			}

			// SOURCE PHONE NUMBER
			frameStr.append("SRCPHONENUM");
			frameStr.append(Constants.EQUAL);
			//frameStr.append(BusinessProxy.sSelf.mSrcNumber);
			frameStr.append(BusinessProxy.sSelf.getUserId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// DEVICE-ID
			frameStr.append("CLIENTID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.getClientId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// user id
			frameStr.append("USERID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(BusinessProxy.sSelf.getUserId());
			frameStr.append(Constants.FIELD_SEPARATOR);

			// TRANSACTION ID
			frameStr.append("TRANSACTIONID");
			frameStr.append(Constants.EQUAL);
			frameStr.append(aRequest.mIdList[0]);
			if (aRequest.mMessageOp != Constants.FRAME_TYPE_GET_GROUP_DETAILS && aRequest.mMessageId != null) {
				//				if (aRequest.mMessageOp != Constants.FRAME_TYPE_GET_GROUP_DETAILS && aRequest.mMessageId > 0) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("MESSAGEID");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mMessageId);
			}
			if (aRequest.mRTMLMessageId != null) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("RTML_MESSAGEID");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mRTMLMessageId);
			}
			if (aRequest.mMessageOp == Constants.MSG_OP_SET) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("READMESSAGEID");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mMessageIDString);
			}
			if (aRequest.mExtraCommand != null && aRequest.mExtraCommand.length() > 0) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("EXTRACOMMAND");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mExtraCommand);
			}
			switch (aRequest.mMessageOp) {
			case Constants.MSG_OP_GET_PROFILE:
			case Constants.MSG_OP_GROUP_PROFILE:
			case Constants.MSG_OP_GET_FRNDS_COMMUNITIES:
			case Constants.MSG_OP_GET_MEDIA:
				if (aRequest.mMessageIDString != null) {
					frameStr.append(Constants.FIELD_SEPARATOR);
					frameStr.append("GETPROFILE");
					frameStr.append(Constants.EQUAL);
					frameStr.append(aRequest.mMessageIDString);
				}
				break;
			}
			if (Constants.MSG_OP_MULTIPLE_DELETE == aRequest.mMessageOp) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("MULTIPLEDELETE");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mMessageIDString);
			}
			if (aRequest.mDirectionMore > 0) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("DIRECTION");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mDirectionMore);
			}

			if (aRequest.mReturnCount > 0 && aRequest.mMessageOp != Constants.MSG_OP_SEARCH_USER) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("RETURNCOUNT");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mReturnCount);
			}

			if (aRequest.mScript != null) {
				if (aRequest.mScript[0] != null) {
					if (aRequest.mMessageOp == Constants.MSG_OP_GROUP_DETAILS) {
						frameStr.append(Constants.FIELD_SEPARATOR);
						frameStr.append("FORGROUP");
						frameStr.append(Constants.EQUAL);
						frameStr.append(aRequest.mScript[0]);
					} else {
						frameStr.append(Constants.FIELD_SEPARATOR);
						frameStr.append("SEARCHTEXT");
						frameStr.append(Constants.EQUAL);
						frameStr.append(aRequest.mScript[0]);
					}
				}
			}
			//			if (aRequest.mMessageOp == Constants.FRAME_TYPE_GET_GROUP_DETAILS && aRequest.mMessageId > 0) {
			if (aRequest.mMessageOp == Constants.FRAME_TYPE_GET_GROUP_DETAILS && aRequest.mMessageId != null) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("FORGROUP");
				frameStr.append(Constants.EQUAL);
				frameStr.append(aRequest.mMessageId);
			}
			if (aRequest.mMessageOp != Constants.FRAME_TYPE_GET_GROUP_DETAILS) {
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("BUDDY_TS");
				frameStr.append(Constants.EQUAL);
				frameStr.append(BusinessProxy.sSelf.mBuddyTimeStamp);
				// GROUP_TS
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("GROUP_TS");
				frameStr.append(Constants.EQUAL);
				frameStr.append(BusinessProxy.sSelf.mGroupTimeStamp);
				// INBOX_TS
				frameStr.append(Constants.FIELD_SEPARATOR);
				frameStr.append("INBOX_TS");
				frameStr.append(Constants.EQUAL);
				frameStr.append(BusinessProxy.sSelf.mInboxTimeStamp);
			}

			aRequest.mRetryCount[0] = 0;
			aRequest.mHeader[0] = frameStr.toString();
			System.out.println("=====Inbox Header -- " + aRequest.mHeader[0]);
			frameStr = null;
		} catch (Exception e) {
			e.printStackTrace();
			if (Logger.ENABLE)
				Logger.error(TAG, "--createInboxFrame():[ERROR] --" + e, e);
			return Constants.ERROR_CREATE_FRAME;
		}
		return Constants.ERROR_NONE;
	}

	// ------------------------------------------------------------------------------

	public byte[] parseScriptAndCreateOtsFrame(OutboxTblObject aRequestObject) {
		byte[] otsFrame = null;
		try {
			parseScript(aRequestObject);
			if (mScriptTable.isEmpty() == false) {
				switch (aRequestObject.mOp[0]) {
				case Constants.FRAME_TYPE_SIGNUP:
				case Constants.FRAME_TYPE_FORGOT_PASSWORD:
				case Constants.FRAME_TYPE_CHECK_AVAILABILITY:
				case Constants.FRAME_TYPE_LOGIN:
				case Constants.FRAME_TYPE_GET_PROFILE:
				case Constants.FRAME_TYPE_GET_PREFERENCES:
				case Constants.FRAME_TYPE_GET_ACC_SETTING:
				case Constants.FRAME_TYPE_COMM_MSG_STATUS:
				case Constants.FRAME_TYPE_GET_EXTENDED_PROFILE:
				case Constants.FRAME_TYPE_UPDATE_PROFILE:
				case Constants.FRAME_TYPE_LOGOFF:
				case Constants.FRAME_TYPE_ACTIVATE_CODE:
					constructRegBitmap(aRequestObject);
					otsFrame = createOtsRegFrame(aRequestObject);
					break;
				case Constants.FRAME_TYPE_VTOV:
					constructVoiceBitmap();
					otsFrame = createOtsVoiceFrame(aRequestObject);
					break;
				case Constants.FRAME_TYPE_INBOX_PLAY:
				case Constants.FRAME_TYPE_INBOX_SAVE_VOICE:
				case Constants.FRAME_TYPE_INBOX_MORE:
				case Constants.FRAME_TYPE_INBOX_DEL:
				case Constants.FRAME_TYPE_INBOX_DEL_ALL:
				case Constants.FRAME_TYPE_INBOX_REFRESH:
				case Constants.FRAME_TYPE_SEARCH_OTS_BUDDY:
				case Constants.FRAME_TYPE_PHCONTACTS_INVITE:
				case Constants.FRAME_TYPE_INBOX_GET_PROFILE:
				case Constants.FRAME_TYPE_VIEW_GROUP_PROFILE:
				case Constants.FRAME_TYPE_CHECK_AVAIL_COMM:
					// case Constants.FRAME_TYPE_DEL_OTS_BUDDY:
					// case Constants.FRAME_TYPE_DEL_ALL_OTS_BUDDY:
					// case Constants.FRAME_TYPE_GROUP_CREATE_NEW:
					// case Constants.FRAME_TYPE_GROUP_DEL:
					// case Constants.FRAME_TYPE_GROUP_ADD_MEM:
					// case Constants.FRAME_TYPE_GROUP_DETAILS:
					// case Constants.FRAME_TYPE_GROUP_DEL_MEM:
					// case
					// Constants.FRAME_TYPE_GROUP_UPDATE_MEM_STATUS:
				case Constants.FRAME_TYPE_GET_IM_SETTING:
					// for
					// Sending
					// IM
					// Setting
					// request.
				case Constants.FRAME_TYPE_GET_IM_OFFLINE:
				case Constants.FRAME_TYPE_BUDDY_GET_OFFLINE:
				case Constants.FRAME_TYPE_IM_SIGN_OUT:
				case Constants.FRAME_TYPE_GET_GROUP_DETAILS:
					// for
					// Sending
					// IM
					// Setting
					// request.
				case Constants.FRAME_TYPE_CHECK_UPGRADE:
				case Constants.FRAME_TYPE_PREVIEW_DEFAULT_BUZZ:
				case Constants.FRAME_TYPE_INBOX_CHECK_NEW:				
					constructInboxBitmap(aRequestObject);
					otsFrame = createOtsInboxFrame(aRequestObject);
					break;
				case Constants.FRAME_TYPE_BOOKMARK:
					constructBookMarkBitmap(aRequestObject);
					otsFrame = createOtsBookmarkFrame(aRequestObject);
					break;
				}
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseScriptAndCreateOtsFrame():[ERROR] --Failed to build ots REG frame" + ex, ex);
		}
		return otsFrame;
	}

	// ------------------------------------------------------------------------------

	private void parseScript(OutboxTblObject aRequestObject) {
		try {
			String requestHeader = aRequestObject.mHeader[0];
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseScript():Entry::");
			int requestHeaderLen = requestHeader.length();
			int begIndex = 0;
			int endIndex = 0;
			String script;
			String value;
			mOtsFrameLen = 0;
			mScriptTable = new Hashtable<String, String>();
			while (endIndex < requestHeaderLen) {
				// Parsing Script
				endIndex = requestHeader.indexOf('=', begIndex);
				script = requestHeader.substring(begIndex, endIndex);
				// Parsing Value
				begIndex += script.length() + 1;
				endIndex = requestHeader.indexOf(',', begIndex);
				if (endIndex == -1) {
					endIndex = requestHeader.length();
				}
				value = requestHeader.substring(begIndex, endIndex);
				// Logger.verbose(TAG, "--parseScript():[INFO]---SCRIPT == "
				// + script + "::VALUE == " + value);
				mScriptTable.put(script, value);

				begIndex += value.length() + 1;
				// mOtsFrameLen += value.length();
			}
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--parseScript():[INFO] - Exit parse Script.");
		} catch (Exception e) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseScript():[ERROR]:: " + e, e);
		}

	}

	// ------------------------------------------------------------------------------

	/**
	 * This method deals with the creation of the bitmap structure of the frame to be transffered. In this method the already created HashTable is used to create the Bitmap sturcture. The bitmap is
	 * created according to the entry found in the HashTable. Suppose if the entry is found as USERNAME then it increses the total lenth uses by the frame(mOtsFrameLen) and sets the first bit of the
	 * variable iBitmap.
	 */
	private void constructRegBitmap(OutboxTblObject aRequestObject) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructRegBitmap():Entry::");
		mBitmap = 0;
		mOtsFrameLen = 0;
		String tempStr;
		String valueStr;
		Enumeration<String> scriptEnum = mScriptTable.keys();
		Enumeration<String> valueEnum = mScriptTable.elements();
		while (scriptEnum.hasMoreElements() && valueEnum.hasMoreElements()) {
			tempStr = scriptEnum.nextElement();
			valueStr = valueEnum.nextElement();
			if (Logger.ENABLE)
				Logger.verbose(TAG, "--constructRegBitmap() KEY = " + tempStr + ", VALUE = " + valueStr);
			if (tempStr.equals("USERNAME")) { // userid
				mOtsFrameLen += valueStr.length() + 1; // 1 byte for the length
				// of the user name and
				// length of the name
				mBitmap |= USER_ID_BITMASK;
			} else if (tempStr.equals("PASSWORD")) {
				mOtsFrameLen += valueStr.length() + 1; // 1 byte for the length
				// of password and then
				// length of password
				mBitmap |= PASSWORD_BITMASK;
			} else if (tempStr.equals("INVISIBLE")) {
				mOtsFrameLen += 1; // Value in 1 byte
				mBitmap |= INVISIBLE_MODE_BITMASK;
			} else if (tempStr.equals("ACTIVATIONCODE")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= PASSWORD_UPDATE_BITMASK;
			} else if (tempStr.equals("OPERATION")) {
				mOtsFrameLen++;
				mBitmap |= MSG_OP_BITMASK;
			} else if (tempStr.equals("CLIENTVER")) {
				mOtsFrameLen += valueStr.length() + 1; // 1 byte to store length
				// of the client version
				// and then length of
				// client version
				mBitmap |= CLIENT_VER_BITMASK;
			} else if (tempStr.equals("MSGTYPE")) {
				mOtsFrameLen++;
				mBitmap |= MSG_TYPE_BITMASK;
			} else if (tempStr.equals("AGERANGE")) {
				mOtsFrameLen = valueStr.length() + 1;
				mBitmap |= AGE_BITMASK;
			} else if (tempStr.equals("NOTIFICATIONS")) {
				mOtsFrameLen++;
				mBitmap |= NOTIFICATION_BITMASK;
			} else if (tempStr.equals("COPYMESSAGETO")) {
				mOtsFrameLen++;
				mBitmap |= PREF_NOTIFY_BITMASK;
			} else if (tempStr.equals("VIEW_PROFILE")) {
				mOtsFrameLen++;
				mBitmap |= VIEW_PROFILE_BITMASK;
			} else if (tempStr.equals("PASSWORD_UPDATE")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= PASSWORD_UPDATE_BITMASK;
			} else if (tempStr.equals("CLIENTID")) {
				mOtsFrameLen += 4; // +4 for device id
				mBitmap |= DEV_ID_BITMASK;
			} else if (tempStr.equals("SRCPHONENUM")) {
				mOtsFrameLen += valueStr.length() + 1; // 1 for length and the
				// length
				mBitmap |= SOURCE_BITMASK;
			} else if (tempStr.equals("EMAILID")) {
				mOtsFrameLen += valueStr.length() + 1; // 1 for length and then
				// length
				mBitmap |= EMAIL_ID_BITMASK;
			} else if (tempStr.equals("NAME")) {
				mOtsFrameLen += valueStr.length() + 1; // 1 for length and then
				// length
				mBitmap |= USER_NAME_BITMASK;
			} else if (tempStr.equals("HOMEADD")) {
				mOtsFrameLen += valueStr.length() + 1; // same above
				mBitmap |= HOME_ADDR_BITMASK;
			} else if (tempStr.equals("IMSI")) {
				mOtsFrameLen += valueStr.length() + 1; // same above
				mBitmap |= DEV_IMSI_BITMASK;
			} else if (tempStr.equals("IMEI")) {
				if (valueStr != null && valueStr.length() <= 127) //Write data in 1 bytes
					mOtsFrameLen += valueStr.length() + 1; // 18th july - now length will go in two bytes
				else
					mOtsFrameLen += valueStr.length() + 2 + 1; // 18th july - now length will go in two bytes - 1 byte extra, if value is greater than 127
				mBitmap |= DEV_IMEI_BITMASK;
			} else if (tempStr.equals("CARRIER")) {
				mOtsFrameLen += valueStr.length() + 1; // same above
				mBitmap |= CARRIER_BITMASK;
			} else if (tempStr.equals("TRANSACTIONID")) {
				mOtsFrameLen += 4; // add only 4-transid.length;
				mBitmap |= TRANSACTION_ID_BITMASK;
			} else if (tempStr.equals("PHONENUM")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= PHONE_NUM_BITMASK;
			} else if (tempStr.equals("BUDDY_TS")) {
				mOtsFrameLen += BusinessProxy.sSelf.mBuddyTimeStamp.length() + 1;
				mBitmap |= REG_BUDDY_TS;
			} else if (tempStr.equals("PLATFORM")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= PLATFORM_BITMASK;
			} else if (tempStr.equals("GMT_OFFSET")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= BIT_GMT_OFFSET;
			} else if (tempStr.equals("BIRTHDAY")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= BIRTHDAY_BITMASK;
			} else if (tempStr.equals("VENDORNAME")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= VENDOR_NAME_BITMASK;
			} else if (tempStr.equals("LANGUAGE")) {
				mOtsFrameLen++;
				mBitmap |= LANGUAGE_BITMASK;
			}
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructRegBitmap() -- Exit");
	}

	// ------------------------------------------------------------------------------

	private byte[] createOtsRegFrame(OutboxTblObject aRequestObject) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsRegFrame():Entry::");
		byte[] otsFrame = null;
		String tempStr;
		int index = 0;
		int bitmap = 0;
		int offset = 0;
		byte payloadCount = 0;
//		System.out.println("---mScriptTable : "+mScriptTable.toString());
		try {
			// following lines
			if (aRequestObject.mPayloadData.mPayloadTypeBitmap > 0) {
				mBitmap |= VOICE_PAYLOAD_BITMASK_FOR_SIGNUP; // 1 << 29 - for
				// payload in registration.
				mOtsFrameLen += 1; // for payload count
				bitmap = aRequestObject.mPayloadData.mPayloadTypeBitmap;
				while (bitmap > 0) {
					switch (bitmap & (1 << offset)) {
					case Payload.PAYLOAD_BITMAP_VOICE:
						mOtsFrameLen += 2; // for attachment count + payload type
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mVoice);
						break;
					case Payload.PAYLOAD_BITMAP_TEXT:
						mOtsFrameLen += 2; // for attachment count + payload type
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mText);
						break;
					case Payload.PAYLOAD_BITMAP_PICTURE:
						mOtsFrameLen += 2; // for attachment count + payload type
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mPicture);
						break;
					case Payload.PAYLOAD_BITMAP_VIDEO:
						mOtsFrameLen += 2; // for attachment count + payload type
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mVideo);
						break;
					}
					bitmap = bitmap & ~(1 << offset);
					offset++;
				}
			}

			mOtsFrameLen += 8; // 4 for frame length and 4 for bitmap
			otsFrame = new byte[mOtsFrameLen];
			// Frame length
			index = setDataToFrame(otsFrame, index, mOtsFrameLen, 4); // writing
			index = setDataToFrame(otsFrame, index, mBitmap, 4); // writing 4
			// Run a loop for Bitmap
			int bitMap = mBitmap;
			int offSet = 0;
			while (bitMap != 0) {
				switch (bitMap & (1 << offSet)) {
				case USER_ID_BITMASK:
					tempStr = (String) mScriptTable.get("USERNAME");
					// Length of source
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case PASSWORD_BITMASK:
					tempStr = (String) mScriptTable.get("PASSWORD");
					// Length of source
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case INVISIBLE_MODE_BITMASK:
					otsFrame[index++] = (byte) aRequestObject.mLoginAsInvisible;
					break;
				case MSG_TYPE_BITMASK:
					otsFrame[index++] = (byte) 1;
					break;
				case SOURCE_BITMASK:
					tempStr = (String) mScriptTable.get("SRCPHONENUM");
					// Length of source
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case TRANSACTION_ID_BITMASK:
					tempStr = (String) mScriptTable.get("TRANSACTIONID");
					index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
					break;
				case MSG_OP_BITMASK:
					tempStr = (String) mScriptTable.get("OPERATION");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					break;
				case CLIENT_VER_BITMASK:
					tempStr = (String) mScriptTable.get("CLIENTVER");
					otsFrame[index++] = (byte) (tempStr.length() & 0xff); // Length
					// of
					// source
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case EMAIL_ID_BITMASK:
					tempStr = (String) mScriptTable.get("EMAILID");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case DEV_ID_BITMASK:
					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getClientId(), 4);
					break;
				case HOME_ADDR_BITMASK:
					tempStr = (String) mScriptTable.get("HOMEADD");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case USER_NAME_BITMASK: // first middle last
					tempStr = (String) mScriptTable.get("NAME");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case DEV_IMSI_BITMASK:
					tempStr = (String) mScriptTable.get("IMSI");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case DEV_IMEI_BITMASK:
					tempStr = (String) mScriptTable.get("IMEI");
					//otsFrame[index++] = (byte) tempStr.length();
					if (tempStr != null && tempStr.length() <= 127) //Write data in 1 bytes
					{
						otsFrame[index++] = (byte) tempStr.length();
						System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
						index += otsFrame[index - 1];
					} else if (tempStr != null && tempStr.length() > 127)// write length in two byte
					{
						otsFrame[index++] = (byte) (-1);
						index = setDataToFrame(otsFrame, index, tempStr.length(), 2);
						System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, tempStr.length());
						index += tempStr.length();
					}

					/*
					 * index = setDataToFrame(otsFrame, index, tempStr.length(), 2); System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, tempStr.length()); index += tempStr.length();
					 */
					break;
				case CARRIER_BITMASK:
					tempStr = (String) mScriptTable.get("CARRIER");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case AGE_BITMASK:
					otsFrame[index++] = (byte) Integer.parseInt((String) mScriptTable.get("AGERANGE"));
					break;
				case NOTIFICATION_BITMASK:
					otsFrame[index++] = (byte) Integer.parseInt((String) mScriptTable.get("NOTIFICATIONS"));
					break;
				case PREF_NOTIFY_BITMASK:
					otsFrame[index++] = (byte) Integer.parseInt((String) mScriptTable.get("COPYMESSAGETO"));
					break;
				case PHONE_NUM_BITMASK:
					tempStr = (String) mScriptTable.get("PHONENUM");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case VENDOR_NAME_BITMASK:
					tempStr = (String) mScriptTable.get("VENDORNAME");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case LANGUAGE_BITMASK:
					otsFrame[index++] = (byte) Integer.parseInt((String) mScriptTable.get("LANGUAGE"));
					break;
				/*
				 * case COMPRESSION_BITMASK: otsFrame[index++] = (byte) Integer.parseInt((String) mScriptTable.get("COMPRESS")); break;
				 */
				case VOICE_PAYLOAD_BITMASK_FOR_SIGNUP:
					// Payload count
					/*
					 * otsFrame[index++] = (byte) (1 & 0xff); //payload type otsFrame[index++] = (byte) 4; //retreive from config //Payload length index = setDataToFrame(otsFrame, index,
					 * aRequestObject.iPayloadData.mVoice[0].length, 4); System.arraycopy(aRequestObject.iPayloadData.mVoice [0], 0, otsFrame, index, aRequestObject.iPayloadData.mVoice[0].length);
					 * index += aRequestObject.iPayloadData.mVoice[0].length;
					 */
					// Payload count
					otsFrame[index++] = payloadCount;
					bitmap = aRequestObject.mPayloadData.mPayloadTypeBitmap;
					offset = 0;
					while (bitmap > 0) {
						switch (bitmap & (1 << offset)) {
						case Payload.PAYLOAD_BITMAP_VOICE:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_VOICE;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mVoice, aRequestObject.mPayloadData.mVoiceType);
							break;
						case Payload.PAYLOAD_BITMAP_TEXT:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_TEXT;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mText, aRequestObject.mPayloadData.mTextType);
							break;
						case Payload.PAYLOAD_BITMAP_PICTURE:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_PICTURE;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mPicture, aRequestObject.mPayloadData.mPictureType);
							break;
						case Payload.PAYLOAD_BITMAP_VIDEO:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_VIDEO;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mVideo, aRequestObject.mPayloadData.mVideoType);
							break;
						}
						bitmap = bitmap & ~(1 << offset);
						offset++;
					}
					break;
				case REG_BUDDY_TS:
					tempStr = BusinessProxy.sSelf.mBuddyTimeStamp;
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case VIEW_PROFILE_BITMASK:
					otsFrame[index++] = (byte) Integer.parseInt((String) mScriptTable.get("VIEW_PROFILE"));
					break;
				case PASSWORD_UPDATE_BITMASK:
					if (Constants.FRAME_TYPE_ACTIVATE_CODE == aRequestObject.mOp[0])
						tempStr = (String) mScriptTable.get("ACTIVATIONCODE");// originally
					// there was PASSWORD_UPDATE
					else
						tempStr = (String) mScriptTable.get("PASSWORD_UPDATE");// originally
					// there was PASSWORD_UPDATE
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				// case PASSWORD_UPDATE_BITMASK:
				// tempStr = (String) mScriptTable.get("ACTIVATIONCODE");// originally there was
				// PASSWORD_UPDATE
				// otsFrame[index++] = (byte) tempStr.length();
				// System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index -
				// 1]);
				// index += otsFrame[index - 1];
				// break;
				case PLATFORM_BITMASK:
					tempStr = (String) mScriptTable.get("PLATFORM");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case BIT_GMT_OFFSET:
					tempStr = (String) mScriptTable.get("GMT_OFFSET");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case BIRTHDAY_BITMASK:
					tempStr = (String) mScriptTable.get("BIRTHDAY");
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				}// End switch()
				bitMap = bitMap & ~(1 << offSet);
				offSet++;
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--createOtsRegFrame():[ERROR] --Failed to build ots REG frame" + ex, ex);
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsRegFrame():Exit::");
		return otsFrame;
	}

	// ------------------------------------------------------------------------------

	private void constructVoiceBitmap() {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructVoiceBitmap():Entry::");
		mBitmap = 0;
		mOtsFrameLen = 0;
		String tempStr = "";
		String valueStr = "";
		Enumeration<String> scriptEnum = mScriptTable.keys();
		Enumeration<String> valueEnum = mScriptTable.elements();
		while (scriptEnum.hasMoreElements() && valueEnum.hasMoreElements()) {
			tempStr = (String) scriptEnum.nextElement();
			valueStr = (String) valueEnum.nextElement();
			if (tempStr.equals("MSGTYPE")) {
				mOtsFrameLen++;
				mBitmap |= MSG_TYPE_BITMASK;
			} else if (tempStr.equals("SRCPHONENUM")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= VOICE_FRAME_SOURCE_BITMASK;
			} else if (tempStr.equals("OPERATION")) {
				mOtsFrameLen++;
				mBitmap |= MSG_OP_BITMASK;
			} else if (tempStr.equals("USERID")) {
				mOtsFrameLen += 4;
				mBitmap |= VOICE_FRAME_USERID_BITMASK;
			} else if (tempStr.equals("TIME")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= VOICE_FRAME_TIME_BITMASK;
			} else if (tempStr.equals("GREETINGID")) {
				mOtsFrameLen += 2;
				mBitmap |= VOICE_FRAME_GREETING_BITMASK;
			} else if (tempStr.equals("CATEGORYID")) {
				mOtsFrameLen += 2;
				mBitmap |= VOICE_FRAME_CATEGORY_BITMASK;
			} else if (tempStr.equals("DESTPHONECOUNT")) {
				// length must be stored in 2 bytes
				try {
					mOtsFrameLen += valueStr.getBytes(Constants.ENC).length + 2;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mBitmap |= VOICE_FRAME_DESTINATION_BITMASK;
			} else if (tempStr.equals("MESSAGEID")) {
				mOtsFrameLen += 2 + valueStr.length();
				//mOtsFrameLen += 4;
				mBitmap |= VOICE_FRAME_MSG_ID_BITMASK;
			} else if (tempStr.equals("TRANSACTIONID")) {
				mOtsFrameLen += 4;
				mBitmap |= VOICE_FRAME_TRANS_ID_BITMASK;
			} else if (tempStr.equals("CLIENTID")) {
				mOtsFrameLen += 4;
				mBitmap |= VOICE_FRAME_DEV_ID_BITMASK;
			} else if (tempStr.equals("BUDDY_TS")) {
				mOtsFrameLen += (BusinessProxy.sSelf.mBuddyTimeStamp.length() + 1);
				mBitmap |= VOICE_FRAME_BUDDY_BITMASK;
			} else if (tempStr.equals("GROUP_TS")) {
				try{
				mOtsFrameLen += (BusinessProxy.sSelf.mGroupTimeStamp.length() + 1);
				mBitmap |= VOICE_FRAME_GROUP_BITMASK;
				}catch (Exception e) {
					// TODO: handle exception
				}
			} else if (tempStr.equals("INBOX_TS")) {
				mOtsFrameLen += valueStr.length() + 1;
				mBitmap |= VOICE_FRAME_MESSAGE_TIMESTAMP;
			} else if (tempStr.equals("TOPMSGID")) {
				mOtsFrameLen += 2 + valueStr.length();
				//mOtsFrameLen += 4;
				mBitmap |= VOICE_FRAME_TOP_MSD_ID;
			} else if (tempStr.equals("UPLOADPRIVATEMEDIA")) {
				mOtsFrameLen += 4;
				mBitmap |= VOICE_FRAME_DRM_BITMASK;
			} else if (tempStr.equals("AUTOPLAY")) {
				// increase the frame length = 2 (length for command) + actual command
				tempStr = (String) mScriptTable.get("EXTRACOMMAND");
				// Two bytes for the length
				if (tempStr != null)
					mOtsFrameLen += 2 + tempStr.length();
				mBitmap |= VOICE_FRAME_BUZZ_DRM_BITMASK;
			} else if (tempStr.equals("SPECIALMSG")) {
				// increase the frame length = 2 (length for command) + actual
				// command
				tempStr = (String) mScriptTable.get("SPECIALMSG");
				if (tempStr != null)
					mOtsFrameLen += 1; // Two bytes for the lentgh
				mBitmap |= VOICE_FRAME_SPECIALMSG;
			}else if (tempStr.equals("RTANIMMSG")) {
				//For RT Animation MESSAGING
				tempStr = (String) mScriptTable.get("RTANIMMSG");
				if (tempStr != null)
					mOtsFrameLen += 1; 
				//Two bytes for the length and Command
				tempStr = (String) mScriptTable.get("EXTRACOMMAND");
				if (tempStr != null)
					mOtsFrameLen += 2 + tempStr.length();
				mBitmap |= VOICE_FRAME_RT_ANIMATION;
			}
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructVoiceBitmap():Exit::");
	}

	// ------------------------------------------------------------------------------

	private byte[] createOtsVoiceFrame(OutboxTblObject aRequestObject) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsVoiceFrame():Entry::");
		byte[] otsFrame = null;
		String tempStr = null;
		int index = 0;
		int bitmap = 0;
		int offset = 0;
		byte payloadCount = 0;
		try {
			if (aRequestObject.mPayloadData.mPayloadTypeBitmap > 0) {
				mBitmap |= VOICE_PAYLOAD_BITMASK;
				mOtsFrameLen += 1; // for payload count
				bitmap = aRequestObject.mPayloadData.mPayloadTypeBitmap;
				while (bitmap > 0) {
					mOtsFrameLen += 2; // payload type(voice, picture, text) + payload count of that type
					switch (bitmap & (1 << offset)) {
					case Payload.PAYLOAD_BITMAP_VOICE:
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mVoice);
						break;
					case Payload.PAYLOAD_BITMAP_TEXT:
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mText);
						break;
					case Payload.PAYLOAD_BITMAP_PICTURE:
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mPicture);
						break;
					case Payload.PAYLOAD_BITMAP_VIDEO:
						payloadCount++;
						setAttachmentLength(aRequestObject.mPayloadData.mVideo);
						break;
					}
					bitmap = bitmap & ~(1 << offset);
					offset++;
				}
			}// if (aRequestObject.mPayloadTypeBitmap > 0)
			mOtsFrameLen += 8; // 4 for frame length and 4 for bitmap
			// Logger.verbose(TAG,
			// "--createOtsVoiceFrame():[INFO]--Frame Length == "
			// + mOtsFrameLen);
			otsFrame = new byte[mOtsFrameLen];
			// Frame length
			index = setDataToFrame(otsFrame, index, mOtsFrameLen, 4);
			// BitMap
			index = setDataToFrame(otsFrame, index, mBitmap, 4);
			// Run a loop for Bitmap
			int bitMap = mBitmap;
			int offSet = 0;
			while (bitMap != 0) {
				int maskType = bitMap & (1 << offSet);
				switch (maskType) {
				case MSG_TYPE_BITMASK:
					otsFrame[index++] = (byte) 2; // VTOV
					break;
				case VOICE_FRAME_SOURCE_BITMASK:
					tempStr = (String) mScriptTable.get("SRCPHONENUM");
					// Length of source
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case MSG_OP_BITMASK:
					tempStr = (String) mScriptTable.get("OPERATION");
					//System.out.println("Message OP at final time - " + tempStr);
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					if (aRequestObject.mRetryCount[0] > 0) {
						otsFrame[index - 1] |= 0x80;
						if (Logger.ENABLE)
							Logger.verbose(TAG, "--createOtsVoiceFrame():[INFO]--######RETRYING######  COUNT = " + aRequestObject.mRetryCount[0]);
					}
					break;
				case VOICE_FRAME_USERID_BITMASK:
					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getUserId(), 4);
					break;
				case VOICE_FRAME_TIME_BITMASK:
					tempStr = (String) mScriptTable.get("TIME");
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case VOICE_FRAME_GREETING_BITMASK:
					tempStr = (String) mScriptTable.get("GREETINGID");
					index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
					break;
				case VOICE_FRAME_CATEGORY_BITMASK:
					tempStr = (String) mScriptTable.get("CATEGORYID");
					index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
					break;
				case VOICE_FRAME_DESTINATION_BITMASK:
					tempStr = (String) mScriptTable.get("DESTPHONECOUNT");
					if(tempStr != null)
					{
//					System.out.println("DESTINATION - "+tempStr);
//					System.out.println("DESTINATION LENGTH - "+tempStr.length());
//					System.out.println("DESTINATION BYTE LENGTH - "+tempStr.getBytes(Constants.ENC).length);
					index = setDataToFrame(otsFrame, index, tempStr.getBytes(Constants.ENC).length, 2);
					System.arraycopy(tempStr.getBytes(Constants.ENC), 0, otsFrame, index, tempStr.getBytes(Constants.ENC).length);
					index += tempStr.getBytes(Constants.ENC).length;
					}
					else
					{
						System.out.println("DESTINATION is NULL");
					}
					break;
				case VOICE_FRAME_MSG_ID_BITMASK:
					tempStr = (String) mScriptTable.get("MESSAGEID");
					index = setDataToFrame(otsFrame, index, tempStr.length(), 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, tempStr.length());
					index += tempStr.length();
					//					tempStr = (String) mScriptTable.get("MESSAGEID");
					//					index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
					break;
				case VOICE_FRAME_TRANS_ID_BITMASK:
					tempStr = (String) mScriptTable.get("TRANSACTIONID");
					index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
					break;
				case VOICE_FRAME_DEV_ID_BITMASK:
					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getClientId(), 4);
					break;
				case VOICE_FRAME_BUDDY_BITMASK:
					tempStr = BusinessProxy.sSelf.mBuddyTimeStamp;
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case VOICE_FRAME_GROUP_BITMASK:
					tempStr = BusinessProxy.sSelf.mGroupTimeStamp;
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case VOICE_FRAME_TOP_MSD_ID:
					//					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getTopMessageIdOnClient(), 4);
					tempStr = (String) mScriptTable.get("TOPMSGID");
					index = setDataToFrame(otsFrame, index, tempStr.length(), 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, tempStr.length());
					index += tempStr.length();
					break;
				case VOICE_FRAME_MESSAGE_TIMESTAMP:
					tempStr = BusinessProxy.sSelf.mInboxTimeStamp;
					otsFrame[index++] = (byte) tempStr.length();
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case VOICE_FRAME_BUZZ_DRM_BITMASK:
					tempStr = (String) mScriptTable.get("EXTRACOMMAND");
					if (tempStr != null) {
						index = setDataToFrame(otsFrame, index, tempStr.length(), 2);
						System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, tempStr.length());
						index += tempStr.length();
					}
					break;
				case VOICE_FRAME_SPECIALMSG:
					tempStr = (String) mScriptTable.get("SPECIALMSG");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					break;
				case VOICE_FRAME_RT_ANIMATION:
					tempStr = (String) mScriptTable.get("RTANIMMSG");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					//For RT Animation Command
					tempStr = (String) mScriptTable.get("EXTRACOMMAND");
					if (tempStr != null) {
						index = setDataToFrame(otsFrame, index, tempStr.length(), 2);
						System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, tempStr.length());
						index += tempStr.length();
					}
					break;
				case VOICE_PAYLOAD_BITMASK:
					// Payload count
					otsFrame[index++] = payloadCount;
					bitmap = aRequestObject.mPayloadData.mPayloadTypeBitmap;
					offset = 0;
					while (bitmap > 0) {
						switch (bitmap & (1 << offset)) {
						case Payload.PAYLOAD_BITMAP_VOICE:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_VOICE;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mVoice, aRequestObject.mPayloadData.mVoiceType);
							break;
						case Payload.PAYLOAD_BITMAP_TEXT:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_TEXT;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mText, aRequestObject.mPayloadData.mTextType);
							break;
						case Payload.PAYLOAD_BITMAP_PICTURE:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_PICTURE;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mPicture, aRequestObject.mPayloadData.mPictureType);
							break;
						case Payload.PAYLOAD_BITMAP_VIDEO:
							otsFrame[index++] = Payload.PAYLOAD_TYPE_VIDEO;
							index = fillAttachment(otsFrame, index, aRequestObject.mPayloadData.mVideo, aRequestObject.mPayloadData.mVideoType);
							break;
						}
						bitmap = bitmap & ~(1 << offset);
						offset++;
					}
					break;
				}// End switch()
				bitMap = bitMap & ~(1 << offSet);
				offSet++;
			}
		} catch (OutOfMemoryError outOfMemory) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--OUT OF MEMORY--- ", outOfMemory);
		} catch (Exception ex) {
			ex.printStackTrace() ;
			if (Logger.ENABLE)
				Logger.error(TAG, "--createOtsVoiceFrame--[ERROR] --Failed to build ots VOICE frame " + ex, ex);
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsVoiceFrame--Exit::");
//		System.out.println("----otsFrame----frameengine--:"+new String (otsFrame));
		return otsFrame;
	}

	// ------------------------------------------------------------------------------
	
	private void constructBookMarkBitmap(OutboxTblObject aRequestObject) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructInboxBitmap():Entry::");
		mBitmap = 0;
		mOtsFrameLen = 0;
		String tempStr = "";
		String valueStr = "";
		Enumeration<String> scriptEnum = mScriptTable.keys();
		Enumeration<String> scriptValueEnum = mScriptTable.elements();
		while (scriptEnum.hasMoreElements() && scriptValueEnum.hasMoreElements()) {
			tempStr = (String) scriptEnum.nextElement();
			valueStr = (String) scriptValueEnum.nextElement();
			if (tempStr.equals("MSGTYPE")) {
				mOtsFrameLen++; // Message Type needs 1 byte
				mBitmap |= RTBM_GENERAL_MESSAGETYPE;
			} else if (tempStr.equals("SRCPHONENUM")) {
				mOtsFrameLen++; // source phone number's length is stored in 1 byte
				mOtsFrameLen += valueStr.length(); // source phone number's length
				mBitmap |= RTBM_GENERAL_SOURCE;
			} else if (tempStr.equals("OPERATION")) {
				mOtsFrameLen++; // operation code need 1 byte
				mBitmap |= RTBM_GENERAL_MESSAGEOPERATION;
			} else if (tempStr.equals("RETURNCOUNT")) {
				mOtsFrameLen++; // return count needs only one byte
				mBitmap |= RTBM_BOOKMARK_RETURN_COUNT;
			}  else if (tempStr.equals("USERID")) {
				mOtsFrameLen += 4; // User id is an integer value and needs 4
				// bytes to store
				mBitmap |= RTBM_BOOKMARK_USERID;
			} else if (tempStr.equals("CLIENTID")) {
				mOtsFrameLen += 4; // Client id is an integer value so need 4
				// bytes
				mBitmap |= RTBM_BOOKMARK_CLIENTID;
			} 			
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructInboxBitmap():Exit::");
	}

	
	private void constructInboxBitmap(OutboxTblObject aRequestObject) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructInboxBitmap():Entry::");
		mBitmap = 0;
		mOtsFrameLen = 0;
		String tempStr = "";
		String valueStr = "";
		Enumeration<String> scriptEnum = mScriptTable.keys();
		Enumeration<String> scriptValueEnum = mScriptTable.elements();
		while (scriptEnum.hasMoreElements() && scriptValueEnum.hasMoreElements()) {
			tempStr = (String) scriptEnum.nextElement();
			valueStr = (String) scriptValueEnum.nextElement();
			if (tempStr.equals("MSGTYPE")) {
				mOtsFrameLen++; // Message Type needs 1 byte
				mBitmap |= MSG_TYPE_BITMASK;
			} else if (tempStr.equals("SRCPHONENUM")) {
				mOtsFrameLen++; // source phone number's length is stored in 1 byte
				mOtsFrameLen += valueStr.length(); // source phone number's length
				mBitmap |= VOICE_FRAME_SOURCE_BITMASK;
			} else if (tempStr.equals("OPERATION")) {
				mOtsFrameLen++; // operation code need 1 byte
				mBitmap |= MSG_OP_BITMASK;
			} else if (tempStr.equals("RETURNCOUNT")) {
				mOtsFrameLen++; // return count needs only one byte
				mBitmap |= INBOX_RETURN_COUNT;
			} else if (tempStr.equals("DIRECTION")) {
				mOtsFrameLen++; // direction needs one byte ether up or down
				mBitmap |= INBOX_SELECT_DIR;
			} else if (tempStr.equals("MESSAGEID")) {
				mOtsFrameLen += 2 + valueStr.length(); //Id is an integer value and need 4 bytes to store
				//mOtsFrameLen += 4; // Id is an integer value and need 4 bytes to store
				mBitmap |= INBOX_MSG_ID;
			} else if (tempStr.equals("RTML_MESSAGEID")) {
				mOtsFrameLen += 2 + valueStr.length(); //Id is an integer value and need 4 bytes to store
				mBitmap |= INBOX_RTML_MSG_ID;
			} else if (tempStr.equals("READMESSAGEID")) {// READMESSAGEID
				mOtsFrameLen += 2 + valueStr.length();
				//				tempStr = (String) mScriptTable.get("READMESSAGEID");
				//				mOtsFrameLen += 1; // lenth in 1 byte
				//				mOtsFrameLen += tempStr.length();
				mBitmap |= INBOX_READ_MESSAGE;
			} else if (tempStr.equals("EXTRACOMMAND")) // READMESSAGEID EXTRACOMMAND
			{
				tempStr = (String) mScriptTable.get("EXTRACOMMAND");
				mOtsFrameLen += 2 + tempStr.length(); // Two bytes for the
				// lentgh
				mBitmap |= INBOX_GET_PROFILE;
			} else if (tempStr.equals("GETPROFILE")) // GETPROFILE
			{
				tempStr = (String) mScriptTable.get("GETPROFILE");
				mOtsFrameLen += 2 + tempStr.length(); // Two bytes for the
				// lentgh
				mBitmap |= INBOX_GET_PROFILE;
			} else if (tempStr.equals("TRANSACTIONID")) {
				mOtsFrameLen += 4; // transaction id is a integer value so it
				// need 4 bytes to be stored
				mBitmap |= INBOX_TRANS_ID;
			} else if (tempStr.equals("SEARCHTEXT")) {
				String str = (String) mScriptTable.get("SEARCHTEXT");
				if (aRequestObject.mOp[0] == Constants.FRAME_TYPE_PHCONTACTS_INVITE) {
					if (str.indexOf("^|") != -1) {
						str = str.replace("^|", ",");
						//	System.out.println("Search String - "+tempStr);
					}
					mOtsFrameLen += 4 + str.length(); // 4 bytes for the lentgh
				} else
					mOtsFrameLen += 2 + str.length(); // Two bytes for the
				// lentgh
				mBitmap |= INBOX_SEARCH_TEXT;
			} else if (tempStr.equals("ADDBUDDY")) {
				mOtsFrameLen += 1; // 1 byte to store the length of the buddy
				mOtsFrameLen += valueStr.length(); // the length of the the
				// buddy to be stored
				mBitmap |= INBOX_BUDDY_NAME;
			} else if (tempStr.equals("USERID")) {
				mOtsFrameLen += 4; // User id is an integer value and needs 4
				// bytes to store
				mBitmap |= INBOX_USER_ID;
			} else if (tempStr.equals("CLIENTID")) {
				mOtsFrameLen += 4; // Client id is an integer value so need 4
				// bytes
				mBitmap |= INBOX_DEV_ID;
			} else if (tempStr.equals("BUDDY_TS")) {
				mOtsFrameLen += (1 + BusinessProxy.sSelf.mBuddyTimeStamp.length()); // the length of the buddy time stamp
				mBitmap |= INBOX_BUDDY_TS;
			} else if (tempStr.equals("GROUP_TS")) {try{
				mOtsFrameLen += (1 + BusinessProxy.sSelf.mGroupTimeStamp.length()); // the length of the group time stamp
				mBitmap |= INBOX_GROUP_TS;
			}catch (Exception e) {
				// TODO: handle exception
			}
			} else if (tempStr.equals("INBOX_TS")) {
				mOtsFrameLen += 1; // 1 byte to store the group time stamp's length
				mOtsFrameLen += valueStr.length(); //the length of the group time stamp
				mBitmap |= INBOX_MESSAGE_TIMESTAMP;
			} else if (tempStr.equals("TOPMSGID")) {
				mOtsFrameLen += 2;
				mOtsFrameLen += valueStr.length();
				//mOtsFrameLen += 4;
				mBitmap |= INBOX_TOP_MSG_ID;
			} else if (tempStr.equals("FORGROUP")) {
				switch (aRequestObject.mOp[0]) {
				case Constants.FRAME_TYPE_GET_GROUP_DETAILS:
					// case Constants.FRAME_TYPE_GROUP_DEL:
					// case Constants.FRAME_TYPE_GROUP_DETAILS:
					// case Constants.FRAME_TYPE_GROUP_DEL_MEM:
					mOtsFrameLen += 4; // the delete group requires only the
					// group id and which is of integer
					// type
					break;
				}
				/*
				 * else if(aRequestObject.iOp[0] == Constants.FRAME_TYPE_GROUP_CREATE_NEW) { mOtsFrameLen += 5; //1 byte for length of group 4 byte for length of member list's length mOtsFrameLen +=
				 * valueStr.length(); // length of the group name } else if(aRequestObject.iOp[0] == Constants.FRAME_TYPE_GROUP_ADD_MEM) { mOtsFrameLen += 8; //4 bytes for group id and 4 byte for the
				 * length of the members mOtsFrameLen += valueStr.length(); } /
				 */
				mBitmap |= INBOX_GROUP_NAME; // 13th bit is on
			} else if (tempStr.equals("EXTRAPARAM")) {
				/*
				 * if(aRequestObject.iOp[0] == Constants.FRAME_TYPE_GROUP_DEL_MEM || aRequestObject.iOp[0] == Constants.FRAME_TYPE_GROUP_UPDATE_MEM_STATUS) mOtsFrameLen += 4; //member id else
				 * if(aRequestObject.iOp[0] == Constants.FRAME_TYPE_GROUP_CREATE_NEW || aRequestObject.iOp[0] == Constants.FRAME_TYPE_GROUP_ADD_MEM) mOtsFrameLen += valueStr.length(); //for length
				 * already has been incremented in FORGROUP Section /
				 */
			}
			/*
			 * else if(tempStr.equals("EXTRAPARAM1")) { if(aRequestObject.iOp[0] == Constants.FRAME_TYPE_GROUP_UPDATE_MEM_STATUS) mOtsFrameLen += 1; } /
			 */
			else if (tempStr.equals("MULTIPLEDELETE")) {
				mOtsFrameLen += 2 + valueStr.length();
				//				String[] ids = valueStr.split(";");
				//				mOtsFrameLen += 2;
				//				for (int i = 0; i < ids.length; i++) {
				//					mOtsFrameLen += ids[i].length() + 2;
				//				}
				//				mOtsFrameLen = mOtsFrameLen + (valueStr.split(";").length * 4 + 2);
				mBitmap |= INBOX_MULTIPLE_DELETE;
			}
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--constructInboxBitmap():Exit::");
	}

	// ------------------------------------------------------------------------------
	private byte[] createOtsBookmarkFrame(OutboxTblObject aRequest) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsInboxFrame():Entry::");
		byte[] otsFrame = null;
		String tempStr = null;
		int index = 0;
		int length = 0;
		try {
			mOtsFrameLen += 8; // 4 for frame length and 4 for bitmap
			// Logger.verbose(TAG, "--createOtsInboxFrame():[INFO]--Frame Length == " + mOtsFrameLen);
			otsFrame = new byte[mOtsFrameLen];
			// Frame length
			index = setDataToFrame(otsFrame, index, mOtsFrameLen, 4);
			// BitMap
			index = setDataToFrame(otsFrame, index, mBitmap, 4);
			// Run a loop for Bitmap
			int bitMap = mBitmap;
			int offSet = 0;
//			System.out.println("--------aRequest.mOp[0]-----"+aRequest.mOp[0]);
			while (bitMap != 0) {
				switch (bitMap & (1 << offSet)) {
				case MSG_TYPE_BITMASK:
					otsFrame[index++] = (byte) 6; // BOOKMARK
					break;
				case MSG_OP_BITMASK:
					tempStr = (String) mScriptTable.get("OPERATION");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					if (aRequest.mRetryCount[0] > 0) {
						otsFrame[index - 1] |= 0x80;
						if (Logger.ENABLE)
							Logger.verbose(TAG, "--createOtsInboxFrame():[INFO]--######RETRYING###### COUNT = " + aRequest.mRetryCount[0]);
					}
					break;
				case VOICE_FRAME_SOURCE_BITMASK:
					tempStr = (String) mScriptTable.get("SRCPHONENUM");
					// Length of source
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case INBOX_RETURN_COUNT:
					tempStr = (String) mScriptTable.get("RETURNCOUNT");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					break;
				case INBOX_USER_ID:
					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getUserId(), 4);
					break;
				case INBOX_BOOKMRK_CLIENT_ID:
					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getClientId(), 4);
					break;
				}// End switch()
				bitMap = bitMap & ~(1 << offSet);
				offSet++;
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--createOtsInboxFrame():[ERROR] --Failed to build ots Inbox frame" + ex, ex);
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsInboxFrame():Exit::");
		return otsFrame;
	}
	private byte[] createOtsInboxFrame(OutboxTblObject aRequest) {
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsInboxFrame():Entry::");
		byte[] otsFrame = null;
		String tempStr = null;
		int index = 0;
		int length = 0;
		try {
			mOtsFrameLen += 8; // 4 for frame length and 4 for bitmap
			// Logger.verbose(TAG, "--createOtsInboxFrame():[INFO]--Frame Length == " + mOtsFrameLen);
			otsFrame = new byte[mOtsFrameLen];
			// Frame length
			index = setDataToFrame(otsFrame, index, mOtsFrameLen, 4);
			// BitMap
			index = setDataToFrame(otsFrame, index, mBitmap, 4);
			// Run a loop for Bitmap
			int bitMap = mBitmap;
			int offSet = 0;
//			System.out.println("--------aRequest.mOp[0]-----"+aRequest.mOp[0]);
			while (bitMap != 0) {
				switch (bitMap & (1 << offSet)) {
				case MSG_TYPE_BITMASK:
					if (aRequest.mOp[0] == Constants.FRAME_TYPE_SEARCH_OTS_BUDDY || aRequest.mOp[0] == Constants.FRAME_TYPE_PHCONTACTS_INVITE)
						otsFrame[index++] = (byte) 5; // SEARCH
					else if(aRequest.mOp[0] == Constants.FRAME_TYPE_BOOKMARK)
						otsFrame[index++] = (byte) 6; // BOOKMARK
					else
						otsFrame[index++] = (byte) 4; // INBOX change op code
					break;
				case MSG_OP_BITMASK:
					tempStr = (String) mScriptTable.get("OPERATION");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					if (aRequest.mRetryCount[0] > 0) {
						otsFrame[index - 1] |= 0x80;
						if (Logger.ENABLE)
							Logger.verbose(TAG, "--createOtsInboxFrame():[INFO]--######RETRYING###### COUNT = " + aRequest.mRetryCount[0]);
					}
					break;
				case VOICE_FRAME_SOURCE_BITMASK:
					tempStr = (String) mScriptTable.get("SRCPHONENUM");
					// Length of source
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case INBOX_RETURN_COUNT:
					tempStr = (String) mScriptTable.get("RETURNCOUNT");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					break;
				case INBOX_SELECT_DIR:
					tempStr = (String) mScriptTable.get("DIRECTION");
					otsFrame[index++] = (byte) Integer.parseInt(tempStr);
					break;
				case INBOX_TRANS_ID:
					tempStr = (String) mScriptTable.get("TRANSACTIONID");
					index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
					break;
				case INBOX_MSG_ID:
					tempStr = (String) mScriptTable.get("MESSAGEID");
					length = tempStr.length();
					index = setDataToFrame(otsFrame, index, length, 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, length);
					index += length;
					//					tempStr = (String) mScriptTable.get("MESSAGEID");
					//					index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
					break;
				case INBOX_RTML_MSG_ID:
					tempStr = (String) mScriptTable.get("RTML_MESSAGEID");
					length = tempStr.length();
					index = setDataToFrame(otsFrame, index, length, 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, length);
					index += length;
					break;
				case INBOX_READ_MESSAGE:
					tempStr = (String) mScriptTable.get("READMESSAGEID");
					length = tempStr.length();
					index = setDataToFrame(otsFrame, index, length, 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, length);
					index += length;
					//					tempStr = (String) mScriptTable.get("READMESSAGEID");
					//					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					//					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					//					index += otsFrame[index - 1];
					break;
				case INBOX_GET_PROFILE:
					tempStr = (String) mScriptTable.get("GETPROFILE");
					if (tempStr == null || tempStr.equalsIgnoreCase("null"))
						tempStr = (String) mScriptTable.get("EXTRACOMMAND");
					length = tempStr.length();
					index = setDataToFrame(otsFrame, index, length, 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, length);
					index += length;
					break;
				case INBOX_SEARCH_TEXT:
					tempStr = (String) mScriptTable.get("SEARCHTEXT");
					if (tempStr.indexOf("^|") != -1) {
						tempStr = tempStr.replace("^|", ",");
						//System.out.println("Search String - " + tempStr);
					}
					length = tempStr.length();
					if (aRequest.mOp[0] == Constants.FRAME_TYPE_PHCONTACTS_INVITE)
						index = setDataToFrame(otsFrame, index, length, 4);
					else
						index = setDataToFrame(otsFrame, index, length, 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, length);
					index += length;
					break;
				case INBOX_BUDDY_NAME:
					tempStr = (String) mScriptTable.get("ADDBUDDY");
					otsFrame[index++] = (byte) (tempStr.length() & 0xff);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case INBOX_USER_ID:
					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getUserId(), 4);
					break;
				case INBOX_DEV_ID:
					index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getClientId(), 4);
					break;
				case INBOX_BUDDY_TS:
					otsFrame[index++] = (byte) BusinessProxy.sSelf.mBuddyTimeStamp.length();
					System.arraycopy(BusinessProxy.sSelf.mBuddyTimeStamp.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case INBOX_GROUP_TS:
					try{
					otsFrame[index++] = (byte) BusinessProxy.sSelf.mGroupTimeStamp.length();
					System.arraycopy(BusinessProxy.sSelf.mGroupTimeStamp.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					}catch (Exception e) {
						// TODO: handle exception
					}
					break;
				case INBOX_GROUP_NAME:
					switch (aRequest.mOp[0]) {
					case Constants.FRAME_TYPE_GET_GROUP_DETAILS:
						tempStr = (String) mScriptTable.get("FORGROUP");
						index = setDataToFrame(otsFrame, index, Integer.parseInt(tempStr), 4);
						break;
					}
					break;
				case INBOX_TOP_MSG_ID: // Added top msgId for Refresh
					//index = setDataToFrame(otsFrame, index, BusinessProxy.sSelf.getTopMessageIdOnClient(), 4);
					tempStr = (String) mScriptTable.get("TOPMSGID");
					length = tempStr.length();
					index = setDataToFrame(otsFrame, index, length, 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, length);
					index += length;
					break;
				case INBOX_MESSAGE_TIMESTAMP:
					otsFrame[index++] = (byte) BusinessProxy.sSelf.mInboxTimeStamp.length();
					System.arraycopy(BusinessProxy.sSelf.mInboxTimeStamp.getBytes(), 0, otsFrame, index, otsFrame[index - 1]);
					index += otsFrame[index - 1];
					break;
				case INBOX_MULTIPLE_DELETE:
					tempStr = (String) mScriptTable.get("MULTIPLEDELETE");
					length = tempStr.length();
					index = setDataToFrame(otsFrame, index, length, 2);
					System.arraycopy(tempStr.getBytes(), 0, otsFrame, index, length);
					index += length;
					//					tempStr = (String) mScriptTable.get("MULTIPLEDELETE");
					//					String[] msgIds = tempStr.split(";");
					//					index = setDataToFrame(otsFrame, index, msgIds.length, 2);
					//					for (int l = 0; l < msgIds.length; ++l) {
					//						index = setDataToFrame(otsFrame, index, msgIds[l].length(), 2);
					//						System.arraycopy(msgIds[l].getBytes(), 0, otsFrame, index, msgIds[l].length());
					//						index += msgIds[l].length();
					//					}
					//					for (int l = 0; l < msgIds.length; ++l)
					//						index = setDataToFrame(otsFrame, index, Integer.parseInt(msgIds[l]), 4);
					//					msgIds = null;
					// System.arraycopy(tempStr.getBytes(), 0 , otsFrame, index , tempStr.length());
					break;
				}// End switch()
				bitMap = bitMap & ~(1 << offSet);
				offSet++;
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--createOtsInboxFrame():[ERROR] --Failed to build ots Inbox frame" + ex, ex);
		}
		if (Logger.ENABLE)
			Logger.verbose(TAG, "--createOtsInboxFrame():Exit::");
		return otsFrame;
	}

	private void setAttachmentLength(byte[][] aAttachment) {
		mOtsFrameLen += (1 + 1 + 4) * aAttachment.length;
		// 1 for attachment name len,1 for attachment type, 4 for attachment length
		for (int i = 0; i < aAttachment.length; i++) {
			mOtsFrameLen += aAttachment[i].length; // attachment length
		}
	}

	private int fillAttachment(byte[] otsFrame, int index, byte[][] aAttachment, byte[] aAttachType) {
		otsFrame[index++] = (byte) aAttachment.length; // attachment count
		for (int j = 0; j < aAttachment.length; j++) {
			// attachment type
			otsFrame[index++] = aAttachType[j];
			index = setDataToFrame(otsFrame, index, aAttachment[j].length, 4);
//			String str = new String(aAttachment[j]);
//			System.out.println("----otsFrame---txt frameengine---:"+str);
//			System.out.println("----otsFrame---txt frameengine--aAttachment[j].length-:"+aAttachment[j].length);
			// Payload
			System.arraycopy(aAttachment[j], 0, otsFrame, index, aAttachment[j].length);
			index += aAttachment[j].length;
		}
		return index;
	}

	// ------------------------------------------------------------------------------

	private int setDataToFrame(byte[] aFrame, int aBegIdx, int aValue, int count) {
		for (int i = count - 1; i > -1; --i)
			aFrame[aBegIdx++] = (byte) (((aValue >> (8 * i)) & 0xff));
		return aBegIdx;
	}

	// ------------------------------------------------------------------------------
	// OTS REG FRAME BITMASK
	private static final int MSG_TYPE_BITMASK = 1 << 0;
	private static final int MSG_OP_BITMASK = 1 << 1;
	private static final int SOURCE_BITMASK = 1 << 2;
	private static final int USER_ID_BITMASK = 1 << 3; // user name or user id
	private static final int PASSWORD_BITMASK = 1 << 4;
	private static final int CLIENT_VER_BITMASK = 1 << 5;
	private static final int DEV_ID_BITMASK = 1 << 6;
	private static final int EMAIL_ID_BITMASK = 1 << 7;
	private static final int USER_NAME_BITMASK = 1 << 8; // first/second/last name
	private static final int HOME_ADDR_BITMASK = 1 << 9;
	private static final int AGE_BITMASK = 1 << 10;
	private static final int DEV_IMSI_BITMASK = 1 << 11;
	private static final int DEV_IMEI_BITMASK = 1 << 12;
	private static final int CARRIER_BITMASK = 1 << 13;
	private static final int TRANSACTION_ID_BITMASK = 1 << 14;
	private static final int NOTIFICATION_BITMASK = 1 << 15;
	private static final int PHONE_NUM_BITMASK = 1 << 16;
	private static final int REG_BUDDY_TS = 1 << 17;
	private static final int PLATFORM_BITMASK = 1 << 18;
	private static final int BIT_GMT_OFFSET = 1 << 19;
	private static final int VIEW_PROFILE_BITMASK = 1 << 20;
	private static final int INVISIBLE_MODE_BITMASK = 1 << 21;
	private static final int PREF_NOTIFY_BITMASK = 1 << 22;
	private static final int BIRTHDAY_BITMASK = 1 << 24;
	private static final int VENDOR_NAME_BITMASK = 1 << 25;
	private static final int LANGUAGE_BITMASK = 1 << 26;
	private static final int PASSWORD_UPDATE_BITMASK = 1 << 27;
	private static final int VOICE_PAYLOAD_BITMASK_FOR_SIGNUP = 1 << 29;// changed
	private static final int VOICE_PAYLOAD_BITMASK = 1 << 30;
	// OTS VTOV FRAME BITMASK
	private static final int VOICE_FRAME_SOURCE_BITMASK = 1 << 2;
	private static final int VOICE_FRAME_USERID_BITMASK = 1 << 3;
	private static final int VOICE_FRAME_TIME_BITMASK = 1 << 11;
	private static final int VOICE_FRAME_GREETING_BITMASK = 1 << 5;
	private static final int VOICE_FRAME_CATEGORY_BITMASK = 1 << 6;
	private static final int VOICE_FRAME_DESTINATION_BITMASK = 1 << 7;

	private static final int VOICE_FRAME_TRANS_ID_BITMASK = 1 << 9;
	private static final int VOICE_FRAME_DEV_ID_BITMASK = 1 << 10;
	private static final int VOICE_FRAME_BUDDY_BITMASK = 1 << 12;
	private static final int VOICE_FRAME_GROUP_BITMASK = 1 << 13;
	private static final int VOICE_FRAME_TOP_MSD_ID = 1 << 14;
	private static final int VOICE_FRAME_MESSAGE_TIMESTAMP = 1 << 15;
	// The is is being used for auto Play now
	private static final int VOICE_FRAME_DRM_BITMASK = 1 << 16;
	private static final int VOICE_FRAME_BUZZ_DRM_BITMASK = 1 << 18;
	//For RT Animation Testing
	private static final int VOICE_FRAME_RT_ANIMATION = 1 << 17;
	private static final int VOICE_FRAME_SPECIALMSG = 1 << 19;
	private static final int VOICE_FRAME_MSG_ID_BITMASK = 1 << 20;

	// The is is being used for auto Play now INBOX FRAME BITMAP
	private static final int INBOX_RETURN_COUNT = 1 << 0x3;
	private static final int INBOX_SELECT_DIR = 1 << 0x4;
	private static final int INBOX_TRANS_ID = 1 << 0x5;

	private static final int INBOX_READ_MESSAGE = 1 << 7; // FOR READ MESSAGES
	private static final int INBOX_USER_ID = 1 << 8;
	private static final int INBOX_DEV_ID = 1 << 9;
	private static final int INBOX_BOOKMRK_CLIENT_ID = 1 << 6;
	private static final int INBOX_BUDDY_TS = 1 << 10;
	private static final int INBOX_BUDDY_NAME = 1 << 11;
	private static final int INBOX_GROUP_TS = 1 << 12;
	private static final int INBOX_GROUP_NAME = 1 << 13;
	private static final int INBOX_TOP_MSG_ID = 1 << 14; // for top msgID in refresh
	private static final int INBOX_MESSAGE_TIMESTAMP = 1 << 15; //Inbox TimeStamp
	private static final int INBOX_SEARCH_TEXT = 1 << 16; // changed for new buddy search.
	private static final int INBOX_MULTIPLE_DELETE = 1 << 17;
	private static final int INBOX_GET_PROFILE = 1 << 18;
	private static final int INBOX_MSG_ID = 1 << 20;
	private static final int INBOX_RTML_MSG_ID = 1 << 21;
	// Values for Message type
	//private static final int MSG_TYPE_BIT_VALUE_FOR_CONNECTION_CHK = 6;
	
	//Inbox BookMark Request
	private static final int RTBM_GENERAL_MESSAGETYPE    =    1 << 0;
	private static final int RTBM_GENERAL_MESSAGEOPERATION =  1 << 1;
	private static final int  RTBM_GENERAL_SOURCE        =     1 << 2;
	private static final int RTBM_BOOKMARK_SELECTDIRECTION   =   1 << 4;
			private static final int RTBM_BOOKMARK_USERID       =        1 << 5;
					private static final int RTBM_BOOKMARK_CLIENTID     =        1 << 6;
					private static final int RTBM_BOOKMARK_MULTIPLEDELETE	=   1 << 9;
					private static final int RTBM_BOOKMARK_RETURN_COUNT		=   1 << 11;
					private static final int RTBM_BOOKMARK_BKID				=   1 << 20;
}
