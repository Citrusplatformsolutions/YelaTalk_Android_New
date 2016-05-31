package com.kainat.app.android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.ResponseObject;

public class AccountSetupActivity extends UIActivityManager implements OnClickListener {
	private static final String TAG = AccountSetupActivity.class.getSimpleName();
	public static final String TITLE = "title";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_setup_screen);
		String title = (String) DataModel.sSelf.removeObject(TITLE);
		if (null == title) {
			title = "Settings";
		}
		((TextView) findViewById(R.id.accountSetupScreen_title)).setText(title);
		((Button) findViewById(R.id.accountSetupScreen_submit)).setOnClickListener(this);
		((Button) findViewById(R.id.accountSetupScreen_cancelButton)).setOnClickListener(this);
		
		//Add Google Analytics
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Account Setup Screen");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onBackPressed() {
		if(onBackPressedCheck())return;
		super.onBackPressed();
	}
	public void notificationFromTransport(ResponseObject response) {
		if (Logger.ENABLE)
			Logger.info(TAG, "notificationFromTransport-- Response Code= " + response.getResponseCode() + "  Sent Op= " + response.getSentOp());
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accountSetupScreen_submit:
			popScreen();
			break;
		case R.id.accountSetupScreen_cancelButton:
			popScreen();
			break;
		}
	}

}