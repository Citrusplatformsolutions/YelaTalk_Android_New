package com.kainat.app.android;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.autocomplete.tag.ChipsAdapter;
import com.kainat.app.autocomplete.tag.ChipsItem;
import com.kainat.app.autocomplete.tag.ChipsMultiAutoCompleteTextview;


public class TokenDummyActivity extends Activity {

	ChipsAdapter chipsAdapter = null;
	ArrayList<ChipsItem> preSelection =  new ArrayList<ChipsItem>();
	private String preSel = "Ravi Vikash Tariq";
	ChipsMultiAutoCompleteTextview ch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.token_dummy_activity);

		 ch = (ChipsMultiAutoCompleteTextview) findViewById(R.id.chipsMultiAutoCompleteTextview1);
		//ch.setText(preSel);
	//	ch.setSelection(preSel.length());
		
			
//		String[] countries = getResources().getStringArray(R.array.country);
//		TypedArray imgs = getResources().obtainTypedArray(R.array.flags);


		ArrayList<ChipsItem> arrCountry = new ArrayList<ChipsItem>();


		//		for(int i = 0;i<countries.length;i++){
		//			arrCountry.add(new ChipsItem(countries[i], android.R.drawable.presence_offline));//imgs.getResourceId(i, -1) ));@android:drawable/presence_offline
		//			Log.i("Main Activity", arrCountry.get(i).getTitle() +  " = " + arrCountry.get(i).getImageid());
		//		}

		//Log.i("MainActivity", "Array :" + arrCountry.size());

		/*ChipsItem obj1 = new ChipsItem();
		obj1.setTitle("Indore");
		obj1.setImageid(android.R.drawable.presence_offline);
		preSelection.add(obj1);

		ChipsItem obj2 = new ChipsItem();
		obj2.setTitle("Bhopal");
		obj2.setImageid(android.R.drawable.presence_offline);
		preSelection.add(obj2);

		ChipsItem obj3 = new ChipsItem();
		obj3.setTitle("Mumbai");
		obj3.setImageid(android.R.drawable.presence_offline);
		preSelection.add(obj3);

		ChipsItem obj4 = new ChipsItem();
		obj4.setTitle("Delhi");
		obj4.setImageid(android.R.drawable.presence_offline);
		preSelection.add(obj4);

		ChipsItem obj5 = new ChipsItem();
		obj5.setTitle("Pune");
		obj5.setImageid(android.R.drawable.presence_offline);
		preSelection.add(obj5);

		ChipsItem obj6 = new ChipsItem();
		obj6.setTitle("Banglore");
		obj6.setImageid(android.R.drawable.presence_offline);
		preSelection.add(obj6);*/

		chipsAdapter = new ChipsAdapter(this, arrCountry);
		ch.setAdapter(chipsAdapter);

		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Token Dumy Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//ch.setChips();
	}

	public void onClick(View view){
		if(chipsAdapter != null){
			String obj[] = chipsAdapter.getTags();
			for(byte b = 0; b < obj.length; b++){
				System.out.println("Tag value = "+b+" , "+obj[b]);
			}
		}
	}

}
