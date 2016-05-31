package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kainat.app.android.R;
import com.kainat.app.android.bean.FeaturedUserBean;
import com.kainat.app.android.helper.FeatureUserTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.util.CursorToObject;

public class FeatureUserListAdapter extends CursorAdapter {

	RTAdaptorInf adaptorInf;
    public void setAdaptorInf(RTAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}

	public FeatureUserListAdapter(Context context, Cursor c, boolean autoRequery,RTAdaptorInf adaptorInf) {
		super(context, context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_FEATUREDUSER, null,
				null, null,
				FeatureUserTable.INSERT_TIME+" ASC"), autoRequery);
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
        View retView = inflater.inflate(R.layout.discovery_featureuser_list, parent, false);
        return retView;
    }

	@Override
    public void bindView(View convertView, Context context, Cursor cursor) {
		FeaturedUserBean featuredUserBean  = CursorToObject.featuredUserObject(cursor) ;
    	adaptorInf.viewFeatureUserList(convertView, context,  featuredUserBean) ;
    }
	@Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}