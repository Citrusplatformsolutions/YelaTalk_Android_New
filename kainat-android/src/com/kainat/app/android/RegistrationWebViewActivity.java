package com.kainat.app.android;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;

public class RegistrationWebViewActivity extends UIActivityManager {
//	public static String TERMS_CONDITIONS = "http://iphone.rocketalk.com/mypage/termsandc/comservices";

	private WebView mWebView;
	private Button back_button;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_webview_layout);
//		showAnimationDialog("RockeTalk", "Loading..", false);
		showLoading();
		back_button = (Button) findViewById(R.id.web_back_button);
		back_button.setOnClickListener(this);
		back_button.setVisibility(View.GONE) ;
		mWebView = (WebView) findViewById(R.id.community_webview);
//		System.out.println("--------COM_TERM_AND_SERVICES-"+COM_TERM_AND_SERVICES);
		mWebView.loadUrl("http://" + Urls.WAP_HOST + "/mypage/termsandc/services");
//		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebViewClient(new HelloWebViewClient());
		mWebView.setWebChromeClient(new WebChromeClient() {
			
			public void onProgressChanged(WebView view, int progress) {
				
				final String t = view.getTitle();
				if(t!=null){
					try{
					String ar[] = Utilities.split(t,"~||~");
//					Terms of Service~||~TermsService
					((TextView) findViewById(R.id.title)).setText(ar[0]);
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				if (progress == 100) {
					view.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							hideLoading();
						}
					},5000);
				}
			}
			public void onPageFinished(WebView view, String url) {
				final String t = view.getTitle();
				if(t!=null){
					try{
					String ar[] = Utilities.split(t,"~||~");
//					Terms of Service~||~TermsService
					((TextView) findViewById(R.id.title)).setText(ar[0]);
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
				view.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hideLoading();
					}
				},5000);
			}
		});
	}

	private void changeTitle(final String title) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (title == null || (title != null && title.trim().length() <= 0)) {
					((TextView) findViewById(R.id.title)).setText("RockeTalk");
				} else {
					((TextView) findViewById(R.id.title)).setText(title);
				}
			}
		});
		//myScreenName("GRID");
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
	private class HelloWebViewClient extends WebViewClient {
		
		public HelloWebViewClient() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			System.out.println("---------url------: "+url);
			if(url.indexOf("mailto")!=-1)//RTMLMAIL:ID='support@rocketalk.com'
			{
				Intent it = new Intent(Intent.ACTION_SEND);
				it.setType("text/plain");

				it.putExtra(Intent.EXTRA_EMAIL,
						new String[] { getString(R.string.tocheck) });
				// it.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");//com.google.android.gm.ComposeActivityGmail
				Intent intent = new Intent(
						android.content.Intent.ACTION_SEND);
				intent.setType("text/html");
				// intent.setType("text/plain");
				final PackageManager pm = getPackageManager();
				final List<ResolveInfo> matches = pm.queryIntentActivities(
						intent, 0);
				ResolveInfo best = null;
				for (final ResolveInfo info : matches) {
					if (info.activityInfo.packageName.endsWith(".gm")
							|| info.activityInfo.name.toLowerCase()
									.contains("gmail")) {
						best = info;
						break;
					}
				}

				if (best != null) {
					it.setClassName(best.activityInfo.packageName,
							best.activityInfo.name);
				}
				try {
					startActivity(it);
				} catch (android.content.ActivityNotFoundException ex) {
					ex.printStackTrace();
					startActivity(Intent.createChooser(it, "Send mail..."));
					Toast.makeText(RegistrationWebViewActivity.this,
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
				}
				return true ;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
	}
}