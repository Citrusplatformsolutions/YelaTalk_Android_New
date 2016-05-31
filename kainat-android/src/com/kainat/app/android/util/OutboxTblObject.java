package com.kainat.app.android.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.util.Log;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.util.format.Payload;

public class OutboxTblObject {
	private static final String TAG = OutboxTblObject.class.getSimpleName();
	private boolean log = true;
	private static final Object mutex = new Object();
	//------------------------------------------------------------------------------
	final static public int MAX_RETRY_COUNT = 3;
	//------------------------------------------------------------------------------
	public int[] mIdList; //Transaction id
	public int[] mOp; //operation code
	public int[] mApi;
	public int[] mRetryCount;
	public int[] mBitmap;
	public long[] mSentTime;
	public String[] mHeader;
	public String[] mScript; //used for search text
	public Payload mPayloadData;
	public Object mRequestInfo;
	public int mMessageOp;
	public int mPayloadCount;
	public String[] mDestContacts;
	public String mMessageId; //used for reply/fwd id
	public String mRTMLMessageId; //used for reply/fwd id
	public int mDirectionMore;
	public int mFetchCount;
	public String mMessageIDString;
	public int mInOutCount = 0;
	public int mReturnCount;
	public String mExtraCommand;
	public int mAutoPlay;
	public int mChatRequest;
	public int mMessageType;
	public int mChatMessageType;
	public int mRTAnimationMessage;
	public int mLoginAsInvisible;
	public long payloadSize = 0;
    public byte httpEngineIndex = 0;
//    public int currentProcessedFirstRecordId = -1;
//    public int currentProcessedSecondRecordId = -1;
	
	//------------------------------------------------------------------------------
	public void close() {
		mPayloadData = null;
		mRequestInfo = null;
	}

	//------------------------------------------------------------------------------
	public OutboxTblObject() {
	}

	//------------------------------------------------------------------------------
	public OutboxTblObject(int aCount, int aOperation, int aMessageOp) {
		this(aCount);
		this.mOp[0] = aOperation;
		this.mMessageOp = aMessageOp;
//		System.out.println("---------mMessageOp----: "+mMessageOp);
	}

