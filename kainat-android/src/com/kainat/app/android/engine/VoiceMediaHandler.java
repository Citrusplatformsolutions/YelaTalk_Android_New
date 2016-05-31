package com.kainat.app.android.engine;

public interface VoiceMediaHandler {
	public void voiceRecordingStarted(final String recordingPath);

	public void voiceRecordingCompleted(final String recordedVoicePath);

	public void voicePlayStarted();

	public void voicePlayCompleted();
	public void onError(int i);
	public void onDureationchanged(long total,long current);
}
