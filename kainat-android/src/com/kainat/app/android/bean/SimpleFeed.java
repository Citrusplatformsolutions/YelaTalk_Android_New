package com.kainat.app.android.bean;


public class SimpleFeed {
	protected YTError[] ytErrors;
	protected Status status;
	protected String message;

	public enum Status {
		success, error
	}

	public SimpleFeed(YTError ytError, String message) {
		this.ytErrors = new YTError[] { ytError };
		this.status = Status.error;
		this.message = message;
	}

	public SimpleFeed(YTError[] citrusErrors, String message) {
		this.ytErrors = citrusErrors;
		this.status = Status.error;
		this.message = message;
	}

	public SimpleFeed(String message) {
		this.status = Status.success;
		this.message = message;
	}

	public YTError[] getCitrusErrors() {
		return ytErrors;
	}

	public void setCitrusErrors(YTError[] ytErrors) {
		this.ytErrors = ytErrors;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
