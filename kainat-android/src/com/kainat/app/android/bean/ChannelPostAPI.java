package com.kainat.app.android.bean;

import java.util.Date;
import java.util.List;

public class ChannelPostAPI {

	private long channelPostId;
	private String groupName;
	private UserAPI postedByUser;
	private String text;
	private String hashtags;
	private List<MediaContentUrl> imageMediaContentUrlList;
	private List<MediaContentUrl> audioMediaContentUrlList;
	private long audioPlayCount;
	private List<MediaContentUrl> videoMediaContentUrlList;
	private long videoPlayCount;
	private long likesCount;
	private long commentsCount;
	private long shareCount;
	private long viewCount;
	private String createdDate;

	public ChannelPostAPI(long channelPostId, String groupName,
			UserAPI postedByUser, String text, String hashtags,
			List<MediaContentUrl> imageMediaContentUrlList,
			List<MediaContentUrl> audioMediaContentUrlList,
			long audioPlayCount,
			List<MediaContentUrl> videoMediaContentUrlList,
			long videoPlayCount, long likesCount, long commentsCount,
			long shareCount, long viewCount, String createdDate) {
		this.channelPostId = channelPostId;
		this.groupName = groupName;
		this.postedByUser = postedByUser;
		this.text = text;
		this.hashtags = hashtags;
		this.imageMediaContentUrlList = imageMediaContentUrlList;
		this.audioMediaContentUrlList = audioMediaContentUrlList;
		this.audioPlayCount = audioPlayCount;
		this.videoMediaContentUrlList = videoMediaContentUrlList;
		this.videoPlayCount = videoPlayCount;
		this.likesCount = likesCount;
		this.commentsCount = commentsCount;
		this.shareCount = shareCount;
		this.viewCount = viewCount;
		this.createdDate = createdDate;
	}

	public long getChannelPostId() {
		return channelPostId;
	}

	public void setChannelPostId(long channelPostId) {
		this.channelPostId = channelPostId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public UserAPI getPostedByUser() {
		return postedByUser;
	}

	public void setPostedByUser(UserAPI postedByUser) {
		this.postedByUser = postedByUser;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}

	public List<MediaContentUrl> getImageMediaContentUrlList() {
		return imageMediaContentUrlList;
	}

	public void setImageMediaContentUrlList(
			List<MediaContentUrl> imageMediaContentUrlList) {
		this.imageMediaContentUrlList = imageMediaContentUrlList;
	}

	public List<MediaContentUrl> getAudioMediaContentUrlList() {
		return audioMediaContentUrlList;
	}

	public void setAudioMediaContentUrlList(
			List<MediaContentUrl> audioMediaContentUrlList) {
		this.audioMediaContentUrlList = audioMediaContentUrlList;
	}

	public long getAudioPlayCount() {
		return audioPlayCount;
	}

	public void setAudioPlayCount(long audioPlayCount) {
		this.audioPlayCount = audioPlayCount;
	}

	public List<MediaContentUrl> getVideoMediaContentUrlList() {
		return videoMediaContentUrlList;
	}

	public void setVideoMediaContentUrlList(
			List<MediaContentUrl> videoMediaContentUrlList) {
		this.videoMediaContentUrlList = videoMediaContentUrlList;
	}

	public long getVideoPlayCount() {
		return videoPlayCount;
	}

	public void setVideoPlayCount(long videoPlayCount) {
		this.videoPlayCount = videoPlayCount;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getShareCount() {
		return shareCount;
	}

	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}

	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

}
