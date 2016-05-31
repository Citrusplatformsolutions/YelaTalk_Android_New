package com.kainat.app.android.util;

import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParserAndDataManager extends DefaultHandler {
	boolean parent = true;
	boolean entry = false;
	boolean author = false;
	boolean featuredAction = false;
//	String value;
	public Feed feed = null;
	Feed.Entry entryClass;
	boolean isMoretext;
	StringBuilder text = new StringBuilder();
	Vector mediaUrl = new Vector() ;
	public static String streem =null ;
	
	public void setStreemTag(String streemL ){
		this.streem = streemL ;
	}
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (feed == null)
			feed = new Feed();
		String tagName = localName.trim();

		if (tagName.equalsIgnoreCase(Feed.TAG_ENTRY)) {
			entry = true;
			entryClass = feed.new Entry();			
			mediaUrl = new Vector() ;
		}

		if (tagName.equalsIgnoreCase(Feed.TAG_AUTHOR)) {
			feed.author = feed.new Author();
			author = true;
		}
		if (tagName.equalsIgnoreCase(Feed.TAG_FEATURED_ACTION)) {
			feed.author = feed.new Author();
			featuredAction = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_GENERATOR)) {
			feed.generator = feed.new Generator();
		}
		Hashtable<String, String> temp = new Hashtable<String, String>();
		
		boolean mediaparse = false ;
		for (int i = 0; i < attributes.getLength(); i++) {
			String value = attributes.getValue(i).trim();
			if (!entry && tagName.equalsIgnoreCase(Feed.TAG_CATEGORY)) {
				feed.category.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_GENERATOR)) {
				feed.generator.props.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			} else if (!entry && (tagName.equalsIgnoreCase(Feed.TAG_RT_TRACKING) || tagName.equalsIgnoreCase(Feed.TAG_RT_TRACKING2))) {
				feed.tracking.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_LINK)) {
				temp.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			}
			if (entry) {
//				System.out.println("-----------value--"+value);
				if(value.equalsIgnoreCase("http://schemas.rocketalk.com/2010#collection.mediaurl")||mediaparse){
//					System.out.println("-----------attributes.getLocalName(i).trim()--"+attributes.getLocalName(i).trim());
					
					mediaparse = true ;
					mediaUrl.add(value);
//					if(this.streem != null)
//						System.out.println("----------------this.streem is not null----------");
//					else						
//						System.out.println("----------------this.streem is null----------");
					
//					System.out.println("-----------value--"+value);
					if((this.streem == null && attributes.getLocalName(i).trim().equalsIgnoreCase("href"))
							||(this.streem != null && attributes.getLocalName(i).trim().equalsIgnoreCase("url_type")))
					{
						mediaparse = false ;
//						System.out.println("-------------------this is done-------------------");
//						entryClass.media = mediaUrl ;
					}
					continue;
				}
				mediaparse = false ;
				if (tagName.equalsIgnoreCase(Feed.TAG_RT_STATISTICS) || tagName.equalsIgnoreCase(Feed.TAG_RT_STATISTICS2))
					entryClass.rt_statistics.put(attributes.getLocalName(i).trim().toLowerCase(), value);
				else if (tagName.equalsIgnoreCase(Feed.TAG_RT_RATING) || tagName.equalsIgnoreCase(Feed.TAG_RT_RATING2))
					entryClass.rt_rating.put(attributes.getLocalName(i).trim().toLowerCase(), value);
				else if (tagName.equalsIgnoreCase(Feed.TAG_RT_VOTING) || tagName.equalsIgnoreCase(Feed.TAG_RT_VOTING2))
					entryClass.rt_voting.put(attributes.getLocalName(i).trim().toLowerCase(), value);
				else if (tagName.equalsIgnoreCase(Feed.TAG_CONTENT))
					entryClass.content.put(attributes.getLocalName(i).trim().toLowerCase(), value);
				else if (tagName.equalsIgnoreCase(Feed.TAG_LINK))
					temp.put(attributes.getLocalName(i).trim().toLowerCase(), value);
				else if (tagName.equalsIgnoreCase(Feed.TAG_CATEGORY))
					temp.put(attributes.getLocalName(i).trim().toLowerCase(), value);
				else if (tagName.equalsIgnoreCase(Feed.TAG_IMG_THUMB_URL))
					temp.put(attributes.getLocalName(i).trim().toLowerCase(), value);
			}
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_ERROR)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_LINK)) {
			feed.link.add(temp);
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_NEXT_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_PREV_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWING)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERS)) {
			text = new StringBuilder();
			isMoretext = true;
		}if (!entry && tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNT)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNTURL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERSURL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWINGURL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_USER_ID)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_profileUrl)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_LINK)) {
			entryClass.link.add(temp);
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_CATEGORY)) {
			entryClass.category.add(temp);
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_IMG_THUMB_URL)) {
			entryClass.category.add(temp);
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_SUMMARY)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_COMMENT_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_IMG_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_AUDIO_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_VIDEO_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_IMG_THUMB_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_USER_THUMB_URL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWING)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERS)) {
			text = new StringBuilder();
			isMoretext = true;
		}if (entry && tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNT)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNTURL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERSURL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWINGURL)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_subscriberUserName)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_subscriberFirstName)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_subscriberLastName)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_mediastatsurl)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_profileUrl)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_CURRENT_LOCATION)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		if (entry && tagName.equalsIgnoreCase(Feed.TAG_DEFAULT_LOCATION)) {
			text = new StringBuilder();
			isMoretext = true;
		}
		
		text = new StringBuilder();
		isMoretext = true;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (localName.equalsIgnoreCase(Feed.TAG_AUTHOR)) {
			author = false;
		}
		String tagName = localName.trim();
		String value = text.toString();
		if (!entry && tagName.equalsIgnoreCase(Feed.TAG_ID)) {
			feed.id = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_OBJECT_ID)) {
			feed.object_id = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_UPDATED)) {
			feed.updated = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_WATER_MARK)) {
			feed.waterMark = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_WATER_MARK_WIDE)) {
			feed.waterMarkWide = value;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_NEXT_URL)) {
			feed.nexturl = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_ERROR)) {
			feed.errorMsg = text.toString();
			isMoretext = false;
		}else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_PREV_URL)) {
			feed.prevurl = text.toString();
			isMoretext = false;
		}else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_TITLE)) {
			feed.title = value;
		}  else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_SEO)) {
			feed.seo = value;
		}else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_LOGO)) {
			feed.logo = value;
		} else if (!entry && (tagName.equalsIgnoreCase(Feed.TAG_OPENSEARCH_TOTALRESULTS) || tagName.equalsIgnoreCase(Feed.TAG_OPENSEARCH_TOTALRESULTS2))) {
			feed.openSearch_totalResults = value;
		} else if (!entry && (tagName.equalsIgnoreCase(Feed.TAG_OPENSEARCH_ITEMPERPAGES) || tagName.equalsIgnoreCase(Feed.TAG_OPENSEARCH_ITEMPERPAGES2))) {
			feed.openSearch_itemsPerPage = value;
		} else if (!entry && (tagName.equalsIgnoreCase(Feed.TAG_OPENSEARCH_STARTINDEX) || tagName.equalsIgnoreCase(Feed.TAG_OPENSEARCH_STARTINDEX2))) {
			feed.openSearch_startIndex = value;
		} else if (!entry && author) {
			if (!entry && tagName.equalsIgnoreCase(Feed.TAG_NAME))
				feed.author.name = value;
			else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_URI))
				feed.author.uri = value;
			else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FIRST_NAME))
				feed.author.firstName = value;
			else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_LAST_NAME))
				feed.author.lastName = value;
		} else if (/* !entry && */tagName.equalsIgnoreCase(Feed.TAG_AUTHOR)) {
			author = false;
		} else if (tagName.equalsIgnoreCase(Feed.TAG_ENTRY)) {
			entryClass.media = mediaUrl ;
//			System.out.println("------feed.entry.title"+entryClass.title);
//			System.out.println("------feed.entry.defaultLocation"+entryClass.defaultLocation);
//			System.out.println("------feed.entry.currentLocation"+entryClass.currentLocation);
//			System.out.println("------feed.entry.title"+entryClass.title);
//			System.out.println("------feed.entry.title"+entryClass.title);
			
			feed.entry.add(entryClass);
			entry = false;
		} else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_GENERATOR)) {
			feed.generator.value = value;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERS)) {
			feed.followers = text.toString();
			isMoretext = false;
		}else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWING)) {
			feed.following = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNT)) {
			feed.mediaCount = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNTURL)) {
			feed.mediaCountURL = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERSURL)) {
			feed.followersURL = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_FOLLOWINGURL)) {
			feed.followingURL = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_userName)) {
			feed.userName = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_firstName)) {
			feed.firstName = text.toString();
			isMoretext = false;
		}else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_lastName)) {
			feed.lastName = text.toString();
			isMoretext = false;
		}else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_USER_ID)) {
			feed.userId = text.toString();
			isMoretext = false;
		}
		else if (!entry && tagName.equalsIgnoreCase(Feed.TAG_profileUrl)) {
			feed.profileUrl = text.toString();
			isMoretext = false;
		}
		else if (tagName.equalsIgnoreCase(Feed.TAG_COMMENT_COUNT))
			feed.comment_count = value;
		if (entry) {
			if (tagName.equalsIgnoreCase(Feed.TAG_ID))
				entryClass.id = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_PUBLISHED))
				entryClass.published = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_UPDATED))
				entryClass.updated = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_TITLE))
				entryClass.title = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_SUMMARY)) {
				entryClass.summary = text.toString();
				isMoretext = false;
			} else if (tagName.equalsIgnoreCase(Feed.TAG_COMMENT_URL)) {
				entryClass.comment_url = text.toString();// "http://124.153.95.188/tejas/feeds/comments?comment_type=media&object_id=425313754&start_page=1&num_comments=20";//value;
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_USER_THUMB_URL)) {
				entryClass.user_thumb = text.toString();// "http://124.153.95.188/tejas/feeds/comments?comment_type=media&object_id=425313754&start_page=1&num_comments=20";//value;
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_COMMENT_COUNT))
				entryClass.comment_count = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_FBURL))
				entryClass.fburl = value;
			else if (author) {
				if (tagName.equalsIgnoreCase(Feed.TAG_NAME))
					entryClass.author.name = value;
				else if (tagName.equalsIgnoreCase(Feed.TAG_URI))
					entryClass.author.uri = value;
				else if (tagName.equalsIgnoreCase(Feed.TAG_FIRST_NAME))
					entryClass.author.firstName = value;
				else if (tagName.equalsIgnoreCase(Feed.TAG_LAST_NAME))
					entryClass.author.lastName = value;
			}			
			else if (tagName.equalsIgnoreCase(Feed.TAG_FIRST_NAME))
					entryClass.firstname = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_LAST_NAME))
					entryClass.lastname = value;			
			else if (tagName.equalsIgnoreCase(Feed.TAG_PARENT_ID))
				entryClass.parent_id = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_USERNAME))
				entryClass.username = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_TEXT))
				entryClass.text = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_LIKES))
				entryClass.likes = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_DISLIKES))
				entryClass.dislikes = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_LIKE_VALUE))
				entryClass.like_value = value;
			else if (tagName.equalsIgnoreCase(Feed.TAG_IMG_URL)) {
				entryClass.img_url = text.toString();
				isMoretext = false;
			} else if (tagName.equalsIgnoreCase(Feed.TAG_AUDIO_URL)) {
				entryClass.audio_url = text.toString();
				isMoretext = false;
			} else if (tagName.equalsIgnoreCase(Feed.TAG_VIDEO_URL)) {
				entryClass.video_url = text.toString();
				isMoretext = false;
			} else if (tagName.equalsIgnoreCase(Feed.TAG_IMG_THUMB_URL)) {
				entryClass.img_thumb_url = text.toString();
				isMoretext = false;
			} else if (tagName.equalsIgnoreCase(Feed.TAG_LIKE_VALUE)) {
				entryClass.like_value = value;
			}else if (tagName.equalsIgnoreCase(Feed.TAG_subscriberUserName)) {
				entryClass.subscriberUserName = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_subscriberFirstName)) {
				entryClass.subscriberFirstName = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_subscriberLastName)) {
				entryClass.subscriberLastName = text.toString();
				isMoretext = false;
			}else if (tagName.equalsIgnoreCase(Feed.TAG_mediastatsurl)) {
				entryClass.mediastatsurl = text.toString();
				isMoretext = false;
			}
			
			else if (tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERS)) {
				entryClass.followers = text.toString();
				isMoretext = false;
			}else if (tagName.equalsIgnoreCase(Feed.TAG_FOLLOWING)) {
				entryClass.following = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNT)) {
				entryClass.mediaCount = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_MEDIACOUNTURL)) {
				entryClass.mediaCountURL = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_FOLLOWERSURL)) {
				entryClass.followersURL = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_FOLLOWINGURL)) {
				entryClass.followingURL = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_profileUrl)) {
				entryClass.profileUrl = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_RT_LOCATION)) {
				entryClass.location = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_DEFAULT_LOCATION)) {
				entryClass.defaultLocation = text.toString();
				isMoretext = false;
			}
			else if (tagName.equalsIgnoreCase(Feed.TAG_CURRENT_LOCATION)) {
				entryClass.currentLocation = text.toString();
				isMoretext = false;
			}
		}

		if (tagName.equalsIgnoreCase(Feed.TAG_ENTRY)) {
			entry = false;
		}
		text = new StringBuilder();
		isMoretext = false;
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
//		if (isMoretext) {
			text.append(new String(ch, start, length));
//		} else
//			value = new String(ch, start, length);
	}
}