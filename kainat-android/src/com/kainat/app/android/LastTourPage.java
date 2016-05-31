package com.kainat.app.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class LastTourPage extends Fragment{
int mCurrentPage;
	int imageRes ;
	RelativeLayout mainlayout;
	public LastTourPage(int imageRes){
		this.imageRes=imageRes ;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		Bundle data = getArguments();
		mCurrentPage = data.getInt("current_page", 0);*/
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.last_tour_screen, container,false);	
		/*ImageView imageView = (ImageView)v.findViewById(R.id.imageView1);//new ImageView(getActivity()) ;
		imageView.setImageResource(imageRes) ;
		imageView.setScaleType(ScaleType.FIT_XY);*/
	/*	ImageView imageView_top 	= (ImageView) v.findViewById(R.id.imageView_top_l);
		TextView textView_header 	= (TextView)v.findViewById(R.id.textView_header_l);
		TextView textView_desc 		= (TextView)v.findViewById(R.id.textView_desc_l);*/
		mainlayout					= (RelativeLayout)v.findViewById(R.id.mainlayout_l);
		if(imageRes==0)
		{
			mainlayout.setBackgroundResource(R.drawable.tour_33);
		}
		else
		{
			mainlayout.setBackgroundResource(R.drawable.tour_3);
		}
		/*imageView_top.setImageResource(R.drawable.icon);
		textView_header.setText(R.string.connect_tour);
		textView_desc.setText(R.string.bond_over);*/
		v.findViewById(R.id.go).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
				Intent intent = new Intent(getActivity(),
						KainatContactActivity.class);
				intent.putExtra("HEADER_TITLE", getString(R.string.friend));
				intent.putExtra("REG_FLOW", true);
				startActivity(intent);
				/*Intent intent = new Intent(getActivity(),InterestActivity.class);
				intent.putExtra("REG_FLOW", true);
				startActivity(intent);*/
			}
		});
		return v;//imageView;		
	}
}