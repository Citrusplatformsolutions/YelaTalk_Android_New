package com.kainat.app.android.bean;

import com.kainat.app.android.util.CommunityFeed.Entry;

import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MediaBean {
	private static final String TAG = "MediaBean";
	Object obj;
	int view;
	private volatile ProgressBar mProgressBar;
	private ImageView mMedia_play;
	private LinearLayout mOutermedia;
	private LinearLayout mtime_panel;
	String playerState;
	private TextView streemStatus;
	private TextView total_autio_time;
	private TextView played_autio_time;
	private TextView dummyp;
	private Entry entry;
	private String mFilename;
	private String autoplay;
	private String msgId;
	private int playingPercentage;
	private boolean mIsPlaying;

	public boolean ismIsPlaying() {
		return mIsPlaying;
	}
	public void setmIsPlaying(boolean mIsPlaying) {
		this.mIsPlaying = mIsPlaying;
	}
	public int getPlayingPercentage() {
		return playingPercentage;
	}
	public void setPlayingPercentage(int playingPercentage) {
		this.playingPercentage = playingPercentage;
	}

	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getAutoplay() {
		return autoplay;
	}
	public void setAutoplay(String autoplay) {
		this.autoplay = autoplay;
	}
	public String getPlayerState() {
		return playerState;
	}
	public Entry getEntry() {
		return entry;
	}
	public void setEntry(Entry entry) {
		this.entry = entry;
	}
	public void setPlayerState(String playerState) {
		this.playerState = playerState;
	}
	public LinearLayout getMtime_panel() {
		return mtime_panel;
	}
	public void setMtime_panel(LinearLayout mtime_panel) {
		this.mtime_panel = mtime_panel;
	}
	public TextView getDummyp() {
		return dummyp;
	}
	public void setDummyp(TextView dummyp) {
		this.dummyp = dummyp;
	}
	public ImageView getmMedia_play() {
		return mMedia_play;
	}
	public void setmMedia_play(ImageView mMedia_play) {
		this.mMedia_play = mMedia_play;
	}
	public TextView getStreemStatus() {
		return streemStatus;
	}
	public void setStreemStatus(TextView streemStatus) {
		this.streemStatus = streemStatus;
	}
	public TextView getTotal_autio_time() {
		return total_autio_time;
	}
	public void setTotal_autio_time(TextView total_autio_time) {
		this.total_autio_time = total_autio_time;
	}
	public TextView getPlayed_autio_time() {
		return played_autio_time;
	}
	public void setPlayed_autio_time(TextView played_autio_time) {
		this.played_autio_time = played_autio_time;
	}
	public LinearLayout getmOutermedia() {
		return mOutermedia;
	}
	public void setmOutermedia(LinearLayout mOutermedia) {
		this.mOutermedia = mOutermedia;
	}


	public void setmProgressBar(ProgressBar mProgressBar) {
		this.mProgressBar = mProgressBar;
	}
	public String getmFilename() {
		return mFilename;
	}
	public void setmFilename(String mFilename) {
		this.mFilename = mFilename;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public int getView() {
		return view;
	}
	public void setView(int view) {
		this.view = view;
	}
	public ProgressBar getProgressBar() {
		return mProgressBar;
	}
	public void setProgressBar(ProgressBar progressBar) {
		Log.d(TAG, "setProgressBar " + mFilename + " to " + progressBar);
		mProgressBar = progressBar;
	}
}
