package com.kainat.app.android.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.model.Buddy;
import com.kainat.app.android.util.format.Payload;

public final class InboxTblObject {

	private static final String TAG = InboxTblObject.class.getSimpleName();

	public static final byte INBOX = 1;
	public static final byte BUDDY_LIST = 2;
	public static final byte GROUP_LIST = 3;
	public static final byte IM_LIST = 4;
	// ------------------------------------------------------------------------------
	public static final byte DIR_DOWN = 1;
	public static final byte DIR_UP = 2;
	// ------------------------------------------------------------------------------
	public static final byte INBOX_FIELD_MSG_ID = 1;
	public static final byte INBOX_FIELD_NAME = 2;
	public static final byte INBOX_FIELD_NUMBER = 3;
	public static final byte INBOX_FIELD_TIME = 4;
	public static final byte INBOX_FIELD_STATUS = 5;
	public static final byte INBOX_FIELD_EMAIL = 6;
	public static final byte INBOX_FIELD_VOICE = 7;
	public static final byte INBOX_FIELD_TEXT = 8;
	public static final byte INBOX_FIELD_VEDIO = 9;
	public static final byte INBOX_FIELD_PAYLOAD_BITMAP = 10;
	public static final byte INBOX_FIELD_MSG_DRM = 11;
	public static final byte INBOX_FIELD_MSG_NOTIFICATION = 12;
	// ------------------------------------------------------------------------------
	public static final byte DRM_DONOTFORWARD = 1 << 0;
	public static final byte DRM_DONOTREPLY = 1 << 1;
	public static final byte DRM_DONOTSAVECONTENTS = 1 << 2;
	public static final byte DRM_EXPIRES = 1 << 3;
	public static final byte DRM_AUTOPLAY = 1 << 4;
	public static final byte DRM_PRIVATE_MSG = 1 << 5;
	public static final byte DRM_SYSTEM_MSG = 1 << 6;
	public static final int DRM_DONOT_VIEW_PROFILE = 1 << 7;
	// ----------------------------------------------------------------
	public static final byte NOTI_MEDIA_MESSAGE = 1 << 0;
	// public static final byte NOTI_NOTIFYONVIEWING = 1 << 1;
	public static final int NOTI_ROCKET_BUZZ = 1 << 1;
	public static final int NOTI_ROCKET_BUZZ_REPLY = 1 << 2;
	// public static final byte NOTI_NOTIFYONDELETION = 1 << 2;
	public static final byte NOTI_NOTIFYONSAVINGOFCONTENT = 1 << 3;
	public static final byte NOTI_SAVEDMSG = 1 << 4;
	public static final int NOTI_AUTO_REPLY = 1 << 5;
	public static final int NOTI_BUZZ_AUTOPLAY = 1 << 11;
	// 1 << 12 is assigned to Icelib.
	public static final int NOTI_CHAT_MSG = 1 << 13;
	public static final int NOTI_NON_FRIEND_MSG = 1 << 14;//For Cover page disabling
	public static final int NOTI_FRIEND_INVITATION_MSG = 1 << 15;
	// ------------------------------------------------------------------------------
	public final static int INBOX_WINDOW_SIZE = 2 * Constants.INBOX_MAX_REC_SIZE;

	private int mItemCount;

	public ArrayList<?> mMessageId;
	public ArrayList<String> mName;
	public ArrayList<String> mPhoneNumber;
	public ArrayList<String> mReceivingTime;
	public ArrayList<?> mMessageStatus;
	public ArrayList<String> mEmailID;
	public ArrayList<Integer> mBitmap;
	public ArrayList<?> mDRMBitmap;
	public ArrayList<?> mNotificationBitmap;
	public ArrayList<Payload> mPayload;

	private byte mWhichOperation = INBOX;

