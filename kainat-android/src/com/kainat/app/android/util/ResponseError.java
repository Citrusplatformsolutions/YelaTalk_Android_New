package com.kainat.app.android.util;

public class ResponseError {
	
	public static final String STATUS_ERROR = "error" ;
	
	public static final String USER_SERVICE_FAILED_EXCEPTION = "RT_ERR_10101" ;
	public static final String MULTIPLE_ACCOUNT_EXCEPTION = "RT_ERR_10102" ;
	public static final String USER_NOT_PRESENT_IN_SYSTEM = "RT_ERR_10103" ;
	public static final String USER_STATUS_BLOCKED = "RT_ERR_10104" ;
	public static final String USER_STATUS_FAKE = "RT_ERR_10105" ;
	public static final String USER_STATUS_INVALID = "RT_ERR_10106" ;
	public static final String USER_STATUS_INACTIVE = "RT_ERR_10107" ;
	public static final String MODE_MISSING = "RT_VERR_21030" ;
	public static final String EMAIL_ID_IN_USE = "RT_VERR_21031" ;
	public static final String PASSWORD_AUTHENTICATION_REQUIRED = "RT_ERR_10108" ;
	public static final String AUTHENTICATION_FAILED = "RT_ERR_10007" ;
	public static final String INVALID_CHAR = "RT_VERR_21042" ;
	
	public static final String NOT_CONNECTED = "NOT_CONNECTED" ;
	
	public static final String _NOT_VERIFIED = "{\"message\":\"Account not verified.\",\"status\":\"error\",\"code\":\"NOT_CONNECTED\"}";
	public static final String _404 = "{\"message\":\"Oops, Kainat is having trouble getting connected. Please try again or try after some time.\",\"status\":\"error\",\"code\":\"NOT_CONNECTED\"}";
	public static final String _404_GP = "{\"message\":\" Check your G+ account details. It seems to be blocked.\",\"status\":\"error\",\"code\":\"NOT_CONNECTED\"}";
	public static final String _Nothing_to_save = "{\"desc\":\"No new data found for update\",\"status\":\"2\"}";
//	09-23 11:51:56.849: I/System.out(25098): -----response:{"desc":"No data found for profile update","status":"2"}

}
