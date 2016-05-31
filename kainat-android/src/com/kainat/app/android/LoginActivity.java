package com.kainat.app.android;

import java.util.Hashtable;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.User;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.Auth;
import com.kainat.app.android.inf.AuthInf;
import com.kainat.app.android.util.AdConnectionManager;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.RTDialog;
import com.kainat.app.android.util.RequestType;
import com.kainat.app.android.util.ResponseError;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;

public class LoginActivity extends UIActivityManager implements OnClickListener,AuthInf {

	Handler handler = new Handler();
	Spinner spinner;
	boolean select = false;
	LoginActivity mContext ;
	RTDialog rTDialog ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this ;
		
//		Utilities.cleanDB(this);
		
		
		setContentView(R.layout.login_activity);
		TextView tv = (TextView)findViewById(R.id.take_tour);
		tv.setOnClickListener(this) ;
		
		tv = (TextView)findViewById(R.id.join);
		tv.setOnClickListener(this) ;
		
		findViewById(R.login.login_fb).setOnClickListener(this) ;
		
		
		findViewById(R.id.login).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SettingData.sSelf.setUserName(((TextView)findViewById(R.id.username)).getText().toString()) ;
				SettingData.sSelf.setPassword(((TextView)findViewById(R.id.pass)).getText().toString()) ;
				SettingData.sSelf.setEmail(null);//((TextView)findViewById(R.id.username)).toString()) ;
				
				User.getInstance().userName  = SettingData.sSelf.getUserName() ;
				User.getInstance().password  = SettingData.sSelf.getPassword();
				User.getInstance().email  = SettingData.sSelf.getEmail();
				
				
				new AutoLogin().execute("");
			}
		}) ;
		findViewById(R.id.logo).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				findViewById(R.id.rt_login).setVisibility(View.VISIBLE) ;
			}
		}) ;
		// Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication())
						.getTracker(TrackerName.APP_TRACKER);
				t.setScreenName("Login Screen");
				t.set("&uid",""+BusinessProxy.sSelf.getUserId());
				t.send(new HitBuilders.AppViewBuilder().build());
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		 finish();
		 Intent intent = new Intent(LoginActivity.this,LanguageActivity.class) ;
		 startActivity(intent) ;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.terms_condition_link:
			Intent intent = new Intent(LoginActivity.this, CommunityWebViewActivity.class);
			startActivity(intent);
			break;
		case R.id.take_tour:
//			finish();
			 intent = new Intent(LoginActivity.this,
					TourActivity.class);
			startActivity(intent);
			break;
			
		case R.id.join:
			 intent = new Intent(LoginActivity.this,
					 VerificationActivity.class);
			startActivity(intent);
			 
			
			break;
		case R.login.login_fb:			
			rTDialog = new RTDialog(this, null,
					getString(R.string.signing_in_fb));
			rTDialog.show();
			Auth.getInstance().setListener(this);
			Auth.getInstance().initFB(this);
			
