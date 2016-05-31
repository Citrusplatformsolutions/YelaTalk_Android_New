package com.kainat.app.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.CommunityFallowerAdapter;
import com.kainat.app.android.adaptor.ContactInviteAdapter;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.ContactManager;
import com.kainat.app.android.inf.InviteInf;
import com.kainat.app.android.inf.InviteInfAdd;
import com.kainat.app.android.model.KainatContact;
import com.kainat.app.android.model.PhoneContact;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.CommunityFeed.Entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class KainatInviteActivity extends UIActivityManager implements InviteInfAdd{
	ListView contact_to_invite_listview;
	ContactInviteAdapter mCIA ;
	private ContactManager mContactManager;
	private List<PhoneContact> phoneContactList = new ArrayList<PhoneContact>();
	private KainatInviteActivity self = this;
	int inviteCount = 0;
	Button btn_invite;
	TextView txt_msg;
	ArrayList<String> contactInviteArr = new ArrayList<String>();
	ImageView clean_search_iv_member;
	EditText communitymemeberserch;
	protected void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kainat_invite_screen);
		//
		init();
		fetchContact();
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Invite Contact Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());	
	}
	private ProgressDialog rtDialog;
	private void fetchContact() {
		// TODO Auto-generated method stub
		//	if (null == phoneContactList) {
		//	adapter = new ContactAdapter(ContactActivity.this);
		rtDialog = ProgressDialog.show(KainatInviteActivity.this, "", getString(R.string.please_wait_while_loadin), true);
		new Thread(new Runnable() {
			public void run() {
				mContactManager  = new ContactManager(self, null);
				phoneContactList = mContactManager.getAllContact(false,true);
				//adapter.setNewItemList(phoneContactList);
				runOnUiThread(new Runnable() {
					public void run() {
						if(rtDialog != null && rtDialog.isShowing())
							rtDialog.dismiss();
						bindAdapter();
					}
				});
			}
		}).start();

		//}
	}
	public boolean getUserNameChecked(String username){
		boolean ishere = false;
		if(contactInviteArr != null)
		for(int i=0;i<contactInviteArr.size();i++){
			if(contactInviteArr.get(i).equals(username)){
				ishere = true;
				break;
			}
		}
		
		return ishere;
	}
	public void bindAdapter(){
		mCIA = new ContactInviteAdapter(this,self, phoneContactList,KainatInviteActivity.this);
		contact_to_invite_listview.setAdapter(mCIA);
	}
	private void init() {
		// TODO Auto-generated method stub
		contact_to_invite_listview = (ListView)findViewById(R.id.contact_to_invite_listview);
		btn_invite				   = (Button)findViewById(R.id.btn_invite);
		txt_msg					   = (TextView)findViewById(R.id.txt_msg);
		txt_msg.setVisibility(View.VISIBLE);
		btn_invite.setVisibility(View.GONE);
		
		clean_search_iv_member = (ImageView)findViewById(R.id.clean_search_iv_member);
		communitymemeberserch  = (EditText)findViewById(R.id.community_search_edt);
		communitymemeberserch.addTextChangedListener(new TextWatcher() {
			 
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(phoneContactList != null && phoneContactList.size() > 0){
				String text = communitymemeberserch.getText().toString();
				localFilter(text);
				}
			}
 
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
 
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
/*String text = communitymemeberserch.getText().toString();
				localFilter(text);*/
			/*	String text = communitymemeberserch.getText().toString();
				localFilter(""+arg0.toString());*/
			}
		});
		clean_search_iv_member.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				communitymemeberserch.setText("");
			}
		});
		
		
		
		btn_invite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phoneNo = null;
				for(int i=0;i<contactInviteArr.size();i++){
					if(i==0){
						phoneNo= contactInviteArr.get(i);
					}else
					{
						phoneNo= phoneNo+";" + contactInviteArr.get(i);
					}
				}
				sendSmsByVIntent(phoneNo, getString(R.string.invite_text));
			}
		});
	}

	public void sendSmsByVIntent(String phoneNo, String message) {
		Intent smsVIntent = new Intent(Intent.ACTION_VIEW);
		// prompts only sms-mms clients
		smsVIntent.setType("vnd.android-dir/mms-sms");
		smsVIntent.putExtra("address", phoneNo);
		smsVIntent.putExtra("sms_body", message);
		try{
			startActivity(smsVIntent);
		} catch (Exception ex) {
			/* Toast.makeText(MainActivity.this, "Your sms has failed...",
		                     Toast.LENGTH_LONG).show();*/
			ex.printStackTrace();
		}
	}

	protected void sendSMSMessage(String phoneNo, String message) {
		// Log.i("Send SMS", "");

		//String phoneNo = txtphoneNo.getText().toString();
		// String message = txtMessage.getText().toString();

		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNo, null, message, null, null);
			Toast.makeText(getApplicationContext(), "SMS sent.",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),"SMS faild, please try again.",Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	
	private void localFilter(String val) {
		
		if(val.equals(""))
		{
			mCIA =  new ContactInviteAdapter(this,self, phoneContactList,KainatInviteActivity.this);
				contact_to_invite_listview.setAdapter(mCIA);
			//contactAdaptor.setNewItemList(kainatContactList);
		/*	runOnUiThread(new Runnable() {
				public void run() {
					contactAdaptor.notifyDataSetChanged();
					contactAdaptor.notifyDataSetInvalidated();
				}
			});*/
		}else
		{
			// TODO Auto-generated method stub
			final List<PhoneContact> searchList = new ArrayList<PhoneContact>();
			for (PhoneContact bud : phoneContactList) {
				if (bud.name.trim().toLowerCase().contains(val.toLowerCase().toString())) {
					
					searchList.add(bud);
				}
			}
			mCIA =  new ContactInviteAdapter(this,self, searchList,KainatInviteActivity.this);
			contact_to_invite_listview.setAdapter(mCIA);
		}
		
		
		
		//String nexturl = feed.nexturl;
	//	fetchDataFromDbCommunity();
	//	List<PhoneContact> phoneContactList = new ArrayList<PhoneContact>();
	/*	final List<PhoneContact> searchList = new ArrayList<PhoneContact>();
		if(val.equals(""))
		{
			for (PhoneContact bud : phoneContactList) {
					searchList.add(bud);
				}
			mCIA.setNewItemList(searchList);
		}else
		{
			for (PhoneContact bud : phoneContactList) {
				if (bud.groupName.trim().startsWith(val.toLowerCase().toString()) || bud.groupName.trim().startsWith(val.toUpperCase().toString()) || bud.displayName.trim().startsWith(val.toLowerCase().toString()) || bud.displayName.trim().startsWith(val.toUpperCase().toString())) {
					searchList.add(bud);
				}
				if(bud.groupName.toLowerCase().contains(val.toLowerCase().toString())){
					searchList.add(bud);
				}
			}
			
			mCIA.setNewItemList(searchList);
		
		}
		*/
	}
	
	
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clickedcount(int value, int position, String username) {
		// TODO Auto-generated method stub
		if(value == 1)
		{
			inviteCount = inviteCount + 1;
		}else
		{
			inviteCount = inviteCount - 1;
		}

		btn_invite.setText(getString(R.string.invite)+ " ("+inviteCount+")");

		if(inviteCount <= 0){
			txt_msg.setVisibility(View.VISIBLE);
			btn_invite.setVisibility(View.GONE);
		}else
		{
			txt_msg.setVisibility(View.GONE);
			btn_invite.setVisibility(View.VISIBLE);

		}
		contactnumber(position,value,username);
	}

	public String contactnumber(int pos,int value, String val){
		String number = null;
		try{
			number = val;
			//number =  phoneContactList.get(pos).getMobileno();
		}catch(Exception e){

		}
		if(value == 1)
		{
			if(number!=null)
				contactInviteArr.add(number);
		}else
		{
			for(int i=0;i<contactInviteArr.size();i++){
				if(contactInviteArr.get(i).equals(number)){
					contactInviteArr.remove(i);
				}
			}
		}

		return null;	
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}
}

