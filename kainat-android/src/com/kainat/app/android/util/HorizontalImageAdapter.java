package com.kainat.app.android.util;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kainat.app.android.R;

public class HorizontalImageAdapter extends BaseAdapter {

 private Activity context;

 private static ImageView imageView;

 private List plotsImages;

 private static ViewHolder holder;
 private LayoutInflater l_Inflater;

 public HorizontalImageAdapter(Activity context, List plotsImages) {

  this.context = context;
  this.plotsImages = plotsImages;
  l_Inflater = LayoutInflater.from(context);
 }

 @Override
 public int getCount() {
  return plotsImages.size();
 }

 @Override
 public Object getItem(int position) {
  return null;
 }

 @Override
 public long getItemId(int position) {
  return 0;
 }

 @Override
 public View getView(int position, View convertView, ViewGroup parent) {

  if (convertView == null) {

   holder = new ViewHolder();

   convertView = l_Inflater.inflate(R.layout.listview_item, null);
   holder = new ViewHolder();
   holder.imageView = (ImageView) convertView.findViewById(R.id.icon);

   convertView.setTag(holder);

  } else {

   holder = (ViewHolder) convertView.getTag();
  }

  holder.imageView.setImageDrawable((Drawable)plotsImages.get(position));

  return convertView;
 }

 private static class ViewHolder {
  ImageView imageView;
 }

}