//			login();
			break;
		}
	}

	public void setLocale(String lang) {

		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
//		Intent refresh = new Intent(this, AndroidLocalize.class);
//		startActivity(refresh);
	}

	@Override
	public void onAuthSucceed() {
		social = new Social();
		social.execute("");
//		AppUtil.showTost(mContext, "onAuthSucceed") ;
	}

	Social social;

	@Override
	public void onAuthFail(String error) {
		// TODO Auto-generated method stub
//		AppUtil.showTost(mContext, "onAuthFail") ;
		closeDialog();
	}

	@Override
	public void onLogoutBegin() {
		// TODO Auto-generated method stub
//		AppUtil.showTost(mContext, "onLogoutBegin") ;
		
	}

	@Override
	public void onLogoutFinish() {
//		AppUtil.showTost(mContext, "onLogoutFinish") ;
		closeDialog();
	}

	private class Social extends AsyncTask<String, Void, String> {

		Bitmap bitmap ;
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			do {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			} while (User.getInstance().userId == null);
			
//			bitmap =AppUtil. getBitmapFromURL(User.getInstance().profilePic) ;
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			AppUtil.showTost(mContext, User.getInstance().userId) ;
			
			closeDialog();			
			
			Intent intent = new Intent(LoginActivity.this,
					VerificationActivity.class);
			startActivity(intent);
			
			super.onPostExecute(result);
			
//			login();
			
			
		}
	}
	
	private void closeDialog(){
		if(rTDialog!=null && rTDialog.isShowing())
			rTDialog.dismiss() ;
	}
	
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStop() {
		closeDialog();
		super.onStop();
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == mContext.RESULT_OK) {
			switch (requestCode) {
			case 32665:
				if (Auth.getInstance().mFacebook != null)
					Auth.getInstance().mFacebook.authorizeCallback(requestCode,
							resultCode, data);
				break;
			}	
		}
	}
	
	
	private class AutoLogin extends AsyncTask<String, Void, String> {

		Bitmap bitmap;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			l();
//			login();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	public void l() {
		try {
			Hashtable<String, String> headerParam = new Hashtable<String, String>();
			headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
			Hashtable<String, String> postParam = new Hashtable<String, String>();
//			SettingData.sSelf.getType()
//			if (SettingData.sSelf.getType() != null
//					&& SettingData.sSelf.getType().equalsIgnoreCase(
//							User.getInstance().LOGIN_FACEBOOK)) 
			{
				User.getInstance().userName  = SettingData.sSelf.getUserName() ;
				User.getInstance().password  = SettingData.sSelf.getPassword();
				User.getInstance().email  = SettingData.sSelf.getEmail();
//				if (1 == 1)
					headerParam.put("client_param",
							ClientProperty.CLIENT_PARAMS + "::requesttype##"
									+ RequestType.LOGIN_FB_AUTO);
//				else if (User.getInstance().multipleAccountOnRT)
//					headerParam.put("client_param",
//							ClientProperty.CLIENT_PARAMS + "::requesttype##"
//									+ RequestType.LOGIN_FB_MULTIPLE);
//				else
//					headerParam.put("client_param",
//							ClientProperty.CLIENT_PARAMS + "::requesttype##"
//									+ RequestType.LOGIN_FB);

				postParam.put("mode",
						User.getInstance().LOGIN_ROCKETALK_SHORT_NAME);// mode=fb
																		// rt
				if(SettingData.sSelf.getAppkey() != null)
					postParam.put("appkey", SettingData.sSelf.getAppkey());
				
				if (SettingData.sSelf.getPartneruserkey() != null)
				postParam.put("partneruserkey",
						SettingData.sSelf.getPartneruserkey());// fb

				if (User.getInstance().userName != null
						&& !checkEmail(User.getInstance().userName)
						&& !checkPhone(User.getInstance().userName)) {
					postParam.put("username", User.getInstance().userName);
					if (User.getInstance().userName != null
							&& User.getInstance().userName.indexOf("~") != -1) {

						String un[] = User.getInstance().userName.split("~");
						postParam.put("fname", un[0]);
						postParam.put("lname", un[1]);
						postParam.put("username", un[0] + " " + un[1]);
					}
				}
				if (User.getInstance().userName != null
						&& checkEmail(User.getInstance().userName)
						&& !checkPhone(User.getInstance().userName))
					postParam.put("emailid", User.getInstance().userName);
				if (User.getInstance().userName != null
						&& !checkEmail(User.getInstance().userName)
						&& checkPhone(User.getInstance().userName))
					postParam.put("phone", User.getInstance().userName);
				if (User.getInstance().password != null)
					postParam.put("password", User.getInstance().password);

				if (User.getInstance().multipleAccountOnRT
						&& (User.getInstance().userName != null && User
								.getInstance().userName.indexOf("~") != -1)) {
					String un[] = User.getInstance().userName.split("~");
					postParam.put("fname", un[0]);
					postParam.put("lname", un[1]);
					postParam.put("username", un[0] + " " + un[1]);
				}

//				System.out.println(postParam.toString());
				String response = AdConnectionManager.getInstance().uploadByteData(
								"http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/user/login",
								postParam, headerParam, null);//
				login(response);
//				Log.d("", response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login(final String response) {

		// if(1==1)return ;

//		final String response = "{\"requesttype\":\"1005\",\"clientId\":\"1659\",\"userId\":7404025,\"lastTransactionId\":6447,\"type\":\"Normal\",\"firstName\":\"nag\",\"lastName\":\"nag\",\"ageGroup\":\"25-34\",\"dob\":\"22/4/1980\",\"city\":\"\",\"country\":\"United Kingdom\",\"state\":\"Delhi\",\"emailId\":\"engjghggghgihg@uuuu.com\",\"gender\":\"F\",\"mobileNumber\":\"9717098526\",\"mobileModel\":\"GT-I9105\",\"userName\":\"Qts\",\"password\":\"aaaaaa\",\"advertisementParam\":\"ageGroup##25-34::platForm##25-34::birthday##22/4/1980::country##United Kingdom::state##Delhi::email##engjghggghgihg@uuuu.com::gender##F::mobile##9717098526::modelModel##GT-I9105\",\"isEmailVerified\":true,\"upgradeType\":0,\"isMobileVerificationNeeded\":false,\"animationData\":{\"animationUrl\":\"http://tejas.rocketalk.com/tejas/feeds/api/animation/getAnimation\",\"animationListUrl\":\"http://tejas.rocketalk.com/tejas/feeds/api/animation/getAnimationList\"},\"buddyTimeStamp\":\"0000-11-16 10:27:13\",\"mediaCategoryList\":[{\"categoryId\":\"102\",\"categoryName\":\"Food \u0026 Drink\"},{\"categoryId\":\"103\",\"categoryName\":\"Movies \u0026 TV\"},{\"categoryId\":\"104\",\"categoryName\":\"Music\"},{\"categoryId\":\"105\",\"categoryName\":\"Sports\"},{\"categoryId\":\"106\",\"categoryName\":\"Nature \u0026 Outdoors\"},{\"categoryId\":\"107\",\"categoryName\":\"Lifestyle tools\"},{\"categoryId\":\"108\",\"categoryName\":\"Travel \u0026 Places\"},{\"categoryId\":\"109\",\"categoryName\":\"Fashion \u0026 Beauty\"},{\"categoryId\":\"110\",\"categoryName\":\"Arts \u0026 Literature\"},{\"categoryId\":\"111\",\"categoryName\":\"How to \u0026 Learning\"},{\"categoryId\":\"112\",\"categoryName\":\"People \u0026 Relationships\"},{\"categoryId\":\"113\",\"categoryName\":\"Religion \u0026 Spirituality\"},{\"categoryId\":\"114\",\"categoryName\":\"News \u0026 Citizen journalism\"},{\"categoryId\":\"115\",\"categoryName\":\"Humour\"},{\"categoryId\":\"116\",\"categoryName\":\"Others\"}]}";
		if (response != null) {
			try {
				final JSONObject jsonObject = new JSONObject(response);

				if (jsonObject.has("status")
						&& jsonObject.getString("status").equalsIgnoreCase(
								ResponseError.STATUS_ERROR)) {
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							try{
								AppUtil.showTost(LoginActivity.this, jsonObject.getString("message")) ;
								closeDialog();
								
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					
					return;
				} else {
					if (jsonObject.has("mediaCategoryList")) {
						JSONArray jSONArray = jsonObject
								.getJSONArray("mediaCategoryList");
						BusinessProxy.sSelf.mediaCategories = new String[jSONArray
								.length()];
						BusinessProxy.sSelf.mediaCategoryIDs = new String[jSONArray
								.length()];

						for (int i = 0; i < jSONArray.length(); i++) {
							JSONObject nameObjectw = jSONArray.getJSONObject(i);

							if (nameObjectw.has("categoryId")) {
								BusinessProxy.sSelf.mediaCategoryIDs[i] = nameObjectw
										.getString("categoryId");
							}
							if (nameObjectw.has("categoryName")) {
								BusinessProxy.sSelf.mediaCategories[i] = nameObjectw
										.getString("categoryName");
							}
						}
						Constants.values = BusinessProxy.sSelf.mediaCategories;
						Constants.valuesId = BusinessProxy.sSelf.mediaCategoryIDs;
					}

//					if (jsonObject.has("advertisementParam")) {
//						BusinessProxy.sSelf.advertisementData.put("userParam",
//								jsonObject.getString("advertisementParam"));
//					}
//
//					if (jsonObject.has("animationURL"))
//						BusinessProxy.sSelf.singleAnimationURL = jsonObject
//								.getString("animationURL");
//
//					if (jsonObject.has("animationListURL"))
//						BusinessProxy.sSelf.animationListURL = jsonObject
//								.getString("animationListURL");

					if (jsonObject.has("clientId"))
						BusinessProxy.sSelf.setClientId(Integer
								.parseInt(jsonObject.getString("clientId")));
//					if (Logger.LOGS_ENABLE_USERID)
//						System.out.println("CURRENT USER ID IS - "
//								+ BusinessProxy.sSelf.getUserId());

					if (jsonObject.has("userId"))
						BusinessProxy.sSelf.setUserId(Integer
								.parseInt(jsonObject.getString("userId")));
//					if (Logger.LOGS_ENABLE_USERID)
//						System.out.println("SETTING USER ID TO - "
//								+ BusinessProxy.sSelf.getUserId());

					if (jsonObject.has("lastTransactionId"))
						BusinessProxy.sSelf.setTransactionId(Integer
								.parseInt(jsonObject
										.getString("lastTransactionId")));

					if (jsonObject.has("lastTransactionId"))
						BusinessProxy.sSelf.setTransactionId(Integer
								.parseInt(jsonObject
										.getString("lastTransactionId")));

					if (jsonObject.has("type"))
						SettingData.sSelf.setType(jsonObject.getString("type"));

					SettingData.sSelf.setType(User.getInstance().LOGIN_VIA);

					if (jsonObject.has("firstName"))
						SettingData.sSelf.setFirstName(jsonObject
								.getString("firstName"));

					if (jsonObject.has("lastName"))
						SettingData.sSelf.setLastName(jsonObject
								.getString("lastName"));

					if (jsonObject.has("lastName"))
						SettingData.sSelf.setLastName(jsonObject
								.getString("lastName"));

					try {
						if (jsonObject.has("isEmailVerified"))
							SettingData.sSelf.setEmailVerified(Boolean
									.parseBoolean(jsonObject
											.getString("isEmailVerified")));

					} catch (Exception e) {
						// TODO: handle exception
					}

					if (jsonObject.has("ageGroup"))
						SettingData.sSelf.setAgeGroup(jsonObject
								.getString("ageGroup"));

					if (jsonObject.has("dob"))
						SettingData.sSelf.setBirthDate(jsonObject
								.getString("dob"));

					if (jsonObject.has("country"))
						SettingData.sSelf.setCountry(jsonObject
								.getString("country"));

					if (jsonObject.has("state"))
						SettingData.sSelf.setState(jsonObject
								.getString("state"));

					if (jsonObject.has("emailId"))
						SettingData.sSelf.setEmail(jsonObject
								.getString("emailId"));

					if (jsonObject.has("mobileNumber")) {
						SettingData.sSelf.setMobile(jsonObject
								.getString("mobileNumber"));
						SettingData.sSelf.setNumber(jsonObject
								.getString("mobileNumber"));
					}

					if (jsonObject.has("mobileModel"))
						SettingData.sSelf.setMobileModel(jsonObject
								.getString("mobileModel"));

					if (jsonObject.has("userName"))
						SettingData.sSelf.setUserName(jsonObject
								.getString("userName"));

					if (jsonObject.has("password"))
						SettingData.sSelf.setPassword(jsonObject
								.getString("password"));

					if (jsonObject.has("buddyTimeStamp"))
						BusinessProxy.sSelf.mBuddyTimeStamp = jsonObject
								.getString("buddyTimeStamp");
					else
						BusinessProxy.sSelf.mBuddyTimeStamp = "1000-00-00 00:00:00.0";

					BusinessProxy.sSelf.mBuddyTimeStamp = "0000-00-00 00:00:00.0";
					BusinessProxy.sSelf.mGroupTimeStamp = "0000-00-00 00:00:00.0";
					BusinessProxy.sSelf.mInboxTimeStamp = "0000-00-00 00:00:00.0";
					
					if (jsonObject.has("upgradeType")) {
						if (aResponse == null)
							aResponse = new ResponseObject();
						aResponse.setUpgradeType(Integer.parseInt(jsonObject
								.getString("upgradeType")));
					}
					if (jsonObject.has("upgradeRTML")) {
						if (aResponse == null)
							aResponse = new ResponseObject();
						String rtml = jsonObject.getString("upgradeRTML");
						rtml = rtml.replace("\u003c", "<");
						rtml = rtml.replace("\u003d", ">");
						aResponse.setUpgradeURL(jsonObject
								.getString("upgradeRTML"));
					}
					if (jsonObject.has("isMobileVerificationNeeded")) {
						if (aResponse == null)
							aResponse = new ResponseObject();
						try {
							aResponse.setMobileVerificationNeeded(Boolean
									.parseBoolean(jsonObject.getString(
											"isMobileVerificationNeeded")
											.trim()));
						} catch (Exception e) {

						}
					}

					try {

						if (Auth.getInstance().mFacebook != null
								&& Utilities.getInt(LoginActivity.this,
										Constants.FIRST_LOGIN) == 0) {
							String Description = "";
							BusinessProxy.sSelf.loginWithFb = true;
						} else if (Auth.getInstance().mFacebook != null) {
							BusinessProxy.sSelf.loginWithFb = true;

						}
						if (aResponse != null
								&& (aResponse.getUpgradeType() <= 0 && !aResponse
										.isMobileVerificationNeeded()))
							aResponse = null;
						if (!BusinessProxy.sSelf.isRegistered())
							handleLoginResponseNew(aResponse);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {

			}
		}
		if (!SettingData.sSelf.isEmailVerified()
				&& BusinessProxy.sSelf.isRegistered()) {
			getIntent().putExtra("password", SettingData.sSelf.getPassword());
			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				onPostExecute(response);
			}
		});

	}
	protected void onPostExecute(String response) {

		if (aResponse == null || aResponse.getUpgradeType() != 1)
			pushNewScreen(KainatHomeActivity.class, false);
		if (aResponse == null || aResponse.getUpgradeType() <= 0) {
			BusinessProxy.sSelf.setNetActive(true);
			BusinessProxy.sSelf.startRefresh();
			// pushNewScreen(KainatHomeActivity.class, false);
		} else if (aResponse != null
				&& (aResponse.getUpgradeType() == 1 || aResponse
						.getUpgradeType() == 2)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
						displayUpgradeScreen(aResponse);
					} catch (Exception e) {

					}
				}
			}).start();

			if (aResponse.getUpgradeType() == 1)
				BusinessProxy.sSelf.stopNetworkActivity();
		}
	}
	ResponseObject aResponse;
	
	@Override
	protected void onResume() {
		super.onResume();
		User.getInstance().clean() ;
	}
}
