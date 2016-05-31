package com.kainat.app.android.adaptor;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kainat.app.android.R;
import com.kainat.app.android.bean.LandingPageBean;
import com.kainat.app.android.util.CursorToObject;

public class ActivityAdapter extends CursorAdapter {

	RTAdaptorInf adaptorInf;
    public void setAdaptorInf(RTAdaptorInf adaptorInf) {
		this.adaptorInf= adaptorInf;
	}
    public ActivityAdapter(Context context, Cursor c, boolean autoRequery,RTAdaptorInf adaptorInf) {
		super(context, c, autoRequery);
		this.adaptorInf=adaptorInf;
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.landing_activity, parent, false);
        return retView;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
    	LandingPageBean landingPageBean  = CursorToObject.activityObject(cursor) ;
    	adaptorInf.viewActivity(convertView, context,  landingPageBean,false) ;
    	if((getCount()-1)==cursor.getPosition()){
    	}
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      if (observer != null) {
        super.unregisterDataSetObserver(observer);
      }
//      QuickContactBadge badge ;
    } 
}