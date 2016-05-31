package com.kainat.app.android.bean;


public class User {

	public String LOGIN_ROCKETALK = "ROCKETALK";
	public String LOGIN_FACEBOOK = "FACEBOOK";
	public String LOGIN_GOOGLE_PLUS = "GOOGLE_PLUS";
	public String LOGIN_TWITER = "TWITER";
	
	public String LOGIN_ROCKETALK_SHORT_NAME = "rt";
	public String LOGIN_FACEBOOK_SHORT_NAME = "fb";
	public String LOGIN_GOOGLE_PLUS_SHORT_NAME = "gp";
	public String LOGIN_TWITER_SHORT_NAME = "tw";

	private static User self;

	public String LOGIN_VIA;
	public String userId;
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNameL() {
		return userNameL;
	}

	public void setUserNameL(String userNameL) {
		this.userNameL = userNameL;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String name;
	public String firstName;
	public String lastName;
	public String middleName;
	public String link;
	public String userName;
	public String userNameL;
	public String password;
	public String birthday;
	public String locationId;
	public String locationName;
	public String homeTownId;
	public String homeTownName;
	public String locale;
	public String country;
	public String verified;
	public String gender;
	public String timezone;
	public String email;
	public String displayName;// gp
	public String isPlusUser;// gp
	public String objectType;// gp
	public String kind;// gp
	public String etag;// gp
	public String url;// gp
	public String profilePic;
	public String profilePicAbsPath;
	public boolean multipleAccountOnRT;
	
	public static User getInstance() {
		if (self == null) {
			self = new User();
		}
		return self;
	}

	public void clean() {
		LOGIN_VIA=null;
		userId=null;
		name=null;
		firstName=null;
		lastName=null;
		middleName=null;
		link=null;
		userName=null;
		password=null;
		birthday=null;
		locationId=null;
		locationName=null;
		homeTownId=null;
		homeTownName=null;
		locale=null;
		verified=null;
		gender=null;
		timezone=null;
		email=null;
		displayName=null;
		isPlusUser=null;
		objectType=null;
		kind=null;
		etag=null;
		url=null;
		profilePic=null;
		profilePicAbsPath=null;
		multipleAccountOnRT=false;
	}

	@Override
	public String toString() {
		return "LOGIN_VIA:" + LOGIN_VIA + "\n" + "userId:" + userId + "\n"
				+ "firstName:" + firstName + "\n" + "lastName:" + lastName
				+ "\n" + "middleName:" + middleName + "\n" + "email:" + email
				+ "\n" + "displayName:" + displayName + "\n" + "link:" + link
				+ "\n" + "userName:" + userName + "\n" + "birthday:" + birthday
				+ "\n" + "locationId:" + locationId + "\n" + "locationName:"
				+ locationName + "\n" + "homeTownId:" + homeTownId + "\n"
				+ "homeTownName:" + homeTownName + "\n" + "locale:" + locale
				+ "\n" + "verified:" + verified + "\n" + "gender:" + gender
				+ "\n" + "timezone:" + timezone + "\n"+"profilePic:"+profilePic;
	}
}
