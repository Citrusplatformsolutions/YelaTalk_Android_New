package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kainat.app.android.R;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.util.CursorToObject;
import com.kainat.app.android.util.Feed;

public class MediaAdapter extends CursorAdapter {

	RTAdaptorInf adaptorInf;
    public void setAdaptorInf(RTAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}
	public MediaAdapter(Context context, Cursor c, boolean autoRequery,RTAdaptorInf adaptorInf) {
		super(context, context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_MEDIA, null,
				null, null,
				null), autoRequery);
				this.adaptorInf= adaptorInf;
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext(	));
        View retView = inflater.inflate(R.layout.discovery_feature_media, parent, false);
        return retView;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
    	Feed.Entry entry  = CursorToObject.mediaEntryObject(cursor) ;
    	adaptorInf.viewMedia(convertView, context,  entry) ;
    }   
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}