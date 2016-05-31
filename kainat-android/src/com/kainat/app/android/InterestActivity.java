package com.kainat.app.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.IntrestAdapter;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.inf.ItemSelectionListener;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.ResponseObject;

public class InterestActivity extends UIActivityManager implements
OnClickListener, ItemSelectionListener {

	StringBuffer buffer = new StringBuffer();
	Handler handler = new Handler();
	LinearLayout cat1_ll, cat2_ll, cat3_ll, cat4_ll;

	String[] web = { "travel", "lifestyle", "Spirituality", "Food", "Design",
			"Prayers", "Hadiths", "Culture & Arts"

	};
	int[] imageId = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher };

	View done;
	ListView listView;
	static String[] listItemArray = new String[] {"Sports" ,
		"News", 
		"Entertainment" ,
		"Lifestyle",
	"Spirituality" };
	static String[] listItemArray_IMG = new String[] { "Sports" ,
		"News", 
		"Entertainment" ,
		"Lifestyle",
	"Spirituality"};
	IntrestAdapter discoverAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.kainat_interest_activity);
		done 	 = findViewById(R.id.done);
		done.setOnClickListener(this);
		if(getLocale().equalsIgnoreCase("en")){ 
			listItemArray =	getResources().getStringArray(R.array.community_recomendad2_intrest);
		}
		else{
			listItemArray =	getResources().getStringArray(R.array.community_recomendad_intrest_ar);
		}

		listView = (ListView) findViewById(R.id.grdview_discover);
		discoverAdapter		 =  new IntrestAdapter( this,listItemArray ,listItemArray_IMG, this);
		listView.setAdapter(discoverAdapter);
		/*listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				KeyValue.setString(InterestActivity.this, KeyValue.INTEREST,
						listItemArray[position]);
				UIActivityManager.selectedCategoryName = listItemArray[position];
				Intent intent = new Intent(InterestActivity.this,
						KainatContactActivity.class);
				intent.putExtra("HEADER_TITLE", getString(R.string.friend));
				intent.putExtra("REG_FLOW", true);
				startActivity(intent);
				finish();
			}

		});*/
		try {
			handleLoginResponseNew(null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Interest Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(getLocale().equalsIgnoreCase("en")){
			listItemArray = getResources().getStringArray(R.array.community_recomendad2_intrest);
		}else
		{
			listItemArray = getResources().getStringArray(R.array.community_recomendad_intrest_ar);
		}
		discoverAdapter.notifyDataSetChanged();
		super.onResume();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			break;
		case R.id.done:
			String selItems  = discoverAdapter.getSelection();
//			System.out.println("Selection item = "+selItems);
			KeyValue.setString(InterestActivity.this, KeyValue.INTEREST, selItems);
			UIActivityManager.selectedCategoryName = selItems;
			Intent intent = new Intent(InterestActivity.this,
					KainatHomeActivity.class);
			startActivity(intent);
			/*Intent intent = new Intent(InterestActivity.this,
					KainatContactActivity.class);
			intent.putExtra("HEADER_TITLE", getString(R.string.friend));
			intent.putExtra("REG_FLOW", true);
			startActivity(intent);*/
			finish();
			break;
		}
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	public void getSelectionItem(int item) {
		// TODO Auto-generated method stub
		if(item  > 0){
			done.setVisibility(View.VISIBLE);
		}else{
			done.setVisibility(View.GONE);
		}
	}

}