	//--------------------------------------------------------------------------------------
	public OutboxTblObject(int aCount) {
		mIdList = new int[aCount];
		mOp = new int[aCount];
		mApi = new int[aCount];
		mRetryCount = new int[aCount];
		mHeader = new String[aCount];
		mScript = new String[aCount];
		mSentTime = new long[aCount];
		mBitmap = new int[aCount];
		mPayloadData = new Payload(aCount);
		mInOutCount = aCount;
		payloadSize = 0;
	}
//http://fabiensanglard.net/mobile_progressive_playback/index.php
	public int addRecords(String aTable) {
		int ret = Constants.ERROR_NONE;
		try {
			synchronized (mutex) {
				int recCount = BusinessProxy.sSelf.getRecordCount(aTable);
				if (log)
					Log.i(TAG, "addRecords-- record count = " + recCount + " for Table= " + aTable);
				if (recCount >= Constants.OUTBOX_MAX_REC_SIZE) {
					if (aTable.equals(DBEngine.OUTBOX_TABLE)) {
						return Constants.ERROR_OUTQUEUE_FULL;
					} else {
						List<Integer> ids = BusinessProxy.sSelf.getAllIdsForTable(DBEngine.SENTBOX_TABLE);
						
						if (!ids.isEmpty()) {
							ContentValues values = BusinessProxy.sSelf.getOutboxTblObjectDetails(ids.get(0), (byte)-1, DBEngine.SENTBOX_TABLE);
							int bitmap = values.getAsInteger(DBEngine.OUTBOX_PAYLOAD_BITMAP);
							byte[] payloadData = values.getAsByteArray(DBEngine.OUTBOX_PAYLOAD);
							//@Voice
							if (0 < (bitmap & Payload.PAYLOAD_BITMAP_VOICE)) {
								ByteArrayInputStream bytein = new ByteArrayInputStream(payloadData);
								DataInputStream dataInput = new DataInputStream(bytein);
								int count = dataInput.readInt();
								if (0 < count) {
									byte[] dummy = new byte[count];
									dataInput.readFully(dummy);
									String filename = new String(dummy, Constants.ENC);
									File file = new File(filename);
									if (file.exists()) {
										file.delete();
									}
									dummy = null;
								}
							}
							if (0 < (bitmap & Payload.PAYLOAD_BITMAP_PICTURE)) {
								ByteArrayInputStream bytein = new ByteArrayInputStream(payloadData);
								DataInputStream dataInput = new DataInputStream(bytein);
								//voice
								int count = dataInput.readInt();
								if (0 < count) {
									dataInput.skip(count);
									dataInput.skip(1);
								}
								//text = 
								count = dataInput.readInt();
								if (0 < count) {
									dataInput.skip(count);
									dataInput.skip(1);
								}
								count = dataInput.readInt();
								if (0 < count) {
									byte filedata[];
									File f;
									String fileName;
									for (int i = 0; i < count; i++) {
										filedata = new byte[dataInput.readInt()];
										dataInput.read(filedata, 0, filedata.length);
										fileName = new String(filedata, Constants.ENC);
										if (fileName.toLowerCase().endsWith(Constants.CAMERA_FILE_END)) {
											f = new File(fileName);
											if (f.exists()) {
												f.delete();
											}
										}
										dataInput.readByte();
									}
								}
							}
							Object is[] = (Object[])ids.toArray();
							Arrays.sort(is);
							List<Integer> deletingIds = new ArrayList<Integer>();
							//deletingIds.add(ids.get(0));
							deletingIds.add((Integer)is[0]);
							BusinessProxy.sSelf.deleteForIDs(deletingIds, DBEngine.SENTBOX_TABLE);
						}
					}
				}
				ContentValues values = new ContentValues();
				values.put(DBEngine._ID, this.mIdList[0]);
				values.put(DBEngine.OUTBOX_OPERATION, this.mOp[0]);
				values.put(DBEngine.OUTBOX_API, this.mApi[0]);
				values.put(DBEngine.OUTBOX_RETRY_COUNT, this.mRetryCount[0]);
				values.put(DBEngine.OUTBOX_HEADER, this.mHeader[0]);
				values.put(DBEngine.OUTBOX_SCRIPT, this.mScript[0]);
				Calendar cal = Calendar.getInstance();
				values.put(DBEngine.OUTBOX_TIME, cal.getTime().getTime());
				values.put(DBEngine.OUTBOX_PAYLOAD_BITMAP, this.mPayloadData.mPayloadTypeBitmap);
				ByteArrayOutputStreamUtil byteout = new ByteArrayOutputStreamUtil();
				DataOutputStream dataOutStream = new DataOutputStream(byteout);
				writeAttachment(dataOutStream);
				values.put(DBEngine.OUTBOX_PAYLOAD, byteout.toByteArray());
				if (aTable.equals(DBEngine.OUTBOX_TABLE)){
					values.put(DBEngine.MSG_STATUS, 0); 
					values.put(DBEngine.OUTBOX_PAYLOAD_SIZE, payloadSize);
					}
				byteout.close();
				List<ContentValues> list = new ArrayList<ContentValues>();
				list.add(values);
				if(this.mHeader[0].indexOf("OPERATION=20")==-1){
				BusinessProxy.sSelf.insertValuesInTable(aTable, list);
				}else{
					if(log)
						Log.i(TAG, "---DB---OPERATION=20");
				}
				if(log)
					Log.i(TAG, "---DB---"+values.toString());
			}
		} catch (Throwable th) {
			if (log)
				Log.e(TAG, "addRecords-- ERROR " + th.getMessage(), th);
			ret = Constants.ERROR_DB;
		}
		
		return ret;
	}

