package com.kainat.app.android;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.core.NetworkTransport;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;

public class CheatActivity extends UIActivityManager implements OnClickListener//,AdapterView.OnItemSelectedListener 
{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cheat);

		spincs1= (Spinner) findViewById(R.id.cs1_spinner);
		String[] values = getResources().getStringArray(R.array.cs12);
		for (int i = 0; i < values.length; i++) {
			if (values[i].equalsIgnoreCase(Urls.SERVER_MAIN_ADDRESS)) {
				spincs1.setSelection(i);
				break;
			}
		}
		///////////////////

		spincs2 = (Spinner) findViewById(R.id.cs2_spinner);
		values = getResources().getStringArray(R.array.cs12);
		for (int i = 0; i < values.length; i++) {
			if (values[i].equalsIgnoreCase(Urls.SERVER_FALLBACK_ADDRESS)) {
				spincs2.setSelection(i);
				break;
			}
		}
				/////////////////
		spincsTejas = (Spinner) findViewById(R.id.tejas_spinner);
		values = getResources().getStringArray(R.array.tejas);
		for (int i = 0; i < values.length; i++) {
			if (values[i].equalsIgnoreCase(Urls.TEJAS_HOST)) {
				spincsTejas.setSelection(i);
				break;
			}
		}
				///
		spincsWap = (Spinner) findViewById(R.id.wap_spinner);
		values = getResources().getStringArray(R.array.wap);
		for (int i = 0; i < values.length; i++) {
			if (values[i].equalsIgnoreCase(Urls.WAP_HOST)) {
				spincsWap.setSelection(i);
				break;
			}
		}
		version = (TextView) findViewById(R.id.version);
		version.setText(YelatalkApplication.clientProperty.getProperty(YelatalkApplication.clientProperty.CLIENT_VERSION));
		
		appversion = (TextView) findViewById(R.id.appversion);
		appversion.setText(YelatalkApplication.clientProperty.getProperty(YelatalkApplication.clientProperty.APPLICAITON_VERSION));
	
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Cheat Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}
	Spinner spincs1,spincs2,spincsTejas,spincsWap;
	TextView version,appversion ;
	public void notificationFromTransport(ResponseObject response) {
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			BusinessProxy.sSelf.advertisementData = new HashMap<String, String>();
			Urls.TEJAS_HOST = String.valueOf(((Spinner) findViewById(R.id.tejas_spinner)).getSelectedItem());;
			Urls.WAP_HOST = String.valueOf(((Spinner) findViewById(R.id.wap_spinner)).getSelectedItem());;
			String s = String.valueOf(((Spinner) findViewById(R.id.cs1_spinner)).getSelectedItem());
			Urls.SERVER_MAIN_ADDRESS = s;
			YelatalkApplication.clientProperty.putProperty(YelatalkApplication.clientProperty.SERVER_MAIN_ADDRESS,s);
			
			s = String.valueOf(((Spinner) findViewById(R.id.cs2_spinner)).getSelectedItem());
			Urls.SERVER_FALLBACK_ADDRESS = s;
			YelatalkApplication.clientProperty.putProperty(YelatalkApplication.clientProperty.SERVER_FALLBACK_ADDRESS,s);
	
			s = version.getText().toString();
			YelatalkApplication.clientProperty.putProperty(YelatalkApplication.clientProperty.CLIENT_VERSION,s);
			
			s = appversion.getText().toString();
			YelatalkApplication.clientProperty.putProperty(YelatalkApplication.clientProperty.APPLICAITON_VERSION,s);
			try {
				NetworkTransport.getInstance().configureNetGateway();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Urls.COM_TERM_AND_SERVICES= "http://"+Urls.WAP_HOST+"/mypage/termsandc/comservices";
			finish();			
			break;
		}
	}

	public void onBackPressed() {
		if(onBackPressedCheck())return;
		finish();
		super.onBackPressed();
	}
}
