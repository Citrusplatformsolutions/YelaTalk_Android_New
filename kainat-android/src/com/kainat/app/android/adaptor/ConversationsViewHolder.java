package com.kainat.app.android.adaptor;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.uicontrol.CImageView;

public class ConversationsViewHolder {
	public TextView messageView ;
	public TextView subject ;
	public CImageView me_image ;
	public View me_image_layout ;
	public LinearLayout video ;
	public CImageView videoIcon ;
	public CImageView animIcon ;
	public TextView datetime ;
	public TextView conversation_id ;
	public TextView unread_count ;
	public TextView source ;
	public ImageView menuLeft ;
	public CImageView voice ;
	public TextView voicePlay ;
	public ConversationList conversationList;
	public LinearLayout images;
	public LinearLayout animLayoutRightImage;
	public LinearLayout animLayoutLeftImage;
	public TextView receving_voiceLeft ;
	public TextView receving_videoLeft ;
	public View viewAll;
	public TextView notification ;
	public CImageView online_offline ;
	public TextView sep ;
	public View datacontainer ;
	public CheckBox checkBox ;
	public CImageView arrowViewForSystem ;
	public TextView messageViewRight ;
//	public CImageView me_imageRight ;
	public LinearLayout videoRight ;
	public CImageView otherImageRight ;
	public View other_image_layout ;
	public TextView datetimeRight ;
	public TextView conversation_idRight ;
	public TextView unread_countRight ;
	public TextView sourceRight ;
	public LinearLayout imagesRight;
	public TextView menuRight ;
	public TextView special ;
	public TextView receving_voiceRight ;
	public TextView receving_videoRight ;
	public TextView sending_status_right ;
	public CImageView voiceRight;
	public TextView voiceRightPlay;
	public View viewAllRight;
	public TextView notificationRight ;
	public View left ;
	public View right ;
	public ImageView multiselect_delete;
	
	public TextView deleteConversations ; 
	
//	public TextView friend_invite ;
//	public TextView inbox_accept ;
//	public TextView inbox_decline ;
//	public TextView inbox_ignore ;
	
	public TextView view_rtml ;
	public TextView view_rtml_right ;
	
	
//	public CImageView voice ;
	public CImageView unread ;
	public CImageView image ;
	
	public ProgressBar progressBar ;
	public TextView loadingText ;
	
}
