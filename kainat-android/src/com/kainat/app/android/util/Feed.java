package com.kainat.app.android.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.graphics.drawable.Drawable;

public final class Feed implements Serializable{
	transient public Drawable waterMarkDrawable;

	transient public Drawable drawable;
	transient public static final String TAG_ID = "id";
	transient public static final String TAG_USER_ID = "userId";
	transient public static final String TAG_userName = "userName";
	transient public static final String TAG_firstName = "firstName";
	transient public static final String TAG_lastName = "lastName";
	
	transient public static final String TAG_UPDATED = "updated";
	transient public static final String TAG_WATER_MARK = "watermarkurl";//320x420
	transient public static final String TAG_WATER_MARK_WIDE = "watermarkurlwide";//320x240 watermarkurlwide
	transient public static final String TAG_NEXT_URL = "nexturl";
	transient public static final String TAG_PREV_URL = "prevurl";
	transient public static final String TAG_CATEGORY = "category";
	transient public static final String TAG_TITLE = "title";
	transient public static final String TAG_SEO = "seo";
	transient public static final String TAG_SUBTITLE = "subtitle";
	transient public static final String TAG_LOGO = "logo";
	transient public static final String TAG_LINK = "link";
	transient public static final String TAG_AUTHOR = "author";
	transient public static final String TAG_FEATURED_ACTION = "featuredAction";
	
	transient public static final String TAG_NAME = "name";
	transient public static final String TAG_URI = "uri";
	transient public static final String TAG_GENERATOR = "generator";
	transient public static final String TAG_OPENSEARCH_TOTALRESULTS = "openSearch:totalResults";
	transient public static final String TAG_OPENSEARCH_STARTINDEX = "openSearch:startIndex";
	transient public static final String TAG_OPENSEARCH_ITEMPERPAGES = "openSearch:itemsPerPage";
	transient public static final String TAG_OPENSEARCH_TOTALRESULTS2 = "totalResults";
	transient public static final String TAG_OPENSEARCH_STARTINDEX2 = "startIndex";
	transient public static final String TAG_OPENSEARCH_ITEMPERPAGES2 = "itemsPerPage";
	transient public static final String TAG_RT_TRACKING = "rt:tracking";
	transient public static final String TAG_RT_TRACKING2 = "tracking";
	transient public static final String TAG_ENTRY = "entry";
	transient public static final String TAG_PUBLISHED = "published";
	transient public static final String TAG_CONTENT = "content";
	transient public static final String TAG_SUMMARY = "summary";
	transient public static final String TAG_RT_STATISTICS = "rt:statistics";
	transient public static final String TAG_RT_STATISTICS2 = "statistics";
	transient public static final String TAG_RT_RATING = "rt:rating";
	transient public static final String TAG_RT_RATING2 = "rating";
	transient public static final String TAG_RT_VOTING = "rt:voting";
	transient public static final String TAG_RT_VOTING2 = "voting";
	transient public static final String TAG_COMMENT_URL = "comment_url";
	transient public static final String TAG_COMMENT_COUNT = "comment_count";
	transient public static final String TAG_FBURL = "fburl";
	transient public static final String TAG_FIRST_NAME = "firstName";
	transient public static final String TAG_LAST_NAME = "lastName";
	transient public static final String STREEM_MEDIA = "STREEM_MEDIA";
	transient public static final String TAG_CURRENT_LOCATION = "currentLocation";
	transient public static final String TAG_DEFAULT_LOCATION = "defaultLocation";
	//comment tag
	transient public static final String TAG_OBJECT_ID = "object_id";
	transient public static final String TAG_PARENT_ID = "parent_id";
	transient public static final String TAG_USERNAME = "username";
	transient public static final String TAG_TEXT = "text";
	transient public static final String TAG_LIKES = "likes";
	transient public static final String TAG_DISLIKES = "dislikes";
	transient public static final String TAG_IMG_URL = "img_url";
	transient public static final String TAG_AUDIO_URL = "audio_url";
	transient public static final String TAG_VIDEO_URL = "vid_url";
	transient public static final String TAG_LIKE_VALUE = "like_value";
	
	transient public static final String TAG_IMG_THUMB_URL = "img_thumb_url";
	transient public static final String TAG_USER_THUMB_URL = "thumbUrl";
	