	public int getRecords(final boolean withPayload, final boolean isForNetwork, final int transactionId, final byte mRequestNo, final String table) {
		int ret = Constants.ERROR_NONE;
		try {
			synchronized (mutex) {
				List<Integer> ids = BusinessProxy.sSelf.getAllIdsForTable(table);
//				System.out.println("---DB-ids--"+ids.toString());
				if (!ids.isEmpty()) {
					int listSize = ids.size();
					if (this.mInOutCount > listSize) {
						this.mInOutCount = listSize;
					}
					ContentValues values = null;
					if (transactionId > 0 && ids.contains(new Integer(transactionId))) {
						values = BusinessProxy.sSelf.getOutboxTblObjectDetails(transactionId, mRequestNo, DBEngine.OUTBOX_TABLE);
						if(values!= null)
							putContentsToValues(withPayload, values);
						else {
							if (log)
								Log.i(TAG, "PSS.--:run()::[INFO] -- ERROR_DB calling " );
							ret = Constants.ERROR_DB;
							}
					} else {
						boolean get = false ;
						int i = 0;
						for (; i < /*this.mInOutCount*/ids.size() && !get; i++) {
							values = BusinessProxy.sSelf.getOutboxTblObjectDetails(ids.get(i), mRequestNo, DBEngine.OUTBOX_TABLE);
							if(values!=null){
								get = true ;
							putContentsToValues(withPayload, values);
							if (isForNetwork) {
								int retryCount = values.getAsInteger(DBEngine.OUTBOX_RETRY_COUNT);
								final String id = values.getAsInteger(DBEngine._ID).toString();
								BusinessProxy.sSelf.updateTableValues(DBEngine.OUTBOX_TABLE, id, values);
								values.clear();
								if(retryCount>0)
									values.put(DBEngine.MSG_STATUS, 0);
								values.put(DBEngine.OUTBOX_RETRY_COUNT, ++retryCount);
								BusinessProxy.sSelf.updateTableValues(DBEngine.OUTBOX_TABLE, id, values);
							}else
								values.put(DBEngine.MSG_STATUS, 0);
						}else {
							//ret = Constants.ERROR_DB;
						}
						}
						if(!get)ret = Constants.ERROR_DB;
						
					}
				} else {
					this.mInOutCount = 0;
				}
			}
		} catch (Exception ex) {
			if (log)
				Log.e(TAG, "getRecords -- ERROR " + ex.getMessage(), ex);
			ret = Constants.ERROR_SYSTEM;
		}
		return ret;
	}

	public String getDestinations(int index) {
		String str = "";
		if (this.mHeader != null && this.mHeader.length > index && this.mHeader[index] != null) {
			str = this.mHeader[index].substring(this.mHeader[index].indexOf("DESTPHONECOUNT") + "DESTPHONECOUNT".length() + 1);
			if (str.indexOf(',') != -1)
				str = str.substring(0, str.indexOf(','));
		}
		return str;
	}

	public int deleteRecordWithId(int id) throws Exception {
		int recordId = 0;
		synchronized (mutex) {
			List<Integer> idList = BusinessProxy.sSelf.getAllIdsForTable(DBEngine.OUTBOX_TABLE);
			if (idList.contains(id)) {
				idList.clear();
				idList.add(id);
				BusinessProxy.sSelf.deleteForIDs(idList, DBEngine.OUTBOX_TABLE);
			}
		}
		return recordId;
	}

	public int updateAllTransactionIds(int startTransactionId, String table) throws Exception {
		List<Integer> idList = BusinessProxy.sSelf.getAllIdsForTable(table);
		if (idList.isEmpty()) {
			return Constants.ERROR_NONE;
		}
		ContentValues updatedValues = new ContentValues();
		for (Integer id : idList) {
			updatedValues.put(DBEngine._ID, ++startTransactionId);
			BusinessProxy.sSelf.updateTableValues(DBEngine.OUTBOX_TABLE, id.toString(), updatedValues);
		}
		return startTransactionId;
	}

	private void putContentsToValues(final boolean withPayload, final ContentValues values) throws IOException {
		this.mIdList[0] = values.getAsInteger(DBEngine._ID);
		this.mOp[0] = values.getAsInteger(DBEngine.OUTBOX_OPERATION);
		this.mApi[0] = values.getAsInteger(DBEngine.OUTBOX_API);
		this.mRetryCount[0] = values.getAsInteger(DBEngine.OUTBOX_RETRY_COUNT);
		this.mHeader[0] = values.getAsString(DBEngine.OUTBOX_HEADER);
		this.mScript[0] = values.getAsString(DBEngine.OUTBOX_SCRIPT);
		this.mPayloadData.mPayloadTypeBitmap = values.getAsByte(DBEngine.OUTBOX_PAYLOAD_BITMAP);
		fillPayloadData(this.mPayloadData, values.getAsByteArray(DBEngine.OUTBOX_PAYLOAD), withPayload);
		if(log)
			Log.i(TAG, "putContentsToValues :: DB : "+this.mHeader[0]);
	}

