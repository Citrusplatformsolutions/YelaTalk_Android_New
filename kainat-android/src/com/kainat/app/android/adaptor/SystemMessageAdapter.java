
package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kainat.app.android.R;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.CursorToObject;
import com.rockerhieu.emojicon.EmojiconTextView;

public class SystemMessageAdapter extends CursorAdapter {
	Context context ;
	ConversiotionAdaptorInf adaptorInf;
    public void setAdaptorInf(ConversiotionAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}

	public SystemMessageAdapter(Context context, Cursor c, boolean autoRequery,ConversiotionAdaptorInf adaptorInf) {
		super(context, c, autoRequery);
				this.adaptorInf= adaptorInf;
	this.context = context ;			
	}
	
	public void multiSelect(){
		
	}
	
	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.message_system, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder();
        
//        retView.findViewById(R.message_row.datacontainer).setBackgroundResource(R.drawable.landingpagebackground) ; ;
        messageViewHolder.datacontainer = retView.findViewById(R.message_row.datacontainer);
        messageViewHolder.messageView =(TextView)retView.findViewById(R.message_row.message);
        messageViewHolder.datetime =(TextView)retView.findViewById(R.message_row.date_time);
        messageViewHolder.me_image =(CImageView)retView.findViewById(R.message_row.me_image);
        messageViewHolder.me_image_layout =retView.findViewById(R.message_row.me_image_layout);
        messageViewHolder.video =(LinearLayout)retView.findViewById(R.message_row.landing_activity_videoLayout);
//      messageViewHolder.otherImage =(CImageView)retView.findViewById(R.message_row_right.other_image);
        messageViewHolder.images =(LinearLayout)retView.findViewById(R.message_row.landing_activity_ImageLayout);
        messageViewHolder.conversation_id =(TextView)retView.findViewById(R.message_row.conversation_id);
        messageViewHolder.menuLeft =(ImageView)retView.findViewById(R.message_row.menu);
        messageViewHolder.voice =(CImageView)retView.findViewById(R.message_row.voice);
        
        messageViewHolder.viewAll =retView.findViewById(R.message_row.all_layout);
        messageViewHolder.notification =(TextView)retView.findViewById(R.message_row.notification);
        
        messageViewHolder.subject =(TextView)retView.findViewById(R.conversation.subject);
        
        messageViewHolder.receving_voiceLeft =(TextView)retView.findViewById(R.message_row.receving_voice);
        messageViewHolder.receving_videoLeft =(TextView)retView.findViewById(R.message_row.receving_video);
        messageViewHolder.sep =(TextView)retView.findViewById(R.activity_list_row.line);
        messageViewHolder.sep.setVisibility(View.GONE) ;
        messageViewHolder.messageViewRight =(TextView)retView.findViewById(R.message_row_right.message);
        messageViewHolder.datetimeRight =(TextView)retView.findViewById(R.message_row_right.date_time);
//      messageViewHolder.me_imageRight =(CImageView)retView.findViewById(R.message_row_right.other_image);
        messageViewHolder.videoRight =(LinearLayout)retView.findViewById(R.message_row_right.landing_activity_videoLayout);
        messageViewHolder.otherImageRight =(CImageView)retView.findViewById(R.message_row_right.other_image);
        messageViewHolder.other_image_layout =retView.findViewById(R.message_row_right.other_image_layout);
        messageViewHolder.imagesRight =(LinearLayout)retView.findViewById(R.message_row_right.landing_activity_ImageLayout);
        messageViewHolder.conversation_idRight =(TextView)retView.findViewById(R.message_row_right.conversation_id);
        messageViewHolder.menuRight =(TextView)retView.findViewById(R.message_row_right.menu);
        messageViewHolder.voiceRight =(CImageView)retView.findViewById(R.message_row_right.voice);
        messageViewHolder.viewAllRight =retView.findViewById(R.message_row_right.all_layout);
        messageViewHolder.notificationRight =(TextView)retView.findViewById(R.message_row_right.notification);
        
//        messageViewHolder.friend_invite =(TextView)retView.findViewById(R.message_row.view_Friend_Request);
        
