package com.kainat.app.android;

import java.util.Vector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ResponseObject;

public class PushNotificationActivity extends UIActivityManager implements
		OnClickListener {

	Vector<String> notification = new Vector<String>() ;
	ListView lv1;
	Customlistadapter cus;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_notification_screen);
		notification = C2DMBroadcastReceiver.notification ;
		lv1 = (ListView) findViewById(R.id.list);
		cus = new Customlistadapter();
		lv1.setAdapter(cus);
		C2DMBroadcastReceiver.notification = new Vector<String>() ;
		
		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("PushNotification Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	@Override
	public void onBackPressed() {
		C2DMBroadcastReceiver.notification = new Vector<String>() ;
		super.onBackPressed();
	}
	
	public class Customlistadapter extends BaseAdapter {

		private LayoutInflater mInflater;
		Context c;

		public int getCount() {
			return notification.size(); // number of
																// listview
																// rows.
		}

		public Object getItem(int arg0) {
			return notification.get(arg0);
		}

		public View getView(final int pos, View arg1, ViewGroup arg2) {
			final ViewHolder vh;
			 this.mInflater = LayoutInflater.from(PushNotificationActivity.this);  
			   
			vh = new ViewHolder();
			String s = (String) getItem(pos);
			if (arg1 == null) {
				arg1 = mInflater.inflate(R.layout.push_notification_row, arg2,
						false);
				vh.tv1 = (TextView) arg1.findViewById(R.id.textView1);
				vh.tv2 = (TextView) arg1.findViewById(R.id.textView2);
			} else {
				arg1.setTag(vh);
			}
			try{
			vh.tv1.setText(s.substring(0,s.indexOf(":")));
			vh.tv2.setText(s.substring(s.indexOf(":")+1));
			}catch (Exception e) {
				vh.tv1.setVisibility(View.GONE) ;
				vh.tv2.setText(s);
			}
			
			return arg1;
		}

		class ViewHolder // use a viewholder for smooth scrolling and
							// performance.
		{
			TextView tv1, tv2;

		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

}
