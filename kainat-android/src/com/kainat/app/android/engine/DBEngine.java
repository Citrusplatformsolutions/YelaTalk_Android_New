package com.kainat.app.android.engine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.DBAdapter;
import com.kainat.app.android.model.Buddy;
import com.kainat.app.android.model.InboxMessage;
import com.kainat.app.android.model.LoginData;
import com.kainat.app.android.model.OutboxMessage;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.InboxTblObject;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.format.Payload;

public class DBEngine {
	private static final String TAG = DBEngine.class.getSimpleName();

	public static final String BUDDY_TABLE = "buddy_tbl";
	public static final String GROUP_TABLE = "group_tbl";
	public static final String MESSENGER_TABLE = "messenger_tbl";
	public static final String OUTBOX_TABLE = "outbox_tbl";
	public static final String SENTBOX_TABLE = "sentbox_tbl";
	public static final String INBOX_TABLE = "inbox_tbl";
	public static final String AGENT_TABLE = "agent_tbl";
	public static final String PUSH_TABLE = "push_tbl";
	public static final String LOGIN_TABLE = "login_tbl";
	public static final String RECEPIENTS_TABLE = "recepients_tbl";
	public static final String IMEI_TABLE ="imei_table";
	public static final String _ID = "_id";
	
	public static final String MSG_STATUS = "msgstatus";
	public static final String IMEI="imei";
	public static final String INBOX_SENDER = "sender";
	public static final String INBOX_RECEPIENTS = "recepients";
	public static final String INBOX_STATUS = "status";
	public static final String INBOX_BITMAP = "bitmap";
	public static final String INBOX_DRM = "drm";
	public static final String INBOX_NOTIFICATION = "notification";
	public static final String INBOX_TIME = "time";
	public static final String INBOX_ATTACHMENTS = "attachments";

	public static final String AGENT_SENDER = "sender";
	public static final String AGENT_RECEPIENTS = "recepients";
	public static final String AGENT_STATUS = "status";
	public static final String AGENT_BITMAP = "bitmap";
	public static final String AGENT_DRM = "drm";
	public static final String AGENT_NOTIFICATION = "notification";
	public static final String AGENT_TIME = "time";
	public static final String AGENT_ATTACHMENTS = "attachments";

	public static final String OUTBOX_OPERATION = "operation";
	public static final String OUTBOX_API = "api";
	public static final String OUTBOX_RETRY_COUNT = "retrycount";
	public static final String OUTBOX_HEADER = "header";
	public static final String OUTBOX_SCRIPT = "script";
	public static final String OUTBOX_TIME = "time";
	public static final String OUTBOX_PAYLOAD_BITMAP = "payloadbitmap";
	public static final String OUTBOX_PAYLOAD = "paydata";
	public static final String OUTBOX_PAYLOAD_SIZE = "paydatasize";
	
	public static final String BUDDY_COUNTER = "counter";
	public static final String BUDDY_NAME = "name";
	public static final String BUDDY_REAL_NAME = "realname";
	public static final String BUDDY_STATUS = "status";
	public static final String BUDDY_GENDER = "gender";
	public static final String BUDDY_MEDIA = "media";

	public static final String GROUP_NAME = "name";
	public static final String GROUP_STATUS_AND_COUNT = "statusandcount";
	public static final String GROUP_ROLE = "role";

	public static final String MESSENGER_NAME = "name";
	public static final String MESSENGER_REAL_NAME = "realname";
	public static final String MESSENGER_STATUS = "status";
	public static final String MESSENGER_CUSTOM_MESSAGE = "message";

	public static final String PUSH_REGID = "regid";

	public static final String LOGIN_USERNAME = "username";
	public static final String LOGIN_EMAIL_OR_MOBILE = "email_or_mobile";
	public static final String LOGIN_DISPLAYABLE_EMAIL_OR_MOBILE = "displayable_email_or_mobile";
	public static final String LOGIN_TIME = "time";