        messageViewHolder.sending_status_right =(TextView)retView.findViewById(R.message_row_right.sending_statud);
        messageViewHolder.receving_voiceRight =(TextView)retView.findViewById(R.message_row_right.receving_voice);
        messageViewHolder.receving_videoRight =(TextView)retView.findViewById(R.message_row_right.receving_video);
        messageViewHolder.right =retView.findViewById(R.message_row_right.right);
        
        messageViewHolder.right =retView.findViewById(R.message_row_right.right);
        messageViewHolder.left =retView.findViewById(R.message_row.left);
        
//        messageViewHolder.inbox_accept =(TextView)retView.findViewById(R.message_row.inbox_accept);
//        messageViewHolder.inbox_decline =(TextView)retView.findViewById(R.message_row.inbox_decline);
//        messageViewHolder.inbox_ignore =(TextView)retView.findViewById(R.message_row.inbox_ignore);
        
//        messageViewHolder.view_rtml =(TextView)retView.findViewById(R.message_row_right.view_rtml);
        messageViewHolder.view_rtml =(TextView)retView.findViewById(R.message_row.view_rtml);
        messageViewHolder.arrowViewForSystem=(CImageView)retView.findViewById(R.id.inboxLayout_arrowImage);
        messageViewHolder.multiselect_delete  = (ImageView)retView.findViewById(R.message_row.multiselect_imgeview);
        
        retView.setTag(messageViewHolder) ;
        return retView ;
       
    }

	@Override
    public void bindView(View convertView, Context context, Cursor cursor) {
//		System.out.println("------------saved cursor count :adaptor "+getCount());
		Message message  = CursorToObject.conversationObject(cursor,context) ;
//		convertView.findViewById(R.message_row.activity_layout).setTag(messageViewHolder) ;;
    	adaptorInf.viewMessage(convertView, context,  message) ;
    }
	@Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}

