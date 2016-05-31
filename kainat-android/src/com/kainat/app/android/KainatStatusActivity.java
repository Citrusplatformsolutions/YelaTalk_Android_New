package com.kainat.app.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.RequestType;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.format.SettingData;

public class KainatStatusActivity extends UIActivityManager{
	EditText edt_status;
	TextView txt_count,prev,button_post;
	ProgressDialog rTDialog;
	String str_status="";
	String oldStatusString = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kainat_status_activity);
		//
		init();
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Status Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}

	private void init() {
		// TODO Auto-generated method stub
		edt_status = (EditText)findViewById(R.id.edt_status);
		txt_count  = (TextView)findViewById(R.id.txt_count);
		prev  	   = (TextView)findViewById(R.id.prev);
		button_post= (TextView)findViewById(R.id.button_post);

		//Get Old status from Data Model
		oldStatusString = (String) DataModel.sSelf.getObject(DMKeys.USER_STATUS + "STATUS");
		DataModel.sSelf.getRemoveBoolean(DMKeys.USER_STATUS); 
		if(oldStatusString != null && oldStatusString.length() > 0)
			edt_status.setText(oldStatusString);

		button_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//finish();

				str_status= edt_status.getText().toString();
				if(str_status != null && str_status.trim().length() > 0)
					new ProfileTask().execute("");
				else
				{
					//Show Alert
					Toast.makeText(KainatStatusActivity.this, getString(R.string.please_enter_status_message), Toast.LENGTH_LONG).show();
				}
			}
		});


		prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		txt_count.setText(getString(R.string._140));
		TextWatcher mTextEditorWatcher = new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				txt_count.setText(""+(140-(s.length())));
			}

			public void afterTextChanged(Editable s) {
			}
		};
		edt_status.addTextChangedListener(mTextEditorWatcher);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	// AsynTask for Status Post

	private class ProfileTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
//			rTDialog = new RTDialog(KainatStatusActivity.this, null, getString(R.string.updating));
			rTDialog = ProgressDialog.show(KainatStatusActivity.this, "", getString(R.string.loading_dot), true);
			rTDialog.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
				Hashtable<String, String> postParam = new Hashtable<String, String>();
				String responseString = "" ;
				HttpClient httpclientE = new DefaultHttpClient();
				HttpPost httppostw = new HttpPost( "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/postcurrentstatus?");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("statusText", new String(str_status.getBytes("UTF-8"))));
					httppostw.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
//					System.out.println(nameValuePairs.toString());
					httppostw.setHeader("RT-Params", ClientProperty.RT_PARAMS);				
					httppostw.setHeader("client_param",ClientProperty.CLIENT_PARAMS + "::requesttype##"+ RequestType.LOGIN_FB_AUTO);
					httppostw.setHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(),SettingData.sSelf.getPassword()));
					HttpResponse response = httpclientE.execute(httppostw);
					HttpEntity r_entity = response.getEntity();
					responseString = EntityUtils.toString(r_entity);
//					System.out.println("KainatStatusActivity::ProfileTask::doInBackground():responseString -> "+responseString);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//					responseString = AdConnectionManager.getInstance()
				//							.uploadByteData2(url, postParam, headerParam, null);

				Gson gson = new Gson();
				final com.kainat.app.android.bean.Error error = gson.fromJson(responseString,
						com.kainat.app.android.bean.Error.class);
				if(error!=null){
					if(error.getStatus()==2){
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AppUtil.showTost(KainatStatusActivity.this, error.getDesc());		
							}
						});
						return null;
					}
				}
				return responseString;

			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(KainatStatusActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			closeDialog();
			if(response!=null)
			{	//{"desc":"Status Posted","status":"1"}
				//showmsg(response);
				JSONObject js;
				try {
					js = new JSONObject(response);
					final String desc = js.getString("desc");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							AppUtil.showTost(KainatStatusActivity.this, desc);//+"\n"+e.getLocalizedMessage()) ;
						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}
			super.onPostExecute(response);
		}
	}

	private void closeDialog(){
		if(rTDialog!=null && rTDialog.isShowing())
			rTDialog.dismiss() ;
	}


	//

}