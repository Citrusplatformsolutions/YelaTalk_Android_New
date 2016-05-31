package com.kainat.app.android.util;

import java.util.Hashtable;

public final class ResponseObject {
	public byte[] mResponseData;
	private int mUserId;
	private int mSentTransactionId;
	private int mLastTransactionId;
	private int mSentOp;
	private int mResponseCode;
	private int mMessageCount;
	private Object mData;
	private InboxTblObject mBuddyObject;
	private InboxTblObject mGroupObject;
	private InboxTblObject mIMBuddyObject;
	private InboxTblObject mMessangerObject;
	private int mError;
	private int mUpgradeType; // 0 for no upgrade...1 for hard upgrade..2 for soft upgrade
	private String mUpgradeURL;
	private String mClientControl;
	private String mSuggestedNames;
	public Hashtable<String, String> mKeyValues;
	private int iSpecialValues;//new changed for emer
	public byte iHttpEngineIndex = 0;
	
	public boolean isMobileVerificationNeeded ;
	
	

	public int getiSpecialValues() {
		return iSpecialValues;
	}

	public void setiSpecialValues(int iSpecialValues) {
		this.iSpecialValues = iSpecialValues;
	}

	/**
	 * Gets the value of mResponseData
	 * 
	 * @return The mResponseData
	 */
	public final byte[] getResponseData() {
		return mResponseData;
	}

	/**
	 * Sets the value of responseData
	 * 
	 * @param responseData
	 *            The mResponseData to set
	 */
	public final void setResponseData(byte[] responseData) {
		mResponseData = responseData;
	}

	/**
	 * Gets the value of mUserId
	 * 
	 * @return The mUserId
	 */
	public final int getUserId() {
		return mUserId;
	}

	/**
	 * Sets the value of userId
	 * 
	 * @param userId
	 *            The mUserId to set
	 */
	public final void setUserId(int userId) {
		mUserId = userId;
	}

	/**
	 * Gets the value of mSentTransactionId
	 * 
	 * @return The mSentTransactionId
	 */
	public final int getSentTransactionId() {
		return mSentTransactionId;
	}

	/**
	 * Sets the value of sentTransactionId
	 * 
	 * @param sentTransactionId
	 *            The mSentTransactionId to set
	 */
	public final void setSentTransactionId(int sentTransactionId) {
		mSentTransactionId = sentTransactionId;
	}

	/**
	 * Gets the value of mLastTransactionId
	 * 
	 * @return The mLastTransactionId
	 */
	public final int getLastTransactionId() {
		return mLastTransactionId;
	}

	/**
	 * Sets the value of lastTransactionId
	 * 
	 * @param lastTransactionId
	 *            The mLastTransactionId to set
	 */
	public final void setLastTransactionId(int lastTransactionId) {
		mLastTransactionId = lastTransactionId;
	}

	/**
	 * Gets the value of mSentOp
	 * 
	 * @return The mSentOp
	 */
	public final int getSentOp() {
		return mSentOp;
	}

	/**
	 * Sets the value of sentOp
	 * 
	 * @param sentOp
	 *            The mSentOp to set
	 */
	public final void setSentOp(int sentOp) {
		mSentOp = sentOp;
	}

	/**
	 * Gets the value of mResponseCode
	 * 
	 * @return The mResponseCode
	 */
	public final int getResponseCode() {
		return mResponseCode;
	}

	/**
	 * Sets the value of responseCode
	 * 
	 * @param responseCode
	 *            The mResponseCode to set
	 */
	public final void setResponseCode(int responseCode) {
		mResponseCode = responseCode;
	}

	/**
	 * Gets the value of mMessageCount
	 * 
	 * @return The mMessageCount
	 */
	public final int getMessageCount() {
		return mMessageCount;
	}

	/**
	 * Sets the value of messageCount
	 * 
	 * @param messageCount
	 *            The mMessageCount to set
	 */
	public final void setMessageCount(int messageCount) {
		mMessageCount = messageCount;
	}

	/**
	 * Gets the value of mData
	 * 
	 * @return The mData
	 */
	public final Object getData() {
		return mData;
	}

