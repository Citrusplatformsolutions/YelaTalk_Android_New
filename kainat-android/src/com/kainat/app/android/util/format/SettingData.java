package com.kainat.app.android.util.format;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingData {

	public static SettingData sSelf;
	private Context context;

	

	private String mUserName;
	private int mUserID;
	private int mClientID;
	private int mLastTransactionId;
	private String mPassword;
	private int mGender;
	private boolean mIsRemember;
	private int mRefreshPeriod = 120;
	private byte mAlertMode = 1;
	private boolean messageSound;
	private boolean messagebluetooth;
	private String mEmail;
	private int mVolumeLevel = 10;
	private int mRefresh;
	private String mFirstName;
	private String mLastName;

	private String mAddress;
	private String mCity;
	private String mState;
	private String mZip;
	private String mCountry;

	private String mBirthDate;
	private String mCarrier;
	private byte mNotification;
	private String mNumber;
	private byte mCopyMessageTo;
	private byte mCommunityMessagePreference;
	private boolean mAutoReply;
	private String mAutoReplyMessage;
	private int mDelPrefWhenOffline;

	private String mLanguage;
	private String mRelationshipStatus;
	private String mSexualOrientation;
	private String mLookingFor;
	private String mEducation;
	private String mOccupation;
	private String mInterest;
	private String mFavoriteMovie;
	private String mFavoriteMusic;
	private String mActivationCode;

	private boolean nameEditable;
	private boolean updateSecurityQuestion = true;

	private String loginnedUserName;
	private String loginnedEmailOrMobile;
	private String loginnedDisplayableEmailOrMobile;

	private String loginFirstName;
	private String loginLastName;
	private boolean userNameActive;
	
	
	private String type;//new
	private String ageGroup;//new
	private String mobile;//new
	
	private String partneruserkey;//new
	
	private String appkey;//new
	

	private String mobileModel;//new
	
	private boolean isEmailVerified;//new
	private boolean isMobileNumberVerified;//new
	public boolean isMobileNumberVerified() {
		return isMobileNumberVerified;
	}

	public void setMobileNumberVerified(boolean isMobileNumberVerified) {
		this.isMobileNumberVerified = isMobileNumberVerified;
	}

	public boolean isSecurityQuestionSet() {
		return isSecurityQuestionSet;
	}

	public void setSecurityQuestionSet(boolean isSecurityQuestionSet) {
		this.isSecurityQuestionSet = isSecurityQuestionSet;
	}

	private boolean isSecurityQuestionSet;//new
	
	
	
	
	public boolean isEmailVerified() {
		return true;//isEmailVerified;
	}

	public void setEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	private boolean loginWithFullOption ;
	
	private String socialImagePaht ;
	
	
	public String getSocialImagePaht() {
		return socialImagePaht;
	}

	public void setSocialImagePaht(String socialImagePaht) {
		this.socialImagePaht = socialImagePaht;
	}

	public boolean isUserNameActive() {
		return userNameActive;
	}

	public void setUserNameActive(boolean userNameActive) {
		this.userNameActive = userNameActive;
	}
	
	public boolean isloginWithFullOption() {
		return loginWithFullOption;
	}

	public void setLoginWithFullOption(boolean loginWithFullOption) {
		this.loginWithFullOption = loginWithFullOption;
	}
	
	public boolean isLogOffFromserver() {
		return logOffFromserver;
	}

	public void setLogOffFromserver(boolean logOffFromserver) {
		this.logOffFromserver = logOffFromserver;
	}

	private boolean logOffFromserver;

	//	private boolean oldUser;

	//	public boolean isOldUser() {
	//		return oldUser;
	//	}
	//
	//	public void setOldUser(boolean oldUser) {
	//		this.oldUser = oldUser;
	//	}

	public boolean isNameEditable() {
		return nameEditable;
	}

	public String getLoginnedUserName() {
		return loginnedUserName;
	}

	public void setLoginnedUserName(String loginnedUserName) {
		this.loginnedUserName = loginnedUserName;
	}

	public String getLoginnedEmailOrMobile() {
		return loginnedEmailOrMobile;
	}

	public void setLoginnedEmailOrMobile(String loginnedEmailOrMobile) {
		this.loginnedEmailOrMobile = loginnedEmailOrMobile;
	}

	public String getLoginnedDisplayableEmailOrMobile() {
		return loginnedDisplayableEmailOrMobile;
	}

	public void setLoginnedDisplayableEmailOrMobile(String loginnedDisplayableEmailOrMobile) {
		this.loginnedDisplayableEmailOrMobile = loginnedDisplayableEmailOrMobile;
	}

	public void setNameEditable(boolean nameEditable) {
		this.nameEditable = nameEditable;
	}

	public boolean canUpdateSecurityQuestion() {
		return updateSecurityQuestion;
	}

	public void setUpdateSecurityQuestion(boolean updateSecurityQuestion) {
		this.updateSecurityQuestion = updateSecurityQuestion;
	}

	public String getLoginFirstName() {
		return loginFirstName;
	}

	public void setLoginFirstName(String loginFirstName) {
		this.loginFirstName = loginFirstName;
	}

	public String getLoginLastName() {
		return loginLastName;
	}

	public void setLoginLastName(String loginLastName) {
		this.loginLastName = loginLastName;
	}

	public static void createInstance(Context context) {
		synchronized (SettingData.class) {
			if (null == sSelf) {
				sSelf = new SettingData();
				sSelf.context = context;
				sSelf.loadSetting();
			}
		}
	}
	public static void createInstance(Context context,boolean loadSetting) {
		synchronized (SettingData.class) {
			if (null == sSelf) {
				sSelf = new SettingData();
				sSelf.context = context;
				if(loadSetting)
				sSelf.loadSetting();
			}
		}
	}

	public static void destroyInstance() {//nagendra
		sSelf = null;
	}

	private void loadSetting() {
		SharedPreferences pref = this.context.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
		this.mIsRemember = pref.getBoolean("isremember", false);
		this.mUserName = pref.getString("username", null);
		this.mUserID = pref.getInt("userid", 0);
		this.mClientID = pref.getInt("clientid", 0);
		this.mLastTransactionId = pref.getInt("lasttransactionid", 0);
		this.mFirstName = pref.getString("firstname", null);
		this.mLastName = pref.getString("lastname", null);
		this.mPassword = pref.getString("password", null);
		this.mRefresh = pref.getInt("refresh", 120);
		this.mAlertMode = (byte) pref.getInt("alert", 1);
		this.mVolumeLevel = pref.getInt("volume", 10);
		this.loginnedUserName = pref.getString("last_login_user", null);
		this.loginnedEmailOrMobile = pref.getString("last_login_email", null);
		this.loginFirstName = pref.getString("login_firstname", null);
		this.loginLastName = pref.getString("login_lastname", null);
		this.messageSound=pref.getBoolean("message_sound", false);
		this.messagebluetooth =pref.getBoolean("message_bluetooth", false);
		this.loginWithFullOption =pref.getBoolean("loginWithFullOption", false);
		
		this.mEmail =pref.getString("mEmail", null);
		this.mNumber =pref.getString("mNumber", null);
		this.userNameActive =pref.getBoolean("userNameActive", false);
		
		this.type =pref.getString("type", null);
		this.appkey =pref.getString("appkey", null);
		this.partneruserkey =pref.getString("partneruserkey", null);
	}

	public boolean updateSetting() {
		if(this.mUserName !=null && this.mUserID !=0  ){
		SharedPreferences.Editor editor = this.context.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE).edit();
		editor.putBoolean("isremember", this.mIsRemember);
		editor.putString("username", this.mUserName);
		editor.putInt("userid", this.mUserID);
		editor.putInt("clientid", this.mClientID);
		editor.putInt("lasttransactionid", this.mLastTransactionId);
		editor.putString("firstname", this.mFirstName);
		editor.putString("lastname", this.mLastName);
		editor.putString("password", this.mPassword);
		editor.putInt("refresh", this.mRefresh);
		editor.putInt("alert", this.mAlertMode);
		editor.putInt("volume", this.mVolumeLevel);
		editor.putString("last_login_user", this.loginnedUserName);
		editor.putString("last_login_email", this.loginnedEmailOrMobile);
		editor.putString("login_firstname", this.loginFirstName);
		editor.putString("login_lastname", this.loginLastName);
		editor.putBoolean("message_sound", this.messageSound);
		editor.putBoolean("message_bluetooth", this.messagebluetooth);
		
		editor.putString("mEmail", this.mEmail);
		editor.putString("mNumber", this.mNumber);
		editor.putBoolean("userNameActive", this.userNameActive);
		
		editor.putBoolean("loginWithFullOption", this.loginWithFullOption);
		
		editor.putString("type", this.type);
		editor.putString("appkey", this.appkey);
		editor.putString("partneruserkey", this.partneruserkey);
		
		return editor.commit();}
		else
		{
			return false;
		}
	}

	public void deleteSettings() {
//		SharedPreferences.Editor editor = this.context.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE).edit();
//		editor.clear();
//		editor.commit();
	}

	public static void deleteInstance() {
		synchronized (SettingData.class) {
			sSelf = null;
		}
	}

	/**
	 * Sets the value of UserName
	 * 
	 * @param mUserName
	 *            The UserName to set
	 */
	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	/**
	 * Gets the value of UserID
	 * 
	 * @return The UserID
	 */
	public int getUserID() {
		return mUserID;
	}
	/**
	 * Sets the value of UserID
	 * 
	 * @param UserID
	 *            The UserID to set
	 */
	public void setUserID(int mUserID) {
		this.mUserID = mUserID;
	}
	/**
	 * Gets the value of ClientID
	 * 
	 * @return The UserID
	 */
	public int getClientID() {
		return mClientID;
	}
	/**
	 * Sets the value of ClientID
	 * 
	 * @param UserID
	 *            The UserID to set
	 */
	public void setClientID(int mClientID) {
		this.mClientID = mClientID;
	}
	/**
	 * Gets the value of mLastTransactionId
	 * 
	 * @return The mLastTransactionId
	 */
	public int getLastTransactionId() {
		return mLastTransactionId;
	}
	/**
	 * Sets the value of mLastTransactionId
	 * 
	 * @param mLastTransactionId
	 *            The mLastTransactionId to set
	 */
	public void setLastTransactionId(int mLastTransactionId) {
		this.mLastTransactionId = mLastTransactionId;
	}
	
	/**
	 * Gets the value of UserName
	 * 
	 * @return The UserName
	 */
	public String getUserName() {
		return mUserName;
	}

	/**
	 * Sets the value of Name
	 * 
	 * @param mName
	 *            The Name to set
	 */
	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String mName) {
		this.mLastName = mName;
	}

	public String getFirstName() {
		return mFirstName;
	}
	
	public String getDisplayName() {
		return mFirstName + " " +mLastName;
	}

	public void setFirstName(String mName) {
		this.mFirstName = mName;
	}

	public String getFullName() {
		StringBuilder text = new StringBuilder();
		if (null != mFirstName) {
			text.append(mFirstName);
			text.append(' ');
		}
		if (null != mLastName) {
			text.append(mLastName);
		}
		return text.toString();
	}

	/**
	 * Sets the value of Password
	 * 
	 * @param mPassword
	 *            The Password to set
	 */
	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	/**
	 * Gets the value of Password
	 * 
	 * @return The Password
	 */
	public String getPassword() {
		return "aaaaaa";//mPassword;
	}

	/**
	 * Sets the value of Gender
	 * 
	 * @param mGender
	 *            The Gender to set
	 */
	public void setGender(int mGender) {
		this.mGender = mGender;
	}

	/**
	 * Gets the value of Gender
	 * 
	 * @return The Gender
	 */
	public int getGender() {
		return mGender;
	}

	/**
	 * Sets the value of IsRemember
	 * 
	 * @param mIsRemember
	 *            The IsRemember to set
	 */
	public void setRemember(boolean mIsRemember) {
		this.mIsRemember = mIsRemember;
	}

	/**
	 * Gets the value of IsRemember
	 * 
	 * @return The IsRemember
	 */
	public boolean isRemember() {
		return mIsRemember;
	}

	/**
	 * Sets the value of RefreshPeriod
	 * 
	 * @param mRefreshPeriod
	 *            The RefreshPeriod to set
	 */
	public void setRefreshPeriod(int mRefreshPeriod) {
		this.mRefreshPeriod = mRefreshPeriod;
	}

	/**
	 * Gets the value of RefreshPeriod
	 * 
	 * @return The RefreshPeriod
	 */
	public int getRefreshPeriod() {
		return mRefreshPeriod;
	}

	/**
	 * Sets the value of AlertMode
	 * 
	 * @param mAlertMode
	 *            The AlertMode to set
	 */
	public void setAlertMode(byte mAlertMode) {
		this.mAlertMode = mAlertMode;
	}

	
	public void setSoundMessage(boolean messageSound){
		this.messageSound=messageSound;
	}
	public void setbleutooothMessage(boolean messagebluetooth){
		this.messagebluetooth=messagebluetooth;
	}
	
	/**
	 * Gets the value of AlertMode
	 * 
	 * @return The AlertMode
	 */
	public byte getAlertMode() {
		return mAlertMode;
	}
	public boolean getMessageSound(){
		return messageSound;
	}
	public boolean getMessageBluetooth(){
		return messagebluetooth;
	}
	/**
	 * Sets the value of VolumeLevel
	 * 
	 * @param mVolumeLevel
	 *            The VolumeLevel to set
	 */
	public void setVolumeLevel(int mVolumeLevel) {
		this.mVolumeLevel = mVolumeLevel;
	}

	/**
	 * Gets the value of VolumeLevel
	 * 
	 * @return The VolumeLevel
	 */
	public int getVolumeLevel() {
		return mVolumeLevel;
	}

	/**
	 * Sets the value of Language
	 * 
	 * @param mLanguage
	 *            The Language to set
	 */
	public void setLanguage(String mLanguage) {
		this.mLanguage = mLanguage;
	}

	/**
	 * Gets the value of Language
	 * 
	 * @return The Language
	 */
	public String getLanguage() {
		return mLanguage;
	}

	/**
	 * Sets the value of RelationshipStatus
	 * 
	 * @param mRelationshipStatus
	 *            The RelationshipStatus to set
	 */
	public void setRelationshipStatus(String mRelationshipStatus) {
		this.mRelationshipStatus = mRelationshipStatus;
	}

	/**
	 * Gets the value of RelationshipStatus
	 * 
	 * @return The RelationshipStatus
	 */
	public String getRelationshipStatus() {
		return mRelationshipStatus;
	}

	/**
	 * Sets the value of SexualOrientation
	 * 
	 * @param mSexualOrientation
	 *            The SexualOrientation to set
	 */
	public void setSexualOrientation(String mSexualOrientation) {
		this.mSexualOrientation = mSexualOrientation;
	}

	/**
	 * Gets the value of SexualOrientation
	 * 
	 * @return The SexualOrientation
	 */
	public String getSexualOrientation() {
		return mSexualOrientation;
	}

	/**
	 * Sets the value of LookingFor
	 * 
	 * @param mLookingFor
	 *            The LookingFor to set
	 */
	public void setLookingFor(String mLookingFor) {
		this.mLookingFor = mLookingFor;
	}

	/**
	 * Gets the value of LookingFor
	 * 
	 * @return The LookingFor
	 */
	public String getLookingFor() {
		return mLookingFor;
	}

	/**
	 * Sets the value of Education
	 * 
	 * @param mEducation
	 *            The Education to set
	 */
	public void setEducation(String mEducation) {
		this.mEducation = mEducation;
	}

	/**
	 * Gets the value of Education
	 * 
	 * @return The Education
	 */
	public String getEducation() {
		return mEducation;
	}

	/**
	 * Sets the value of Occupation
	 * 
	 * @param mOccupation
	 *            The Occupation to set
	 */
	public void setOccupation(String mOccupation) {
		this.mOccupation = mOccupation;
	}

	/**
	 * Gets the value of Occupation
	 * 
	 * @return The Occupation
	 */
	public String getOccupation() {
		return mOccupation;
	}

	/**
	 * Sets the value of Interest
	 * 
	 * @param mInterest
	 *            The Interest to set
	 */
	public void setInterest(String mInterest) {
		this.mInterest = mInterest;
	}

	/**
	 * Gets the value of Interest
	 * 
	 * @return The Interest
	 */
	public String getInterest() {
		return mInterest;
	}

	/**
	 * Sets the value of FavoriteMovie
	 * 
	 * @param mFavoriteMovie
	 *            The FavoriteMovie to set
	 */
	public void setFavoriteMovie(String mFavoriteMovie) {
		this.mFavoriteMovie = mFavoriteMovie;
	}

	/**
	 * Gets the value of FavoriteMovie
	 * 
	 * @return The FavoriteMovie
	 */
	public String getFavoriteMovie() {
		return mFavoriteMovie;
	}

	/**
	 * Sets the value of Favorite Music
	 * 
	 * @param mFavoriteMusic
	 *            The Favorite Music to set
	 */
	public void setFavoriteMusic(String mFavoriteMusic) {
		this.mFavoriteMusic = mFavoriteMusic;
	}

	/**
	 * Gets the value of Favorite Music
	 * 
	 * @return The Favorite Music
	 */
	public String getFavoriteMusic() {
		return mFavoriteMusic;
	}

	/**
	 * Sets the value of ActivationCode
	 * 
	 * @param mActivationCode
	 *            The activation Code to set
	 */
	public void setActivationCode(String mActivationCode) {
		this.mActivationCode = mActivationCode;
	}

	/**
	 * Gets the value of ActivationCode
	 * 
	 * @return The activation Code
	 */
	public String getActivationCode() {
		return mActivationCode;
	}

	/**
	 * Sets the value of mEmail
	 * 
	 * @param mEmail
	 *            The mEmail to set
	 */
	public void setEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	/**
	 * Gets the value of mEmail
	 * 
	 * @return The mEmail
	 */
	public String getEmail() {
		return mEmail;
	}

	/**
	 * Sets the value of mAddress
	 * 
	 * @param mAddress
	 *            The mAddress to set
	 */
	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	/**
	 * Gets the value of mAddress
	 * 
	 * @return The mAddress
	 */
	public String getAddress() {
		return mAddress;
	}

	/**
	 * Sets the value of mCity
	 * 
	 * @param mCity
	 *            The mCity to set
	 */
	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	/**
	 * Gets the value of mCity
	 * 
	 * @return The mCity
	 */
	public String getCity() {
		return mCity;
	}

	/**
	 * Sets the value of mState
	 * 
	 * @param mState
	 *            The mState to set
	 */
	public void setState(String mState) {
		this.mState = mState;
	}

	/**
	 * Gets the value of mState
	 * 
	 * @return The mState
	 */
	public String getState() {
		return mState;
	}

	/**
	 * Sets the value of mZip
	 * 
	 * @param mZip
	 *            The mZip to set
	 */
	public void setZip(String mZip) {
		this.mZip = mZip;
	}

	/**
	 * Gets the value of mZip
	 * 
	 * @return The mZip
	 */
	public String getZip() {
		return mZip;
	}

	/**
	 * Sets the value of mCountry
	 * 
	 * @param mCountry
	 *            The mCountry to set
	 */
	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	/**
	 * Gets the value of mCountry
	 * 
	 * @return The mCountry
	 */
	public String getCountry() {
		if(mCountry==null)
			mCountry = "India";
		return mCountry;
	}

	/**
	 * Sets the value of birthDate
	 * 
	 * @param birthDate
	 *            The birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.mBirthDate = birthDate;
	}

	/**
	 * Gets the value of birthDate
	 * 
	 * @return The birthDate
	 */
	public String getBirthDate() {
		return mBirthDate;
	}

	/**
	 * Sets the value of carrier
	 * 
	 * @param carrier
	 *            The carrier to set
	 */
	public void setCarrier(String carrier) {
		this.mCarrier = carrier;
	}

	/**
	 * Gets the value of carrier
	 * 
	 * @return The carrier
	 */
	public String getCarrier() {
		if(mCarrier==null || mCarrier.length() <=0)
			return null ;
		return mCarrier;
	}

	/**
	 * Sets the value of notification
	 * 
	 * @param notification
	 *            The notification to set
	 */
	public void setNotification(byte notification) {
		this.mNotification = notification;
	}

	/**
	 * Gets the value of notification
	 * 
	 * @return The notification
	 */
	public byte getNotification() {
		return mNotification;
	}

	/**
	 * Gets the value of number
	 * 
	 * @return The number
	 */
	public final String getNumber() {
		return mNumber;
	}

	/**
	 * Sets the value of number
	 * 
	 * @param number
	 *            The number to set
	 */
	public final void setNumber(String number) {
		this.mNumber = number;
	}

	/**
	 * Gets the value of copyMessageTo
	 * 
	 * @return The copyMessageTo
	 */
	public final byte getCopyMessageTo() {
		return mCopyMessageTo;
	}

	/**
	 * Sets the value of copyMessageTo
	 * 
	 * @param copyMessageTo
	 *            The copyMessageTo to set
	 */
	public final void setCopyMessageTo(byte copyMessageTo) {
		this.mCopyMessageTo = copyMessageTo;
	}

	/**
	 * Gets the value of communityMessagePreference
	 * 
	 * @return The communityMessagePreference
	 */
	public final byte getCommunityMessagePreference() {
		return mCommunityMessagePreference;
	}

	/**
	 * Sets the value of communityMessagePreference
	 * 
	 * @param communityMessagePreference
	 *            The communityMessagePreference to set
	 */
	public final void setCommunityMessagePreference(byte communityMessagePreference) {
		this.mCommunityMessagePreference = communityMessagePreference;
	}

	/**
	 * Gets the value of autoReply
	 * 
	 * @return The autoReply
	 */
	public final boolean isAutoReply() {
		return mAutoReply;
	}

	/**
	 * Sets the value of autoReply
	 * 
	 * @param autoReply
	 *            The autoReply to set
	 */
	public final void setAutoReply(boolean autoReply) {
		this.mAutoReply = autoReply;
	}

	/**
	 * Gets the value of autoReplyMessage
	 * 
	 * @return The autoReplyMessage
	 */
	public final String getAutoReplyMessage() {
		try{
		return mAutoReplyMessage.trim();
		}catch (Exception e) {
return null;
		}
	}

	public final boolean getAutoReply() {
		return mAutoReply;
	}

	/**
	 * Sets the value of autoReplyMessage
	 * 
	 * @param autoReplyMessage
	 *            The autoReplyMessage to set
	 */
	public final void setAutoReplyMessage(String autoReplyMessage) {
		this.mAutoReplyMessage = autoReplyMessage;
	}

	/**
	 * Sets the value of mDelPrefWhenOffline
	 * 
	 * @param mDelPrefWhenOffline
	 *            The mDelPrefWhenOffline to set
	 */
	public void setDelPrefWhenOffline(int mDelPrefWhenOffline) {
		this.mDelPrefWhenOffline = mDelPrefWhenOffline;
	}

	/**
	 * Gets the value of mDelPrefWhenOffline
	 * 
	 * @return The mDelPrefWhenOffline
	 */
	public int getDelPrefWhenOffline() {
		return mDelPrefWhenOffline;
	}
//this is onlu for dubug purpose
	public boolean isIm(){
		try{
		//if(getUserName()!=null && (getUserName().trim().toLowerCase().equalsIgnoreCase("qts")||getUserName().trim().toLowerCase().equalsIgnoreCase("nagendra1020")))
		return false ;
//		else
//			return false;
		}catch (Exception e) {
			return false;
		}
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobileModel() {
		return mobileModel;
	}

	public void setMobileModel(String mobileModel) {
		this.mobileModel = mobileModel;
	}
	public String getPartneruserkey() {
		return partneruserkey;
	}

	public void setPartneruserkey(String partneruserkey) {
		this.partneruserkey = partneruserkey;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

}
