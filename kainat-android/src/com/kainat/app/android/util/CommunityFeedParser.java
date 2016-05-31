package com.kainat.app.android.util;

import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.kainat.app.android.util.CommunityFeed.Entry;

public class CommunityFeedParser extends DefaultHandler {
	private boolean entry = false;
//	private String value;
	public CommunityFeed feed = null;
	private CommunityFeed.Entry entryClass;
	private Hashtable<String, String> attrTable;
	private boolean moreUrlText;
	private StringBuilder urlText = null;
	private StringBuilder topMessageId = null;
	Vector mediaUrl = new Vector() ;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (feed == null) {
			feed = new CommunityFeed();
		}
		String tagName = localName.trim();
		if (tagName.equalsIgnoreCase(CommunityFeed.TAG_ENTRY)) {
			entry = true;
			entryClass = new Entry();
			mediaUrl = new Vector() ;
		}
		if (attrTable == null) {
			attrTable = new Hashtable<String, String>();
		}
		if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_SEARCH_MEMBER_URL)) {
			urlText = new StringBuilder();
			moreUrlText = true;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_SOCKETED_MESSAGE_URL)) {
			urlText = new StringBuilder();
			moreUrlText = true;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_SOCKET_URL)) {
			urlText = new StringBuilder();
			moreUrlText = true;
		} else if (entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_CATEGORY)) {
			urlText = new StringBuilder();
			moreUrlText = true;
		} else if (entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_DESCRIPTION)) {
			urlText = new StringBuilder();
			moreUrlText = true;
		}
		else if (entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_browseMsgUrl)) {
			urlText = new StringBuilder();
			moreUrlText = true;
		}
		else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_NEXT_URL)) {
			urlText = new StringBuilder();
			moreUrlText = true;
		}
		else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_TOP_MSG_ID)) {
			topMessageId = new StringBuilder();
			moreUrlText = true;
		}
		boolean mediaparse = false ;
		for (int i = 0; i < attributes.getLength(); i++) {
			String value = attributes.getValue(i).trim();
			
			if(entry){
				if(value.equalsIgnoreCase("http://schemas.rocketalk.com/2010#collection.mediaurl")||mediaparse){
//					System.out.println("-----------attributes.getLocalName(i).trim()--"+attributes.getLocalName(i).trim());
					
					mediaparse = true ;
					mediaUrl.add(value);
//					System.out.println("-----------value--"+value);
					if(attributes.getLocalName(i).trim().equalsIgnoreCase("url_type")){
						mediaparse = false ;
//						entryClass.media = mediaUrl ;
					}
					entryClass.thumbUrl = value;
					continue;
				}
			}
			if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_RT_TRACKING)) {
				feed.tracking.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			} 
			else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_LINK)) {
				feed.link.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			} 
			else if (entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_BUTTON)) {
				attrTable.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			} else if (entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_IMG_THUMB_URL)) {
				entryClass.imgThumbUrlAttrs.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			}
		}
		urlText = new StringBuilder();
		moreUrlText = true;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		String tagName = localName.trim();
		String value = urlText.toString();//
		if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_TITLE)) {
			feed.title = value;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_TOP_MSG_ID)) {
			feed.topMessageId = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_SEO)) {
			feed.seo = value;
		}
		else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_LINK)){
			feed.links.add(feed.link);
			feed.link = new Hashtable<String, String>() ;
		}
		else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_TOTAL_GROUP_COUNT)) {
			if (value != null && value.trim().length() > 0)
				feed.totalGroupCount = Integer.parseInt(value);
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_GROUP_ID)) {
			if (value != null && value.trim().length() > 0)
				feed.groupId = Integer.parseInt(value);
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_TOTAL_COUNT)) {
			if (value != null && value.trim().length() > 0)
				feed.totalCount = Integer.parseInt(value);
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_SHOW_MODERATION_ALERT)) {
			feed.showModerationAlert = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_WATER_MARK)) {
			feed.waterMark = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_WATER_MARK_WIDE)) {
			feed.waterMarkWide = value;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_IS_OWNER)) {
			if (value != null && value.trim().length() > 0)
				feed.isOwner = Integer.parseInt(value);
		}else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_ADMIN_STATE)) {
			if (value != null && value.trim().length() > 0)
				feed.adminState = value;
		}else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_FEATURED)) {
			if (value != null && value.trim().length() > 0)
				feed.featured = value;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_IS_ADMIN)) {
			if (value != null && value.trim().length() > 0)
				feed.isAdmin = Integer.parseInt(value);
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_SEARCH_MEMBER_URL)) {
			feed.searchMemberUrl = urlText.toString();
			moreUrlText = false;
			urlText = null;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_COMMUNITY_URL)) {
			feed.communityUrl = value;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_PROFILE_URL)) {
			feed.profileUrl = value;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_SOCKETED_MESSAGE_URL)) {
			feed.socketedMessageUrl = urlText.toString();
			moreUrlText = false;
			urlText = null;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_SOCKET_URL)) {
			feed.socketUrl = urlText.toString();
			moreUrlText = false;
			urlText = null;
		} else if (!entry && tagName.equalsIgnoreCase(CommunityFeed.TAG_NEXT_URL)) {
			feed.nexturl = urlText.toString();
			moreUrlText = false;
			urlText = null;
		} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_ENTRY)) {
			feed.entry.add(entryClass);
			entryClass.media = mediaUrl ;
			entry = false;
			attrTable = null;
		}
		if (entry) {
			if (tagName.equalsIgnoreCase(CommunityFeed.TAG_GROUP_ID)) {
				entryClass.groupId = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_GROUP_NAME)) {
				entryClass.groupName = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_TAGS)) {
				entryClass.tags = value;
			}
			else if(tagName.equalsIgnoreCase(CommunityFeed.TAG_REPORT_COUNT)){
				entryClass.reportCount = Integer.parseInt(value);
			}else if(tagName.equalsIgnoreCase(CommunityFeed.TAG_REPORT_MESSAGE_URL)){
				entryClass.reportMessageUrl = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_BUTTON)) {
				entryClass.joinOrLeave = value;
			} 
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_CATEGORY)) {
				entryClass.category = urlText.toString();
				moreUrlText = false;
				urlText = null;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_DESCRIPTION)) {
				entryClass.description = urlText.toString();
				moreUrlText = false;
				urlText = null;
			} 
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_browseMsgUrl)) {
				entryClass.browseMsgUrl = urlText.toString();
				moreUrlText = false;
				urlText = null;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_IS_MODERATED)) {
				entryClass.moderated = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_AUTO_ACCEPT_USER)) {
				entryClass.autoAcceptUser = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_IS_PUBLIC)) {
				entryClass.publicCommunity = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_LAST_UPDATED)) {
				entryClass.lastUpdated = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_TIME_OF_CREATION)) {
				entryClass.createdOn = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_NO_OF_MEMBERS)) {
				if (value != null && value.trim().length() > 0)
					entryClass.noOfMember = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_NO_OF_MESSAGES)) {
				if (value != null && value.trim().length() > 0)
					entryClass.noOfMessage = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_OWNER_USER_ID)) {
				if (value != null && value.trim().length() > 0)
					entryClass.ownerId = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_WELCOME_MSG_ID)) {
				entryClass.welcomeMsgId = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_MSG_ID)) {
				entryClass.msgId = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_VENDOR_NAME)) {
				entryClass.vendorName = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_NO_OF_ONLINE_USERD)) {
				if (value != null && value.trim().length() > 0)
					entryClass.noOfOnlineMember = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_COMMON_FRIEND_COUNT)) {
				if (value != null && value.trim().length() > 0)
					entryClass.commonFriendCount = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_ACTIVE_MEMBERS)) {
				if (value != null && value.trim().length() > 0)
					entryClass.activeMember = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_MEMBER_PERMISSION)) {
				entryClass.memberPermission = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_IS_ADMIN)) {
				if (value != null && value.trim().length() > 0)
					entryClass.admin = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_THUMB_URL)) {
				if (value != null && value.trim().length() > 0)
				entryClass.thumbUrl = value;
				//MANOJ SINGH
				//DAte 22/07/2015
				// Please don't comment above line it is used in follower of commity
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_PROFILE_URL)) {
				entryClass.profileUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_MESSAGE_URL)) {
				entryClass.messageUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_SEARCH_MEMBER_URL)) {
				entryClass.searchMemberUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_DISPLAY_NAME)) {
				entryClass.displayName = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_COUNT)) {
				if (value != null && value.trim().length() > 0)
					entryClass.count = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_URL)) {
				entryClass.url = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_TYPE)) {
				entryClass.type = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_USER_ID)) {
				if (value != null && value.trim().length() > 0)
					entryClass.userId = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_USER_NAME)) {
				entryClass.userName = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_USER_GENDER)) {
				entryClass.genderType = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_TIME_OF_REQUEST)) {
				entryClass.timeOfRequest = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_MESSAGE_ID)) {
				entryClass.messageId = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_PARENT_MESSAGE_ID)) {
				entryClass.parentMessageId = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_SENDER_ID)) {
				if (value != null && value.trim().length() > 0)
					entryClass.senderId = Integer.parseInt(value);
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_SENDER_NAME)) {
				entryClass.senderName = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_MESSAGE_STATE)) {
				entryClass.messageState = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_DRM_FLAGS)) {
				entryClass.drmFlags = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_CREATED_DATE)) {
				entryClass.createdDate = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_MESSAGE_TEXT)) {
				entryClass.messageText = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_IMG_THUMB_URL)) {
				entryClass.imgThumbUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_IMG_URL)) {
				entryClass.imgUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_VID_THUMB_URL)) {
				entryClass.vidThumbUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_VID_URL)) {
				entryClass.vidUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_AUDIO_URL)) {
				entryClass.audioUrl = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_FIRST_NAME)) {
				entryClass.firstName = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_LAST_NAME)) {
				entryClass.lastName = value;
			} else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_USER_STATUS)) {
				entryClass.userStatus = value;
			} 
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_REPLYTO)) {
				entryClass.replyTo = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_REPLYTO_FIRST_NAME)) {
				entryClass.replyToFirstName = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_REPLYTO_LAST_NAME)) {
				entryClass.replyToLastName = value;
			}
			
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_ownerUsername)) {
				entryClass.ownerUsername = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_ownerProfileUrl)) {
				entryClass.ownerProfileUrl = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_ownerDisplayName)) {
				entryClass.ownerDisplayName = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_ownerThumbUrl)) {
				entryClass.ownerThumbUrl = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_adminState)) {
				entryClass.adminState = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_FEATURED)) {
				entryClass.featured = value;
			}
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_pushNotif)) {
				entryClass.pushNotif = value;
			}
			
			
			else if (tagName.equalsIgnoreCase(CommunityFeed.TAG_BUTTON)) {
				StringBuffer key = new StringBuffer();
				key.append(entryClass.groupName).append("_").append(entryClass.userName);
				Hashtable<String, Hashtable<String, String>> table = entryClass.buttons.get(key.toString());
				if (table == null) {
					table = new Hashtable<String, Hashtable<String, String>>();
				}
				table.put(value, attrTable);
				entryClass.buttons.put(key.toString(), table);
				attrTable = null;
			}
		}
		urlText = new StringBuilder();
		moreUrlText = false;
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		try{
//		if (moreUrlText) {
			urlText.append(new String(ch, start, length));
//		} else {
//			value = new String(ch, start, length);
//		}
//		System.out.println("values:"+value);
		}catch (Exception e) {
//			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}