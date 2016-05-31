package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kainat.app.android.R;
import com.kainat.app.android.helper.GroupEventTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.util.CursorToObject;
import com.kainat.app.android.util.Feed;

public class MediaCommentAdapter extends CursorAdapter {

	RTAdaptorInf adaptorInf;
    public void setAdaptorInf(RTAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}

	public MediaCommentAdapter(Context context, Cursor c, boolean autoRequery,RTAdaptorInf adaptorInf) {
		super(context, context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_MEDIA_COMMENT, null, null,
				null, GroupEventTable.INSERT_TIME+" DESC"), true);
				this.adaptorInf= adaptorInf;
	}
	
	
//	(Context context, Cursor c, boolean autoRequery) {
//		super(context, context.getContentResolver().query(
//				MediaContentProvider.CONTENT_URI_GROUPEVENT, null,
//				null, null,
//				null), autoRequery);
//	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext(	));
        View retView = inflater.inflate(R.layout.activity_comment_display, parent, false);
        return retView;
    }

	@Override
    public void bindView(View convertView, Context context, Cursor cursor) {
		Feed.Entry entry  = CursorToObject.mediaCommentEntryObject(cursor) ;
    	adaptorInf.viewMediaComment(convertView, context,  entry) ;
    }
	@Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}