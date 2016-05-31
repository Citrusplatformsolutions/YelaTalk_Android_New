package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kainat.app.android.R;
import com.kainat.app.android.bean.MediaLikeDislike;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.util.CursorToObject;

public class MediaUserLikeAdapter extends CursorAdapter {

	MediaUserLikeAdaptorInf adaptorInf;
    public void setAdaptorInf(MediaUserLikeAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}

	public MediaUserLikeAdapter(Context context, Cursor c, boolean autoRequery,MediaUserLikeAdaptorInf adaptorInf) {
		super(context, context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_LIKES, null, null,
				null, null), true);
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
        View retView = inflater.inflate(R.layout.like_user_row, parent, false);
        return retView;
    }

	@Override
    public void bindView(View convertView, Context context, Cursor cursor) {
		MediaLikeDislike mediaLikeDislike  = CursorToObject.cursorToMediaLikeDislike(cursor) ;
    	adaptorInf.viewMediaUserLikeDislik(convertView, context,  mediaLikeDislike) ;
    }
	@Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}