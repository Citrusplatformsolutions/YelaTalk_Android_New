package com.kainat.app.android.util;


public class Urls {
	
	public static String ADD_KEY = "rtandroid748";

	public static byte SERVER_TYPE_LOCAL = 0;
	public static byte SERVER_TYPE_LIVE = 1;
	public static byte SERVER_TYPE_STAGING = 2;
	public static byte SERVER_TYPE_DEV = 3;
	public static byte SERVER_TYPE_168 = 4;
	public static byte SERVER_TYPE_174 = 5;
	public static byte SERVER_TYPE_BAKRI = 6;
	public static byte SERVER_TYPE_ROCKETALK = 7;
	

	public static String TEJAS_HOST;
	public static String ADD_AUTH;
	public static String WAP_HOST;
	public static String COM_TERM_AND_SERVICES;
	public static String SERVER_MAIN_ADDRESS;
	public static String SERVER_FALLBACK_ADDRESS;
	public static String GET_COUNTRY_CODE = "http://ip-api.com/json";

	public static byte SERVER = SERVER_TYPE_LIVE;
	public static String URL_LIVE = "yelatalkprod.com";
	public static String URL_DEV = "54.164.75.109:8080";
//	public static byte SERVER = SERVER_TYPE_DEV;
	
	static {
		if(Constants.isBuildLive)
			SERVER = SERVER_TYPE_LIVE;
		else
			SERVER = SERVER_TYPE_DEV;
		
		if (SERVER == SERVER_TYPE_LOCAL) {
			TEJAS_HOST = "192.168.1.13:18080";
			SERVER_MAIN_ADDRESS = "http://"+TEJAS_HOST+"/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://"+TEJAS_HOST+"/rocketalk/im";
			
			ADD_AUTH = "http://advt.rocketalk.com/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android.rocketalk.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST
					+ "/mypage/termsandc/comservices";
			
		} 
		else if (SERVER == SERVER_TYPE_LIVE) {
			//Live server
			TEJAS_HOST = URL_LIVE;
			
			SERVER_MAIN_ADDRESS = "http://"+TEJAS_HOST+"/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://"+TEJAS_HOST+"/rocketalk/im";
			
			ADD_AUTH = "http://advt.rocketalk.com/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android.rocketalk.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST + "/mypage/termsandc/comservices";
		} 
		else if (SERVER == SERVER_TYPE_DEV) {
			//Dev server
			TEJAS_HOST = URL_DEV;
			
			SERVER_MAIN_ADDRESS = "http://"+TEJAS_HOST+"/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://"+TEJAS_HOST+"/rocketalk/im";
			
			ADD_AUTH = "http://advt.rocketalk.com/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android.rocketalk.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST + "/mypage/termsandc/comservices";
		} 
		else if (SERVER == SERVER_TYPE_ROCKETALK) {
			SERVER_MAIN_ADDRESS = "http://app00.rocketalk-production.com/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://app02.rocketalk-production.com/rocketalk/im";
			TEJAS_HOST = "api.rocketalk-production.com";
			ADD_AUTH = "http://advt.rocketalk.com/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android.rocketalk.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST
					+ "/mypage/termsandc/comservices";

		}else if (SERVER == SERVER_TYPE_STAGING) {
			SERVER_MAIN_ADDRESS = "http://124.153.95.132/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://124.153.95.132/rocketalk/im";
			TEJAS_HOST = "124.153.95.129";
			ADD_AUTH = "http://advt.rocketalk.com/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android-stag.onetouchstar.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST
					+ "/mypage/termsandc/comservices";
		} else if (SERVER == SERVER_TYPE_168) {
			SERVER_MAIN_ADDRESS = "http://124.153.95.168:9088/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://124.153.95.168:9088/rocketalk/im";
			TEJAS_HOST = "124.153.95.168:28080";
			ADD_AUTH = "http://" + TEJAS_HOST
					+ "/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android-rtd5.onetouchstar.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST
					+ "/mypage/termsandc/comservices";
		} else if (SERVER == SERVER_TYPE_174) {
			SERVER_MAIN_ADDRESS = "http://124.153.95.174:8088/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://124.153.95.174:8088/rocketalk/im";
			TEJAS_HOST = "124.153.95.174:28080";
			ADD_AUTH = "http://" + TEJAS_HOST
					+ "/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android-rtd5.onetouchstar.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST
					+ "/mypage/termsandc/comservices";
		}else if (SERVER == SERVER_TYPE_BAKRI) {
			SERVER_MAIN_ADDRESS = "http://app11.rocketalk.com/rocketalk/im";
			SERVER_FALLBACK_ADDRESS = "http://app12.rocketalk.com/rocketalk/im";
			TEJAS_HOST = "tejas11.rocketalk.com";
			ADD_AUTH = "http://" + TEJAS_HOST
					+ "/tejas/feeds/api/advt/getAuth?";
			WAP_HOST = "android11.rocketalk.com";
			COM_TERM_AND_SERVICES = "http://" + WAP_HOST
					+ "/mypage/termsandc/comservices";
		}
	}

}
