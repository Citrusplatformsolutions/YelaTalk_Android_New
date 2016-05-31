package com.kainat.app.android.engine;

public interface MediaEventsHandler {
	byte MEDIA_RECORDER_INFO_MAX_DURATION_REACHED = 1;
	byte MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED = 2;
	byte MEDIA_RECORDER_INFO_UNKNOWN = 3;
	byte MEDIA_RECORDER_ERROR_UNKNOWN = 4;

	byte MEDIA_PLAYING_COMPLETED = 20;
	byte MEDIA_PLAYING_FAILED = 21;
	byte MEDIA_PLAYING_STOPPED = 22;
	byte MEDIA_PLAYING_PAUSED = 23;
	byte MEDIA_PLAYING_RESUMED = 24;

	public void mediaEvent(byte event);
}
