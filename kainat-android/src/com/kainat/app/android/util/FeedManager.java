package com.kainat.app.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;

public class FeedManager {
	
	public static final String MEDIA_FEATURED = "MEDIA_FEATURED" ;
	public static final String MEDIA_CATEGEORIES = "MEDIA_CATEGEORIES" ;
	public static final String MEDIA_MY_ALBUM= "MY_ALBUM" ;
	public static final String MEDIA_NEW= "MEDIA_NEW" ;
	public static final String MEDIA_POPULAR= "MEDIA_POPULAR" ;
	public static final String MEDIA_VIDEO= "MEDIA_VIDEO" ;
	public static final String MEDIA_TO_FOLLOW= "MEDIA_TO_FOLLOW" ;
//	public static final String MEDIA_MY_ALBUM= "MY_ALBUM" ;
	
	
	public static final String MY_COMMUNITY = "MY_COMMUNITY" ;
	public static final String RECOMENDED_COMMUNITY = "RECOMENDED_COMMUNITY" ;
	
	static Hashtable<String, Feed> feedStorage = new Hashtable<String, Feed>();
	static Hashtable<String, CommunityFeed> feedStorageComm = new Hashtable<String, CommunityFeed>();
	public static void storeFeed(String feedName,Feed feed){
//		if(!SettingData.sSelf.isIm())
		if(feed != null){
		feedStorage.put(feedName, feed);
		if(Logger.ENABLE)Logger.debug("", "------------------------saving feed-----------"+feedName) ;
		}
	}
	public static  CommunityFeed getFeedCom(String feedName){
		return feedStorageComm.get(feedName);
	}
	public static void storeFeedCom(String feedName,CommunityFeed feed){
		if(feed != null){
			feedStorageComm.put(feedName, feed);
		if(Logger.ENABLE)Logger.debug("", "------------------------saving feed-----------"+feedName) ;
		}
	}
	public static  Feed getFeed(String feedName){
//		if(SettingData.sSelf.isIm())
//			return null ;
		return feedStorage.get(feedName);
	}
	public static void clearAll()
	{
		feedStorageComm = null ;
		feedStorageComm = new Hashtable<String, CommunityFeed>();
		/*
		feedStorage = null ;
		feedStorage = new Hashtable<String, Feed>();*/
	}
	public static void clearAllA()
	{
		feedStorageComm = null ;
		feedStorageComm = new Hashtable<String, CommunityFeed>();
		
		feedStorage = null ;
		feedStorage = new Hashtable<String, Feed>();
	}
	public static Feed.Entry getEntry(String name,Context context)
	{
		//.read(istream)
		String pathd = String.format("%s%s%s.ser", context.getCacheDir(), File.separator,
				name);
		try{
//			System.out.println("--------deserializeObject-- path-------"+pathd);
			FileInputStream fin = new FileInputStream(pathd);
//			System.out.println("--------fin.available()-- path-------"+fin.available());
			byte[] entrys =new byte[fin.available()];
//			int read = 0;
//			byte[] buffer = new byte[1024];
//			while ((read = fin.read()) != -1) {
//				System.out.println("-------------"+read);
//			}	
			fin.read(entrys);
			Feed.Entry entry = (Feed.Entry)Utilities.deserializeObject(entrys);
//			if(entry != null)
//			System.out.println("--------deserializeObject-- path-------"+entry.id);
			fin.close();
			entrys = null ;
			return entry ;
		}catch (Exception e) {		
			e.printStackTrace();
		}
		return null ;
	}
	public static void saveEntry(Feed.Entry entry,Context context)
	{
		String pathd = String.format("%s%s%s.ser", context.getCacheDir(), File.separator,
				"1");
		try{
//		for (int i = 0; i < feed.entry.size(); i++) 
		{			
//			Feed.Entry entry = feed.entry.get(i);
			pathd = String.format("%s%s%s.ser", context.getCacheDir(), File.separator,
					entry.id);
//			System.out.println("--------serializeObject-- path-------"+pathd);
			byte[] entrys = Utilities.serializeObject(entry);
//			if(entrys != null)
//			System.out.println("--------entrys.length-- path-------"+entrys.length);
			
				FileOutputStream fout = new FileOutputStream(pathd);
				fout.write(entrys);
				fout.flush();
				fout.close();
				entrys = null ;
					
		}
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	public static void saveFeed(Feed feed,Context context)
	{
		String pathd = String.format("%s%s%s.ser", context.getCacheDir(), File.separator,
				"1");
		try{
		for (int i = 0; i < feed.entry.size(); i++) 
		{			
			Feed.Entry entry = feed.entry.get(i);
			pathd = String.format("%s%s%s.ser", context.getCacheDir(), File.separator,
					entry.id);
//			System.out.println("--------serializeObject-- path-------"+pathd);
			byte[] entrys = Utilities.serializeObject(entry);
//			if(entrys != null)
//			System.out.println("--------entrys.length-- path-------"+entrys.length);
			
				FileOutputStream fout = new FileOutputStream(pathd);
				fout.write(entrys);
				fout.flush();
				fout.close();
				entrys = null ;
					
		}
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	public static void removeUnwantedContent(Feed feed)
	{
//		if(1==2)
		for (int i = 0; i < feed.entry.size(); i++) 
		{			
			//cat-id-content
			Feed.Entry entry = feed.entry.get(i);
			entry.audio_url = null ;
//			entry. id = "";
			entry.status = 0;
//			entry.drawable = null;
//			entry.inputStream = null;
//			entry.published = "";
//			entry.updated = "";
//			entry.title = "";
//			public Vector<Hashtable<String, String>> category = new Vector<Hashtable<String, String>>();
//			public Hashtable<String, String> content = new Hashtable<String, String>();
			entry.summary = "";
//			entry.author = new Author();
			entry.link = new Vector<Hashtable<String, String>>();
//			entry.rt_statistics = new Hashtable<String, String>();
//			entry.rt_rating = new Hashtable<String, String>();
//			entry.rt_voting = new Hashtable<String, String>();
			entry.user_thumb = "";
			entry.comment_url = "";
//			entry.comment_count = "";
			entry.fburl = "";
			//comment data
			entry.username = "";
			entry.text = "";
			entry.likes = "";
			entry.dislikes = "";		
			entry.report = "";
			entry.like_value = "";
			entry.undo = "";
			entry.img_url = "";
			entry.audio_url = "";
			entry.video_url = "";
			entry.img_thumb_url = "";
			entry.num_images = "";
			entry.followers = "";
			entry.following = "";
			entry.mediaCount = "";
			entry.mediaCountURL = "";
			entry.followersURL = "";
			entry.followingURL = "";
			entry.subscriberUserName= "";
			entry.subscriberFirstName= "";
			entry.subscriberLastName= "";
			entry.mediastatsurl="";
			entry.profileUrl="";
			entry.location="";
		}
	}
	
	public static void copyEntry(Feed destFeed ,Feed srcFeed){
		destFeed.entry = new Vector<Feed.Entry>() ;
		
		destFeed.userName =srcFeed.userName ;
		destFeed.firstName =srcFeed.firstName ;
		destFeed.lastName =srcFeed.lastName ;
		destFeed.userId =srcFeed.userId ;
		destFeed.id = srcFeed.id;
		destFeed.object_id = srcFeed.object_id;
		destFeed.updated = srcFeed.updated;
		destFeed.waterMark = srcFeed.waterMark;
		destFeed.waterMarkWide = srcFeed.waterMarkWide;
		destFeed.nexturl = srcFeed.nexturl;
		destFeed. category = new Hashtable<String, String>();
		destFeed. category = (Hashtable<String, String>)srcFeed. category.clone() ;
		destFeed.title = srcFeed.title;
		destFeed.logo = srcFeed.logo;
		destFeed.link = new Vector<Hashtable<String, String>>();
		destFeed.link = (Vector<Hashtable<String, String>>)srcFeed.link.clone();
//		transient publ ic Author author = new Author();
		destFeed.generator = new Feed().new Generator();
		destFeed.generator.value = srcFeed.generator.value ;
		destFeed. generator.props = (Hashtable<String, String>)srcFeed. generator.props.clone() ;
		
		destFeed.openSearch_totalResults = srcFeed.openSearch_totalResults;
		destFeed.openSearch_startIndex = srcFeed.openSearch_startIndex;
		destFeed.openSearch_itemsPerPage = srcFeed.openSearch_itemsPerPage;
		destFeed.tracking = new Hashtable<String, String>();
		destFeed. tracking = (Hashtable<String, String>)srcFeed. tracking.clone() ;
			
		destFeed.followers = srcFeed.followers;
		destFeed.following = srcFeed.following;
		destFeed.mediaCount = srcFeed.mediaCount;
		destFeed.mediaCountURL = srcFeed.mediaCountURL;
		destFeed.followersURL = srcFeed.followersURL;
		destFeed.followingURL = srcFeed.followingURL;
		destFeed.subscriberUserName= srcFeed.subscriberUserName;
		destFeed.subscriberFirstName= srcFeed.subscriberFirstName;
		destFeed.subscriberLastName= srcFeed.subscriberLastName;
		destFeed.mediastatsurl=srcFeed.mediastatsurl;
		destFeed.profileUrl=srcFeed.profileUrl;
		
		for (int i = 0; i < srcFeed.entry.size(); i++) 
		{			
			//cat-id-content
			Feed.Entry entry = srcFeed.entry.get(i);
			Feed.Entry destentry = new Feed().new Entry() ;
			destentry.audio_url = entry.audio_url;
			destentry.id = entry. id ;
			destentry.status = entry.status;
			destentry.published = entry.published ;
			destentry.updated = entry.updated;
			destentry.title = entry.title;
			destentry.category = entry.category;//new Vector<Hashtable<String, String>>();
//			if(entry.category !=null && entry.category.size()>0)
//			Collections.copy(destentry.category,entry.category);
			destentry.content = new Hashtable<String, String>();
			
//			 for(Hashtable.Entry<String, String> entryL : entry.content.centries()) {
//			        copy.put(entry.getKey(), entry.getValue().clone());
//			    }
//			Collections.copy(destentry.content,entry.content);
			destentry.content = (Hashtable<String, String>)entry.content.clone() ;
			destentry.summary = entry.summary;
			destentry.author = new Feed().new Author();
			destentry.author.firstName =  entry.author.firstName;
			destentry.author.lastName =  entry.author.lastName;
			destentry.author.name =  entry.author.name;
			destentry.author.uri =  entry.author.uri;
			
			destentry.link = entry.link;//new Vector<Hashtable<String, String>>();
//			if(entry.link !=null && entry.link.size()>0)
//			Collections.copy(destentry.link,entry.link);
			destentry.rt_statistics = entry.rt_statistics = new Hashtable<String, String>();
			destentry.rt_rating = entry.rt_rating = new Hashtable<String, String>();
			destentry.rt_voting = entry.rt_voting = new Hashtable<String, String>();
			destentry.user_thumb = entry.user_thumb;
			destentry.comment_url = entry.comment_url;
			destentry.comment_count = entry.comment_count;
			destentry.fburl = entry.fburl;
			//comment data
			destentry.username = entry.username;
			destentry.text = entry.text;
			destentry.likes = entry.likes;
			destentry.dislikes = entry.dislikes;		
			destentry.report = entry.report;
			destentry.like_value = entry.like_value;
			destentry.undo = entry.undo;
			destentry.img_url = entry.img_url;
			destentry.audio_url = entry.audio_url;
			destentry.video_url = entry.video_url;
			destentry.img_thumb_url = entry.img_thumb_url;
			destentry.num_images = entry.num_images;
			destentry.followers = entry.followers;
			destentry.following = entry.following;
			destentry.mediaCount = entry.mediaCount;
			destentry.mediaCountURL = entry.mediaCountURL;
			destentry.followersURL = entry.followersURL;
			destentry.followingURL = entry.followingURL;
			destentry.subscriberUserName = entry.subscriberUserName;
			destentry.subscriberFirstName = entry.subscriberFirstName;
			destentry.subscriberLastName = entry.subscriberLastName;
			destentry.mediastatsurl = entry.mediastatsurl;
			destentry.profileUrl = entry.profileUrl;
			destentry.location = entry.location;
			destFeed.entry.add(destentry) ;
		}
	}
}
