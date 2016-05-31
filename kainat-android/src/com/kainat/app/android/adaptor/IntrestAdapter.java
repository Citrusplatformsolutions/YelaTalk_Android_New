package com.kainat.app.android.adaptor;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kainat.app.android.R;
import com.kainat.app.android.inf.ItemSelectionListener;

public class IntrestAdapter extends BaseAdapter {


	private Context context;
	private final String[] gridValues;
	private final String[] img;

	ArrayList<String> list = new ArrayList<String>();
	private boolean isSelectionArray[] = null;

	private ItemSelectionListener iSelectionListenter = null;
	//Constructor to initialize values
	public IntrestAdapter(Context context, String[ ] gridValues,String[ ] img, ItemSelectionListener mSelectionListenter) {

		this.context        = context;
		this.gridValues     = gridValues;
		this.img     		= img;
		this.iSelectionListenter = mSelectionListenter;
		isSelectionArray = new boolean[gridValues.length];
	}

	@Override
	public int getCount() {

		// Number of times getView method call depends upon gridValues.length
		return gridValues.length;
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}


	// Number of times getView method call depends upon gridValues.length

	public View getView(final int position, View convertView, ViewGroup parent) {

		// LayoutInflator to call external grid_item.xml file

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);

			// get layout from grid_item.xml ( Defined Below )

			gridView = inflater.inflate( R.layout.intrest_row , null);

			// set value into textview

			TextView abc_text = (TextView) gridView.findViewById(R.id.grid_item_label);
			abc_text.setVisibility(View.VISIBLE);
			abc_text.setText(gridValues[position]);

			final ImageView selImageView = (ImageView)gridView.findViewById(R.id.add_btn_invite);
			selImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if(isSelectionArray[position]){
						isSelectionArray[position] = false;
					}else{
						isSelectionArray[position] = true;
					}

					if(isSelectionArray[position]){
						selImageView.setImageResource(R.drawable.xx_tick_invite);
						list.add(gridValues[position]);
					}else{
						selImageView.setImageResource(R.drawable.xx_plus_invite);
						if(list.contains(gridValues[position])){
							list.remove(gridValues[position]);
						}
					}

					if(list != null){
						iSelectionListenter.getSelectionItem(list.size());
					}
				}
			});

			// set image based on selected text

			//ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
			com.kainat.app.android.uicontrol.CImageView imageView = (com.kainat.app.android.uicontrol.CImageView) gridView.findViewById(R.id.grid_item_image);
			int res  = context.getResources().getIdentifier(img[position].toLowerCase(), "drawable", context.getPackageName());
			imageView.setImageResource(res);

		} else {

			gridView = (View) convertView;
		}

		return gridView;
	}

	public String getSelection(){
		String temp = "";
		if(list.size() > 0){
			for(byte b = 0; b < list.size(); b ++){
				temp = temp  + list.get(b) +",";
			}
			temp = temp.substring(0, temp.length() - 1).trim();
		}
		return temp;
	}

}
