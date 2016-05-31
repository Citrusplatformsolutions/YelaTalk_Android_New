package com.kainat.app.android.util;

import java.util.ArrayList;
import java.util.Arrays;

import com.kainat.app.android.YelatalkApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefManager {

	private SharedPreferences pref;
	// editor for shared refrence
	Editor editor;
	Context mContext;
	int PRIVATE_MODE = 0;
	public static final byte GROUP_ADMIN_INFO = 1;
	public static final byte GROUP_ACTIVE_INFO = 2;
	public static final byte GROUP_OWNER_INFO = 3;
	private final String PREF_NAME = "SuperChatPref";

	private final String FIRST_TIME_APP = "mode"; // For Frash User
	private final String USER_ID = "user_id";
	private final String USER_DOMAIN = "user_domain";
	private final String USER_ORG_NAME = "org_name";
	private final String SIP_SERVER = "sip_address";
	private final String GROUP_SERVER_STATE = "group_server_state";
	private final String USER_SIP_PASSWORD = "user_sip_assword";
	private final String USER_PASSWORD = "user_password";

	private final String USER_NAME_ID = "user_name_id";
	private final String USER_DISPLAY_NAME = "name";
	private final String USER_STATUS_MESSAGE = "user_status_message";
	private final String AUTH_STATUS = "status";
	private final String USER_PHONE = "mobile_number";
	private final String USER_EMAIL = "email_id";
	private final String USER_FILE_ID = "user_file_id";
	private final String LAST_ONLINE = "last_online";
	private final String USER_VARIFIED = "varified";
	private final String MOBILE_VARIFIED = "mobile_varified";
	private final String MOBILE_REGISTERED = "mobile_registered";
	private final String PROFILE_ADDED = "profile_added";
	private final String USER_EPR_COMPLETE = "epr_complete";
	private final String USER_LOGED_OUT = "logout";
	private final String CHAT_COUNTER = "chat_counter";
	private final String DEVICE_TOKEN = "device_token";
	private final String ALL_RECENT_USERS = "all_recent_users";
	private final String ALL_RECENT_DOMAINS = "all_recent_domains";
	private final String SHOW_HELP_SCREEN_TIMELINE = "show_help_timeline" ;
	private final String SHOW_HELP_SCREEN_CHAT = "show_help_chat" ;
	private static SharedPrefManager sharedPrefManager;

	private SharedPrefManager(Context context) {
		this.mContext = context;
		pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public static SharedPrefManager getInstance() {
		if (sharedPrefManager == null) {
			sharedPrefManager = new SharedPrefManager(YelatalkApplication.applicationcContext);
		}
		return sharedPrefManager;
	}
	public void setProfileAdded(String userName,boolean flag){
		editor.putBoolean(PROFILE_ADDED+userName, flag);
		editor.commit();
	}
	public void setMobileVerified(String mobileNumber,boolean flag){
		editor.putBoolean(MOBILE_VARIFIED+mobileNumber, flag);
		editor.commit();
	}
	public void setMobileRegistered(String mobileNumber,boolean flag){
		editor.putBoolean(MOBILE_REGISTERED+mobileNumber, flag);
		editor.commit();
	}
	public boolean isProfileAdded(String userName){
		return pref.getBoolean(PROFILE_ADDED+userName, false);
	}
	public boolean isMobileVerified(String mobileNumber){
		return pref.getBoolean(MOBILE_VARIFIED+mobileNumber, false);
	}
	public boolean isMobileRegistered(String mobileNumber){
		return pref.getBoolean(MOBILE_REGISTERED+mobileNumber, false);
	}
public void setContactModified(boolean status){
	editor.putBoolean("contact_modified", status);
	editor.commit();
}
public boolean isContactModified(){
	boolean value = pref.getBoolean("contact_modified", true);
	return value;
}
	public void setAppMode(String message) {
		editor.putString(FIRST_TIME_APP, message);
		editor.commit();
	}

	public String getAppMode() {
		String value = pref.getString(FIRST_TIME_APP, null);
		return value;
	}
	public String getRecentDomains(){
		String value = pref.getString(ALL_RECENT_DOMAINS, "");
		return value;
	}
	public void saveRecentDomains(String domain) {
		String values = getRecentDomains();
		if(values == null || values.equals(""))
			values = domain;
		else{
			if(values.contains(domain))
				return;
			values += (","+domain);
			}
		editor.putString(ALL_RECENT_DOMAINS, values);
		editor.commit();
	}
	public String getSipServerAddress() {
		String value = pref.getString(SIP_SERVER, "");
		return value;
	}

	public String getAuthStatus() {
		String value = pref.getString(AUTH_STATUS, null);
		return value;
	}

	
	public void saveAuthStatus(String name) {
		editor.putString(AUTH_STATUS, name);
		editor.commit();
	}
	public void saveLastOnline(long time) { // new1
		editor.putLong(LAST_ONLINE, time);
		editor.commit();
	}
	public long getLastOnline() { // new1
		long value = pref.getLong(LAST_ONLINE, 0);
		return value;
	}
	public void saveUserFileId(String userName,String fileId) {
		editor.putString(USER_FILE_ID+userName, fileId);
		editor.commit();
	}
	public String getUserFileId(String userName) {
		String value = pref.getString(USER_FILE_ID+userName, null);
		return value;
	}
	public String getUserPassword() {
		String value = pref.getString(USER_PASSWORD, "");
		return value;
	}

	public void saveUserPassword(String pass) {
		editor.putString(USER_PASSWORD, pass);
		editor.commit();
	}
	public void saveUserGroupInfo(String groupName, String groupPerson, byte infoType, boolean isSet) {
//		String myName = getUserNameId();
		editor.putBoolean(groupName+"_"+groupPerson+"_"+infoType, isSet);
		editor.commit();
	}
	public boolean isAdmin(String groupName, String groupPerson){
		return pref.getBoolean(groupName+"_"+groupPerson+"_"+GROUP_ADMIN_INFO, false);
	}
	public boolean isOwner(String groupName, String groupPerson){
		return pref.getBoolean(groupName+"_"+groupPerson+"_"+GROUP_OWNER_INFO, false);
	}
	public boolean isGroupActive(String groupName, String groupPerson){
		return pref.getBoolean(groupName+"_"+groupPerson+"_"+GROUP_ACTIVE_INFO, false);
	}
	public boolean saveUsersOfGroup(String groupName,String groupPerson) {
		String prevName = getUsersOfGroup(groupName);
		if(isGroupUserPresent(prevName,groupPerson))
			return false;
		if (prevName.equals(""))
			editor.putString("Gp_Users"+groupName, groupPerson);
		else
			editor.putString("Gp_Users"+groupName, prevName + "%#%" + groupPerson);
		editor.commit();
		return true;
	}
	private boolean isGroupUserPresent(String allUsers,String groupPerson){
		for(String person:allUsers.split("%#%")){
			if(person.equals(groupPerson))
				return true;
		}
		return false;
	}
	public void saveGroupName(String groupName,String displayName) {
		String prevName = getGroupName();
		saveGroupDisplayName(groupName, displayName);
		if (prevName.equals(""))
			editor.putString("Group_Name", groupName);
		else
			editor.putString("Group_Name", prevName + "%#%" + groupName);
		editor.commit();
	}
	public void removeUsersFromGroup(String groupName,String groupPerson) {
		String result = "";
		String prevName = getUsersOfGroup(groupName);
		if (!prevName.equals("")){
			if(isGroupUserPresent(prevName,groupPerson)){
				if(prevName.contains("%#%")){
					ArrayList<String> list = new ArrayList<String>(Arrays.asList(prevName.split("%#%")));
					for(String item:list){
						if(!item.equals(groupPerson))
							result+=(item+"%#%");
					}
					if(result.endsWith("%#%"))
						result = result.substring(0, result.lastIndexOf("%#%"));
				}else{
					result = prevName.replace(groupPerson, "");
				}
			editor.putString("Gp_Users"+groupName, result);
			editor.commit();
		}
		}
	}
	public void removeGroupName(String groupName) {
		String prevName = getGroupName();
		removeGroupDisplayName(groupName);
		if (!prevName.equals("")){
			if(prevName.contains(groupName)){
				if(prevName.contains(groupName+"%#%")){
					prevName = prevName.replace(groupName+"%#%", "");
				}else if(prevName.contains("%#%"+groupName)){
					prevName = prevName.replace("%#%"+groupName, "");
				}else
					prevName = prevName.replace(groupName, "");
			editor.putString("Group_Name", prevName);
//			editor.putString("Gp_Users"+groupName, "");
			editor.commit();
		}
		}
	}
	public boolean isGroupChat(String groupName) {
		boolean ret = false;
		if(groupName==null)
			return false;
		String groups = getGroupName();
		if (groups != null) {
			if (!groups.equals("") && groups.contains("%#%")) {
				for (String name : groups.split("%#%")) {
					if (name.equals(groupName)) {
						return true;
					}
				}
			} else if (groupName.equals(groups)) {
				ret = true;
			}
		}
		return ret;
	}

//	public String getUserSipPassword() {
//		String value = pref.getString(USER_SIP_PASSWORD, "");
//		return value;
//	}
//
//	public void saveUserSipPassword(String pass) {
//		editor.putString(USER_SIP_PASSWORD, pass);
//		editor.commit();
//	}
	public String getUsersOfGroup(String groupName) {
		String value = pref.getString("Gp_Users"+groupName, "");
		return value;
	}
	public String getGroupName() {
		String value = pref.getString("Group_Name", "");
		return value;
	}
	public String[] getGroupNamesArray() {
		String array[] = new String[1];
		String groups = getGroupName();
		if (groups != null) {
			if (!groups.equals("") && groups.contains("%#%")) {
				array = groups.split("%#%");
				
			} else{
				array[0] = groups;
			}
		}
		return array;
	}
	public ArrayList<String> getGroupUsersList(String groupName) {
		ArrayList<String> list = new ArrayList<String>();
		String groups = getUsersOfGroup(groupName);
		if (groups != null && !groups.equals("")) {
			if (groups.contains("%#%")) {
				list = new ArrayList<String>(Arrays.asList(groups.split("%#%")));
			} else{
				list.add(groups);
			}
		}
		return list;
	}
	public String getDisplayName() {
		String value = pref.getString(USER_DISPLAY_NAME, null);
		return value;
	}

	public String getUserStatusMessage(String userName) {
		String value = pref.getString(USER_STATUS_MESSAGE+userName, "I Am On SuperChat");
		return value;
	}
	public String getUserName() {
		String value = pref.getString(USER_NAME_ID, null);
		return value;
	}

	public void saveUserPhone(String phone) {
		editor.putString(USER_PHONE, phone);
		editor.commit();
	}

	public String getUserPhone() {
		String value = pref.getString(USER_PHONE, null);
		return value;
	}

	public void saveUserEmail(String phone) {
		editor.putString(USER_EMAIL, phone);
		editor.commit();
	}

	public String getUserEmail() {
		String value = pref.getString(USER_EMAIL, null);
		return value;
	}

	public void saveDisplayName(String name) {
		editor.putString(USER_DISPLAY_NAME, name);
		editor.commit();
	}
	public String getUserServerName(String userName) {
		String value = userName;
		try{
		value = pref.getString(USER_DISPLAY_NAME+userName, userName);
		}catch(Exception e){}
		return value;
	}
	public void saveUserServerName(String userName , String name) {
		editor.putString(USER_DISPLAY_NAME+userName, name);
		editor.commit();
	}
	public void saveUserStatusMessage(String userName , String status) {
		if(status!=null && status.contains("ESIA"))
			status = status.replace("ESIA", "Super");
		editor.putString(USER_STATUS_MESSAGE+userName, status);
		editor.commit();
	}
	public void saveUserName(String name) {
		editor.putString(USER_NAME_ID, name);
		editor.commit();
	}
	public void saveUserDomain(String domain) {
		editor.putString(USER_DOMAIN, domain);
		editor.commit();
	}

	public String getUserDomain() {
		String value = pref.getString(USER_DOMAIN, "");
		return value;
	}
	public void saveUserOrgName(String org_name) {
		editor.putString(USER_ORG_NAME, org_name);
		editor.commit();
	}
	
	public String getUserOrgName() {
		String value = pref.getString(USER_ORG_NAME, "");
		return value;
	}
	public void saveUserId(long id) {
		editor.putLong(USER_ID, id);
		editor.commit();
	}

	public long getUserId() {
		long value = pref.getLong(USER_ID, 0);
		return value;
	}
	public String getGroupDisplayName(String room) {
		String value = room;
		try{
		value = pref.getString("group_display_name_"+room, room);
		}catch(Exception e){}
		return value;
	}
	public void saveGroupDisplayName(String room,String displayName) {
		editor.putString("group_display_name_"+room, displayName);
		editor.commit();
	}
	public void removeGroupDisplayName(String room) {
		editor.remove("group_display_name_"+room);
		editor.commit();
	}
	public void saveUserVarified(boolean isVarified) {
		editor.putBoolean(USER_VARIFIED, isVarified);
		editor.commit();
	}

	public void saveUserLogedOut(boolean isLogOut) {
		editor.putBoolean(USER_LOGED_OUT, isLogOut);
		editor.commit();
	}

	public void saveChatCounter(int counter) {
		editor.putInt(CHAT_COUNTER, counter);
		editor.commit();
	}

	public void saveChatCountOfUser(String person, int counter) {
		editor.putInt(person, counter);
		editor.commit();
	}

	public void saveUserTypingStatus(String person, boolean status) {
		editor.putBoolean(person + "_typing", status);
		editor.commit();
	}

	public boolean getUserTypingStatus(String person) {
		boolean value = pref.getBoolean(person + "_typing", false);
		return value;
	}

	public boolean getUserVarified() {
		boolean value = pref.getBoolean(USER_VARIFIED, false);
		return value;
	}

	public void saveUserEPR(boolean isEpr) {
		editor.putBoolean(USER_EPR_COMPLETE, isEpr);
		editor.commit();
	}

	public boolean getUserEPRCompleted() {
		boolean value = pref.getBoolean(USER_EPR_COMPLETE, false);
		return value;
	}
	public void saveDeviceToken(String token) {
		editor.putString(DEVICE_TOKEN, token);
		editor.commit();
	}
	
	public String getDeviceToken() {
		String value = pref.getString(DEVICE_TOKEN, null);
		return value;
	}
     //=============================================
	// Date :- 07-05-2016
	// Name :- Manoj Singh
	public void saveHelpScreenTimeline(boolean token) {
		editor.putBoolean(SHOW_HELP_SCREEN_TIMELINE, token);
		editor.commit();
	}
	
	public boolean getHelpScreenTimeline() {
		boolean value = pref.getBoolean(SHOW_HELP_SCREEN_TIMELINE, false);
		return value;
	}
	
	public void saveHelpScreenChat(boolean token) {
		editor.putBoolean(SHOW_HELP_SCREEN_CHAT, token);
		editor.commit();
	}
	
	public boolean getHelpScreenChat() {
		boolean value = pref.getBoolean(SHOW_HELP_SCREEN_CHAT, false);
		return value;
	}
	
	

	//=========================================
	public boolean getUserLogedOut() {
		boolean value = pref.getBoolean(USER_LOGED_OUT, true);
		return value;
	}

	public int getChatCounter() {
		int value = pref.getInt(CHAT_COUNTER, 0);
		return value;
	}

	public int getChatCountOfUser(String person) {
		int value = pref.getInt(person, 0);
		return value;
	}

	public void clearSharedPref() {
		editor.clear();
		editor.commit();
	}

	/*
	 * public void UserDataLogout(){ editor.remove(FIRST_TIME_APP);
	 * editor.remove(USER_ID); editor.remove(SIP_SERVER);
	 * editor.remove(USER_SIP_PASSWORD); editor.remove(USER_PASSWORD);
	 * editor.remove(USER_NAME_ID); editor.remove(USER_NAME);
	 * editor.remove(AUTH_STATUS); editor.remove(USER_PHONE);
	 * editor.remove(USER_EMAIL); editor.remove(USER_VARIFIED);
	 * editor.remove(USER_EPR_COMPLETE); editor.remove(USER_LOGED_OUT);
	 * editor.commit(); }
	 */
}
