package com.kainat.app.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;

public class CountrySelectionActivity extends Activity implements OnClickListener, TextWatcher, OnSchedularListener {
	private ListView list;
	private ListAdapter adapter;
	private int selectedItem = -1;
	private Timer mSearchTimer;
	private static final short SEARCH_INIT_DELAY = 500;
	private static final byte TASK_SEARCH = 1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.country_list);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		selectedItem = getIntent().getIntExtra("country_index", -1);

		list = (ListView) findViewById(R.id.searchCountry_mainList);
		adapter = new ListAdapter();
		for (int i = 0; i < Constants.COUNTRY_CODE.length; i++) {
			adapter.addItem(Constants.COUNTRY_LIST[i], Constants.COUNTRY_CODE[i]);
		}
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ItemClickListener());

		EditText editor = (EditText) findViewById(R.id.searchCountry_inputBox);
		editor.addTextChangedListener(this);
		
		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("CountrySelection Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_Button:
			finish();
			break;
		}
	}

	public void afterTextChanged(Editable s) {

	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (null != mSearchTimer) {
			mSearchTimer.cancel();
			mSearchTimer = null;
		}
		mSearchTimer = new Timer();
		mSearchTimer.schedule(new OtsSchedularTask(this, new Byte(TASK_SEARCH), (byte)0), SEARCH_INIT_DELAY);
	}

	public void onTaskCallback(Object parameter, byte req) {
		byte task = ((Byte) parameter).byteValue();
		switch (task) {
		case TASK_SEARCH:
			List<String> searchList = new ArrayList<String>();
			List<String> searchCodeList = new ArrayList<String>();
			String text = ((EditText) findViewById(R.id.searchCountry_inputBox)).getText().toString().trim().toUpperCase();
			if ("".equals(text)) {
				searchList = Arrays.asList(Constants.COUNTRY_LIST);
				searchCodeList = Arrays.asList(Constants.COUNTRY_CODE);
				adapter.setNewData(searchList, searchCodeList);
				return;
			}
			for (int i = 0; i < Constants.COUNTRY_LIST.length; ++i) {
				String country = Constants.COUNTRY_LIST[i];
				if (country.trim().toUpperCase().startsWith(text)) {
					searchList.add(country);
					searchCodeList.add(Constants.COUNTRY_CODE[i]);
				}
			}
			adapter.setNewData(searchList, searchCodeList);
			break;
		}
	}

	private class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String item = (String) adapter.getItem(position);
			if (item == null || "".equals(item)) {
				return;
			}
			int index = findItemPosition(item);
			Intent intent = getIntent();
			intent.putExtra("country_index", index);
			setResult(RESULT_OK, intent);
			finish();
		}

		private int findItemPosition(String item) {
			for (int i = 0; i < Constants.COUNTRY_LIST.length; i++) {
				if (Constants.COUNTRY_LIST[i].equals(item)) {
					return i;
				}
			}
			return 0;
		}
	}

	private class ListAdapter extends BaseAdapter {
		private List<String> countries = new ArrayList<String>();
		private List<String> codes = new ArrayList<String>();

		public int getCount() {
			return countries.size();
		}

		public Object getItem(int position) {
			return countries.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public boolean areAllItemsEnabled() {
			return true;
		}

		public boolean isEnabled(int position) {
			return true;
		}

		public void addItem(String country, String code) {
			countries.add(country);
			codes.add(code);
		}

		public void setNewData(List<String> _countries, List<String> _codes) {
			this.countries.clear();
			this.countries.addAll(_countries);

			this.codes.clear();
			this.codes.addAll(_codes);

			runOnUiThread(new Runnable() {
				public void run() {
					notifyDataSetChanged();
					notifyDataSetInvalidated();
				}
			});
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.country_row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.country_name);
			label.setText(countries.get(position));
			TextView area = (TextView) row.findViewById(R.id.country_code);
			area.setText(codes.get(position));
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			if (countries.get(position).equals("")) {
				row.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.FILL_PARENT, android.widget.AbsListView.LayoutParams.WRAP_CONTENT));
				area.setVisibility(View.GONE);
				icon.setVisibility(View.GONE);
				row.setClickable(false);
				row.setFocusable(false);
				row.setFocusableInTouchMode(false);
				row.setEnabled(false);
				row.setPadding(0, 0, 0, 0);
				label.setBackgroundColor(R.color.listcolor);
			} else {
				row.setPadding(5, 0, 5, 0);
				if (selectedItem == position) {
					icon.setImageResource(R.drawable.check);
				}
			}
			return row;
		}
	}
}