	transient public static final String TAG_FOLLOWERS = "followers";
	transient public static final String TAG_FOLLOWING	 = "following";
	transient public static final String TAG_MEDIACOUNT	 = "mediacount";
	transient public static final String TAG_MEDIACOUNTURL	 = "mediaCountURL";
	public static final String TAG_FOLLOWERSURL	 = "followersURL";
	transient public static final String TAG_FOLLOWINGURL	 = "followingURL";
	transient public static final String TAG_subscriberUserName	 = "subscriberUserName";
	transient public static final String TAG_subscriberFirstName	 = "subscriberFirstName";
	transient public static final String TAG_subscriberLastName	 = "subscriberLastName";
	transient public static final String TAG_mediastatsurl	 = "mediastatsurl";
	transient public static final String TAG_profileUrl	 = "profileUrl";
	transient public static final String TAG_RT_LOCATION = "location";//
	transient public static final String TAG_ERROR = "errorMsg";//
	
	
	//Attributes
	transient public static final String ATTR_SCHEME = "scheme";
	transient public static final String ATTR_TERM = "term";
	transient public static final String ATTR_REL = "rel";
	transient public static final String ATTR_TYPE = "type";
	transient public static final String ATTR_HREF = "href";
	transient public static final String ATTR_SRC = "src";
	transient public static final String ATTR_URI = "uri";
	transient public static final String ATTR_CODE = "code";
	transient public static final String ATTR_VOTECOUNT = "votecount";
	transient public static final String ATTR_MEDIACOUNT = "mediacount";
	transient public static final String ATTR_AVERAGERATING = "averagerating";
	transient public static final String ATTR_VOTEUPCOUNT = "voteupcount";
	transient public static final String ATTR_VOTEDOWNCOUNT = "votedowncount";
	transient public static final String ATTR_SELFVOTE = "selfvote";
	transient public static final String ATTR_VIEWCOUNT = "viewcount";
	transient public static final String ATTR_LABLE = "label";
	transient public static final String ATTR_NUM_IMAGE = "num_images";
	
	
	transient public String errorMsg ="" ;
	transient public String userName ="" ;
	transient public String firstName ="" ;
	transient public String lastName ="" ;
	transient public String userId ="" ;
	transient public String id = "";
	transient public String object_id = "";
	transient public String updated = "";
	transient public String waterMark = "";
	transient public String waterMarkWide = "";
	transient public String nexturl = "";
	transient public String prevurl = "";
	transient public Hashtable<String, String> category = new Hashtable<String, String>();
	transient public String title = "";
	transient public String seo = "";
	transient public String logo = "";
	transient public Vector<Hashtable<String, String>> link = new Vector<Hashtable<String, String>>();
	transient public Author author = new Author();
	transient public Generator generator = new Generator();
	transient public String openSearch_totalResults = "";
	transient public String openSearch_startIndex = "";
	transient public String openSearch_itemsPerPage = "";
	transient public Hashtable<String, String> tracking = new Hashtable<String, String>();
	transient public static Vector<Entry> entry = new Vector<Entry>();
	
	transient public String followers = "";
	transient public String following = "";
	transient public String mediaCount = "";
	transient public String mediaCountURL = "";
	transient public String followersURL = "";
	transient public String followingURL = "";
	transient public String subscriberUserName= "";
	transient public String subscriberFirstName= "";
	transient public String subscriberLastName= "";
	transient public String mediastatsurl="";
	transient public String profileUrl="";
	
	
	
	public String comment_count = "";
	
	public final class Entry implements Serializable{
		public boolean dummy = false;
		public String id = "";
		public byte status = 0;
//		transient public Drawable drawable;
//		transient public byte[] inputStream;
		public String published = "";
		public String updated = "";
		public String title = "";
		public Vector<Hashtable<String, String>> category = new Vector<Hashtable<String, String>>();
		public Hashtable<String, String> content = new Hashtable<String, String>();
		public String summary = "";
		public Author author = new Author();
		public Vector<Hashtable<String, String>> link = new Vector<Hashtable<String, String>>();
		public Hashtable<String, String> rt_statistics = new Hashtable<String, String>();
		public Hashtable<String, String> rt_rating = new Hashtable<String, String>();
		public Hashtable<String, String> rt_voting = new Hashtable<String, String>();
		public String user_thumb = "";
		public String comment_url = "";
		public String comment_count = "";
		public String fburl = "";
		//comment data
		public String parent_id = "";
		public String username = "";
		public String firstname = "";
		public String lastname = "";
		public String text = "";
		public String likes = "";
		public String dislikes = "";		
		public String report = "";
		public String like_value = "";
		public String undo = "";
		public String img_url = "";
		public String audio_url = "";
		public String video_url = "";
		public String img_thumb_url = "";
		public String num_images = "";
		public String followers = "";
		public String following = "";
		public String mediaCount = "";
		public String mediaCountURL = "";
		public String followersURL = "";
		public String followingURL = "";
		public String subscriberUserName= "";
		public String subscriberFirstName= "";
		public String subscriberLastName= "";
		public String mediastatsurl="";
		public String profileUrl="";
		public String location="";
		public Vector media =null ;
		public String followLink;
		public String unFollowLink;
		public int more;
		public String prev;
		public String next;
		public String currentLocation;
		public String defaultLocation;
//		public Vector<Hashtable<String, String>> link = new Vector<Hashtable<String, String>>();
	}

	public final class Author implements Serializable{
		public String name = "";
		public String uri = "";
		public String firstName = "";
		public String lastName = "";

		public String toString() {
			return "[" + name + ":" + uri + "]";
		}
	}

	public final class Generator implements Serializable{
		public String value = "";
		public Hashtable<String, String> props = new Hashtable<String, String>();

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			Enumeration<String> key = props.keys();
			while (key.hasMoreElements()) {
				String k = key.nextElement();
				String v = props.get(k);
				buffer.append("\n" + k + ":" + v);
			}
			return buffer.toString();
		}

	}

	public static String getAttrScheme(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_SCHEME);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrSrc(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_SRC);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrTerm(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_TERM);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrLabel(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_LABLE);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrRel(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_REL);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrType(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_TYPE);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrHref(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_HREF);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrUri(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_URI);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrCode(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_CODE);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrVotecount(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_VOTECOUNT);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrAveragerating(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_AVERAGERATING);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrVoteupcount(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_VOTEUPCOUNT);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrVotedowncount(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_VOTEDOWNCOUNT);
		if (s != null)
			return s;
		return "NA";
	}
	
	public static String getAttrSelfvote(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_SELFVOTE);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrViewcount(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_VIEWCOUNT);
		if (s != null)
			return s;
		return "NA";
	}

	public static String getAttrmMedaicount(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_MEDIACOUNT);
		if (s != null)
			return s;
		return "NA";
	}
	public static String getNumImage(Hashtable<String, String> hTable) {
		String s = (String) hTable.get(ATTR_NUM_IMAGE);
		if (s != null)
			return s;
		return "NA";
	}
}
