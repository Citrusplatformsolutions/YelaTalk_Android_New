package com.kainat.app.android;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.format.SettingData;


import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends UIActivityManager implements OnClickListener {
	private TextView updateVersion;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		setContentView(R.layout.kainat_about_screen);
		
		findViewById(R.id.prev).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		updateVersion = (TextView) findViewById(R.id.version_update);
		updateVersion.setPaintFlags(updateVersion.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
		updateVersion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Build.VERSION.SDK_INT >= 11)
					new GetVersionUpdateInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				else
					new GetVersionUpdateInfo().execute();
			}
		});
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("About Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}
	public void notificationFromTransport(ResponseObject response) {
	}
	public void onBackPressed() {
		if(onBackPressedCheck())return;
		if (getIntent().getByteExtra("PAGE", (byte) 1) == 1) {
			finish();
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
			return;
		}
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}

	@Override
	public void onError(int i) {
		// TODO Auto-generated method stub
		
	}
	
	ProgressDialog loadingDialog;
	private class GetVersionUpdateInfo extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = ProgressDialog.show(AboutActivity.this, "", getString(R.string.checking), true);
		}
		@Override
		protected String doInBackground(String... urls) {
			String responseStr = null;
			try {				
				String url = "http://www.yelatalkprod.com/tejas/feeds/app/version/latest?platform=android";
				
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				httpget.addHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
				try {
					HttpResponse responseHttp = client.execute(httpget);
					if (responseHttp != null) {
						responseStr = EntityUtils.toString(responseHttp.getEntity());
					}
				} catch (ClientProtocolException e) {

				} catch (IOException e) {

				}
			} catch (Exception e) {
			}
			if(loadingDialog != null && loadingDialog.isShowing())
				loadingDialog.dismiss();
			return responseStr;
		}
		@Override
		protected void onPostExecute(String response) {
			if(loadingDialog != null && loadingDialog.isShowing())
				loadingDialog.dismiss();
			String current_version = YelatalkApplication.clientProperty.getProperty(ClientProperty.CLIENT_VERSION);
			// {"desc":"Latest version information","status":"1","LatestVersion":"yelatalk_ios_1_4_5"}
			if(response != null)
				Log.i("AboutActivity",  "onPostExecute : " +response);
			if(response != null){
				 JSONObject json_response;
				try {
					json_response = new JSONObject(response);
					if(json_response.has("status") && json_response.get("status").equals("1")){
						String new_version = json_response.optString("LatestVersion").toString();
						if(new_version.equals(current_version))
							Toast.makeText(AboutActivity.this, "You have latest version!", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(AboutActivity.this, "New version is available, please take update from google play.", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
				Toast.makeText(AboutActivity.this, "You have latest version!", Toast.LENGTH_SHORT).show();
		}
	}
		
}
