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

public class FeatureUserAdapter extends CursorAdapter {

	RTAdaptorInf adaptorInf;
	
	
    public void setAdaptorInf(RTAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}
	public FeatureUserAdapter(Context context, Cursor c, boolean autoRequery,RTAdaptorInf adaptorInf) {
//		context.startManagingCursor(c);

		super(context, context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_FEATUREDUSER, null,
				FeatureUserTable.STORY_ID+" != ? ", new String[] { "-1"},
				FeatureUserTable.STORY_ID+" DESC"), autoRequery);
				this.adaptorInf= adaptorInf;
	}
	
	
//	(Context context, Cursor c, boolean autoRequery) {
//		super(context, context.getContentResolver().query(
//				MediaContentProvider.CONTENT_URI_FEATUREDUSER, null,
//				null, null,
//				null), autoRequery);
//	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext(	));
        View retView = inflater.inflate(R.layout.discovery_feature_user, parent, false);
        return retView;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
    	FeaturedUserBean featuredUserBean = CursorToObject.featuredUserObject(cursor) ;
    	//System.out.println("featuredUserBean=="+featuredUserBean.)
    	


    	adaptorInf.viewFeatureUser(convertView, context,  featuredUserBean) ;
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}