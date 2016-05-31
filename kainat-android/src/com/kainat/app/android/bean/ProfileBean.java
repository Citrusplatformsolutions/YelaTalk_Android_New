package com.kainat.app.android.bean;

import java.util.Vector;

public class ProfileBean {
	public String userId;

	public String username;
	public String firstName;
	public String lastName;
	public String diaplayName;
	public String address = "";
	public String city = "";
	public String baseCity = "";

	public String state = "";
	public String zip = "";
	public String country = "";
	public String channelCount = "";
	public String getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(String channelCount) {
		this.channelCount = channelCount;
	}

	public String gender;
	public String timeLastUpdate;
	public String birthday = "16";
	public String numberOfBuddies;
	public String numberOfPosts;
	public String numberOfMediaFollowers;
	public String numberOfMediaFollowing;
	public String profile_text = "";
	public String currentStatusText = "";
	public String currentStatusDate = "";
	

	public String status_text;

	public String mobileNumber;
	public boolean isMobileNumberVerified;
	public String email;
	public boolean isEmailVerified;
	public String language = "";
	public boolean isSecurityQuestionSet;
	public String isFriend;
	public boolean isBlocked;
	public boolean isMediaFollower;
	public String followURL;

	
	
	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String getCurrentStatusText() {
		return currentStatusText;
	}

	public void setCurrentStatusText(String currentStatusText) {
		this.currentStatusText = currentStatusText;
	}

	public String getCurrentStatusDate() {
		return currentStatusDate;
	}

	public void setCurrentStatusDate(String currentStatusDate) {
		this.currentStatusDate = currentStatusDate;
	}
	
	public String isFriend() {
		return isFriend;
	}

	public void setFriend(String isFriend) {
		this.isFriend = isFriend;
	}

	public boolean isMediaFollower() {
		return isMediaFollower;
	}

	public void setMediaFollower(boolean isMediaFollower) {
		this.isMediaFollower = isMediaFollower;
	}

	public String getFollowURL() {
		return followURL;
	}

	public void setFollowURL(String followURL) {
		this.followURL = followURL;
	}

	public Vector<Content> pictureMediaList;
	public Vector<Content> audioMediaList;
	public Vector<Content> videoMediaList;
	public Vector<Interest> interest;

	public Vector<Content> pictureMediaListStatus;
	public Vector<Content> audioMediaListStatus;
	public Vector<Content> videoMediaListStatus;

	public String getDisplayName() {
		if ((firstName == null || firstName.trim().length()<=0) && (lastName == null  || lastName.trim().length()<=0))
			return username ;
		if (firstName != null && lastName != null)
			return firstName + " " + lastName;
		else if (firstName != null && lastName == null)
			return firstName;
		else 
			return username ;
	}

	public String getGender() {
		//		if (gender != null && gender.trim().equalsIgnoreCase("m"))
		//			return "Male";
		//		else
		//			return "Female";

		return gender;

	}

	public String getAge() {
		return birthday;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	public String getAddress() {
		return country;
	}

	public String getStatustext() {

		return status_text;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTimeLastUpdate() {
		return timeLastUpdate;
	}

	public void setTimeLastUpdate(String timeLastUpdate) {
		this.timeLastUpdate = timeLastUpdate;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getNumberOfBuddies() {
		if (numberOfBuddies == null)
			return "0";
		return numberOfBuddies;
	}

	public void setNumberOfBuddies(String numberOfBuddies) {
		this.numberOfBuddies = numberOfBuddies;
	}

	public String getNumberOfPosts() {
		if (numberOfPosts == null)
			return "0";
		return numberOfPosts;
	}

	public void setNumberOfPosts(String numberOfPosts) {
		this.numberOfPosts = numberOfPosts;
	}

	public String getNumberOfMediaFollowers() {
		if (numberOfMediaFollowers == null)
			return "0";
		return numberOfMediaFollowers;
	}

	public void setNumberOfMediaFollowers(String numberOfMediaFollowers) {
		this.numberOfMediaFollowers = numberOfMediaFollowers;
	}

	public String getNumberOfMediaFollowing() {
		if (numberOfMediaFollowing == null)
			return "0";
		return numberOfMediaFollowing;
	}

	public void setNumberOfMediaFollowing(String numberOfMediaFollowing) {
		this.numberOfMediaFollowing = numberOfMediaFollowing;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public boolean isMobileNumberVerified() {
		return isMobileNumberVerified;
	}

	public void isMobileNumberVerified(boolean isMobileNumberVerified) {
		this.isMobileNumberVerified = isMobileNumberVerified;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailVerified() {
		return isEmailVerified;
	}

	public void setIsEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isSecurityQuestionSet() {
		return isSecurityQuestionSet;
	}

	public void isSecurityQuestionSet(boolean isSecurityQuestionSet) {
		this.isSecurityQuestionSet = isSecurityQuestionSet;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBaseCity() {
		return city;
	}

	public void setBaseCity(String baseCity) {
		this.baseCity = baseCity;
	}

	public String getProfile_text() {
		return profile_text;
	}

	public void setProfile_text(String profile_text) {
		this.profile_text = profile_text;
	}

	public String getStatus_text() {
		return status_text;
	}

	public void setStatus_text(String status_text) {
		this.status_text = status_text;
	}

	public String getCountyCity() {
		if (city != null && city.trim().length() > 0 && country != null
				&& country.trim().length() > 0)
			return " " +getCity() + " (" + getCountry() + ")";
		else if (country != null && country.trim().length() > 0)
			return " " +country;
		else if (city != null && city.trim().length() > 0)
			return  " " +city;

		return "";
	}
}