	public InboxTblObject(byte value) {
		mMessageId = new ArrayList<Object>();
		mName = new ArrayList<String>();
		mPhoneNumber = new ArrayList<String>();
		mMessageStatus = new ArrayList<Integer>();
		switch (mWhichOperation = value) {
		case INBOX:
			mReceivingTime = new ArrayList<String>();
			mEmailID = new ArrayList<String>();
			mPayload = new ArrayList<Payload>();
			mBitmap = new ArrayList<Integer>();
			// DON'T USE BREAK OTHERWISE DRM AND NOTIFICATION WILL NOT BE
			// INITIALISED FOR INBOX
		case BUDDY_LIST:
			mDRMBitmap = new ArrayList<Integer>();
			mNotificationBitmap = new ArrayList<Integer>();
			break;
		case GROUP_LIST:
			break;
		case IM_LIST:
			mEmailID = new ArrayList<String>(); // THIS IS FOR CUSTOM MESSAGE OF
												// BUDDY
			break;
		}

	}

	@SuppressWarnings("unchecked")
	public void insertField(int aPos, Object aItemValue, byte aItemId) throws Exception {
		@SuppressWarnings("rawtypes")
		ArrayList item = getInboxField(aItemId);
		if (item == null || aPos > item.size())
			return;
		item.add(aPos, aItemValue);
	}

	@SuppressWarnings("unchecked")
	public void appendField(Object aItemValue, byte aItemId) throws Exception {
		@SuppressWarnings("rawtypes")
		ArrayList item = getInboxField(aItemId);
		if (item == null)
			return;
		item.add(aItemValue);
	}

	@SuppressWarnings("unchecked")
	public void updateField(int aPos, Object aItemValue, byte aItemId) throws Exception {
		@SuppressWarnings("rawtypes")
		ArrayList item = getInboxField(aItemId);
		if (item == null || aPos >= item.size())
			return;
		item.set(aPos, aItemValue);
	}

	public Object getField(int aPos, byte aItemId) throws Exception {
		ArrayList<?> item = getInboxField(aItemId);
		if (item == null || aPos >= item.size())
			return null;
		return item.get(aPos);
	}

	private ArrayList<?> getInboxField(byte aItemId) {
		switch (aItemId) {
		case INBOX_FIELD_MSG_ID:
			return mMessageId;
		case INBOX_FIELD_NAME:
			return mName;
		case INBOX_FIELD_NUMBER:
			return mPhoneNumber;
		case INBOX_FIELD_TIME:
			return mReceivingTime;
		case INBOX_FIELD_STATUS:
			return mMessageStatus;
		case INBOX_FIELD_EMAIL:
			return mEmailID;
		case INBOX_FIELD_PAYLOAD_BITMAP:
			return mBitmap;
		case INBOX_FIELD_MSG_DRM:
			return mDRMBitmap;
		case INBOX_FIELD_MSG_NOTIFICATION:
			return mNotificationBitmap;
		default:
			return null;
		}
	}

	/**
	 * Sets the value of itemCount
	 * 
	 * @param itemCount
	 *            The itemCount to set
	 */
	public void setItemCount(int itemCount) {
		this.mItemCount = itemCount;
	}

	/**
	 * Gets the value of itemCount
	 * 
	 * @return The itemCount
	 */
	public int getItemCount() {
		return mItemCount;
	}

	public static boolean isMessageIdExistInDB(String inboxTable, String intValue) {
		if(inboxTable.equals(DBEngine.INBOX_TABLE))
		{
			List<String> ids = BusinessProxy.sSelf.getAllIdsForInboxTable();
			for (int i = 0; i < ids.size(); i++) {
				
			}
			if (ids.contains(intValue)) {
				return true;
			}
		}
		else{
		List<Integer> ids = BusinessProxy.sSelf.getAllIdsForTable(inboxTable);
		for (int i = 0; i < ids.size(); i++) {
			
		}
		if (ids.contains(intValue)) {
			return true;
		}
		}
		return false;
	}

