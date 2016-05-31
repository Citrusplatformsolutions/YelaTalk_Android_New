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
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CursorToObject;

public class CommunityAdapter extends CursorAdapter {

	RTAdaptorInf adaptorInf;
    public void setAdaptorInf(RTAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}
	public CommunityAdapter(Context context, Cursor c, boolean autoRequery,RTAdaptorInf adaptorInf) {
		super(context, context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_COMMUNITY, null,
				null, null,
				null), autoRequery);
				this.adaptorInf= adaptorInf;
	}
	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext(	));
        View retView = inflater.inflate(R.layout.discovery_feature_community, parent, false);
        return retView;
    }
    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
    	CommunityFeed.Entry entry  = CursorToObject.communityEntryObject(cursor) ;
    	adaptorInf.viewCommunity(convertView, context,  entry) ;
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}