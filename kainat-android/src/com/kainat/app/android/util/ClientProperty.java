package com.kainat.app.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.kainat.app.android.R;

public final class ClientProperty {

	private static boolean sIsPropertyLoaded = false;
	private Properties mPropertyLoader;

	// ////////////////////////////////////////////////////////////////////////
	public static final String SERVER_MAIN_ADDRESS = "server.address.main";
	public static final String SERVER_FALLBACK_ADDRESS = "server.address.fallback";
	public static final String CLIENT_VERSION = "mobile.client.version";
	public static final String APPLICAITON_VERSION = "application.version";
	public static final String PLATFORM_NAME = "mobile.platform.name";
	public static final String VENDOR_NAME = "mobile.platform.vendor";
	public static final String DISTRIBUTOR_NAME = "mobile.platform.distributor";
	//int[] constCamcorderProfile ={   CamcorderProfile.QUALITY_HIGH,   CamcorderProfile.QUALITY_LOW};
	public static String RT_PARAMS,CLIENT_PARAMS;
	public String model=null;
	public String clientVersion = "yelatalk_android_";
	/**
	 * @throws Exception
	 */
	public ClientProperty(final Context context) throws IOException {
//		if (!sIsPropertyLoaded) 
		{
			try {
				 String operatorName=null;
				 String operatorSimName=null;
				 String CountryISOCode=null;
				 String mobileNumber=null;
				 String simSerialNumber=null;
					TelephonyManager telephonyManager =((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
					if(telephonyManager!=null){
					  operatorName = telephonyManager.getNetworkOperatorName();
				      operatorSimName = telephonyManager.getSimOperatorName();
				      CountryISOCode = telephonyManager.getNetworkCountryIso();
				      mobileNumber=telephonyManager.getLine1Number();
				      simSerialNumber=telephonyManager.getSimSerialNumber();
					 
			}
					//Camcorder camcorderProfile 
					//	= Camcorder.get(constCamcorderProfile[0]);
					//String profileInfo; 
					//  profileInfo = camcorderProfile.toString() + "\n";   profileInfo += "\n" + "audioBitRate: "     + String.valueOf(camcorderProfile.audioBitRate)     + "\n" + "audioChannels: "     + String.valueOf(camcorderProfile.audioChannels)     + "\n" + "audioCodec: "     + getAudioCodec(ca.audioCodec)     + "\n" + "audioSampleRate: "     + String.valueOf(camcorderProfile.audioSampleRate)     + "\n" + "duration: "     + String.valueOf(camcorderProfile.duration)     + "\n" + "fileFormat: "     + getFileFormat(camcorderProfile.fileFormat)     + "\n" + "quality: "     + String.valueOf(camcorderProfile.quality)     + "\n" + "videoBitRate: "     + String.valueOf(camcorderProfile.videoBitRate)     + "\n" + "videoCodec: "     + getVideoCodec(camcorderProfile.videoCodec)     + "\n" + "videoFrameRate: "     + String.valueOf(camcorderProfile.videoFrameRate)     + "\n" + "videoFrameWidth: "     + String.valueOf(camcorderProfile.videoFrameWidth)     + "\n" + "videoFrameHeight: "     + String.valueOf(camcorderProfile.videoFrameHeight);   
				/*	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					boolean isGPS = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
					if(!isGPS){
						//
						DialogInterface.OnClickListener handler = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								new Thread(new Runnable() {
									public void run() {
										startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
									}
								}).start();
							}
						};
						showAlertMessage("RockeTalk", "Please enable Gps to use location.", new int[] { DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEUTRAL },
								new String[] { "GPS Setting", "Cancel" }, new DialogInterface.OnClickListener[] { handler, null });
						return ;
					}*/
					InputStream input = context.getResources().openRawResource(R.raw.application);
					mPropertyLoader = new Properties();
					loadProperties(input);	
					//imei##593bea58f28c4d9f8246a045cfe266d5;ERROR::timezone##+05:30::vendor##TOMO;ROCKETALK::platformtype##IPHONE::clientversion##RT_IPHONE_320_460V7_4_1::mobilemodel##iPhone Simulator::creationTime##sunnykit_6.0_261804967_0
					
					TimeZone time = TimeZone.getDefault();
					String gmt1=TimeZone.getTimeZone(time.getID()).getDisplayName(false,TimeZone.SHORT);
					CLIENT_PARAMS = "imei##"+Utilities.getPhoneIMEINumber()+"::" ;
					CLIENT_PARAMS = CLIENT_PARAMS+"timezone##"+gmt1+"::";
					
					CLIENT_PARAMS = CLIENT_PARAMS+"vendor##"+getProperty(ClientProperty.VENDOR_NAME)+"::";
					CLIENT_PARAMS = CLIENT_PARAMS+"distributor##"+getProperty(ClientProperty.DISTRIBUTOR_NAME)+"::";
					CLIENT_PARAMS = CLIENT_PARAMS+"platformtype##"+getProperty(ClientProperty.PLATFORM_NAME)+"::";
					try {
						String manifest_version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
						if(manifest_version.indexOf('.') != -1)
							manifest_version = manifest_version.replace('.', '_');
						clientVersion = clientVersion + manifest_version;
						CLIENT_PARAMS = CLIENT_PARAMS+"clientversion##"+clientVersion+"::";
					} catch (NameNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						CLIENT_PARAMS = CLIENT_PARAMS+"distributor##"+getProperty(ClientProperty.CLIENT_VERSION)+"::";
					}
					do
					{
						model = Build.MODEL;
						try{
						if(model == null);{
							Thread.sleep(500);
						}
						}catch (Exception e) {
						}
					}while(model == null);
					
//					if(BusinessProxy.sSelf != null)
//						Utilities.sRegId = BusinessProxy.sSelf.getPushRegId(DBEngine.PUSH_TABLE);
					
					CLIENT_PARAMS = CLIENT_PARAMS+"momodel##"+model+"::";
					CLIENT_PARAMS = CLIENT_PARAMS+"locale##"+"en_US"+"::";// //Locale.getDefault()
					CLIENT_PARAMS = CLIENT_PARAMS+"token##"+Utilities.sRegId+"::";
					
				//	Utilities.getPhoneIMEINumber();
				StringBuilder builder = new StringBuilder("PLATFORM##");
				//Add platfrom and Client version
				builder.append(getProperty(ClientProperty.PLATFORM_NAME));
				builder.append("::");
				builder.append("IMEI##");
				builder.append(Utilities.getPhoneIMEINumberMethod(context));
				builder.append("::");
				builder.append(GPSInfo.getInstance(context).toString());
				builder.append("::");
				builder.append(BuildInfo.getInstance(context).toString());
				builder.append("::");
				builder.append(DisplayInfo.getInstance(context).toString());
				builder.append("::");
				builder.append(CameraInfo.getInstance(context).toString());
				builder.append("::");
			    builder.append("video##"+"3gp,"+"mp4");
			    
				RT_PARAMS =  builder.toString();
				
				//InputStream input = context.getResources().openRawResource(R.raw.application);
				//mPropertyLoader = new Properties();
				//loadProperties(input);
			} catch (IOException ex) {
				if (Logger.ENABLE) {
					Logger.error(getClass().getSimpleName(), "--ClientProperty--ERROR LOADING PROPERTIES", ex);
				}
				throw ex;
			}
		}
	}

	private void loadProperties(InputStream input) throws IOException {
		mPropertyLoader.load(input);
		sIsPropertyLoaded = true;
	}

	public String getProperty(String key) {
		
		if(key.equalsIgnoreCase(ClientProperty.SERVER_MAIN_ADDRESS)){
			return Urls.SERVER_MAIN_ADDRESS;
		}
		if(key.equalsIgnoreCase(ClientProperty.SERVER_FALLBACK_ADDRESS)){
			return Urls.SERVER_FALLBACK_ADDRESS;
		}
		String value = mPropertyLoader.getProperty(key);
		if (null == value) {
			value = getDefaultValueForKey(key);
		}
		return value;
	}
	public void putProperty(String key,String value) {
		 mPropertyLoader.put(key, value) ;
	
	}

	public int getPropertyInt(String key) {
		String value = mPropertyLoader.getProperty(key);
		if (null == value) {
			value = getDefaultValueForKey(key);
			if (null == value || 0 == value.trim().length()) {
				value = "-1";
			}
		}
		return Integer.parseInt(value);
	}

	
	/* private String getAudioCodec(int audioCodec){ 
		 switch(audioCodec){  
		 case AudioEncoder.AMR_NB:    
			 return "AMR_NB";   
			 case AudioEncoder.DEFAULT:  
				 return "DEFAULT";   
				 default:    
					 return "unknown";  
					 }   
		 }



	 private String getFileFormat(int fileFormat)
	 { 
		 switch(fileFormat){   
		 case OutputFormat.MPEG_4:   
			 return "MPEG_4";  
			 case OutputFormat.RAW_AMR:  
				 return "RAW_AMR"; 
				 case OutputFormat.THREE_GPP:   
					 return "THREE_GPP";   
					 case OutputFormat.DEFAULT:   
						 return "DEFAULT";  
						 default:    
							 return "unknown"; 
							 }   
		 }





	 private String getVideoCodec(int videoCodec){  
		 switch(videoCodec){   
		 case VideoEncoder.H263:   
			 return "H263";  
			 case VideoEncoder.H264:  
				 return "H264";  
				 case VideoEncoder.MPEG_4_SP:   
					 return "MPEG_4_SP";   
					 case VideoEncoder.DEFAULT:  
						 return "DEFAULT";  
						 default:   
							 return "unknown";  }   }*/
		
	/**
	 * @param key
	 * @return
	 */
	private String getDefaultValueForKey(String key) {
		// TODO - put default value for all the properties
		String retValue = "";
		if (SERVER_MAIN_ADDRESS.equals(key)) {
			retValue = "";
		} else if (SERVER_FALLBACK_ADDRESS.equals(key)) {
			retValue = "";
		} else if (APPLICAITON_VERSION.equals(key)) {
			retValue = "Version 1.0 (Beta)";
		} else if (PLATFORM_NAME.equals(key)) {
			retValue = "J2ME";
		} else if (VENDOR_NAME.equals(key)) {
			retValue = "TOMO";
		} else if (DISTRIBUTOR_NAME.equals(key)) {
			retValue = "RockeTalk";
		}
		return retValue;
	}

}
