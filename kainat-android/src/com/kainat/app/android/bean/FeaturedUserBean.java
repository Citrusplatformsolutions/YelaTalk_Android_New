package com.kainat.app.android.bean;

public class FeaturedUserBean {

		public String id ;
		public String title ;
		public String totalCount ;
		public String displayName ;
		public String mediaPosts ;
		public String mediaFollowers ;
		public String communitiesMember ;
		public String thumbUrl ;
		public String profileUrl ;
		public String prevUrl ;
		public String nextUrl ;
		public String tracking;	
		public String seo ;	
		public long insertTime = System.currentTimeMillis();
		public int more ;
		@Override
		public String toString() {
			return "[id : "+id 
			+"][title : "+title+"]["
			+"[totalCount : "+totalCount+"]["
			+"[displayName : "+displayName+"]["
			+"[mediaPosts : "+mediaPosts+"]["
			+"[mediaFollowers : "+mediaFollowers+"]["
			+"[communitiesMember : "+communitiesMember+"]["
			+"[thumbUrl : "+thumbUrl+"]["
			+"[profileUrl : "+profileUrl+"]["
			+"[prevUrl : "+prevUrl+"]["
			+"[nextUrl : "+nextUrl+"]["
			+"[tracking : "+tracking+"]["
			+"[seo : "+seo+"]["
			;
		}
}
