package com.kainat.app.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Utilities;

public class PhoneNumberActivity extends UIActivityManager implements OnClickListener {
	private static final int COUNTRY_REQUEST = 1;

	private EditText ccEdittext;
	private Button cnEdittext;
	private EditText mobileEdittext;
	private int selectedCountry = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_number);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent intent = getIntent();
		String countryCode = intent.getStringExtra("countryCode");
		countryCode = countryCode == null ? "" : countryCode;
		String countryName = intent.getStringExtra("countryName");
		countryName = countryName == null ? "" : countryName;
		String mobileNumber = intent.getStringExtra("mobile");
		mobileNumber = mobileNumber == null ? "" : mobileNumber;

		if (!countryCode.equals("") && countryName.equals("")) {
			selectedCountry = findCountryIndexFromCode(countryCode);
			if (selectedCountry != -1) {
				countryName = Constants.COUNTRY_LIST[selectedCountry];
			}
		}

		if ("".equals(countryCode) && "".equals(countryName) && "".equals(mobileNumber)) {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String simCountryCode = tm.getSimCountryIso();
			//Find the SIM country code and populate the country code and country name.
			selectedCountry = findCountryIndexFromShortName(simCountryCode.toUpperCase());
			if (selectedCountry != -1) {
				countryCode = Constants.COUNTRY_CODE[selectedCountry];
				countryName = Constants.COUNTRY_LIST[selectedCountry];
			}
		}

		ccEdittext = (EditText) findViewById(R.id.signup_countryCodeBox);
		ccEdittext.setText(countryCode);
		cnEdittext = (Button) findViewById(R.id.signup_countryBox);
		cnEdittext.setText(countryName);
		mobileEdittext = (EditText) findViewById(R.id.signup_mobileBox);
		mobileEdittext.setText(mobileNumber);
		mobileEdittext.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					return saveMobileNumber(v);
				}
				return false;
			}
		});
		
		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("PhoneNumber Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	private boolean saveMobileNumber(TextView v) {
		Utilities.closeSoftKeyBoard(v, PhoneNumberActivity.this);
		String str = v.getText().toString();
		if (str.length() <= 0) {
			mobileEdittext.requestFocus();
			showSimpleAlert("Error", "Mobile number must contain at least " + Constants.MIN_PHONE_LENGTH + " characters!");
			return true;
		}
		if (str.startsWith("0")) {
			mobileEdittext.requestFocus();
			showSimpleAlert("Error", "Please do not enter 0 before your number. For example: 8585555555");
			return true;
		}
		String number = String.valueOf(Long.valueOf(str).longValue());
		if (Constants.MIN_PHONE_LENGTH > number.length()) {
			mobileEdittext.requestFocus();
			showSimpleAlert("Error", "Mobile number must contain at least " + Constants.MIN_PHONE_LENGTH + " characters!");
			return true;
		}
		if (0 >= ccEdittext.getText().toString().length()) {
			cnEdittext.requestFocus();
			showSimpleAlert("Error", "Please select the country!");
			return true;
		}
		Intent intent = getIntent();
		intent.putExtra("countryCode", ccEdittext.getText().toString());
		intent.putExtra("countryName", cnEdittext.getText().toString());
		intent.putExtra("mobile", number);
		setResult(RESULT_OK, intent);
		finish();
		return true;
	}

	private int findCountryIndexFromShortName(String shortName) {
		for (int i = 0; i < Constants.COUNTRY_SHORT_NAME.length; i++) {
			if (shortName.equals(Constants.COUNTRY_SHORT_NAME[i])) {
				return i;
			}
		}
		return -1;
	}

	private int findCountryIndexFromCode(String simCountryCode) {
		for (int i = 0; i < Constants.COUNTRY_CODE.length; i++) {
			if (simCountryCode.equals(Constants.COUNTRY_CODE[i])) {
				return i;
			}
		}
		return -1;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signup_countryCodeBox:
		case R.id.signup_countryBox:
			Utilities.closeSoftKeyBoard(findViewById(R.id.signup_mobileBox), this);
			Intent intent = new Intent(PhoneNumberActivity.this, CountrySelectionActivity.class);
			intent.putExtra("country_index", selectedCountry);
			startActivityForResult(intent, COUNTRY_REQUEST);
			break;
		case R.id.cancel_Button:
			finish();
			break;
		case R.id.done_Button:
			saveMobileNumber(mobileEdittext);
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case COUNTRY_REQUEST:
				selectedCountry = data.getIntExtra("country_index", -1);
				if (selectedCountry > -1) {
					ccEdittext.setText(Constants.COUNTRY_CODE[selectedCountry]);
					cnEdittext.setText(Constants.COUNTRY_LIST[selectedCountry]);
				}
				break;
			}
		}
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	public void onBackPressed() {
		if(onBackPressedCheck())return;
		finish();
	}
}