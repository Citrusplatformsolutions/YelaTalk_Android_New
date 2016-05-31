package com.kainat.app.android.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaPost implements Serializable{
	public int DB_ID;
	public int TRY;
	public long date;
	public int userId;
	public int clientId;
	public String dist;
	public String userPass;
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
//	public Hashtable<String, Location> mImagesPathLoc = new Hashtable<String, Location>();
	public List<MediaContent> mVideoPath = new ArrayList<MediaContent>();
	public String conversationId ;
	public String comments ;
	public long cltTxnId ;
	
	public String msg_type ;
	public String msg_op ;
	public String req_id ;
	public String req_type;
	public String reference_messageid ;
	
	public String command ;
	public String commandType ;
	public int attachmentSize;
	public int attachment;
	
	public String toString(){
		
		return "[msg_type:"+msg_type+"]"+
				"[msg_op:"+msg_op+"]"+
				"[msg_op:"+mediaText+"]"+
				"[req_id:"+req_id+"]"+
				"[req_type:"+req_type+"]"+
				"[reference_messageid:"+reference_messageid+"]"+
				"[cltTxnId:"+cltTxnId+"]"+
				"[command:"+command+"]"+
				"[commandType:"+commandType+"]"+
				"[dist:"+dist+"]"
				;
	}
	public class MediaContent  implements Serializable
	{
		public String contentPath = null ;
		public String contentStatus = null ;
		public byte contentType = 0 ;
		public String contentServerID = null ;
		public int retry = 0; ;
	}
}