	private DBAdapter adapter;
	private static final String[] ONCREATE_DB_QUERIES = new String[] {
			"CREATE TABLE  if not exists " + BUDDY_TABLE + " (_id INTEGER PRIMARY KEY NOT NULL, counter INTEGER, name TEXT NOT NULL, realname TEXT, status TEXT, gender TEXT, media TEXT);",
			"CREATE TABLE  if not exists " + GROUP_TABLE + " (_id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, statusandcount TEXT, role TEXT);",
			"CREATE TABLE  if not exists " + INBOX_TABLE
					+ " (_id TEXT PRIMARY KEY NOT NULL, sender TEXT NOT NULL, recepients TEXT, status INTEGER, bitmap INTEGER, drm INTEGER, notification INTEGER, time TEXT, attachments BLOB);",
			"CREATE TABLE  if not exists " + AGENT_TABLE
					+ " (_id TEXT PRIMARY KEY NOT NULL, sender TEXT NOT NULL, recepients TEXT, status INTEGER, bitmap INTEGER, drm INTEGER, notification INTEGER, time TEXT, attachments BLOB);",
			"CREATE TABLE  if not exists " + MESSENGER_TABLE + " (_id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, realname TEXT, status TEXT, message TEXT);",

			"CREATE TABLE  if not exists " + OUTBOX_TABLE
					+ " (_id INTEGER PRIMARY KEY NOT NULL, operation INTEGER, api INTEGER, retrycount INTEGER, header TEXT, script TEXT, time INTEGER, payloadbitmap INTEGER, paydata BLOB, msgstatus INTEGER, paydatasize LONG);",
			"CREATE TABLE  if not exists " + PUSH_TABLE + " (_id INTEGER PRIMARY KEY NOT NULL,regid TEXT);",
			"CREATE TABLE  if not exists " + RECEPIENTS_TABLE + " (_id INTEGER PRIMARY KEY NOT NULL,recepients TEXT);",
			"CREATE TABLE  if not exists " + LOGIN_TABLE + " (_id INTEGER PRIMARY KEY NOT NULL, username TEXT, email_or_mobile TEXT, displayable_email_or_mobile TEXT, time INTEGER);",
			"CREATE TABLE  if not exists " + SENTBOX_TABLE
					+ " (_id INTEGER PRIMARY KEY NOT NULL, operation INTEGER, api INTEGER, retrycount INTEGER, header TEXT, script TEXT, time INTEGER, payloadbitmap INTEGER, paydata BLOB);", 
	"CREATE TABLE  if not exists " + IMEI_TABLE
	+ " (id INTEGER PRIMARY KEY NOT NULL,imei TEXT);", };
	public DBEngine(Context context) {
		adapter = new DBAdapter(context, Constants.DATABASE_NAME_1, 1, ONCREATE_DB_QUERIES);
	}

	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	public void close() {
		if (adapter != null) {
			adapter.close();
			adapter = null;
		}
	}