	/**
	 * @param buddyObject
	 * @throws Exception
	 */
	public void addSpecificFields(InboxTblObject aInboxData) throws Exception {
		int loop = 0;
		int totalCount = aInboxData.getItemCount();
		for (; loop < totalCount; loop++) {
			insertField(loop, aInboxData.getField(totalCount - loop - 1, InboxTblObject.INBOX_FIELD_MSG_ID), InboxTblObject.INBOX_FIELD_MSG_ID);
			insertField(loop, aInboxData.getField(totalCount - loop - 1, InboxTblObject.INBOX_FIELD_NAME), InboxTblObject.INBOX_FIELD_NAME);
			insertField(loop, aInboxData.getField(totalCount - loop - 1, InboxTblObject.INBOX_FIELD_NUMBER), InboxTblObject.INBOX_FIELD_NUMBER);
			insertField(loop, aInboxData.getField(totalCount - loop - 1, InboxTblObject.INBOX_FIELD_STATUS), InboxTblObject.INBOX_FIELD_STATUS);
			switch (mWhichOperation) {
			case BUDDY_LIST:
				insertField(loop, aInboxData.getField(totalCount - loop - 1, InboxTblObject.INBOX_FIELD_MSG_DRM), InboxTblObject.INBOX_FIELD_MSG_DRM);
				insertField(loop, aInboxData.getField(totalCount - loop - 1, InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION), InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION);
				break;
			case IM_LIST:
				insertField(loop, aInboxData.getField(totalCount - loop - 1, InboxTblObject.INBOX_FIELD_EMAIL), InboxTblObject.INBOX_FIELD_EMAIL);
				break;
			}
		}
		mItemCount = mMessageId.size();
		// FIXME - Do we need this for android?
		// iStartIndex = 0;
		// iCurrIndex = 0;
		// iEndIndex = mItemCount;

	}

	// @Write code here to update that message in the agent table
	public void addAgentMessage(ResponseObject response, int i, boolean b, int pos, int j) {
		//@ Basically this is now for the MediaStuffMgr.

	}

