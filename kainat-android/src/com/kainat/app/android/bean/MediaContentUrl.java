package com.kainat.app.android.bean;

public class MediaContentUrl {

	private String contentId;
	private String contentType;
	private String contentUrl;
	private String thumbUrl;

	public MediaContentUrl(String contentId, String contentType,
			String contentUrl, String thumbUrl) {
		this.contentId = contentId;
		this.contentType = contentType;
		this.contentUrl = contentUrl;
		this.thumbUrl = thumbUrl;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

}
