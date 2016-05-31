package com.kainat.app.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.User;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.model.Countries;
import com.kainat.app.android.util.AdConnectionManager;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.RTDialog;
import com.kainat.app.android.util.RequestType;
import com.kainat.app.android.util.ResponseError;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;

public class VerificationActivity extends UIActivityManager implements
OnClickListener {

	Handler handler = new Handler();
	EditText mobileField, pin, country_code;
	SMSReceiver smsReceiver;
	String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	RTDialog rTDialog;
	Spinner country_spinner;
	View progress_dilog;
	boolean verifed;
	boolean regInProcess;
	boolean oldUser;
	String countryName;
	TextView timer_txt;
	LinearLayout feedback_linear;
	private CountDownTimer countDownTimer;
	private final long startTime 	= 30 * 1000;
	private final long interval 	= 1 * 1000;
	private boolean timerHasStarted = false;
	private static final String TAG = VerificationActivity.class.getSimpleName();
	private EditText catagoryField;
	private String welcomeSMS = "Hi, welcome to Yelatalk! \n you will receive verification code shortly.";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initMain();
		countDownTimer 		= new MyCountDownTimer(startTime, interval);
		smsReceiver 		= new SMSReceiver();
		IntentFilter filter = new IntentFilter(SMS_RECEIVED);
		registerReceiver(smsReceiver, filter);
		
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Verification Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}

	private TextWatcher pinTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if(!getCodeFromUrl)
				if (!verificationStarted && pin.getText().toString().equalsIgnoreCase("" + verificationCode)) {
					verified();
				}
		}

	};

	private void initMain() 
	{
		regInProcess 	= false; 
		verifed 		= false; 
		regInProcess 	= false; 
		oldUser 		= false; 
		setContentView(R.layout.verification_activity);
		mobileField 	= (EditText) findViewById(R.id.mobile);
		country_code 	= (EditText) findViewById(R.id.country_code);
		progress_dilog 	= findViewById(R.id.progress_dilog);

		progress_dilog.setVisibility(View.GONE);
		// if(SettingData.sSelf.getFirstName()!=null)
		// mobileField

		findViewById(R.id.prev).setOnClickListener(this);

		// findViewById(R.id.next).setOnClickListener(this);
		findViewById(R.id.verification_verify).setOnClickListener(this);  

		country_spinner = (Spinner) findViewById(R.id.country_spinner);
		country_spinner.setAdapter(new ArrayAdapter<Countries.Country>(this,
				R.layout.country_testview_screen, Countries.Country.values()));

		// AppUtil.showTost(this, "---country>"+User.getInstance().country);

		catagoryField = (EditText) findViewById(R.id.catagory_filed_id);
		catagoryField.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				country_spinner.performClick();
				Utilities.closeSoftKeyBoard(v,
						VerificationActivity.this);
				return false;
			}
		});

		country_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View arg1,
					int arg2, long arg3) {
				indexCountryCode = arg2;
				try {
					((TextView) parentView.getChildAt(0))
					.setBackgroundColor(getResources().getColor(
							R.color.list_background));
				} catch (Exception e) {

				}

				String selected = ""
						+ ((Countries.Country) country_spinner
								.getSelectedItem()).getCode();

				countryName =  ((Countries.Country) country_spinner
						.getSelectedItem()).getStationName() ;
				country_code.setText("+" + selected);
				Utilities.closeSoftKeyBoard(arg1,
						VerificationActivity.this);
				catagoryField.setText(country_spinner.getSelectedItem().toString());
				//				AppUtil.showTost(VerificationActivity.this, countryName) ;
				// Countries countries = new Countries() ;
				// countries
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		//Write code to detect Country Code, and show the selection be default :)
		try
		{
			String cc = "";
			cc = getResources().getConfiguration().locale.getCountry();
			if(cc == null || (cc != null && cc.trim().length() == 0))
			{
				TelephonyManager tm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
				cc = tm.getNetworkCountryIso();
				if(cc != null && cc.trim().length() != 0)
				{
					int ind = Countries.getCountryName(cc).getIndex();
					country_spinner.setSelection(ind > 0 ? ind : 0);
				}
			}
			//			if(cc == null || (cc != null && cc.trim().length() == 0))
			//			{
			//				new CheckCountryCode().execute("") ;
			//			}
			if(cc == null || (cc != null && cc.trim().length() == 0))
			{
				if (User.getInstance().country != null) 
				{
					int ind = Countries.getCountry(User.getInstance().country).getIndex();
					country_spinner.setSelection(ind);
				}
				if (indexCountryCode > 0)
					country_spinner.setSelection(indexCountryCode);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		// AppUtil.showTost(VerificationActivity.this, ""+indexCountryCode) ;
	}

	public String getUsername() {
		AccountManager manager = AccountManager.get(this);
		Account[] accounts = manager.getAccountsByType("com.google");
		List<String> possibleEmails = new LinkedList<String>();

		for (Account account : accounts) {
			// TODO: Check possibleEmail against an email regex or treat
			// account.name as an email address only for certain account.type
			// values.
			possibleEmails.add(account.name);
		}

		if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
			String email = possibleEmails.get(0);
			String[] parts = email.split("@");
			if (parts.length > 0 && parts[0] != null)
				return parts[0];
			else
				return null;
		} else
			return null;
	}

	@Override
	public void onBackPressed() {
		if (!verifed) {
			finish();
			Intent intent = new Intent(VerificationActivity.this,
					LanguageActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		} 
		//		else {
		//			moveTaskToBack(true);
		//		}
	}

	String generateRandomWord(int wordLength) {
		Random r = new Random(); // Intialize a Random Number Generator with
		// SysTime as the seed
		StringBuilder sb = new StringBuilder(wordLength);
		for (int i = 0; i < wordLength; i++) { // For each letter in the word
			char tmp = (char) ('a' + r.nextInt('z' - 'a')); // Generate a letter
			// between a and z
			sb.append(tmp); // Add it to the String
		}
		return sb.toString();
	}

	@Override
	public void onClick(final View v) {
		((TextView) findViewById(R.id.pin_not_recived))
		.setVisibility(View.GONE);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mobileField.getWindowToken(), 0);

		switch (v.getId()) {
		case R.id.edit:
			//This is the case where we might change the number, and if number is already logged in, we need to reset setting data.
			SettingData.sSelf.setUserName(null);
			SettingData.sSelf.setEmail(null);
			SettingData.sSelf.setPassword(null);
			initMain();
			if(mobileField != null)
				mobileField.setText(phoneNumberOri);
			verifed = false;
			pin = null;
			regInProcess = false;
			oldUser = false;			
			break ;
		case R.id.prev:
			finish();
			Intent intent = new Intent(VerificationActivity.this, LanguageActivity.class);
			startActivity(intent);
			break;
		case R.id.check_code:
			if(pin.getText().toString() != null && pin.getText().toString().trim().length() > 0)
			{
				CheckCode checkCode = new CheckCode();
				if(checkInternetConnection())
					checkCode.execute(pin.getText().toString());
				else
					showToast(getString(R.string.check_net_connection));
			}
			break ;
		case R.id.done:// verification_verify:
			if(mobileField.getText().toString().trim().length()<=0){
				AppUtil.showTost(this, getString(R.string.sent_text_confirm_title)) ;
				return ;
			}
			String s = mobileField.getText().toString(); 
			TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
			String imsiSIM1 = telephonyInfo.getImsiSIM1();
			String imsiSIM2 = telephonyInfo.getImsiSIM2();
			boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
			boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

			boolean isDualSIM = telephonyInfo.isDualSIM();

			if(isDualSIM && isSIM1Ready && isSIM2Ready){
				//Show different Alert for dual SIM users
				new AlertDialog.Builder(this).setMessage(getString(R.string.sent_text_confirm_title)+"?"+" ")
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int whichButton) {
						verifyMobileNumber();
					}
				}).setNegativeButton(R.string.edit, null).show();
			}
			else
				new AlertDialog.Builder(this).setMessage(getString(R.string.sent_text_confirm_title)+"?"+" ")
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int whichButton) {
						verifyMobileNumber();
					}
				}).setNegativeButton(R.string.edit, null).show();

			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mobileField.getWindowToken(), 0);

			break;
		case R.id.next:
			break;
		case R.id.verification_verify:// done:
			if(!AppUtil.checkInternetConnection(VerificationActivity.this))
				return ;
			if(!regInProcess){
				if(mobileField.getText().toString().trim().length()<=0){
					AppUtil.showTost(this, getString(R.string.sent_text_confirm_title)) ;
					return ;
				}
				s = mobileField.getText().toString(); 
				telephonyInfo = TelephonyInfo.getInstance(this);
				imsiSIM1 = telephonyInfo.getImsiSIM1();
				imsiSIM2 = telephonyInfo.getImsiSIM2();
				isSIM1Ready = telephonyInfo.isSIM1Ready();
				isSIM2Ready = telephonyInfo.isSIM2Ready();
				isDualSIM = telephonyInfo.isDualSIM();
				if(isDualSIM && isSIM1Ready && isSIM2Ready){
					//Show different Alert for dual SIM users
					new AlertDialog.Builder(this).setMessage(getString(R.string.sent_dual_sim))
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {
							regInProcess = true ;
							v.setAlpha(0.4f);								
							reg();

						}
					}).setNegativeButton(R.string.edit, null).show();
				}
				else
					new AlertDialog.Builder(this)
				.setMessage(getString(R.string.sent_text_confirm_title)+"?"+"\n "+s)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int whichButton) {
						regInProcess = true ;
						v.setAlpha(0.4f);								
						reg();

					}
				}).setNegativeButton(R.string.edit, null).show();

				imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mobileField.getWindowToken(), 0);

			}
			break;
		}
	}

	private class LoginRegistrationSocial extends
	AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		ResponseObject aResponse;

		@Override
		protected String doInBackground(String... urls) {
			try {
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
				User.getInstance().LOGIN_VIA = User.getInstance().LOGIN_ROCKETALK;
				Hashtable<String, String> postParam = new Hashtable<String, String>();
				if (User.getInstance().LOGIN_VIA.equalsIgnoreCase(User.getInstance().LOGIN_ROCKETALK)) 
				{
					postParam.put("mode", "rt");// mode=fb rt
					postParam.put("emailid", SettingData.sSelf.getEmail());// (Y/N values)
					headerParam.put("client_param", ClientProperty.CLIENT_PARAMS + "::requesttype##"+ RequestType.REG_RT);
				} else if (User.getInstance().LOGIN_VIA.equalsIgnoreCase(User.getInstance().LOGIN_FACEBOOK)) 
				{
					postParam.put("mode", "fb");// mode=fb rt (fb/gp/tw)
					postParam.put("appkey", Constants.FACEBOOK_APP_ID);// mode=fb
					// rt
					postParam.put("partneruserkey", User.getInstance().userId);// fb
					// or
					// gp
					// email
					// id
					postParam.put("emailid", User.getInstance().email);// (Y/N
					// possible
					// values)
					headerParam.put("client_param",
							ClientProperty.CLIENT_PARAMS + "::requesttype##"
									+ RequestType.REG_FB);

				} else if (User.getInstance().LOGIN_VIA.equalsIgnoreCase(User
						.getInstance().LOGIN_GOOGLE_PLUS)) {
					// User.getInstance().email="nagendra.testing@gmail.com";
					postParam.put("mode", "gp");// mode=fb rt (fb/gp/tw)
					postParam.put("appkey", Constants.GOOGLE_APP_ID);// mode=fb
					// rt
					// (fb/gp/tw)
					postParam.put("partneruserkey", User.getInstance().userId);// fb
					// or
					// gp
					// email
					// id
					postParam.put("emailid", User.getInstance().email);// (Y/N
					// possible
					// values)
					headerParam.put("client_param",
							ClientProperty.CLIENT_PARAMS + "::requesttype##"
									+ RequestType.REG_GP);
				}

				phoneNumberSend = phoneNumberSend.replace(" ", "");
				phoneNumberSend = phoneNumberSend.replace("_", "");
				phoneNumberSend = phoneNumberSend.replace("-", "");
				phoneNumberSend = phoneNumberSend.replace("+", "");
				phoneNumberSend = phoneNumberSend.trim();
				postParam.put("phonenumber", phoneNumberSend);
				postParam.put("password", SettingData.sSelf.getPassword());
				postParam.put("dob", SettingData.sSelf.getBirthDate());
				postParam.put("fname", SettingData.sSelf.getFirstName());
				postParam.put("lname", SettingData.sSelf.getLastName());
				postParam.put("gender", "M");
				postParam.put("country", countryName);
				postParam.put("locale", KeyValue.getString(VerificationActivity.this, KeyValue.LANGUAGE));

				if (postParam != null){
					Log.i(TAG, "LoginRegistrationSocial :: doInBackground : postParam ==> "+postParam.toString());
				}
				String response = null;
				try
				{
					response = AdConnectionManager.getInstance().uploadByteData(
							"http://" + Urls.TEJAS_HOST+ "/tejas/feeds/api/user/register",
							postParam, headerParam, null);
				}
				catch(FileNotFoundException fnfex)
				{
					fnfex.printStackTrace();
					AppUtil.showTost(VerificationActivity.this, getString(R.string.something_went_wrong));
					return ResponseError._404;
				}
				catch(ConnectException ex)
				{
					ex.printStackTrace();
					AppUtil.showTost(VerificationActivity.this, getString(R.string.something_went_wrong));
					return ResponseError._404;
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					AppUtil.showTost(VerificationActivity.this, getString(R.string.something_went_wrong));
					return ResponseError._404;
				}

				if (response != null) {
					try {
						final JSONObject jsonObject = new JSONObject(response);
						Log.i(TAG, "LoginRegistrationSocial :: doInBackground : response JSON ==> "+jsonObject.toString());
						if (jsonObject.has("status") && jsonObject.getString("status")
								.equalsIgnoreCase(ResponseError.STATUS_ERROR)) {
							if (jsonObject.getString("code").equalsIgnoreCase("RT_ERROR_21047")) 
							{
								BusinessProxy.sSelf.setUserId(jsonObject.getInt("userId"));
								SettingData.sSelf.setUserID(jsonObject.getInt("userId"));
								SettingData.sSelf.setMobile(phoneNumberSend);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										setContentView(R.layout.enter_pin_activity);
										feedback_linear  = (LinearLayout)findViewById(R.id.feedback_linear);
										findViewById(R.id.textView2).setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												mailFeedback();
											}
										});
										timer_txt = (TextView)findViewById(R.id.timer_txt);
										timer_txt.setText(getString(R.string.sms_arrive_xseconds)+ " " + timer_txt.getText() + String.valueOf(startTime / 1000) + " " + getString(R.string.xseconds));
										verifed = true;
										((TextView) findViewById(R.id.title)).setText("+"+phoneNumberSend);
										findViewById(R.id.check_code).setVisibility(View.GONE);
										findViewById(R.id.feedback_linear).setVisibility(View.GONE);
										findViewById(R.id.done).setOnClickListener(VerificationActivity.this);
										findViewById(R.id.done).setVisibility(View.GONE);
										if(pin == null)
											pin = (EditText) findViewById(R.id.pin);
										pin.addTextChangedListener(pinTextWatcher);
										sendSMS(phoneNumberSend, phoneNumberOri, "YelaTalk verification code is:" + verificationCode);		
									}
								});
								verifed = true ;
								KeyValue.setBoolean(VerificationActivity.this, "OLDUSER", true);
								oldUser = true ;
								return "Sucess";
							} 
							return null;
						} 
						else 
						{
							if (jsonObject.has("mediaCategoryList")) {
								JSONArray jSONArray = jsonObject
										.getJSONArray("mediaCategoryList");
								BusinessProxy.sSelf.mediaCategories = new String[jSONArray
								                                                 .length()];
								BusinessProxy.sSelf.mediaCategoryIDs = new String[jSONArray
								                                                  .length()];
								for (int i = 0; i < jSONArray.length(); i++) {
									JSONObject nameObjectw = jSONArray
											.getJSONObject(i);

									if (nameObjectw.has("categoryId")) {
										BusinessProxy.sSelf.mediaCategoryIDs[i] = nameObjectw
												.getString("categoryId");
									}
									if (nameObjectw.has("categoryName")) {
										BusinessProxy.sSelf.mediaCategories[i] = nameObjectw
												.getString("categoryName");
									}
								}
							}

							if (jsonObject.has("clientId")){
								BusinessProxy.sSelf.setClientId(Integer
										.parseInt(jsonObject.getString("clientId")));
								SettingData.sSelf.setClientID(Integer
										.parseInt(jsonObject.getString("clientId")));
							}

							if (jsonObject.has("userId")){
								BusinessProxy.sSelf.setUserId(Integer
										.parseInt(jsonObject.getString("userId")));
								SettingData.sSelf.setUserID(Integer
										.parseInt(jsonObject.getString("userId")));
							}

							if (jsonObject.has("lastTransactionId")){
								BusinessProxy.sSelf
								.setTransactionId(Integer.parseInt(jsonObject.getString("lastTransactionId")));
								SettingData.sSelf.setLastTransactionId(Integer
										.parseInt(jsonObject.getString("userId")));
							}

							if (jsonObject.has("type"))
								SettingData.sSelf.setType(jsonObject.getString("type"));

							if (jsonObject.has("firstName"))
								SettingData.sSelf.setFirstName(jsonObject.getString("firstName"));

							if (jsonObject.has("lastName"))
								SettingData.sSelf.setLastName(jsonObject.getString("lastName"));

							//							if (jsonObject.has("lastName"))
							//								SettingData.sSelf.setLastName(jsonObject
							//										.getString("lastName"));

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

							try {
								if (jsonObject.has("isEmailVerified"))
									SettingData.sSelf.setEmailVerified(Boolean.parseBoolean(jsonObject.getString("isEmailVerified")));
							} catch (Exception e) {
							}

							if (jsonObject.has("mobileModel"))
								SettingData.sSelf.setMobileModel(jsonObject.getString("mobileModel"));

							if (jsonObject.has("userName"))
								SettingData.sSelf.setUserName(jsonObject.getString("userName"));

							if (jsonObject.has("password"))
								SettingData.sSelf.setPassword(jsonObject.getString("password"));
							BusinessProxy.sSelf.mBuddyTimeStamp = "1000-01-01 00:00:00.0";
							BusinessProxy.sSelf.mBuddyTimeStamp = "0000-00-00 00:00:00.0";
							BusinessProxy.sSelf.mGroupTimeStamp = "0000-00-00 00:00:00.0";
							BusinessProxy.sSelf.mInboxTimeStamp = "0000-00-00 00:00:00.0";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (!SettingData.sSelf.isEmailVerified()
						&& BusinessProxy.sSelf.isRegistered()) {
					return null;
				}
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseError._404;
			}
		}

		@Override
		protected void onPostExecute(String response) {

			if (response != null) {
				if (lDialogProgress != null && lDialogProgress.isShowing()) {
					lDialogProgress.dismiss();
					// lDialogProgress.
					lDialogProgress = null;
				}
				if(response.equalsIgnoreCase(ResponseError._404))
				{
					initMain();
					return;
				}
				if (aResponse == null || aResponse.getUpgradeType() != 1) {
					if (User.getInstance().userId == null) {
						SettingData.sSelf.setFirstName("");
						SettingData.sSelf.setLastName("");
						SettingData.sSelf.setEmail("");
						SettingData.sSelf.setGender(-1);
					}
					if(pin == null || timer_txt == null)
						setContentView(R.layout.enter_pin_activity);
					feedback_linear  = (LinearLayout)findViewById(R.id.feedback_linear);
					findViewById(R.id.textView2).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mailFeedback();
								}
							});
					verifed = true;
					timer_txt = (TextView)findViewById(R.id.timer_txt);
					timer_txt.setText(getString(R.string.sms_arrive_xseconds)+" "+ timer_txt.getText() + String.valueOf(startTime / 1000) + " " + getString(R.string.xseconds));
					((TextView) findViewById(R.id.title)).setText("+"+phoneNumberSend);
					findViewById(R.id.done).setOnClickListener(VerificationActivity.this);
					findViewById(R.id.check_code).setVisibility(View.GONE);
					findViewById(R.id.feedback_linear).setVisibility(View.GONE);
					findViewById(R.id.done).setVisibility(View.GONE);
					if(pin == null){
						pin = (EditText) findViewById(R.id.pin);
					}
					pin.addTextChangedListener(pinTextWatcher);
					pin.setFocusable(false);
					//					pin.setVisibility(View.GONE);
					if(!oldUser)
						sendSMS(phoneNumberSend, phoneNumberOri, "YelaTalk verification code is:" + verificationCode);
				}
				if (!SettingData.sSelf.isEmailVerified() && BusinessProxy.sSelf.isRegistered()) 
				{
					// initVerificationScreen() ;
				}
			} 
			else 
			{

				initMain();
				((TextView) findViewById(R.id.pin_not_recived)).setText(String
						.format(getString(R.string.sent_text_confirm_title),
								phoneNumberSend));
				((TextView) findViewById(R.id.pin_not_recived))
				.setVisibility(View.VISIBLE);

				mobileField.setText(phoneNumberOri);
				if (lDialogProgress != null && lDialogProgress.isShowing()) {
					lDialogProgress.dismiss();
					// lDialogProgress.
					lDialogProgress = null;
				}
			}
		}
	}

	public void mailFeedback()
	{
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yelatalkfeedback@gmail.com"});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "YelaTalk Feedback");
		emailIntent.setType("text/plain");
		//	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		final PackageManager pm = getPackageManager();
		final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
		ResolveInfo best = null;
		for(final ResolveInfo info : matches)
			if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
				best = info;
		if (best != null)
			emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		startActivity(emailIntent);
	}
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	private String verificationCode;
	private String phoneNo;
	boolean cancelOperation;

	int indexCountryCode;

	private void verifyMobileNumber() {
		cancelOperation = false;
		//		verificationCode = new Double(Math.random() * 10000).intValue();
		//		if (verificationCode < 1000) {
		//			verificationCode += 1000;
		//		}
		phoneNo = PhoneNumberUtils.formatNumber(mobileField.getText().toString());
		phoneNo = PhoneNumberUtils.convertKeypadLettersToDigits(phoneNo);
		if (phoneNo == null || phoneNo.equals("")) {
			dismissAnimationDialog();
			showSimpleAlert("Error", getString(R.string.enter_mobile));
			findViewById(R.id.verification_verify).setAlpha(1f);
			regInProcess = false ;
			return;
		}
		indexCountryCode = country_spinner.getSelectedItemPosition();
		phoneNo = country_code.getText().toString() + phoneNo;
		setContentView(R.layout.enter_pin_activity);
		pin = (EditText) findViewById(R.id.pin);
		pin.setFocusable(false);
		pin.setClickable(false);
		pin.setEnabled(false);
		verifed = true;
		feedback_linear  = (LinearLayout)findViewById(R.id.feedback_linear);
		findViewById(R.id.textView2).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mailFeedback();
					}
				});
		timer_txt = (TextView)findViewById(R.id.timer_txt);
		timer_txt.setText(getString(R.string.sms_arrive_xseconds)+ " "+timer_txt.getText() + String.valueOf(startTime / 1000) +" " +getString(R.string.xseconds));
		((TextView) findViewById(R.id.title)).setText("+"+phoneNumberSend);
		findViewById(R.id.check_code).setVisibility(View.GONE);
		findViewById(R.id.feedback_linear).setVisibility(View.GONE);
		findViewById(R.id.done).setOnClickListener(this);
		findViewById(R.id.done).setVisibility(View.GONE);
		//if(pin == null)
		//pin = (EditText) findViewById(R.id.pin);
		pin.addTextChangedListener(pinTextWatcher);
		Log.i(TAG, "verifyMobileNumber :: verification code : " + phoneNo + " :"+ verificationCode);
		sendSMS(phoneNo, mobileField.getText().toString(), "YelaTalk verification code is:" + verificationCode);
	}

	private void reg() {
		cancelOperation = false;
		//		verificationCode = Double.valueOf(Math.random() * 10000).intValue();
		//		if (verificationCode < 1000) {
		//			verificationCode += 1000;
		//		}
		phoneNo = PhoneNumberUtils.formatNumber(mobileField.getText().toString());
		phoneNo = PhoneNumberUtils.convertKeypadLettersToDigits(phoneNo);
		if (phoneNo == null || phoneNo.equals("")) {
			dismissAnimationDialog();
			showSimpleAlert(getString(R.string.error), getString(R.string.enter_mobile));			
			findViewById(R.id.verification_verify).setAlpha(1f);
			regInProcess = false;
			return;
		}
		indexCountryCode = country_spinner.getSelectedItemPosition();
		phoneNo = country_code.getText().toString() + phoneNo;
		// setContentView(R.layout.enter_pin_activity);
		// findViewById(R.id.done).setOnClickListener(this);
		// findViewById(R.id.done).setVisibility(View.GONE);
		// pin = (EditText) findViewById(R.id.pin);
		// pin.addTextChangedListener(pinTextWatcher);
		Log.i(TAG, "reg :: verification code : " + phoneNo + " :"+ verificationCode);
		this.phoneNumberSend = phoneNo;
		this.phoneNumberOri = mobileField.getText().toString();
		//As we are using, older API, that requires username, pass, eamil, mobile, country, DOB etc.
		//So Insert dummy data for Registration
		if (User.getInstance().userId == null) 
		{
			SettingData.sSelf.setFirstName("YelaTalk");
			SettingData.sSelf.setLastName("YelaTalk");
			SettingData.sSelf.setEmail(generateRandomWord(15) + "@" + "yelatalk.com");
			SettingData.sSelf.setMobile(mobileField.getText().toString());
			SettingData.sSelf.setPassword("aaaaaa");
			SettingData.sSelf.setGender(1);
			SettingData.sSelf.setCountry("india");
			SettingData.sSelf.setRefreshPeriod(120);
			DataModel.sSelf.storeObject(DMKeys.FROM_REGISTRATION, Boolean.valueOf(true));
			//			BusinessProxy.sSelf.addPush.closeAdd();
			SettingData.sSelf.setBirthDate("10/20/1982");
		} 
		else 
		{
			SettingData.sSelf.setFirstName(User.getInstance().firstName);
			SettingData.sSelf.setLastName(User.getInstance().lastName);
			SettingData.sSelf.setEmail(generateRandomWord(1)+ User.getInstance().email);
			SettingData.sSelf.setMobile(mobileField.getText().toString());
			SettingData.sSelf.setPassword("aaaaaa");
			SettingData.sSelf.setGender(1);// gender.equals("Male") ? 1 : 2);
			SettingData.sSelf.setCountry("india");
			SettingData.sSelf.setRefreshPeriod(120);
			DataModel.sSelf.storeObject(DMKeys.FROM_REGISTRATION, Boolean.valueOf(true));
			//			BusinessProxy.sSelf.addPush.closeAdd();
			SettingData.sSelf.setBirthDate(User.getInstance().birthday);
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mobileField.getWindowToken(), 0);
		progress_dilog.setVisibility(View.VISIBLE);
		if(checkInternetConnection())
			new LoginRegistrationSocial().execute("");
		else
			showToast(getString(R.string.check_net_connection));
	}

	private void verified() {
		timer.cancel();
		SettingData.sSelf.setRemember(true);
		SettingData.sSelf.setNumber(phoneNo);
		if(!oldUser){
			finish();
			Intent intent = new Intent(VerificationActivity.this,KainatCreateProfileActivity.class);
			intent.putExtra("REG_FLOW", true);
			startActivity(intent);
		}else{
			if(checkInternetConnection())
				new CheckSelfCode().execute("");
			else
				showToast(getString(R.string.check_net_connection));
		}

	}

	Timer timer;
	String phoneNumberSend;
	String phoneNumberOri;
	boolean chect = false;

	private synchronized void sendSMS(String phoneNumber,
			final String phoneNumberOri, String message) {
		this.phoneNumberSend = phoneNumber;
		this.phoneNumberOri = phoneNumberOri;
		long waitTill = 30000;
		if (chect)
			waitTill = 5000;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (!chect) {
							if(timer!=null)
								timer.cancel();
							GetVerificationCode getVerificationCode = new GetVerificationCode();
							if(checkInternetConnection())
								getVerificationCode.execute("");
							else
								showToast(getString(R.string.check_net_connection));

							((TextView) findViewById(R.id.msg)).setText(getString(R.string.sending_code_on_this_number));
							pin.setClickable(true);
							pin.setFocusable(true);
							pin.setEnabled(true);
							pin.setFocusableInTouchMode(true);
							pin.setHint(getString(R.string.enter_pin_number));
							//Reset the verification, so that user can't enter as timer has expired.
							Log.w(TAG, "sendSMS 30 secs timeout.");
							verificationCode = "timeout";
							getCodeFromUrl = true;
							if(findViewById(R.id.check_code)!=null){
								findViewById(R.id.check_code).setVisibility(View.VISIBLE);
								findViewById(R.id.feedback_linear).setVisibility(View.VISIBLE);
								findViewById(R.id.check_code).setOnClickListener(VerificationActivity.this);
							}
						} else {
							SettingData.sSelf.setRemember(true);
							SettingData.sSelf.setNumber(phoneNo);
							//							pin.setVisibility(View.VISIBLE);
							/*	if(pin == null)
							pin = (EditText) findViewById(R.id.pin);*/
							pin.setFocusableInTouchMode(true);
							pin.setClickable(true);
							pin.setEnabled(true);
							Log.w(TAG, "sendSMS 30 secs callback, verificationCode : "+verificationCode);
							if(!verificationCode.equalsIgnoreCase(welcomeSMS))
								pin.setText("" + verificationCode);
							findViewById(R.id.done).setVisibility(View.GONE);
							verifed = true;
							((TextView) findViewById(R.id.msg)).setText(getString(R.string.sending_code_on_this_number));
							getCodeFromUrl = true;
							Log.w(TAG, "sendSMS 30 secs callback, getCodeFromUrl : "+getCodeFromUrl);
							if(findViewById(R.id.check_code)!=null){
								findViewById(R.id.check_code).setVisibility(View.VISIBLE);
								findViewById(R.id.feedback_linear).setVisibility(View.VISIBLE);
								findViewById(R.id.check_code).setOnClickListener(VerificationActivity.this);
							}
						}
					}
				});
			}
		}, waitTill);
		//Manipulate the SMS and instead of sending verification code, send a simple text message and verify that.
		verificationCode = message = welcomeSMS;
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage("+"+phoneNumber, null, message, null, null);
		timerHasStarted= false;
		if (!timerHasStarted) {
			countDownTimer.start();
			/*  pin.setFocusable(false);
			    pin.setEnabled(false);
			    pin.setClickable(false);*/
			timerHasStarted = true;
		} else {
			countDownTimer.cancel();
			/*   pin.setFocusable(true);
			    pin.setEnabled(true);
			    pin.setClickable(true);*/
			timerHasStarted = false;
		}
	}
	boolean getCodeFromUrl = false;
	boolean verificationStarted = false;
	public class SMSReceiver extends BroadcastReceiver {
		public void onReceive(Context arg0, Intent intent) {
			// ---get the SMS message passed in---
			Bundle bundle = intent.getExtras();
			SmsMessage[] msgs = null;
			if (bundle != null) {
				// ---retrieve the SMS message received---
				Object[] pdus = (Object[]) bundle.get("pdus");
				msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < msgs.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					String mobileNo = msgs[i].getOriginatingAddress();
					String code = msgs[i].getMessageBody().toString();
					if (PhoneNumberUtils.compare(mobileNo, phoneNo) && code.equals(String.valueOf(verificationCode))){
						if(timer!=null)
							timer.cancel();
						abortBroadcast();
						if(code.indexOf(':') != -1){
							code = code.substring(code.indexOf(":") + 1);
							verified();
							verificationStarted = true;
							pin.setVisibility(View.VISIBLE);
							pin.setText("" + code);
						}else{
							verified();
							verificationStarted = true;
							pin.setVisibility(View.VISIBLE);
							pin.setText(getString(R.string.done));
						}
					}
				}
			}
		}
	}

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}
		@Override
		public void onFinish() {
			timer_txt.setText("");

		}
		@Override
		public void onTick(final long millisUntilFinished) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {

					timer_txt.setText(getString(R.string.sms_arrive_xseconds)+" " +  millisUntilFinished / 1000 +" " +getString(R.string.xseconds));
					//timer_txt.setText("" + millisUntilFinished / 1000);
				}
			});

		}
	}

	private void closeDialog() {
		if (rTDialog != null && rTDialog.isShowing())
			rTDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		closeDialog();
		unregisterReceiver(smsReceiver);
		super.onDestroy();
	}

	public void oldUserLogin() {
		try {
			Hashtable<String, String> headerParam = new Hashtable<String, String>();
			headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
			Hashtable<String, String> postParam = new Hashtable<String, String>();
			// SettingData.sSelf.getType()
			// if (SettingData.sSelf.getType() != null
			// && SettingData.sSelf.getType().equalsIgnoreCase(
			// User.getInstance().LOGIN_FACEBOOK))
			{
				User.getInstance().userName = SettingData.sSelf.getUserName();
				User.getInstance().password = SettingData.sSelf.getPassword();
				User.getInstance().email = SettingData.sSelf.getEmail();
				headerParam.put("client_param", ClientProperty.CLIENT_PARAMS + "::requesttype##" + RequestType.LOGIN_FB_AUTO);
				postParam.put("mode", User.getInstance().LOGIN_ROCKETALK_SHORT_NAME);// mode=fb

				//Add app-key
				if (SettingData.sSelf.getAppkey() != null)
					postParam.put("appkey", SettingData.sSelf.getAppkey());


				if (SettingData.sSelf.getPartneruserkey() != null)
					postParam.put("partneruserkey", SettingData.sSelf.getPartneruserkey());// fb

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

				//				if (User.getInstance().multipleAccountOnRT
				//						&& (User.getInstance().userName != null && User
				//						.getInstance().userName.indexOf("~") != -1)) {
				//					String un[] = User.getInstance().userName.split("~");
				//					postParam.put("fname", un[0]);
				//					postParam.put("lname", un[1]);
				//					postParam.put("username", un[0] + " " + un[1]);
				//				}

				postParam.put("password", "aaaaaa");
				postParam.put("emailid", phoneNumberSend);

				String response = AdConnectionManager.getInstance().uploadByteData(
						"http://" + Urls.TEJAS_HOST+ "/tejas/feeds/api/user/login",
						postParam, headerParam, null);//
				login(response);
				Log.i(TAG, "oldUserLogin :: Response ==> "+response);
			}
		} catch (final Exception e) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AppUtil.showTost(
							VerificationActivity.this,
							getString(R.string.something_went_wrong) + "\n"
									+ e.getLocalizedMessage());
				}
			});
			finish();
			Log.e(TAG, "oldUserLogin : getLocalizedMessage : "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void login(final String response) {
		Log.i(TAG, "login : response : "+response);
		if (response != null) {
			try {
				JSONObject jsonObject = new JSONObject(response);

				if (jsonObject.has("status")
						&& jsonObject.getString("status").equalsIgnoreCase(
								ResponseError.STATUS_ERROR)) {
					if (User.getInstance().LOGIN_VIA.equalsIgnoreCase(User
							.getInstance().LOGIN_FACEBOOK)
							&& !jsonObject.getString("code").equalsIgnoreCase(
									ResponseError.AUTHENTICATION_FAILED)) {
						//						Auth.getInstance().mFacebook = null;
					}
					initMain();
					return;

				} else {
					//					System.out.println("JSON Object 222---- "+jsonObject.toString());
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
					//						.getString("animationURL");
					//
					//					if (jsonObject.has("animationListURL"))
					//						BusinessProxy.sSelf.animationListURL = jsonObject
					//						.getString("animationListURL");

					if (jsonObject.has("clientId"))
						BusinessProxy.sSelf.setClientId(Integer
								.parseInt(jsonObject.getString("clientId")));
					if (Logger.LOGS_ENABLE_USERID)
						System.out.println("CURRENT USER ID IS - "+ BusinessProxy.sSelf.getUserId());

					if (jsonObject.has("userId"))
						BusinessProxy.sSelf.setUserId(Integer
								.parseInt(jsonObject.getString("userId")));
					if (Logger.LOGS_ENABLE_USERID)
						System.out.println("SETTING USER ID TO - "
								+ BusinessProxy.sSelf.getUserId());

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

					if (jsonObject.has("gender")) {
						if (jsonObject.getString("gender")
								.equalsIgnoreCase("M"))
							SettingData.sSelf.setGender(1);
						if (jsonObject.getString("gender")
								.equalsIgnoreCase("F"))
							SettingData.sSelf.setGender(2);
					}

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

					//					if (jsonObject.has("upgradeType")) {
					//						if (aResponse == null)
					//							aResponse = new ResponseObject();
					//						aResponse.setUpgradeType(Integer.parseInt(jsonObject
					//								.getString("upgradeType")));
					//					}
					//					if (jsonObject.has("upgradeRTML")) {
					//						if (aResponse == null)
					//							aResponse = new ResponseObject();
					//						String rtml = jsonObject.getString("upgradeRTML");
					//						rtml = rtml.replace("\u003c", "<");
					//						rtml = rtml.replace("\u003d", ">");
					//						aResponse.setUpgradeURL(jsonObject
					//								.getString("upgradeRTML"));
					//					}
					if (jsonObject.has("isMobileVerificationNeeded")) {
						if (aResponse == null)
							aResponse = new ResponseObject();
						try {
							aResponse.setMobileVerificationNeeded(Boolean.parseBoolean(jsonObject.getString("isMobileVerificationNeeded").trim()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					//					try {
					//						if (Auth.getInstance().mFacebook != null
					//								&& Utilities.getInt(VerificationActivity.this,
					//										Constants.FIRST_LOGIN) == 0) {
					//							String Description = "";
					//							BusinessProxy.sSelf.loginWithFb = true;
					//						} else if (Auth.getInstance().mFacebook != null) {
					//							BusinessProxy.sSelf.loginWithFb = true;
					//						}
					//						if (aResponse != null && (aResponse.getUpgradeType() <= 0 && !aResponse.isMobileVerificationNeeded()))
					//							aResponse = null;
					//					} catch (Throwable e) {
					//						e.printStackTrace();
					//					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				finish();
				Intent intent = new Intent(VerificationActivity.this, SplashActivity.class);
				startActivity(intent);
				showToast(getString(R.string.something_went_wrong));
				return;
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

	ResponseObject aResponse;

	protected void onPostExecute(String response) {

		if (aResponse == null || aResponse.getUpgradeType() != 1) {
			finish();
			Intent intent = new Intent(VerificationActivity.this, KainatCreateProfileActivity.class);
			intent.putExtra("REG_FLOW", true);
			intent.putExtra("oldUser", oldUser);	
			startActivity(intent);
		}
		if (aResponse == null || aResponse.getUpgradeType() <= 0) {
			BusinessProxy.sSelf.setNetActive(true);
			BusinessProxy.sSelf.startRefresh();
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

	private class GetVerificationCode extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			try {				
				//				http://"+Urls.TEJAS_HOST+"/tejas/feeds/verification/mobile/sendcode?mobile=919717098492
				String url = "http://"
						+ Urls.TEJAS_HOST
						+ "/tejas/feeds/verification/mobile/sendcode?mobile="
						+ phoneNumberSend;
				Log.i(TAG, "url : "+url);
				Log.i(TAG, "user id  : "+ BusinessProxy.sSelf.getUserId());
				Log.i(TAG, "user pass  : "+ SettingData.sSelf.getPassword());

				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("RT-APP-KEY", HttpHeaderUtils
						.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(),
								SettingData.sSelf.getPassword()));
				try {
					HttpResponse responseHttp = client.execute(httpget);
					if (responseHttp != null) {
						final String responseStr = EntityUtils.toString(responseHttp.getEntity());
						Log.i(TAG, "GetVerificationCode :: doInBackground : Response ==> "+responseStr);

						Gson gson = new Gson();
						final com.kainat.app.android.bean.Error error = gson.fromJson(responseStr, com.kainat.app.android.bean.Error.class);
						//						final com.kainat.app.android.bean.Error error = new Error(responseStr).getErrorJSONObject();
						if (error != null) {
							if (error.getStatus() == 2) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										AppUtil.showTost(VerificationActivity.this, error.getDesc());
									}
								});

								return null;
							}
						}else
						{
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AppUtil.showTost(VerificationActivity.this, responseStr);
								}
							});
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();

				}
			} catch (Exception e) {

			}
			return null ;
		}

		@Override
		protected void onPostExecute(String response) {
			((TextView) findViewById(R.id.msg))
			.setText(getString(R.string.verification_code_sent));
			findViewById(R.id.progressBar).setVisibility(View.GONE) ;

			findViewById(R.id.edit).setVisibility(View.VISIBLE) ;
			findViewById(R.id.edit).setOnClickListener(VerificationActivity.this);

		}
	}
	private class CheckCode extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			((TextView) findViewById(R.id.msg)).setText(getString(R.string.verifing_code));
			findViewById(R.id.check_code).setVisibility(View.GONE);
			findViewById(R.id.feedback_linear).setVisibility(View.GONE);
			findViewById(R.id.edit).setVisibility(View.GONE);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			try {				
				String url = "http://" + Urls.TEJAS_HOST
						+ "/tejas/feeds/verification/mobile/checkverified?mobile="+phoneNumberSend+"&code=" + urls[0].trim();
				//						+ URLEncoder.encode(urls[0].trim());
				DefaultHttpClient client = new DefaultHttpClient();
				Log.i(TAG, "CheckCode :: doInBackground : checkverified URL ==> "+url);
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
				try {
					if(verificationCode != null && !urls[0].trim().equals(verificationCode)/*Integer.parseInt(urls[0].trim()) != verificationCode*/)
					{
						HttpResponse responseHttp = client.execute(httpget);
						//"{\"desc\":\"Code successfully matched and user has been verified.\",\"status\":\"1\"}" ;
						if (responseHttp != null) 
						{
							final String responseStr = EntityUtils.toString(responseHttp.getEntity());
							Log.i(TAG, "CheckCode :: doInBackground : checkverified responseStr ==> "+responseStr);
							//							System.out.println(responseStr);
							// JSONObject responseObj = new JSONObject(responseStr);
							Gson gson = new Gson();
							final com.kainat.app.android.bean.Error error = gson.fromJson(responseStr, com.kainat.app.android.bean.Error.class);
							//							final com.kainat.app.android.bean.Error error = new Error(responseStr).getErrorJSONObject();
							if (error != null) 
							{
								if (error.getStatus() == 2) 
								{
									runOnUiThread(new Runnable() 
									{

										@Override
										public void run() {
											//										AppUtil.showTost(VerificationActivity.this, error.getDesc());
											//										{"desc":"Code does not match for the verification of mobile number 968866666","status":"2"}
											((TextView) findViewById(R.id.msg)).setText(getString(R.string.code_not_match));
											findViewById(R.id.check_code).setVisibility(View.VISIBLE);
											pin.setClickable(true);
											pin.setEnabled(true);
											findViewById(R.id.feedback_linear).setVisibility(View.VISIBLE);
											findViewById(R.id.edit).setVisibility(View.VISIBLE) ;
											findViewById(R.id.edit).setOnClickListener(VerificationActivity.this);
										}
									});
									return null;
								}
							}
						}
					}

					//Set number verified here, if verification code matches.
					url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/verification/mobile/setverified?mobile="+phoneNumberSend;
					Log.i(TAG, "CheckCode :: doInBackground setverified URL ==> "+url);
					client = new DefaultHttpClient();
					httpget = new HttpGet(url);
					httpget.setHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
					HttpResponse responseHttp = client.execute(httpget);
					final String responseStr2 = EntityUtils.toString(responseHttp.getEntity());
					Log.i(TAG, "CheckCode :: doInBackground : setverified responseStr2 ==> "+responseStr2);
					Gson gson = new Gson();
					final com.kainat.app.android.bean.Error error2 = gson.fromJson(responseStr2,
							com.kainat.app.android.bean.Error.class);
					//					final com.kainat.app.android.bean.Error error2 = new Error(responseStr2).getErrorJSONObject();
					if (error2 != null) {
						if (error2.getStatus() == 2)
							return null;
						else if(oldUser)
							oldUserLogin();
					}
					return responseStr2;
				} catch (ClientProtocolException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String response) {

			if(response!=null){
				finish();
				if(!oldUser){
					Intent intent = new Intent(VerificationActivity.this,KainatCreateProfileActivity.class);
					intent.putExtra("REG_FLOW", true);
					startActivity(intent);
				}else
				{
					//				 pushNewScreen(KainatHomeActivity.class, false);
					//				 SettingData.sSelf.setMobile(phoneNumberSend);
					//					l();
				}
			}else{

			}

		}
	}

	private class CheckSelfCode extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			((TextView) findViewById(R.id.msg))
			.setText(getString(R.string.verifing_code));

			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			if(oldUser)
				oldUserLogin();
			return null;
		}
		//9910968484
		@Override
		protected void onPostExecute(String response) {						

		}
	}
	private class CheckCountryCode extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			((TextView) findViewById(R.id.msg))
			.setText(getString(R.string.verifing_code));

			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {

			try {				
				//http://"+Urls.TEJAS_HOST+"/tejas/feeds/verification/mobile/checkverified?mobile=919717098492&code=4207
				//First check here for local verification, means If user has entered the SMS sent to other number
				String url = Urls.GET_COUNTRY_CODE;
				DefaultHttpClient client = new DefaultHttpClient();
				String countryCode = null;
				HttpGet httpget = new HttpGet(url);
				try {
					HttpResponse responseHttp = client.execute(httpget);
					//"{\"desc\":\"Code successfully matched and user has been verified.\",\"status\":\"1\"}" ;
					if (responseHttp != null) 
					{
						final String responseStr = EntityUtils.toString(responseHttp.getEntity());
						JSONObject jsonObject = new JSONObject(responseStr);
						if(jsonObject.has("status"))
						{
							if(jsonObject.has("countryCode"))
							{
								countryCode = jsonObject.getString("countryCode");
							}
							if(jsonObject.has("country"))
							{
								//Get Country
							}
							if(jsonObject.has("zip"))
							{
								//Get Zip
							}
						}
					}
					return countryCode;
				} catch (ClientProtocolException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String response) {						
			int ind = Countries.getCountryName(response).getIndex();
			country_spinner.setSelection(ind);
		}
	}

}
