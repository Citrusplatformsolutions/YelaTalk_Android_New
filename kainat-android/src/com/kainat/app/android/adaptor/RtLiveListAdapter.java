package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kainat.app.android.R;
import com.kainat.app.android.bean.GroupEventBean;
import com.kainat.app.android.helper.GroupEventTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.util.CursorToObject;

public class RtLiveListAdapter extends CursorAdapter {

	RTAdaptorInf adaptorInf;
    public void setAdaptorInf(RTAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}

	public RtLiveListAdapter(Context context, Cursor c, boolean autoRequery,RTAdaptorInf adaptorInf) {
		super(context, context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_GROUPEVENT, null,
				null, null,
				GroupEventTable.INSERT_TIME+" ASC"), autoRequery);
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
        View retView = inflater.inflate(R.layout.discovery_rtlive_list_row, parent, false);
        return retView;
    }

	@Override
    public void bindView(View convertView, Context context, Cursor cursor) {
    	GroupEventBean groupEventBean = CursorToObject.groupEventObject(cursor) ;
    	adaptorInf.viewRtLiveList(convertView, context,  groupEventBean) ;
    }
	@Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
    } 
}