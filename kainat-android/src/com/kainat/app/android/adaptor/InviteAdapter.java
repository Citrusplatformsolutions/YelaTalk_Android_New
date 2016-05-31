package com.kainat.app.android.adaptor;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.kainat.app.android.KainatMultiImageSelection;
import com.kainat.app.android.R;
import com.kainat.app.android.model.ImageList;
import com.kainat.app.android.model.InviteModel;

public class InviteAdapter extends BaseAdapter {

	ArrayList<InviteModel> mList;
	LayoutInflater mInflater;
	Context mContext;
	SparseBooleanArray mSparseBooleanArray;

	public InviteAdapter(Context context, ArrayList<InviteModel> imageList) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mSparseBooleanArray = new SparseBooleanArray();
		mList = new ArrayList<InviteModel>();
		this.mList = imageList;

	}

/*	public ArrayList<ImageList> getCheckedItems() {
		ArrayList<ImageList> mTempArry = new ArrayList<ImageList>();

		for(int i=0;i<mList.size();i++) {
			if(mSparseBooleanArray.get(i)) {
				mTempArry.add(mList.get(i));
			}
		}

		return mTempArry;
	}*/
	
/*	public void setCheckedItems(int[] mList) {
		for(int i = 0; i < mList.length; i++) {
			mSparseBooleanArray.put(mList[i], true);
		}
		
	}
*/
	@Override
	public int getCount() {
		return 5;
		//return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.row_invite_item, null);
		}
		final Button accept_btn = (Button)convertView.findViewById(R.id.btn_accept);
		final Button ignore_btn = (Button)convertView.findViewById(R.id.btn_ignore);
		/*
		 * final CheckBox mCheckBox  = (CheckBox) convertView.findViewById(R.id.checkBox1);
		   final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
*/
		accept_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "Accept ="+position, Toast.LENGTH_LONG).show();
			}
		});
		ignore_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "Ignore ="+position, Toast.LENGTH_LONG).show();
			}
		});
		return convertView;
	}

}