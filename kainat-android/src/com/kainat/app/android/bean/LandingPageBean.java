package com.kainat.app.android.bean;

import com.kainat.app.android.util.Utilities;


public class LandingPageBean {
	
	public static final String CONTENT_TYPE_PICTURE = "image" ;
	public static final String CONTENT_TYPE_VIDEO = "video" ;
	public static final String CONTENT_TYPE_AUDIO = "audio" ;
	public static final String CONTENT_TYPE_TEXT = "text" ;
	public static final String SEPERATOR = "^" ;
	
	public int column_id = 0;
	public String parent_id = null;
	public String story_id = null;
	public String tracking = null;
	public String sue = null;
	public String title = null;
	public String story_title = null;
	public String story_type  = null;
	public String image_content_thumb_urls = null;
	public String []image_content_thumb_urlsArr = null;
	public String image_content_id = null;
	public String image_content_urls = null;
	public String image_content_click_action = null;
	public String video_content_thumb_urls = null;
	public String video_content_id = null;
	public String video_content_urls = null;
	public String video_content_click_action = null;
	public String voice_content_thumb_urls = null;
	public String voice_content_id = null;
	public String voice_content_urls = null;
	public String voice_content_click_action = null;
	public String desc = null;
	public String descOri = null;
	public String desc2 = null;
	public String published = null;
	public String updated = null;
	public String like = null;
	public String dislike = null;
	public String userthumb_url = null;
	public String comment_url = null;
	public String share_url = null;
	public String prev_url = null;
	public String next_url = null;
	public String firstname = null;
	public String lastname = null;
	public String username = null;
	public String message_state = null;
	public String drm = null;
	public String action = null;
	public String loginUser = null;
	public String tab = null;
	public long date ;	
	public String other_user_thumb_url = null;
	public String other_user_firstname = null;
	public String other_user_lastname = null;
	public String other_user_username = null;
	public String other_user_profile_url = null;
	public String userthumb_profile_url = null;
	public int opType = 0;
	public String mediaText = null;
	public int direction = 1;
	public long insertDate = System.currentTimeMillis();
	
	
	public String[] getStringArrayAfterSplit(String s){
		return Utilities.split(new StringBuffer(s), SEPERATOR) ;
	}
	@Override
	public String toString() {
		return "[story_id : "+story_id 
		+"][title : "+title+"]["
		+"][opType : "+opType+"]["
		+"][direction : "+direction+"]["
		+"][insertDate : "+insertDate+"]["
		+"[desc : "+desc+"]["
		+"[descOri : "+descOri+"]["
		+"[mediaText : "+mediaText+"]["
		+"[published : "+published+"]["
		+"][username : "+username+"]["
		+"[firstname : "+firstname+"]["
		+"[lastname : "+lastname+"]["
		+"][next_url : "+next_url+"]["
		+"[prev_url : "+prev_url+"]["
		+"[tracking : "+tracking+"]["
		+"[story_type : "+story_type+"]["
		+"[userthumb_profile_url : "+userthumb_profile_url+"]["
		+"[userthumb_url : "+userthumb_url+"]["
		+"[image_content_thumb_urls : "+image_content_thumb_urls+"]["
		+"[video_content_thumb_urls : "+video_content_thumb_urls+"]["
		+"[voice_content_thumb_urls : "+voice_content_thumb_urls+"]["
		
		+"[voice_content_thumb_urls : "+voice_content_urls+"]["
		+"[voice_content_thumb_urls : "+video_content_urls+"]["
		+"[voice_content_thumb_urls : "+image_content_urls+"]["
		+"[voice_content_thumb_urls : "+image_content_id+"]["
		+"[voice_content_thumb_urls : "+video_content_id+"]["
		+"[voice_content_thumb_urls : "+voice_content_id+"]["
		+"[voice_content_thumb_urls : "+image_content_click_action+"]["
		+"[voice_content_thumb_urls : "+voice_content_click_action+"]["
		+"[voice_content_thumb_urls : "+video_content_click_action+"]    "
		;
	}
}
