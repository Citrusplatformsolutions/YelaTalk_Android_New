package com.kainat.app.android;

import android.app.Activity;
import android.os.Bundle;

public class HorizontalListViewDemo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.listviewdemo);
		
		//HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
		//listview.setAdapter(mAdapter);
		
	}
	
	private static String[] dataObjects = new String[]{ "Text #1",
		"Text #2",
		"Text #3","Text #1",
		"Text #2",
		"Text #3","Text #1",
		"Text #2",
		"Text #3","Text #1",
		"Text #2",
		"Text #3","Text #1",
		"Text #2",
		"Text #3","Text #1",
		"Text #2",
		"Text #3","Text #1",
		"Text #2",
		"Text #3","Text #1",
		"Text #2",
		"Text #3" ,"Text #1",
		"Text #2",
		"Text #3"       }; 
	
	/*private BaseAdapter mAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(HorizontalListViewDemo.this);
				builder.setMessage("hello from " + v);
				builder.setPositiveButton("Cool", null);
				builder.show();
				
			}
		};

		@Override
		public int getCount() {
			return dataObjects.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
			TextView title = (TextView) retval.findViewById(R.id.title);
			Button button = (Button) retval.findViewById(R.id.clickbutton);
			button.setOnClickListener(mOnButtonClicked);
			title.setText(dataObjects[position]);
			
			return retval;
		}
		
	};*/

}
