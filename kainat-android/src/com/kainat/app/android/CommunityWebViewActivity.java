package com.kainat.app.android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ResponseObject;

public class CommunityWebViewActivity extends UIActivityManager {
	//	public static String TERMS_CONDITIONS = "http://iphone.rocketalk.com/mypage/termsandc/comservices";

	private WebView mWebView;
	private Button back_button;
	private TextView loading_text;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.community_webview_layout);
		init();
		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Community WebView Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());	
	}

	private void init() {
		// TODO Auto-generated method stub
		loading_text = (TextView)findViewById(R.id.loading_text);
		//loading_text.setText(Html.fromHtml(getString(R.string.term_condition)));
		WebView wv = (WebView)findViewById(R.id.community_webview);
		wv.loadUrl("file:///android_asset/termcondition.html");

		ImageView backImageView = (ImageView)findViewById(R.id.back_iv);
		backImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.web_back_button:
			finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if(onBackPressedCheck())return;
		finish();
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		//super.onBackPressed();
	}
	public void notificationFromTransport(ResponseObject response) {
	}
	public void showLoading(){		
		findViewById(R.id.loading_linearlayout).setVisibility(View.VISIBLE);
		findViewById(R.id.community_webview).setVisibility(View.GONE);
	}
	public void hideLoading(){		
		findViewById(R.id.loading_linearlayout).setVisibility(View.GONE);
		findViewById(R.id.community_webview).setVisibility(View.VISIBLE);
	}
}