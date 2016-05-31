package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kainat.app.android.R;
import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.CursorToObject;

public class ConversationsListAdapter extends CursorAdapter {

	Context context ;
	ConversiotionAdaptorInf adaptorInf;
    public void setAdaptorInf(ConversiotionAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}

	public ConversationsListAdapter(Context context, Cursor c, boolean autoRequery,ConversiotionAdaptorInf adaptorInf,String search ) {
		
		
		super(context, /*getCursor(context,search)*/c, autoRequery);
				this.adaptorInf= adaptorInf;
				this.context =context ;
	}

	/*public static Cursor getCursor(Context context,String search ){
		if(search!=null){
			return context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
					MessageConversationTable.PARTICIPANT_INFO+ " like ? or "+MessageConversationTable.SUBJECT+" like ? and "+MessageConversationTable.USER_ID+" = ?", new String[]{search,search,BusinessProxy.sSelf.getUserId()+"" },
					null);
		}else
			return context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
					MessageConversationTable.USER_ID+" =?", new String[] { BusinessProxy.sSelf.getUserId()+"" },
					MessageConversationTable.INSERT_TIME + " DESC");
	}*/
	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//		cursor.moveToFirst();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.conversations_list, parent, false);
        ConversationsViewHolder conversationsViewHolder = new ConversationsViewHolder();
        conversationsViewHolder.conversation_id =(TextView)retView.findViewById(R.conversation_list.conversation_id);
        conversationsViewHolder.unread_count =(TextView)retView.findViewById(R.conversation_list.unread_count);
        conversationsViewHolder.source =(TextView)retView.findViewById(R.conversation_list.source);
        //Deleted from layout
//        conversationsViewHolder.online_offline =(CImageView)retView.findViewById(R.conversation_list.online_offline);
        conversationsViewHolder.datetime =(TextView)retView.findViewById(R.conversation_list.datetime);
        conversationsViewHolder.messageView =(com.rockerhieu.emojicon.EmojiconTextView)retView.findViewById(R.conversation_list.message);
        conversationsViewHolder.me_image =(CImageView)retView.findViewById(R.conversation_list.thumb);
        conversationsViewHolder.deleteConversations =(TextView)retView.findViewById(R.conversation_list.delete);
      //Deleted from layout
//        conversationsViewHolder.checkBox =(CheckBox)retView.findViewById(R.conversation_list.checkBox);
        
        conversationsViewHolder.image =(CImageView)retView.findViewById(R.conversation_list.image);
        conversationsViewHolder.videoIcon =(CImageView)retView.findViewById(R.conversation_list.video);
        conversationsViewHolder.voice =(CImageView)retView.findViewById(R.conversation_list.voice);
        conversationsViewHolder.animIcon=(CImageView)retView.findViewById(R.conversation_list.animicon);
        conversationsViewHolder.unread =(CImageView)retView.findViewById(R.id.unread_notification);
        
        
        //not synh vir viriable name
        conversationsViewHolder.datacontainer =retView.findViewById(R.activity_list_row.loading_layout);
        conversationsViewHolder.right =retView.findViewById(R.id.dividerlanding);
        conversationsViewHolder.viewAll =retView.findViewById(R.id.dividerlanding1);
      //Deleted from layout
//        conversationsViewHolder.left =retView.findViewById(R.id.dividerlanding2);
        
        conversationsViewHolder.viewAllRight =retView.findViewById(R.id.linearLayout122);
        conversationsViewHolder.progressBar=(ProgressBar)retView.findViewById(R.conversation_list.loading);
        conversationsViewHolder.loadingText =(TextView)retView.findViewById(R.conversation_list.loading_text);
        retView.setTag(conversationsViewHolder) ;
        return retView ;
        
    }

	@Override
    public void bindView(View convertView, Context context, Cursor cursor) {
//		cursor.moveToFirst();
		ConversationList conversationList  = CursorToObject.conversationListObject(cursor,context) ;
    	adaptorInf.viewConversations(convertView, context,  conversationList) ;
    }
	@Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
	
	
}