//package com.kainat.app.android.adaptor;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.DataSetObserver;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CursorAdapter;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.kainat.app.android.R;
//import com.kainat.app.android.bean.Message;
//import com.kainat.app.android.uicontrol.CImageView;
//import com.kainat.app.android.util.CursorToObject;
//
//public class SystemMessageAdapter extends CursorAdapter {
//
//	ConversiotionAdaptorInf adaptorInf;
//    public void setAdaptorInf(ConversiotionAdaptorInf adaptorInf) {
//		this.adaptorInf= adaptorInf;
//	}
//
//	public SystemMessageAdapter(Context context, Cursor c, boolean autoRequery,ConversiotionAdaptorInf adaptorInf) {
//		super(context, c, autoRequery);
//				this.adaptorInf= adaptorInf;
//				
//	}
//	
//	
//	
//	@Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//		
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View retView = inflater.inflate(R.layout.system_message, parent, false);
//        MessageViewHolder messageViewHolder = new MessageViewHolder();
//        messageViewHolder.messageView =(TextView)retView.findViewById(R.message_row.message);
//        messageViewHolder.datetime =(TextView)retView.findViewById(R.message_row.date_time);
//        messageViewHolder.me_image =(CImageView)retView.findViewById(R.message_row.me_image);
//        messageViewHolder.me_image_layout =retView.findViewById(R.message_row.me_image_layout);
//        messageViewHolder.video =(LinearLayout)retView.findViewById(R.message_row.landing_activity_videoLayout);
////      messageViewHolder.otherImage =(CImageView)retView.findViewById(R.message_row_right.other_image);
//        messageViewHolder.images =(LinearLayout)retView.findViewById(R.message_row.landing_activity_ImageLayout);
//        messageViewHolder.conversation_id =(TextView)retView.findViewById(R.message_row.conversation_id);
//        messageViewHolder.menuLeft =(TextView)retView.findViewById(R.message_row.menu);
//        messageViewHolder.voice =(CImageView)retView.findViewById(R.message_row.voice);
//        
//        messageViewHolder.viewAll =retView.findViewById(R.message_row.all_layout);
//        messageViewHolder.notification =(TextView)retView.findViewById(R.message_row.notification);
//        
//        messageViewHolder.subject =(TextView)retView.findViewById(R.conversation.subject);
//        
//        messageViewHolder.receving_voiceLeft =(TextView)retView.findViewById(R.message_row.receving_voice);
//        messageViewHolder.receving_videoLeft =(TextView)retView.findViewById(R.message_row.receving_video);
//        
//       //akm messageViewHolder.messageViewRight =(TextView)retView.findViewById(R.message_row_right.message);
//       //akm messageViewHolder.datetimeRight =(TextView)retView.findViewById(R.message_row_right.date_time);
////      messageViewHolder.me_imageRight =(CImageView)retView.findViewById(R.message_row_right.other_image);
//       //akm messageViewHolder.videoRight =(LinearLayout)retView.findViewById(R.message_row_right.landing_activity_videoLayout);
//        //akmmessageViewHolder.otherImageRight =(CImageView)retView.findViewById(R.message_row_right.other_image);
//        //akmmessageViewHolder.other_image_layout =retView.findViewById(R.message_row_right.other_image_layout);
//       //akm messageViewHolder.imagesRight =(LinearLayout)retView.findViewById(R.message_row_right.landing_activity_ImageLayout);
//      //akm  messageViewHolder.conversation_idRight =(TextView)retView.findViewById(R.message_row_right.conversation_id);
//       //akm messageViewHolder.menuRight =(TextView)retView.findViewById(R.message_row_right.menu);
//       //akm messageViewHolder.voiceRight =(CImageView)retView.findViewById(R.message_row_right.voice);
//       //akm messageViewHolder.viewAllRight =retView.findViewById(R.message_row_right.all_layout);
//       //akm messageViewHolder.notificationRight =(TextView)retView.findViewById(R.message_row_right.notification);
//        
//        
//       //akm messageViewHolder.sending_status_right =(TextView)retView.findViewById(R.message_row_right.sending_statud);
//        //akmmessageViewHolder.receving_voiceRight =(TextView)retView.findViewById(R.message_row_right.receving_voice);
//      //akm  messageViewHolder.receving_videoRight =(TextView)retView.findViewById(R.message_row_right.receving_video);
//      //akm  messageViewHolder.right =retView.findViewById(R.message_row_right.right);
//        
//      //akm  messageViewHolder.right =retView.findViewById(R.message_row_right.right);
//        messageViewHolder.left =retView.findViewById(R.message_row.left);
//        
//        messageViewHolder.friend_invite =(TextView)retView.findViewById(R.message_row.view_Friend_Request);
//        //messageViewHolder.inbox_accept =(TextView)retView.findViewById(R.message_row.inbox_accept);
//       // messageViewHolder.inbox_decline =(TextView)retView.findViewById(R.message_row.inbox_decline);
//       // messageViewHolder.inbox_ignore =(TextView)retView.findViewById(R.message_row.inbox_ignore);
//        
////        messageViewHolder.view_rtml =(TextView)retView.findViewById(R.message_row_right.view_rtml);
//        messageViewHolder.view_rtml =(TextView)retView.findViewById(R.message_row.view_rtml);
//        
//        
//        retView.setTag(messageViewHolder) ;
//        return retView ;
//       
//    }
//
//	@Override
//    public void bindView(View convertView, Context context, Cursor cursor) {
////		System.out.println("------------saved cursor count :adaptor "+getCount());
//		Message message  = CursorToObject.conversationObject(cursor) ;
//    	adaptorInf.viewMessage(convertView, context,  message) ;
//    }
//	@Override
//    public void unregisterDataSetObserver(DataSetObserver observer) {
//      if (observer != null) {
//        super.unregisterDataSetObserver(observer);
//      }
//    } 
//}