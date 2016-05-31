package com.kainat.app.android.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MediaPost implements Serializable{
	public int userId;
	public String id;
	public boolean sendOnFaceBook = false ;
//	public String fburl;
	public String status;
	public String fburl;
	public String mediaText;
	public String mediaTag;
	public int cat;
	public String privacy;
//	public Location loc = null;
	public double lat = 0.0;
	public double lon = 0.0;
	public String locAddress;
	public MediaContent mVoicePath;
	public List<MediaContent> mImagesPath = new ArrayList<MediaContent>();
	public Hashtable<String, Location> mImagesPathLoc = new Hashtable<String, Location>();
	public List<MediaContent> mVideoPath = new ArrayList<MediaContent>();
	
	public class MediaContent  implements Serializable
	{
		public String contentPath = null ;
		public byte contentType = 0 ;
		public String contentServerID = null ;
		public int retry = 0; ;
	}
}