	public static void fillPayloadData(Payload payload, byte[] data, boolean withPayload) throws IOException {
		if (null == data)
			return;
		ByteArrayInputStream bytein = new ByteArrayInputStream(data);
		DataInputStream dataInput = new DataInputStream(bytein);
		int count, len;
		byte[] dummy;
		payload.mPayloadTypeBitmap = dataInput.readInt();
		// Voice
		count = dataInput.readInt();
		if (0 < count) {
			payload.mVoice = new byte[1][];
			payload.mVoiceType = new byte[1];
			if (withPayload) {
				dummy = new byte[count];
				dataInput.readFully(dummy);
				payload.mVoice[0] = getFileData(new String(dummy));
			} else {
				payload.mVoice[0] = new byte[count];
				dataInput.readFully(payload.mVoice[0]);
			}
			payload.mVoiceType[0] = dataInput.readByte();
		}
		// Text
		count = dataInput.readInt();
		if (0 < count) {
			payload.mText = new byte[1][];
			payload.mTextType = new byte[1];
			payload.mText[0] = new byte[count];
			dataInput.readFully(payload.mText[0]);
			payload.mTextType[0] = dataInput.readByte();
		}
		// Picture
		count = dataInput.readInt();
		if (0 < count) {
			payload.mPicture = new byte[count][];
			payload.mPictureType = new byte[count];
			for (int i = 0; i < count; i++) {
				len = dataInput.readInt();
				dummy = new byte[len];
				dataInput.readFully(dummy);
				if (withPayload)
					payload.mPicture[i] = getFileData(new String(dummy));
				else {
					payload.mPicture[i] = dummy;
				}
				payload.mPictureType[i] = dataInput.readByte();
			}
		}
		// Video
		count = dataInput.readInt();
		if (0 < count) {
			payload.mVideo = new byte[count][];
			payload.mVideoType = new byte[count];
			for (int i = 0; i < count; i++) {
				len = dataInput.readInt();
				dummy = new byte[len];
				dataInput.readFully(dummy);
				if (withPayload)
					payload.mVideo[i] = getFileData(new String(dummy));
				else {
					payload.mVideo[i] = dummy;
				}
				payload.mVideoType[i] = dataInput.readByte();
			}
		}
	}

	private void writeAttachment(DataOutputStream dataOutStream) throws IOException {
		payloadSize = 0;
		// VOICE
		dataOutStream.writeInt(this.mPayloadData.mPayloadTypeBitmap);
		if (null == this.mPayloadData.mVoice[0]) {
			dataOutStream.writeInt(0);
		} else {
			dataOutStream.writeInt(this.mPayloadData.mVoice[0].length);
			dataOutStream.write(this.mPayloadData.mVoice[0]);
			dataOutStream.writeByte(this.mPayloadData.mVoiceType[0]);
			payloadSize += getFileSize(new String(this.mPayloadData.mVoice[0]));
		}
		//TEXT
		if (null == this.mPayloadData.mText[0]) {
			dataOutStream.writeInt(0);
		} else {
			dataOutStream.writeInt(this.mPayloadData.mText[0].length);
			dataOutStream.write(this.mPayloadData.mText[0]);
			dataOutStream.writeByte(this.mPayloadData.mTextType[0]);
			payloadSize += this.mPayloadData.mText[0].length;
			if(log)
			Log.i(TAG, "writeAttachment :: ----mystatus saving---"+new String(this.mPayloadData.mText[0]));
		}
		//PICTURE
		if (null == this.mPayloadData.mPicture[0]) {
			dataOutStream.writeInt(0);
		} else {
			dataOutStream.writeInt(this.mPayloadData.mPicture.length);
			for (int i = 0; i < this.mPayloadData.mPicture.length; i++) {
				dataOutStream.writeInt(this.mPayloadData.mPicture[i].length);
				dataOutStream.write(this.mPayloadData.mPicture[i]);
				dataOutStream.writeByte(this.mPayloadData.mPictureType[i]);
				payloadSize += getFileSize(new String(this.mPayloadData.mPicture[0]));
			}
		}
		//Video
		if (null == this.mPayloadData.mVideo[0]) {
			dataOutStream.writeInt(0);
		} else {
			dataOutStream.writeInt(this.mPayloadData.mVideo.length);
			for (int i = 0; i < this.mPayloadData.mVideo.length; i++) {
				dataOutStream.writeInt(this.mPayloadData.mVideo[i].length);
				dataOutStream.write(this.mPayloadData.mVideo[i]);
				dataOutStream.writeByte(this.mPayloadData.mVideoType[i]);
				payloadSize += getFileSize(new String(this.mPayloadData.mVideo[0]));
			}
		}
	}

	private static byte[] getFileData(String filePath) throws IOException {
		FileInputStream fin = new FileInputStream(filePath);
		byte[] data = Utilities.readBytes(fin);//new byte[fin.available()];
		fin.read(data, 0, data.length);
		return data;
	}
	public long getFileSize(String filePath)  {
		long size = 0;
		FileInputStream fin;
		try {
			fin = new FileInputStream(filePath);
		
			size = fin.available();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}
}