	public int addToBlankSpaceInInbox(String aTableName) {
		int retVal = Constants.ERROR_NONE;
		try {
			List<ContentValues> list = new ArrayList<ContentValues>();
			int countToAdd = Constants.INBOX_MAX_REC_SIZE - BusinessProxy.sSelf.getRecordCount(aTableName);
			for (int ctr = 0; ctr < countToAdd; ctr++) {
				ContentValues values = new ContentValues();
				writeMessage(ctr, values, false, true);
				list.add(values);
			}
			BusinessProxy.sSelf.insertValuesInTable(aTableName, list);
		} catch (Throwable ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "addToBlankSpaceInInbox--[ERROR] -  " + ex.toString(), ex);
			retVal = Constants.ERROR_DB;
		}
		return retVal;
	}

	public void addRecordsBuddy(ResponseObject response, String table, int startIndex, int count) {
		
		List<Buddy> list = new ArrayList<Buddy>();
		try{
			String realName = "" ;
			String name = "" ;
			
			if(table.equalsIgnoreCase(DBEngine.BUDDY_TABLE))
			{
				if(BusinessProxy.sSelf.onlineFriends == null)
					BusinessProxy.sSelf.onlineFriends = new Hashtable<String, String>();
				
				if(BusinessProxy.sSelf.onlineOfflineFriends == null)
					BusinessProxy.sSelf.onlineOfflineFriends = new Hashtable<String, String>();
				
				if(BusinessProxy.sSelf.buddy == null)
					BusinessProxy.sSelf.buddy = new Vector<String>();
				
				
				
//					BusinessProxy.sSelf.onlineFriends = new Hashtable<String, String>();
			}
//			for (int ctr = 0; ctr < count; ctr++) {
			for (int ctr = count-1; ctr >= 0 ; ctr--) {	
				
				realName = mPhoneNumber.get(ctr);
				name = mName.get(ctr);				
				Buddy buddy = new Buddy();
				buddy.id = (Integer) mMessageId.get(ctr);
				
				buddy.name = name;
				buddy.realName = realName;
				buddy.status = (String) mMessageStatus.get(ctr);
				buddy.gender = (String) mDRMBitmap.get(ctr);
				buddy.media = (String) mNotificationBitmap.get(ctr);
				if(buddy.status.equalsIgnoreCase("online")){
					/*if(BusinessProxy.sSelf.onlineFriends != null)
						BusinessProxy.sSelf.onlineFriends.clear() ;
					
//					if(BusinessProxy.sSelf.onlineFriends == null)
						BusinessProxy.sSelf.onlineFriends = new Hashtable<String, String>();*/
						
						String s = buddy.name;
						if (buddy.name.indexOf("<") != -1 && buddy.name.indexOf(">") != -1) 
						{
							s  = buddy.name.substring(buddy.name.indexOf("<") + 1, buddy.name.indexOf(">"));//, buddy.name.substring(0, buddy.name.indexOf("<"));						
					    }
						BusinessProxy.sSelf.buddy.add(s.toLowerCase()) ;
						if(!BusinessProxy.sSelf.onlineFriends.containsKey(s)){
							BusinessProxy.sSelf.onlineOfflineFriends.put(buddy.name, "1");								
						}
//						System.out.println("isFriendaOnline------1adding----------------------------->" + s);
						BusinessProxy.sSelf.onlineFriends.put(s.toLowerCase(), buddy.name);					
					}else if(buddy.status.equalsIgnoreCase("offline")){
						String s = buddy.name;
						if (buddy.name.indexOf("<") != -1 && buddy.name.indexOf(">") != -1) 
						{
							s  = buddy.name.substring(buddy.name.indexOf("<") + 1, buddy.name.indexOf(">"));//, buddy.name.substring(0, buddy.name.indexOf("<"));						
					    }
						if(BusinessProxy.sSelf.onlineFriends.containsKey(s)){
							BusinessProxy.sSelf.onlineOfflineFriends.put(buddy.name.toLowerCase(), "0");
							BusinessProxy.sSelf.onlineFriends.remove(s);//	
						}
						BusinessProxy.sSelf.buddy.add(s.toLowerCase()) ;
					}
				String s = buddy.name;
				if (buddy.name.indexOf("<") != -1 && buddy.name.indexOf(">") != -1) 
				{
					s  = buddy.name.substring(buddy.name.indexOf("<") + 1, buddy.name.indexOf(">"));//, buddy.name.substring(0, buddy.name.indexOf("<"));						
			    }
				
				
				list.add(buddy);
				
			}
			
		}catch (Exception e) {
			
		}
		BusinessProxy.sSelf.listBuddy = list ;
	}
	public synchronized void addRecords(ResponseObject response, String table, int startIndex, int count) {
		List<ContentValues> contents = new ArrayList<ContentValues>();
		try {
			List<String> inboxMessageIds = BusinessProxy.sSelf.getAllIdsForInboxTable();
			if (Logger.ENABLE) {
				Logger.info(TAG, "addRecords -- row count for table " + table + " = " + inboxMessageIds.size());
			}
			int countToDelete = 0;
			int maxRec;
			if (mWhichOperation == INBOX)
				maxRec = Constants.INBOX_MAX_REC_SIZE;
			else
				maxRec = Constants.MAX_CONTACT_LIST_SIZE;
			if ((inboxMessageIds.size() + count) >= maxRec) {
				countToDelete = inboxMessageIds.size() + count - maxRec;
				// int id;
				List<String> messagesToDelete = new ArrayList<String>();
				for (int ctr = 0; ctr < countToDelete; ctr++) {
					if (mMessageId.contains(inboxMessageIds.get(ctr))){
						
//						Payload payload = BusinessProxy.sSelf.getPayloadForInboxMessage(inboxMessageIds.get(ctr));
//						System.out.println("--------this id  already have in bd: "+inboxMessageIds.get(ctr));
//						if(payload ==null)
//							System.out.println("--------payload is not available for this id: "+inboxMessageIds.get(ctr));
//						else
//							System.out.println("--------payload is available for this id: "+inboxMessageIds.get(ctr));
//						continue;
					}
					messagesToDelete.add(inboxMessageIds.get(ctr));
				}
				BusinessProxy.sSelf.deleteForIDs(messagesToDelete, DBEngine.INBOX_TABLE);
			}
			if (inboxMessageIds.size() > Constants.INBOX_MAX_PAYLOAD_STORE) {
				int size = inboxMessageIds.size();
				List<String> idsForPayloadDelete = new ArrayList<String>();
				for (int i = size - Constants.INBOX_MAX_PAYLOAD_STORE; i > countToDelete; i--) {
					idsForPayloadDelete.add(inboxMessageIds.get(i));
				}
				BusinessProxy.sSelf.deletePayloadForIDs(idsForPayloadDelete);
			}
			for (int ctr = startIndex; ctr < (startIndex + count); ctr++) {
				ContentValues values = new ContentValues();
				if (mMessageId.get(ctr) instanceof Integer) {
					values.put(DBEngine._ID, (Integer) mMessageId.get(ctr));
				} else if (mMessageId.get(ctr) instanceof String) {
//					if (inboxMessageIds != null && inboxMessageIds.size() > 0 && inboxMessageIds.contains(mMessageId.get(ctr)))
//						continue ;
//					System.out.println("----------------invalap --"+mMessageId.get(ctr));
					values.put(DBEngine._ID, (String) mMessageId.get(ctr));
				}
				writeMessage(ctr, values, false, true);
				contents.add(values);
			}
			BusinessProxy.sSelf.insertValuesInTable(table, contents);
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "addRecords-" + ex.getMessage(), ex);
		}
	}

	private void writeMessage(int ctr, ContentValues values, boolean isUpdate, boolean isPayload) throws IOException {
		String realName, name;
		switch (mWhichOperation) {
		case INBOX:
			values.put(DBEngine.INBOX_SENDER, mName.get(ctr));
			values.put(DBEngine.INBOX_RECEPIENTS, mPhoneNumber.get(ctr));
			values.put(DBEngine.INBOX_STATUS, (Integer) mMessageStatus.get(ctr));
			values.put(DBEngine.INBOX_BITMAP, mBitmap.get(ctr));
			values.put(DBEngine.INBOX_DRM, (Integer) mDRMBitmap.get(ctr));
			values.put(DBEngine.INBOX_NOTIFICATION, (Integer) mNotificationBitmap.get(ctr));
			values.put(DBEngine.INBOX_TIME, mReceivingTime.get(ctr));
			if (!this.mPayload.isEmpty()) {
				Payload payload;
				if (isUpdate)
					payload = (Payload) mPayload.get(0);
				else
					payload = (Payload) mPayload.get(ctr);
				byte[] attachment = writeAttachments(payload);
				if (0 < attachment.length && attachment.length < Constants.MAX_DB_DATA_STORE_SIZE)
					values.put(DBEngine.INBOX_ATTACHMENTS, attachment);
			}
			break;
		case BUDDY_LIST:
			realName = mPhoneNumber.get(ctr);
			name = mName.get(ctr);
			values.put(DBEngine.BUDDY_COUNTER, ctr);
			values.put(DBEngine.BUDDY_NAME, name);
			values.put(DBEngine.BUDDY_REAL_NAME, realName);
			values.put(DBEngine.BUDDY_STATUS, (String) mMessageStatus.get(ctr));
			values.put(DBEngine.BUDDY_GENDER, (String) mDRMBitmap.get(ctr));
			values.put(DBEngine.BUDDY_MEDIA, (String) mNotificationBitmap.get(ctr));
			if (realName.startsWith("a:")) {
				BusinessProxy.sSelf.mAgentTable.put(realName, name);
			}
			break;
		case GROUP_LIST:
			values.put(DBEngine.GROUP_NAME, mName.get(ctr));
			values.put(DBEngine.GROUP_ROLE, mPhoneNumber.get(ctr));
			values.put(DBEngine.GROUP_STATUS_AND_COUNT, (String) mMessageStatus.get(ctr));
			break;
		case IM_LIST:

			break;
		}

	}

	public static Payload readAttachment(byte[] data) {
		Payload payload = new Payload();
		if (null == data || 20 > data.length)
			return payload;
		ByteArrayInputStream bytein = new ByteArrayInputStream(data);
		DataInputStream datainput = new DataInputStream(bytein);
		try {
			// VOICE
			int count = datainput.readInt();
			if (0 < count) {
				payload.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VOICE;
				payload.mVoice = new byte[count][];
				payload.mVoiceType = new byte[count];
				for (int i = 0; i < count; i++) {
					payload.mVoiceType[i] = datainput.readByte();
					int len = datainput.readInt();
					payload.mVoice[i] = new byte[len];
					datainput.read(payload.mVoice[i], 0, payload.mVoice[i].length);
				}
			}
			// TEXT
			count = datainput.readInt();
			if (0 < count) {
				payload.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
				payload.mText = new byte[count][];
				payload.mTextType = new byte[count];
				
				for (int i = 0; i < count; i++) {
					payload.mTextType[i] = datainput.readByte();
					int len = datainput.readInt();
					payload.mText[i] = new byte[len];
					datainput.read(payload.mText[i], 0, payload.mText[i].length);
					
				}
				
			}

			// PICTURE
			count = datainput.readInt();
			if (0 < count) {
				payload.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_PICTURE;
				payload.mPicture = new byte[count][];
				payload.mPictureType = new byte[count];
				for (int i = 0; i < count; i++) {
					payload.mPictureType[i] = datainput.readByte();
					int len = datainput.readInt();
					payload.mPicture[i] = new byte[len];
					datainput.read(payload.mPicture[i], 0, payload.mPicture[i].length);
				}
			}

			// VIDEO
			count = datainput.readInt();
			if (0 < count) {
				payload.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VIDEO;
				payload.mVideo = new byte[count][];
				payload.mVideoType = new byte[count];
				for (int i = 0; i < count; i++) {
					payload.mVideoType[i] = datainput.readByte();
					int len = datainput.readInt();
					payload.mVideo[i] = new byte[len];
					datainput.read(payload.mVideo[i], 0, payload.mVideo[i].length);
				}
			}

			// RTML
			count = datainput.readInt();
			if (0 < count) {
				payload.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_RTML;
				payload.mRTML = new byte[count][];
				payload.mRTMLType = new byte[count];
				for (int i = 0; i < count; i++) {
					payload.mRTMLType[i] = datainput.readByte();
					int len = datainput.readInt();
					payload.mRTML[i] = new byte[len];
					datainput.read(payload.mRTML[i], 0, payload.mRTML[i].length);
				}
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "readAttachment--", ex);
		} finally {
			if (null != datainput) {
				try {
					datainput.close();
				} catch (IOException e) {
				}
			}
			if (null != bytein) {
				try {
					bytein.close();
				} catch (IOException e) {
				}
			}
		}
		return payload;
	}

	public static byte[] writeAttachments(Payload payload) throws IOException {
		ByteArrayOutputStreamUtil util = new ByteArrayOutputStreamUtil();
		DataOutputStream writer = new DataOutputStream(util);
		if (null != payload.mVoice) {
			writer.writeInt(payload.mVoice.length);
			for (int i = 0; i < payload.mVoice.length; i++) {
				writer.writeByte(payload.mVoiceType[i]);
				writer.writeInt(payload.mVoice[i].length);
				writer.write(payload.mVoice[i]);
			}
		} else {
			writer.writeInt(0);
		}
		if (null != payload.mText) {
			writer.writeInt(payload.mText.length);
			for (int i = 0; i < payload.mText.length; i++) {
				writer.writeByte(payload.mTextType[i]);
				writer.writeInt(payload.mText[i].length);
				writer.write(payload.mText[i]);
			}
		} else {
			writer.writeInt(0);
		}
		if (null != payload.mPicture) {
			writer.writeInt(payload.mPicture.length);
			for (int i = 0; i < payload.mPicture.length; i++) {
				writer.writeByte(payload.mPictureType[i]);
				writer.writeInt(payload.mPicture[i].length);
				writer.write(payload.mPicture[i]);
			}
		} else {
			writer.writeInt(0);
		}

		if (null != payload.mVideo) {
			writer.writeInt(payload.mVideo.length);
			for (int i = 0; i < payload.mVideo.length; i++) {
				writer.writeByte(payload.mVideoType[i]);
				writer.writeInt(payload.mVideo[i].length);
				Utilities.getConterType(payload.mVideo[i]) ;
				writer.write(payload.mVideo[i]);
			}
		} else {
			writer.writeInt(0);
		}
		if (null != payload.mRTML) {
			writer.writeInt(payload.mRTML.length);
			for (int i = 0; i < payload.mRTML.length; i++) {
				writer.writeByte(payload.mRTMLType[i]);
				writer.writeInt(payload.mRTML[i].length);
				writer.write(payload.mRTML[i]);
			}
		} else {
			writer.writeInt(0);
		}
		writer.close();
		return util.toByteArray();
	}

	/**
	 * @return
	 */
	public int getTopId() {
		return 0;
	}
}