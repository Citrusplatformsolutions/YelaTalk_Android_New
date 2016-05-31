package com.kainat.app.android.adaptor;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kainat.app.android.KainatInviteActivity;
import com.kainat.app.android.R;
import com.kainat.app.android.model.PhoneContact;

public class ContactInviteAdapter  extends BaseAdapter{
	private List<PhoneContact> mItems 		 = new ArrayList<PhoneContact>();
	private Context context;
	private ArrayList<String> invitationlist = new ArrayList<String>();
	int[] selected_contact;
	KainatInviteActivity mA;
	int val = 0;

	public ContactInviteAdapter(KainatInviteActivity kIA,Context context, List<PhoneContact> Items , KainatInviteActivity a) {
		this.context 	= context;
		mItems       	= Items;
		invitationlist	= null;
		selected_contact = new int[Items.size()];
		mA 				= kIA ;
		val 			= 0;
	}
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	public int getCount() {
		return mItems.size();
	}

	public PhoneContact getItem(int position) {
		if (position > -1 && position < mItems.size())
			return mItems.get(position);
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View listItemView;
		final PhoneContact element 	= getItem(position);
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listItemView			= inflator.inflate(R.layout.row_invite_contact, null);
		} else {
			listItemView 			= (View) convertView;
		}
		((TextView) listItemView.findViewById(R.id.txt_name)).setText(element.name);
		((TextView) listItemView.findViewById(R.id.mobile_invite)).setText(element.getMobileno());
		final ImageView add_btn_invite = (ImageView)listItemView.findViewById(R.id.add_btn_invite);
		Log.v("GETVIEW.check-->",""+selected_contact[position]);
		if(mA.getUserNameChecked(element.getMobileno())){
			add_btn_invite.setImageResource(R.drawable.xx_tick_invite);
		}else{
			add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
		}

		add_btn_invite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v("add_btn_invite.setOnClickListener-->",""+selected_contact[position]);
				//				if(selected_contact[position] == 0 && val<10){
				//					selected_contact[position] = 1;	
				//					val = val + 1;
				//					add_btn_invite.setImageResource(R.drawable.xx_tick_invite);
				//				}else if(selected_contact[position] == 1 ){
				//					val = val - 1;
				//					selected_contact[position] = 0;
				//					add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
				//				}

				if( val<10){
					if(mA.getUserNameChecked(element.getMobileno())){
						//
						selected_contact[position] = 0;	
						val--;
						add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
						mA.clickedcount(0, position,element.getMobileno());
					}
					else{
						selected_contact[position] = 1;	
						val++;
						add_btn_invite.setImageResource(R.drawable.xx_tick_invite);
						mA.clickedcount(1, position,element.getMobileno());
					}
				}
				else {
					if(selected_contact[position] == 1){
						selected_contact[position] = 0;	
						val--;
						add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
						mA.clickedcount(0, position,element.getMobileno());
					}
					else{

					}
				
				}

				if(val<10){
				}
				else{
					//show toast
				}
			}
		});

		return listItemView;
	}
}