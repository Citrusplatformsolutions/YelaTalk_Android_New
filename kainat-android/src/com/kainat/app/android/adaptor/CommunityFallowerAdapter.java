package com.kainat.app.android.adaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.kainat.app.android.KainatCommunityFallowerContact;
import com.kainat.app.android.R;
import com.kainat.app.android.model.KainatContact;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.CommunityFeed.Entry;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CommunityFallowerAdapter  extends BaseAdapter{
	private List<KainatContact> mItems 		 = new ArrayList<KainatContact>();
	private Context context;
	private ArrayList<String> invitationlist = new ArrayList<String>();
	int[] selected_contact;
	KainatCommunityFallowerContact mA;
	int val = 0;


	public CommunityFallowerAdapter(KainatCommunityFallowerContact kIA,Context context, List<KainatContact> Items) {
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
	public void setNewItemList(ArrayList<KainatContact> itemList) {
		mItems.clear();
		mItems.addAll(itemList);

	}
	public void showUpdatedContacts(List<KainatContact> Items) {
		mItems = Items;
	}

	public int getCount() {
		return mItems.size();
	}

	public KainatContact getItem(int position) {
		if (position > -1 && position < mItems.size())
			return mItems.get(position);
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View listItemView;
		final KainatContact element 	= getItem(position);
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listItemView			= inflator.inflate(R.layout.row_invite_contact, null);
		} else {
			listItemView 			= (View) convertView;
		}
		((TextView) listItemView.findViewById(R.id.txt_name)).setText(element.name);
		((TextView) listItemView.findViewById(R.id.mobile_invite)).setText(element.getMobileNumber());
		final ImageView add_btn_invite = (ImageView)listItemView.findViewById(R.id.add_btn_invite);
		//		Log.v("GETVIEW.check-->",""+selected_contact[position]);
		/*	final ImageView item_img = (ImageView) listItemView.findViewById(R.id.invite_image);
		imageManager.loadForCommunity(true);
		//
		imageManager.download(element, item_img, handler, ImageDownloader.TYPE_THUMB_BUDDY);
		 */
		final ImageView item_img = (ImageView) listItemView.findViewById(R.id.invite_image);
		ImageDownloader imageManager = new ImageDownloader(2);
		item_img.setTag(element.getUserName());
		//		System.out.println("MemberImg Url = "+element.getUserName());
		imageManager.download(element.getUserName(), item_img, handler,imageManager.TYPE_THUMB_BUDDY);
		//
		if(mA.getUserNameChecked(element.getUserName()))
		{
			add_btn_invite.setImageResource(R.drawable.xx_tick_invite);
		}else{
			add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
		}

		//
		if(element.getIsInChannel() == 1){
			((ProgressBar)listItemView.findViewById(R.id.loading_progress)).setVisibility(View.GONE);
			add_btn_invite.setVisibility(View.VISIBLE);
			add_btn_invite.setImageResource(R.drawable.communitymembers);
		}else if(element.getIsInChannel() == 0){
			add_btn_invite.setVisibility(View.GONE);
			((ProgressBar)listItemView.findViewById(R.id.loading_progress)).setVisibility(View.VISIBLE);
		}else{
			((ProgressBar)listItemView.findViewById(R.id.loading_progress)).setVisibility(View.GONE);
			add_btn_invite.setVisibility(View.VISIBLE);
		/*	if(selected_contact[position] == 1){
				add_btn_invite.setImageResource(R.drawable.xx_tick_invite);
			}else{
				add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
			}*/
		}

		add_btn_invite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(element.getIsInChannel() == 1){
					//					mA.removeFollower(element.getUserName());
					return;
				}
				//				Log.v("add_btn_invite.setOnClickListener-->",""+selected_contact[position]);
				//				if(selected_contact[position] == 0 && val<10){
				//					selected_contact[position] = 1;	
				//					val = val + 1;
				//					add_btn_invite.setImageResource(R.drawable.xx_tick_invite);
				//				}else if(selected_contact[position] == 1 ){
				//					val = val - 1;
				//					selected_contact[position] = 0;
				//					add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
				//				}

				if( val<20){
					if(mA.getUserNameChecked(element.getUserName())){
						//
						selected_contact[position] = 0;	
						val--;
						add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
						mA.clickedcount(0, position,element.getUserName());
					}
					else{
						selected_contact[position] = 1;	
						val++;
						add_btn_invite.setImageResource(R.drawable.xx_tick_invite);
						mA.clickedcount(1, position,element.getUserName());
					}
				}
				else {
					if(selected_contact[position] == 1){
						selected_contact[position] = 0;	
						val--;
						add_btn_invite.setImageResource(R.drawable.xx_plus_invite);
						mA.clickedcount(0, position,element.getUserName());
					}
					else{

					}
				}

				if(val<20){
				}
				else{
					//show toast
				}
			}
		});

		return listItemView;
	}
	ImageDownloader imageManager = new ImageDownloader();
	ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

		@Override
		public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
			try {

			} catch (Exception e) {
			}
		}
	};	
}