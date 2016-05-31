package com.kainat.app.android.adaptor;


import com.kainat.app.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DiscoverAdapter extends BaseAdapter {
	 
    private Context context;
    private final String[] gridValues;
    private final String[] img;
 
    //Constructor to initialize values
    public DiscoverAdapter(Context context, String[ ] gridValues,String[ ] img) {

        this.context        = context;
        this.gridValues     = gridValues;
        this.img     		= img;
    }
     
    @Override
    public int getCount() {
         
        // Number of times getView method call depends upon gridValues.length
        return gridValues.length;
    }
 
    @Override
    public Object getItem(int position) {
         
        return null;
    }
 
    @Override
    public long getItemId(int position) {
         
        return 0;
    }
     
     
    // Number of times getView method call depends upon gridValues.length
     
    public View getView(int position, View convertView, ViewGroup parent) {
 
        // LayoutInflator to call external grid_item.xml file
         
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        View gridView;
 
        if (convertView == null) {
 
            gridView = new View(context);
 
            // get layout from grid_item.xml ( Defined Below )

            gridView = inflater.inflate( R.layout.row_discover , null);
 
            // set value into textview
             
            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);

            textView.setText(gridValues[position]);
 
            // set image based on selected text
             
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);
        	int res 	  = context.getResources().getIdentifier(img[position].toLowerCase(), "drawable", context.getPackageName());
        	imageView.setImageResource(res);
         //  String arrLabel = gridValues[ position ];
 
        } else {

           gridView = (View) convertView;
        }
 
        return gridView;
    }
}
