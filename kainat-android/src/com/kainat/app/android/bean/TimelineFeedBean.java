package com.kainat.app.android.bean;

import java.util.ArrayList;
import java.util.List;

import com.kainat.app.android.model.MediaContentUrlModel;

public class TimelineFeedBean {
	private long channelPostId;
	private String groupName;
	private PostedByUserBean postedByUserBean;
	private String hashtags;

	private int audioPlayCount;
	private int videoPlayCount;
	private int likesCount;
	private int commentsCount;
	private int shareCount;
	private int viewCount;
	private String createdDate;
	private List<MediaContentUrlModel> imageContent;
	private List<MediaContentUrlModel> audioContent;
	private List<MediaContentUrlModel> videoContent;

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

	public PostedByUserBean getPostedByUserBean() {
		return postedByUserBean;
	}

	public void setPostedByUserBean(PostedByUserBean postedByUserBean) {
		this.postedByUserBean = postedByUserBean;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}

	public int getAudioPlayCount() {
		return audioPlayCount;
	}

	public void setAudioPlayCount(int audioPlayCount) {
		this.audioPlayCount = audioPlayCount;
	}

	public int getVideoPlayCount() {
		return videoPlayCount;
	}

	public void setVideoPlayCount(int videoPlayCount) {
		this.videoPlayCount = videoPlayCount;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getShareCount() {
		return shareCount;
	}

	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public List<MediaContentUrlModel> getImageContent() {
		return imageContent;
	}

	public void setImageContent(List<MediaContentUrlModel> imageContent) {
		this.imageContent = imageContent;
	}

	public List<MediaContentUrlModel> getAudioContent() {
		return audioContent;
	}

	public void setAudioContent(List<MediaContentUrlModel> audioContent) {
		this.audioContent = audioContent;
	}

	public List<MediaContentUrlModel> getVideoContent() {
		return videoContent;
	}

	public void setVideoContent(List<MediaContentUrlModel> videoContent) {
		this.videoContent = videoContent;
	}

}
