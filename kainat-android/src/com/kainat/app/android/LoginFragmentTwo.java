package com.kainat.app.android;

import com.kainat.app.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginFragmentTwo extends Fragment{
	int mCurrentPage;
	int imageRes ;
	String mlang;
	ImageView imageView_top;
	TextView textView_header,textView_desc;
	LinearLayout main_linearlayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	Bundle data  = getArguments();
	//	mCurrentPage = data.getInt("current_page", 0);

	}
	LoginFragmentTwo(int imageRes, String lang){
		this.imageRes = imageRes ;
		mlang		  = lang;
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v 			= inflater.inflate(R.layout.activity_tour, container,false);	
		imageView_top 	= (ImageView) v.findViewById(R.id.imageView_top);
		textView_header = (TextView)v.findViewById(R.id.textView_header);
		textView_desc 	= (TextView)v.findViewById(R.id.textView_desc);
		main_linearlayout= (LinearLayout)v.findViewById(R.id.main_linearlayout);
		LoginFragmentTwoCheck(imageRes,mlang);
		/*ImageView imageView = new ImageView(getActivity()) ;
		imageView.setImageResource(imageRes) ;
		imageView.setScaleType(ScaleType.FIT_XY);*/
		return v;		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// If there is new text or color assigned, set em
	/*	imageView_top.setImageResource(R.drawable.icon_number_indicator);
		textView_header.setText(R.string.feel);
		textView_desc.setText(R.string.feel_at_home);*/
	}
	
	public void LoginFragmentTwoCheck(int imageRes, String lang){
		if (imageRes == 0) {
			//feel
			imageView_top.setImageResource(R.drawable.icon);
			textView_header.setText(R.string.feel);
			textView_desc.setText(R.string.feel_at_home);
			main_linearlayout.setBackgroundResource(R.drawable.tour_1);
			if(mlang!=null && mlang.equalsIgnoreCase("ar")){
				main_linearlayout.setBackgroundResource(R.drawable.tour_11);
				//res = R.drawable.tour_11 ;
			}else
			{
				//textView_header.setText(R.string.a_moment_ago);
			}

		} else if (imageRes == 1) {

			//talk
			imageView_top.setImageResource(R.drawable.icon);
			textView_header.setText(R.string.talk);
			textView_desc.setText(R.string.start_a_converstion);
			//int res = R.drawable.tour_2 ;//talk
			main_linearlayout.setBackgroundResource(R.drawable.tour_2);
			if(mlang!=null && mlang.equalsIgnoreCase("ar")){
				main_linearlayout.setBackgroundResource(R.drawable.tour_22);
			}
			// res = R.drawable.tour_22 ;

		} else if (imageRes == 2) {
			//discover
			imageView_top.setImageResource(R.drawable.icon);
			textView_header.setText(R.string.discover);
			textView_desc.setText(R.string.discover_converstions);
			//int res = R.drawable.tour_3 ;//discover
			main_linearlayout.setBackgroundResource(R.drawable.tour_3);
			if(mlang!=null && mlang.equalsIgnoreCase("ar")){
				main_linearlayout.setBackgroundResource(R.drawable.tour_33);
			}
			//	res = R.drawable.tour_33 ;

		}
		else if (imageRes == 3) {
			//int res = R.drawable.tour_4 ;//connect
			imageView_top.setImageResource(R.drawable.icon);
			textView_header.setText(R.string.connect_tour);
			textView_desc.setText(R.string.bond_over);
			main_linearlayout.setBackgroundResource(R.drawable.tour_3);
			if(mlang!=null && mlang.equalsIgnoreCase("ar")){
				main_linearlayout.setBackgroundResource(R.drawable.tour_33);
			}//res = R.drawable.tour_44 ;


		}
	}

}