	public int getImeiId(String tableName){
		int iImeiId = 0;
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(tableName, new String[] { "imei" }, null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				iImeiId = cursor.getInt(0);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "iImeiId-", ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return iImeiId;
	}
	
	public int getRegId(String tableName) {
		int iRegId = 0;
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(tableName, new String[] { "regid" }, null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				iRegId = cursor.getInt(0);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getRegId-", ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return iRegId;
	}

	public String getPushImeiId(String tableName) {
		String iImeiId = null;
		/*Cursor cursor = null;
		try {
			cursor = this.adapter.query(tableName, new String[] { _ID, IMEI }, null, null, null, null, _ID, null);
			while (cursor.moveToNext()) {
				iImeiId = cursor.getString(1);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "imei-", ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}*/
		String sql = "SELECT * FROM " + tableName;
		Cursor cursor = null;
		//int count = 0;
		try {
			cursor = this.adapter.executeRawQuery(sql, null);
			if (cursor.moveToNext()) {
				//count = cursor.getInt(0);
				iImeiId=cursor.getString(1);
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return iImeiId;
		
	}
	
	public String getPushRegId(String tableName) {
		String iRegId = null;
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(tableName, new String[] { _ID, PUSH_REGID }, null, null, null, null, _ID, null);
			while (cursor.moveToNext()) {
				iRegId = cursor.getString(1);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getRegId-", ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return iRegId;
	}

	public void deleteTableContents(String table) {
		try {
			adapter.delte(table, null, null);
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "deleteTableContents-- " + ex.getMessage(), ex);
		}
	}

	public void insertValues(String tableName, List<ContentValues> values) {
		if (null == tableName || null == values || values.isEmpty())
			return;
		for (ContentValues value : values) {
			this.adapter.insertEntry(tableName, value);
//			System.out.println("tableName:---"+tableName);
//			System.out.println("insertValues:---"+value.toString());
		}
	}
	
	

	public int getRowCount(String tableName) {
		String sql = "SELECT COUNT(*) FROM " + tableName;
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = this.adapter.executeRawQuery(sql, null);
			if (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return count;
	}
	public int resetOutboxSatus() {
		List<Integer> idList = getAllIdsForTable(OUTBOX_TABLE);
		if (idList.isEmpty()) {
			return 0;
		}
		ContentValues updatedValues = new ContentValues();
		for (Integer id : idList) {
			updatedValues.put(DBEngine.MSG_STATUS, 0);
			BusinessProxy.sSelf.updateTableValues(OUTBOX_TABLE, id.toString(), updatedValues);
		}
		return idList.size();
	}
	public List<String> getAllIdsForInboxTable() {
		List<String> ids = new ArrayList<String>();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(INBOX_TABLE, new String[] { _ID }, null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				ids.add(cursor.getString(0));
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllIdsForInboxTable-", ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return ids;
	}

	public List<Integer> getAllIdsForTable(String tableName) {
		List<Integer> ids = new ArrayList<Integer>();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(tableName, new String[] { _ID }, null, null, null, null, _ID, null);
			while (cursor.moveToNext()) {
				ids.add(cursor.getInt(0));
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllIdsForTable-", ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return ids;
	}

	public void updateTableValues(String tableName, String id, ContentValues values) {
		try {
			this.adapter.updateEntry(tableName, values, _ID + " = ? ", new String[] { id });
		} catch (Exception ex) {
		}
	}

	public void deletePayloads(String tablename, List<String> ids) {
		if (null == ids || ids.isEmpty()) {
			return;
		}
		try {
			StringBuilder query = new StringBuilder("UPDATE " + tablename + " SET " + INBOX_ATTACHMENTS + " = NULL WHERE " + _ID + " IN (");
			for (String in : ids) {
				query.append(in);
				query.append(',');
			}
			query.setCharAt(query.length() - 1, ')');
			this.adapter.executeSQL(query.toString(), null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void deleteContentForId(String table, List<?> idList) {
		for (Object msgId : idList) {
			if (msgId instanceof String) {
//				if(Logger.ENABLE)
				//System.out.println("deleteContentForId :: "+this.adapter.delte(table, DBEngine._ID + " = ? ", new String[] { (String) msgId })+" id::"+msgId.toString());
				this.adapter.delte(table, DBEngine._ID + " = ? ", new String[] { (String) msgId });
				continue;
			}
//			if(Logger.ENABLE)
//			System.out.println("deleteContentForId :: "+this.adapter.delte(table, DBEngine._ID + " = ? ", new String[] { msgId.toString() })+" id::"+msgId.toString());
			this.adapter.delte(table, DBEngine._ID + " = ? ", new String[] { msgId.toString() });
//			this.adapter.delte(table, DBEngine._ID + " = ? ", new String[] { msgId.toString() });
		}
	}

	public long updateOutboxValues(String table, String id, ContentValues updateValues) {
		try {
			return this.adapter.updateEntry(table, updateValues, _ID + " = ? ", new String[] { id });
		} catch (Exception ex) {
			if (Logger.ENABLE) {
				Logger.error(TAG, "updateOutboxValues -- ERROR - " + ex.getMessage(), ex);
			}
		}
		return 0;
	}

	public ContentValues getOutboxTblObjectDetails(String tableName, int id, byte mRequestNo) {
		ContentValues value = new ContentValues();
		Cursor cursor = null;
		try {
			if(tableName.equals(OUTBOX_TABLE)) {
			cursor = this.adapter.query(tableName, new String[] {OUTBOX_OPERATION, OUTBOX_API, OUTBOX_RETRY_COUNT, OUTBOX_HEADER, OUTBOX_SCRIPT, OUTBOX_TIME, OUTBOX_PAYLOAD_BITMAP, OUTBOX_PAYLOAD, MSG_STATUS, OUTBOX_PAYLOAD_SIZE },
					DBEngine._ID + " = ? ", new String[] { "" + id }, null, null, null, "1");
			if (!cursor.moveToNext()) {
				return value;
			}
			if(mRequestNo != -1)
			{
				int stat = cursor.getInt(8);
//				System.out.println("stat mRequestNo id : "+stat+", "+mRequestNo+", "+id);
				//if(id == 0)
				{
					
					if (Logger.NET_ERROR){
						Logger.NET_ERROR = false ;
						stat = 0 ;
					}
					if (Logger.MULTICHANEL){
						Logger.debug(TAG,"stat"+stat);
					}
				while(stat==1) {
//					System.out.println("stat : "+stat);
					if (!cursor.moveToNext()) {
						return null;
					}
					stat = cursor.getInt(8);
				}
				if (Logger.MULTICHANEL){
					Logger.debug(TAG,"----- message size :  "+cursor.getLong(9));
					Logger.debug(TAG,"----- message id :  "+id);
				}
				if(mRequestNo == 1 && cursor.getLong(9)>50*1024){
					if (Logger.ENABLE)
						Logger.debug(TAG, "PSS.--:run()::[INFO] -- ERROR_DB1 calling " );
					return null;
					}
				}
//				System.out.println("stat mRequestNo id2 : "+stat+", "+mRequestNo+", "+cursor.getLong(9));
				value.put(MSG_STATUS, 1);
			}else
				value.put(MSG_STATUS, 0);
			value.put(_ID, id);
			value.put(OUTBOX_OPERATION, cursor.getInt(0));
			value.put(OUTBOX_API, cursor.getInt(1));
			value.put(OUTBOX_RETRY_COUNT, cursor.getInt(2));
			value.put(OUTBOX_HEADER, cursor.getString(3));
			value.put(OUTBOX_SCRIPT, cursor.getString(4));
			value.put(OUTBOX_TIME, cursor.getLong(5));
			value.put(OUTBOX_PAYLOAD_BITMAP, cursor.getInt(6));
			value.put(OUTBOX_PAYLOAD, cursor.getBlob(7));
//			value.put(MSG_STATUS, cursor.getInt(8));
			value.put(OUTBOX_PAYLOAD_SIZE, cursor.getLong(9));
			}else {
				
					cursor = this.adapter.query(tableName, new String[] {OUTBOX_OPERATION, OUTBOX_API, OUTBOX_RETRY_COUNT, OUTBOX_HEADER, OUTBOX_SCRIPT, OUTBOX_TIME, OUTBOX_PAYLOAD_BITMAP, OUTBOX_PAYLOAD},
							DBEngine._ID + " = ? ", new String[] { "" + id }, null, null, null, "1");
					if (!cursor.moveToNext()) {
						return value;
					}
					value.put(_ID, id);
					value.put(OUTBOX_OPERATION, cursor.getInt(0));
					value.put(OUTBOX_API, cursor.getInt(1));
					value.put(OUTBOX_RETRY_COUNT, cursor.getInt(2));
					value.put(OUTBOX_HEADER, cursor.getString(3));
					value.put(OUTBOX_SCRIPT, cursor.getString(4));
					value.put(OUTBOX_TIME, cursor.getLong(5));
					value.put(OUTBOX_PAYLOAD_BITMAP, cursor.getInt(6));
					value.put(OUTBOX_PAYLOAD, cursor.getBlob(7));
//					value.put(OUTBOX_PAYLOAD_SIZE, cursor.getLong(8));
				
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getOutboxObjectDetails-- ERROR - " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return value;
	}

	public void insertOrUpdateLoginData(String userName, ContentValues values) {
		LoginData loginData = getLoginDataForUser(userName);
		if (null != loginData) {
			updateTableValues(LOGIN_TABLE, Integer.toString(loginData.id), values);
		} else {
			List<Integer> loginDataIds = getAllIdsForTable(DBEngine.LOGIN_TABLE);
			int countToDelete = 0;
			int maxRec = Constants.LOGIN_HISTORY_MAX_REC_SIZE;
			if ((loginDataIds.size() + 1) >= maxRec) {
				countToDelete = loginDataIds.size() + 1 - maxRec;
				List<Integer> dataToDelete = new ArrayList<Integer>();
				for (int ctr = 0; ctr < countToDelete; ctr++) {
					dataToDelete.add(loginDataIds.get(ctr));
				}
				deleteContentForId(DBEngine.LOGIN_TABLE, dataToDelete);
			}
			List<ContentValues> valueItems = new ArrayList<ContentValues>();
			valueItems.add(values);
			insertValues(LOGIN_TABLE, valueItems);
			valueItems = null;
		}
	}

	/**
	 * This method will insert a new entry if no entry found for the given agent otherwise it will update the existing entry.
	 * 
	 * @param agent
	 *            Name of the agent like MediaManager<a:MediaManager>, a:MediaManager
	 * @param values
	 *            Values to be put for that agent.
	 * @return false if updated otherwise true if inserted
	 */
	public boolean insertOrUpdateMessageForAgent(String agent, ContentValues values) {
		InboxMessage message = getMessageForAgent(agent);
		if (null != message) {
			updateTableValues(AGENT_TABLE, message.mId, values);
			return false;
		} else {
			List<ContentValues> valueItems = new ArrayList<ContentValues>();
			valueItems.add(values);
			insertValues(AGENT_TABLE, valueItems);
			valueItems = null;
			return true;
		}
	}

	public LoginData getLoginDataForUser(String userName) {
		LoginData loginData = null;
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(LOGIN_TABLE, new String[] { _ID, LOGIN_USERNAME, LOGIN_EMAIL_OR_MOBILE, LOGIN_DISPLAYABLE_EMAIL_OR_MOBILE, LOGIN_TIME }, "LOWER(" + LOGIN_USERNAME + ") like '"
					+ userName.toLowerCase() + "'", null, null, null, null, null);
			if (cursor.moveToNext()) {
				loginData = new LoginData();
				loginData.id = cursor.getInt(0);
				loginData.username = cursor.getString(1);
				loginData.emailOrMobile = cursor.getString(2);
				loginData.displableEmailOrMobile = cursor.getString(3);
				loginData.time = cursor.getLong(4);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getLoginDataForUser-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return loginData;
	}

	public InboxMessage getMessageForAgent(String sender) {
		InboxMessage message = null;
		Cursor cursor = null;
		try {
			if (-1 < sender.indexOf('<')) {
				int endIndex = sender.indexOf('>');
				if (0 > endIndex) {
					endIndex = sender.length();
				}
				sender = sender.substring(sender.indexOf('<'), endIndex);
			}
			cursor = this.adapter.query(AGENT_TABLE, new String[] { _ID, AGENT_SENDER, AGENT_RECEPIENTS, AGENT_TIME, AGENT_STATUS, AGENT_BITMAP, AGENT_DRM, AGENT_NOTIFICATION, AGENT_ATTACHMENTS },
					"LOWER(" + AGENT_SENDER + ") like '" + sender.toLowerCase() + "%'", null, null, null, null, null);
			if (cursor.moveToNext()) {
				InboxMessage msg = new InboxMessage();
				msg.mId = cursor.getString(0);
				msg.mSenderName = cursor.getString(1);
				msg.mRecepients = cursor.getString(2);
				msg.mTime = cursor.getString(3);
				msg.mStatus = cursor.getInt(4);
				msg.mBitmap = cursor.getInt(5);
				msg.mDRM = cursor.getInt(6);
				msg.mNotification = cursor.getInt(7);
				msg.mPayload = InboxTblObject.readAttachment(cursor.getBlob(8));
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllInboxMessages-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return message;
	}

	public Payload getPayloadForInboxMessage(String id) {
		Payload retValuePayload = new Payload();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(INBOX_TABLE, new String[] { _ID, INBOX_ATTACHMENTS }, _ID + " = ?", new String[] { id }, null, null, null, null);
			if (cursor.moveToNext()) {
				retValuePayload = InboxTblObject.readAttachment(cursor.getBlob(1));
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getPayloadForInboxMessage-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return retValuePayload;
	}
	
	public long migrateInboxOnlogin(String id, ContentValues updateValues) {
		try {
			return this.adapter.updateEntry(INBOX_TABLE, updateValues, _ID + " = ? ", new String[] { id });
		} catch (Exception ex) {
			if (Logger.ENABLE) {
				Logger.error(TAG, "updateOutboxValues -- ERROR - " + ex.getMessage(), ex);
			}
		}
		return 0;
	}
	public List<InboxMessage> getAllInboxIdPayload() {
		List<InboxMessage> messages = new ArrayList<InboxMessage>();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(INBOX_TABLE, new String[] { _ID, INBOX_ATTACHMENTS ,INBOX_DRM, INBOX_NOTIFICATION},
					null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				if(cursor.getBlob(1) == null)continue;
				InboxMessage msg = new InboxMessage();
				msg.mId = cursor.getString(0);			
			
				Payload payload = new Payload();
//				payload.mVoice = new byte[0][];
				
//				payload.mVoice[0] = new byte[cursor.getBlob(1)];
				payload.mVideoType = cursor.getBlob(1);
				msg.mPayload = payload ;
				msg.mDRM = cursor.getInt(2);
				msg.mNotification = cursor.getInt(3);
				messages.add(msg);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllInboxMessages-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return messages;
	}
	public List<InboxMessage> getAllInboxMessages() {
		List<InboxMessage> messages = new ArrayList<InboxMessage>();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(INBOX_TABLE, new String[] { _ID, INBOX_SENDER, INBOX_RECEPIENTS, INBOX_TIME, INBOX_STATUS, INBOX_BITMAP, INBOX_DRM, INBOX_NOTIFICATION, INBOX_ATTACHMENTS },
					null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				InboxMessage msg = new InboxMessage();
				msg.mId = cursor.getString(0);
				msg.mSenderName = cursor.getString(1);
				msg.mRecepients = cursor.getString(2);
				msg.mTime = cursor.getString(3);
				msg.mStatus = cursor.getInt(4);
				msg.mBitmap = cursor.getInt(5);
				msg.mDRM = cursor.getInt(6);
				msg.mNotification = cursor.getInt(7);
//				System.out.println("mNotification : "+msg.mNotification);
//				System.out.println("mSenderName : "+msg.mSenderName);
//				System.out.println("msg.mDRM : "+msg.mDRM);
				msg.mPayload = InboxTblObject.readAttachment(cursor.getBlob(8));
				if(msg.mPayload!=null && msg.mPayload.mText!=null)
				if(new String(msg.mPayload.mText[0], Constants.ENC).toString().length()>60)
					msg.displayText=(new String(msg.mPayload.mText[0], Constants.ENC).toString().substring(0, 60))+"..";
				else
				msg.displayText=new String(msg.mPayload.mText[0], Constants.ENC);
				msg.mPayload.mVideo = null;
				msg.mPayload.mVoice = null;
				msg.mPayload.mRTML = null;
				messages.add(msg);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllInboxMessages-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return messages;
	}
	public List<String> getRecepients(String tableName) {
		List<String> ids = new ArrayList<String>();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(tableName, new String[] { INBOX_RECEPIENTS }, null, null, null, null, _ID, null);
			while (cursor.moveToNext()) {
				ids.add(cursor.getString(0));
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllIdsForTable-", ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return ids;
	}
	public List<OutboxMessage> getAllOutBoxMessages(String tableName) {
		List<OutboxMessage> messages = new ArrayList<OutboxMessage>();
		Cursor cursor = null;
		String order = " DESC";
		byte messageType = OutboxMessage.MESSAGE_OUTBOX;
		if (DBEngine.SENTBOX_TABLE.equals(tableName)) {
			order = " ASC";
			messageType = OutboxMessage.MESSAGE_SENTBOX;
		}
		try {
			if (DBEngine.SENTBOX_TABLE.equals(tableName))
			cursor = this.adapter.query(tableName, new String[] { _ID, OUTBOX_OPERATION, OUTBOX_API, OUTBOX_RETRY_COUNT, OUTBOX_HEADER, OUTBOX_SCRIPT, OUTBOX_TIME, OUTBOX_PAYLOAD_BITMAP,
					OUTBOX_PAYLOAD }, null, null, null, null, OUTBOX_TIME + order, null);
			else
			cursor = this.adapter.query(tableName, new String[] { _ID, OUTBOX_OPERATION, OUTBOX_API, OUTBOX_RETRY_COUNT, OUTBOX_HEADER, OUTBOX_SCRIPT, OUTBOX_TIME, OUTBOX_PAYLOAD_BITMAP,
					OUTBOX_PAYLOAD,MSG_STATUS }, null, null, null, null, OUTBOX_TIME + order, null);
			
			Calendar calendar = Calendar.getInstance();
			StringBuilder time = new StringBuilder();
			int startIndex, endIndex;
			while (cursor.moveToNext()) {
				if (Constants.FRAME_TYPE_VTOV != cursor.getInt(1)) {
					continue;
				}
				OutboxMessage msg = new OutboxMessage();
				msg.mMessageType = messageType;
				msg.mTransactionId = cursor.getInt(0);
				msg.mOperation = cursor.getInt(1);
				msg.mRecepients = cursor.getString(4);
				startIndex = msg.mRecepients.indexOf("DESTPHONECOUNT");
				if (startIndex > -1) {
					startIndex += "DESTPHONECOUNT".length() + 1;
					endIndex = msg.mRecepients.indexOf(Constants.FIELD_SEPARATOR, startIndex);
					msg.mRecepients = msg.mRecepients.substring(startIndex, endIndex);
				}
				calendar.setTimeInMillis(cursor.getLong(6));
				if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
					time.append('0');
				time.append(calendar.get(Calendar.DAY_OF_MONTH));
				time.append('/');
				if ((calendar.get(Calendar.MONTH) + 1) < 10)
					time.append('0');
				time.append(calendar.get(Calendar.MONTH) + 1);
				time.append('/');
				time.append(calendar.get(Calendar.YEAR));
				time.append('\n');
				if (calendar.get(Calendar.HOUR) >= 0 && calendar.get(Calendar.HOUR) < 10)
					time.append('0');
				if (calendar.get(Calendar.AM_PM) == 1) {
					time.append(12 + calendar.get(Calendar.HOUR));
				} else
					time.append(calendar.get(Calendar.HOUR));
				time.append(':');
				if (calendar.get(Calendar.MINUTE) < 10)
					time.append('0');
				time.append(calendar.get(Calendar.MINUTE));
				time.append(':');
				if (calendar.get(Calendar.SECOND) < 10)
					time.append('0');
				time.append(calendar.get(Calendar.SECOND));
				if (calendar.get(Calendar.AM_PM) == 1)
					time.append("PM");
				else
					time.append("AM");

				msg.mTime = time.toString();
				msg.mBitmap = cursor.getInt(7);
				msg.mPayload = new Payload();
				OutboxTblObject.fillPayloadData(msg.mPayload, cursor.getBlob(8), false);
				
				if (DBEngine.OUTBOX_TABLE.equals(tableName))
					msg.mStatus = cursor.getInt(9);
				messages.add(msg);
				time.delete(0, time.length());
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllInboxMessages-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return messages;
	}

	public List<LoginData> getAllLoginData(String tableName) {
		List<LoginData> messages = new ArrayList<LoginData>();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(tableName, new String[] { _ID, LOGIN_USERNAME, LOGIN_EMAIL_OR_MOBILE, LOGIN_DISPLAYABLE_EMAIL_OR_MOBILE, LOGIN_TIME }, null, null, null, null, LOGIN_TIME
					+ " DESC", null);
			while (cursor.moveToNext()) {
				LoginData loginData = new LoginData();
				loginData.id = cursor.getInt(0);
				loginData.username = cursor.getString(1);
				loginData.emailOrMobile = cursor.getString(2);
				loginData.displableEmailOrMobile = cursor.getString(3);
				loginData.time = cursor.getLong(4);
				messages.add(loginData);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllLoginData-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return messages;
	}

	/**
	 * @return
	 */
	public List<Buddy> getAllBuddyInformation() {
		List<Buddy> list = new ArrayList<Buddy>();
		Cursor cursor = null;
		try {
			cursor = this.adapter.query(BUDDY_TABLE, new String[] { _ID, BUDDY_NAME, BUDDY_REAL_NAME, BUDDY_STATUS, BUDDY_GENDER, BUDDY_MEDIA }, null, null, null, null, DBEngine.BUDDY_COUNTER
					+ " DESC", null);
			
//			cursor = this.adapter.query(BUDDY_TABLE, new String[] { _ID, BUDDY_NAME, BUDDY_REAL_NAME, BUDDY_STATUS, BUDDY_GENDER, BUDDY_MEDIA }, null, null, null, null, DBEngine.BUDDY_COUNTER
//					, null);
			
			while (cursor.moveToNext()) {
				Buddy buddy = new Buddy();
				buddy.id = cursor.getInt(0);
				buddy.name = cursor.getString(1);
				buddy.realName = cursor.getString(2);
				buddy.status = cursor.getString(3);
				buddy.gender = cursor.getString(4);
				buddy.media = cursor.getString(5);
				list.add(buddy);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getAllBuddyInformation-- ERROR  " + ex.getMessage(), ex);
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}
}