	/**
	 * Sets the value of data
	 * 
	 * @param data
	 *            The mData to set
	 */
	public final void setData(Object data) {
		mData = data;
	}

	/**
	 * Gets the value of mBuddyObject
	 * 
	 * @return The mBuddyObject
	 */
	public final InboxTblObject getBuddyObject() {
		return mBuddyObject;
	}

	/**
	 * Sets the value of buddyObject
	 * 
	 * @param buddyObject
	 *            The mBuddyObject to set
	 */
	public final void setBuddyObject(InboxTblObject buddyObject) {
		mBuddyObject = buddyObject;
	}

	/**
	 * Gets the value of mGroupObject
	 * 
	 * @return The mGroupObject
	 */
	public final InboxTblObject getGroupObject() {
		
		return mGroupObject;
	}

	/**
	 * Sets the value of groupObject
	 * 
	 * @param groupObject
	 *            The mGroupObject to set
	 */
	public final void setGroupObject(InboxTblObject groupObject) {
		
		mGroupObject = groupObject;
	}

	/**
	 * Gets the value of mMessangerObject
	 * 
	 * @return The mMessangerObject
	 */
	public final InboxTblObject getMessangerObject() {
		return mMessangerObject;
	}

	/**
	 * Sets the value of messangerObject
	 * 
	 * @param messangerObject
	 *            The mMessangerObject to set
	 */
	public final void setMessangerObject(InboxTblObject messangerObject) {
		mMessangerObject = messangerObject;
	}

	/**
	 * Gets the value of mError
	 * 
	 * @return The mError
	 */
	public final int getError() {
		return mError;
	}

	/**
	 * Sets the value of error
	 * 
	 * @param error
	 *            The mError to set
	 */
	public final void setError(int error) {
		mError = error;
	}

	/**
	 * Gets the value of mUpgradeType
	 * 
	 * @return The mUpgradeType
	 */
	public final int getUpgradeType() {
		return mUpgradeType;
	}

	/**
	 * Sets the value of upgradeType
	 * 
	 * @param upgradeType
	 *            The mUpgradeType to set
	 */
	public final void setUpgradeType(int upgradeType) {
		mUpgradeType = upgradeType;
	}

	/**
	 * Gets the value of mUpgradeURL
	 * 
	 * @return The mUpgradeURL
	 */
	public final String getUpgradeURL() {
		return mUpgradeURL;
	}

	/**
	 * Sets the value of upgradeURL
	 * 
	 * @param upgradeURL
	 *            The mUpgradeURL to set
	 */
	public final void setUpgradeURL(String upgradeURL) {
		mUpgradeURL = upgradeURL;
	}

	/**
	 * Gets the value of mClientControl
	 * 
	 * @return The mClientControl
	 */
	public final String getClientControl() {
		return mClientControl;
	}

	/**
	 * Sets the value of clientControl
	 * 
	 * @param clientControl
	 *            The mClientControl to set
	 */
	public final void setClientControl(String clientControl) {
		mClientControl = clientControl;
	}

	/**
	 * Gets the value of mSuggestedNames
	 * 
	 * @return The mSuggestedNames
	 */
	public final String getSuggestedNames() {
		return mSuggestedNames;
	}

	/**
	 * Sets the value of suggestedNames
	 * 
	 * @param suggestedNames
	 *            The mSuggestedNames to set
	 */
	public final void setSuggestedNames(String suggestedNames) {
		mSuggestedNames = suggestedNames;
	}

	/**
	 * Sets the value of mIMBuddyObject
	 * 
	 * @param mIMBuddyObject
	 *            The mIMBuddyObject to set
	 */
	public void setIMBuddyObject(InboxTblObject mIMBuddyObject) {
		this.mIMBuddyObject = mIMBuddyObject;
	}

	/**
	 * Gets the value of mIMBuddyObject
	 * 
	 * @return The mIMBuddyObject
	 */
	public InboxTblObject getIMBuddyObject() {
		return mIMBuddyObject;
	}
	
	public boolean isMobileVerificationNeeded() {
		return isMobileVerificationNeeded;
	}

	public void setMobileVerificationNeeded(boolean isMobileVerificationNeeded) {
		this.isMobileVerificationNeeded = isMobileVerificationNeeded;
	}
}
