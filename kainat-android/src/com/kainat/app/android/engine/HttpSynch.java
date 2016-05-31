package com.kainat.app.android.engine;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.format.SettingData;

public class HttpSynch {

	
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	private HttpSynchInf httpSynchInf;
	private static HttpSynch httpSynch ;
	private boolean isCancel = false ;
	public int requestForID ;
	private int STATE_RUNNING = 1 ;
	private int STATE_FREE = 2 ;
	private int STATE = STATE_FREE ;
	int clientType = 1 ;
	private HttpSynch(){
			
	}
	public static HttpSynch getInstance(){
		if(httpSynch==null)
			httpSynch = new HttpSynch();			
		return httpSynch;
	}
	
	public void cancel(){
		isCancel = true ;
	}
	public void setHttpSynch(HttpSynchInf httpSynchInf) {
		this.httpSynchInf = httpSynchInf;
	}

	public void request(String urlStr,final Hashtable<String, String> heder, String method,int requestForID) {
		isCancel = false ;
		request(urlStr, null, heder, method, requestForID);
	}
	public void request(String urlStr, String method,int requestForID,int clientType) {
		this.clientType =clientType;
		request(urlStr, null, null, method,requestForID);
	}
	public void request(String urlStr, String method,int requestForID) {
		request(urlStr, null, null, method,requestForID);
	}

	public void request(String urlStr,
			final Hashtable<String, String> postParams,
			final Hashtable<String, String> heder, String method,int requestForID) {
		isCancel = false ;
		HttpConnectionHelper helper = null ;
		this.requestForID = requestForID ;
//		System.out.println("--------------feed clientType---------- : "+clientType);
		if(SettingData.sSelf.isIm()){
		System.out.println("--------------feed url----------"+urlStr);
		
			System.out.println("Discovery user------RT-APP-KEY------- : "+BusinessProxy.sSelf.getUserId());
		}
//		System.out.println("--------------feed requestForID----------"+requestForID);
//		System.out.println("--------------STATE------------------"+STATE);
		while(STATE==STATE_RUNNING){
			try{
				Thread.sleep(500); 
//				System.out.println("--------------WATING FOR CLOSE CONNECTION------------------"+STATE);
			}catch (Exception e) {				
			}
		}
		try {
			if(httpSynchInf==null)
				throw new Exception("------------------First set http synch listener----HttpSynchListener");
			if(method==null || (!method.equals(METHOD_POST)&&!method.equals(METHOD_GET)))
				throw new Exception("------------------INVALIDE METHOD----method-"+method);
			InputStream is = null;
			int responseCode;
			int connectionTry = 0;
			do{
				if(connectionTry==1){
					Thread.sleep(2000) ;
				}
			 helper = new HttpConnectionHelper(
					urlStr);			 
			helper.setReadTimeout(30);
			helper.setHeader("RT-APP-KEY", HttpHeaderUtils
					.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));
			helper.setHeader("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());
			if(Constants.FEED_INITIAL_DISCOVERY==requestForID||Constants.FEED_INITIAL_LANDING_PAGE==requestForID)
				clientType = 1 ;
			if (clientType == 0) 
			{
				helper.setHeader("RT-APP-KEY", ""
						+ BusinessProxy.sSelf.getUserId());	
				 
				
			} else {
				String appKey = HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword());
				helper.setHeader("RT-APP-KEY", appKey);
			
				/*String val = BusinessProxy.sSelf
						.getUserId() +":" + SettingData.sSelf
						.getPassword();
				helper.setHeader(
						"RT-APP-KEY",
						val);*/
//				System.out.println("val------"+val);
				
//				System.out.println("Discovery user------RT-APP-KEY------- : "+appKey);
				
			}
//			helper.setHeader("RT-DEV-KEY",
//					"" + BusinessProxy.sSelf.getClientId());
			if(heder!=null)
			for (Iterator<String> iterator = heder.keySet().iterator(); iterator
					.hasNext();) 
			{
				String key = iterator.next();
				String value = heder.get(key);
				helper.setHeader(key, value);
			}
			if(postParams!=null)
			for (Iterator<String> iterator = postParams.keySet().iterator(); iterator
			.hasNext();) 
			{
				String key = iterator.next();
				String value = postParams.get(key);
				helper.setHeader(key, value);
			}
			responseCode = helper.getResponseCode();
			connectionTry++;
			}while(responseCode!=200 && connectionTry < 2);
			if (responseCode != 200) {
				throw new Exception(String.format(
						"Received the response code %d from the URL %s",
						responseCode, urlStr));
			}

			// Read the response
			is = helper.getInputStream();
			if(this.httpSynchInf!=null&&!isCancel)
			this.httpSynchInf.onResponseSucess(IOUtils.read(is),requestForID);
			// return IOUtils.read(is);
		} catch (Exception e) {
			if(this.httpSynchInf!=null&&!isCancel)
			this.httpSynchInf.onError(e.getMessage());	
			this.httpSynchInf.onError(e.getMessage(),requestForID);	
		}
		finally{
			helper.closeConnection();
			STATE=STATE_FREE ;
			this.clientType =0;
		}
	}	
}
