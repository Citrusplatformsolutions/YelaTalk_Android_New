package com.kainat.app.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kainat.util.AppUtil;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.uicontrol.SlideShowScreen;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Downloader;
import com.kainat.app.android.util.EmoticonInf;
import com.kainat.app.android.util.FileDownloadResponseHandler;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.VoiceMedia;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class CommunityPostActivity extends UIActivityManager implements
		Runnable, OnScrollListener, OnSchedularListener, OnClickListener,
		HttpSynchInf, EmoticonInf, ThumbnailReponseHandler,
		FileDownloadResponseHandler,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener,
		EmojiconGridFragment.OnEmojiconClickedListener, VoiceMediaHandler {
	TextView txt_post;
	com.rockerhieu.emojicon.EmojiconEditText edt_posting_text;
	private TextView edt_posting_text_count;
	FrameLayout smileyView = null;
	private static final byte POSITION_PICTURE = 0;
	private static final byte POSITION_CAMERA_PICTURE = 1;
	private static final byte POSITION_MULTI_SELECT_PICTURE = 4;
	private static final byte POSITION_VOICE = 2;
	private static final byte POSITION_VIDEO = 3;
	private static final byte POSITION_PICTURE_RT_CANVAS = 5;
	private static final byte POSITION_TALKING_PIC = 6;
	private String cameraImagePath = null;
	ImageView img_talkingpic, ig_video, img_doodle, img_gallery, img_camera,
			img_audio, community_post_smiley;
	LinearLayout ll_talkingpic, ll_video, ll_doodle, ll_gallery, ll_camera,
			ll_audio;
	TextView prev;
	ImageView media_play, media_close;
	private String mVoicePath, mVideoPath;
	private Vector<String> mPicturePath = new Vector<String>();
	MediaPlayer Audio_player;
	SeekBar mediavoicePlayingDialog_progressbar;
	Handler seekHandler = new Handler();
	private boolean isplaying = false;
	LinearLayout llout_audio_player, media_play_layout;
	ImageView img_thumb_video, img_close_video, img_play_video;
	RelativeLayout llout_video_player;
	LinearLayout ll_imageadd;
	private int progress = 0;
	private boolean isCancelClick = false;
	Dialog dialog;
	static boolean wheelRunning;
	private boolean isTimeOver = false;
	AudioProgressWheel wheelProgress;
	private String mRecordedVoicePath;
	private Button cancelBt;
	final public static byte UI_STATE_IDLE = 2;
	private int iCurrentState = UI_STATE_IDLE;
	final public static byte UI_STATE_RECORDING = 5;
	final public static byte UI_STATE_PLAYING = 12;
	private boolean isAudioVisible = false;
	private final byte RECORDING_MODE = 1;
	private final byte PLAY_MODE = 2;
	private TextView leftTimeView;
	private VoiceMedia mVoiceMedia;
	WakeLock wakeLock;
	PowerManager powerManager;
	private Payload payloadData = new Payload();
	String groupname, group_id;
	String imageServerId = null;
	String videoServerId = null;
	String audioServerId = null;
	ProgressDialog rtDialog;
	Uri imageUri = null;
	HorizontalScrollView scrollView_img;
	final int THUMBSIZE = 250;
	boolean ImageWithAudio = false;

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_post);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		powerManager = (PowerManager) CommunityPostActivity.this
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"My Lock");
		maximumPositiveFrequency();

		init();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void backPressCheck() {

		if (!edt_posting_text.getText().toString().trim().equals("")
				|| (mVoicePath != null && mVoicePath.length() > 0)
				|| (mPicturePath != null && mPicturePath.size() > 0)
				|| (mVideoPath != null && mVideoPath.length() > 0)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.backpress))
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									CommunityPostActivity.this.finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_right_out);
		}
	}

	private void postPressCheck() {
		smileyView.setVisibility(View.GONE);
		if (!edt_posting_text.getText().toString().trim().equals("")
				|| (mVoicePath != null && mVoicePath.length() > 0)
				|| (mPicturePath != null && mPicturePath.size() > 0)
				|| (mVideoPath != null && mVideoPath.length() > 0)) {

			if (!isInternetOn())
				return;
			// Utilities.closeSoftKeyBoard(findViewById(R.id.edt_posting_text),
			// this);
			String textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.id.edt_posting_text))
					.getText().toString();

			sendMessage(textMessage);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.postpress))
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// do things
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			groupname = intent.getStringExtra("group_name");
			group_id = intent.getStringExtra("group_id");
		}
		// TODO Auto-generated method stub
		txt_post = (TextView) findViewById(R.id.txt_post);
		prev = (TextView) findViewById(R.id.prev);
		edt_posting_text = (com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.id.edt_posting_text);
		edt_posting_text_count = (TextView) findViewById(R.id.edt_posting_text_count);
		edt_posting_text
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						500) });
		smileyView = (FrameLayout) findViewById(R.id.emojicons);
		img_talkingpic = (ImageView) findViewById(R.id.img_talkingpic);
		img_doodle = (ImageView) findViewById(R.id.img_doodle);
		img_gallery = (ImageView) findViewById(R.id.img_gallery);
		ig_video = (ImageView) findViewById(R.id.ig_video);
		img_camera = (ImageView) findViewById(R.id.img_camera);
		img_audio = (ImageView) findViewById(R.id.img_audio);
		// linearLayout
		ll_talkingpic = (LinearLayout) findViewById(R.id.ll_talkingpic);
		ll_doodle = (LinearLayout) findViewById(R.id.ll_doodle);
		ll_gallery = (LinearLayout) findViewById(R.id.ll_gallery);
		ll_video = (LinearLayout) findViewById(R.id.ll_video);
		ll_camera = (LinearLayout) findViewById(R.id.ll_camera);
		ll_audio = (LinearLayout) findViewById(R.id.ll_audio);

		community_post_smiley = (ImageView) findViewById(R.id.community_post_smiley);
		media_play = (ImageView) findViewById(R.id.media_play);
		media_close = (ImageView) findViewById(R.id.media_close);
		mediavoicePlayingDialog_progressbar = (SeekBar) findViewById(R.id.mediavoicePlayingDialog_progressbar);
		media_play_layout = (LinearLayout) findViewById(R.id.media_play_layout);
		llout_audio_player = (LinearLayout) findViewById(R.id.llout_audio_player);
		ll_imageadd = (LinearLayout) findViewById(R.id.ll_imageadd);
		scrollView_img = (HorizontalScrollView) findViewById(R.id.scrollView_img);
		ll_imageadd.setVisibility(View.GONE);
		scrollView_img.setVisibility(View.GONE);
		media_play_layout.setVisibility(View.GONE);
		llout_audio_player.setVisibility(View.GONE);
		setEmojiconFragment(false);
		media_play.setTag("PLAY");
		edt_posting_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (smileyView != null && smileyView.isShown()) {
					smileyView.setVisibility(View.GONE);
				}
			}
		});
		prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backPressCheck();
			}
		});
		media_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * if (mVoicePath != null) { if
				 * (media_play.getTag().equals("stop")) { if (Audio_player !=
				 * null && !Audio_player.isPlaying()) Audio_player.start();
				 * media_play.setTag("play");
				 * media_play.setImageResource(R.drawable.pause); isplaying =
				 * true; } else { if (Audio_player != null) Audio_player.stop();
				 * media_play.setImageResource(R.drawable.play);
				 * media_play.setTag("stop"); isplaying = false; } }
				 */

				if (((String) v.getTag()).equals("PLAY")) {
					if (mVoicePath.startsWith("http:")) {
						openPlayScreen(mVoicePath, false, media_play_layout);
					} else {
						openPlayScreen(
								Downloader.getInstance().getPlayUrl(mVoicePath),
								false, media_play_layout);
					}
				} else {
					media_play.setImageResource(R.drawable.play_timeline);
					media_play.setTag("PLAY");
					mediavoicePlayingDialog_progressbar.setProgress(0);
					mediavoicePlayingDialog_progressbar.invalidate();
					RTMediaPlayer.reset();
				}
			}
		});

		media_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Audio_player != null)
					Audio_player.stop();
				media_play.setImageResource(R.drawable.play_timeline);
				media_play.setTag("stop");
				isplaying = false;
				media_play_layout.setVisibility(View.GONE);
				llout_audio_player.setVisibility(View.GONE);
				mVoicePath = null;
				EnableAll();
			}
		});
		img_thumb_video = (ImageView) findViewById(R.id.img_thumb_video);
		img_close_video = (ImageView) findViewById(R.id.img_close_video);
		img_play_video = (ImageView) findViewById(R.id.img_play_video);
		llout_video_player = (RelativeLayout) findViewById(R.id.llout_video_player);
		llout_video_player.setVisibility(View.GONE);

		img_play_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {
					// TLA.notifyDataSetChanged();

					String fileName = mVideoPath.substring(mVideoPath
							.lastIndexOf("/") + 1);
					String fullPath = new StringBuffer(Environment
							.getExternalStorageDirectory().getPath())
							.append('/').append("YelaTalk").append('/')
							.append(fileName).toString();
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(mVideoPath);
					String mimeType = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension(extension);
					File file = new File(mVideoPath);
					Intent player = new Intent(Intent.ACTION_VIEW, Uri
							.fromFile(file));
					if (mimeType != null && mimeType.equalsIgnoreCase("3gp")) {
						player.setDataAndType(Uri.fromFile(file), "video/3gpp");
					} else {
						player.setDataAndType(Uri.fromFile(file), mimeType);
					}
					startActivity(player);

				} catch (Exception e) {

				}

				//

				// /
				/*
				 * final Dialog dialog = new Dialog(CommunityPostActivity.this);
				 * dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				 * dialog.setContentView(R.layout.videodialogue); dialog.show();
				 * WindowManager.LayoutParams lp = new
				 * WindowManager.LayoutParams( LayoutParams.WRAP_CONTENT,
				 * LayoutParams.WRAP_CONTENT);
				 * lp.copyFrom(dialog.getWindow().getAttributes());
				 * dialog.getWindow().setAttributes(lp); VideoView videoview =
				 * (VideoView) dialog .findViewById(R.id.surface_view); Uri uri
				 * = Uri.parse(mVideoPath); videoview.setVideoURI(uri);
				 * videoview.start();
				 */
			}
		});

		img_close_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llout_video_player.setVisibility(View.GONE);
				mVideoPath = null;
				EnableAll();
			}
		});
		community_post_smiley.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utilities.closeSoftKeyBoard(edt_posting_text,
						CommunityPostActivity.this);
				if (smileyView.isShown()) {
					smileyView.setVisibility(View.GONE);
				} else {
					smileyView.setVisibility(View.VISIBLE);
				}
				// isShowSmiley = !isShowSmiley;
			}
		});
		/*
		 * smileyView.setOnFocusChangeListener(new OnFocusChangeListener() {
		 * 
		 * @Override public void onFocusChange(View v, boolean hasFocus) { //
		 * TODO Auto-generated method stub if(hasFocus){
		 * 
		 * }else { smileyView.setVisibility(View.GONE); } } });
		 */
		edt_posting_text.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int aft) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// this will show characters remaining
				edt_posting_text_count.setText(500 - s.toString().length()
						+ "/500");
			}
		});

		ig_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openCameraForVedioRecording();

			}
		});
		img_talkingpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openThumbnailsToAttach(POSITION_TALKING_PIC);
			}
		});
		img_gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPicturePath.size() < 10) {
					// openCamera();
					dialogGallery();
				} else {
					Toast.makeText(getApplicationContext(),
							"Max Attached image is 10.", Toast.LENGTH_LONG)
							.show();
				}
				//

				// openGallery(POSITION_PICTURE);
			}
		});

		img_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPicturePath.size() < 10) {
					openCamera();
				} else {
					Toast.makeText(getApplicationContext(),
							"Max Attached image is 10.", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		img_audio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// openThumbnailsToAttach(POSITION_VOICE);
				wakeLock.acquire();

				if (RTMediaPlayer.isPlaying()) {
					RTMediaPlayer.reset();
					RTMediaPlayer.clear();
				}
				if (mRecordedVoicePath == null) {
					showAudioDialog(RECORDING_MODE);
				} else {
					showAudioDialog(PLAY_MODE);
				}
			}
		});
		img_doodle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openThumbnailsToAttach(POSITION_PICTURE_RT_CANVAS);
			}
		});

		txt_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				postPressCheck();
			}
		});
	}

	private void setEmojiconFragment(boolean useSystemDefault) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.emojicons,
						EmojiconsFragment.newInstance(useSystemDefault))
				.commit();
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment
				.backspace((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.id.edt_posting_text));
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment
				.input((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.id.edt_posting_text),
						emojicon);

	}

	@Override
	public void onFileDownloadResposne(View view, int type, byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileDownloadResposne(View view, int[] type,
			String[] file_urls, String[] file_paths) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEmoticon(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskCallback(Object parameter, byte mRequestObjNo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	private void dialogGallery() {
		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		// setting custom layout to dialog
		dialog.setContentView(R.layout.custom_dialog_gallery);
		dialog.setTitle("Gallery");
		LinearLayout ll_image = (LinearLayout) dialog
				.findViewById(R.id.ll_image);
		LinearLayout ll_video = (LinearLayout) dialog
				.findViewById(R.id.ll_video);
		LinearLayout ll_audio = (LinearLayout) dialog
				.findViewById(R.id.ll_audio);
		if (mPicturePath.size() != 0 && mPicturePath.size() < 10) {
			ll_video.setVisibility(View.GONE);
			ll_audio.setVisibility(View.GONE);
		}
		// adding text dynamically
		ll_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openGallery(POSITION_PICTURE);
				dialog.dismiss();
			}
		});
		ll_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				openThumbnailsToAttach(POSITION_VIDEO);
				dialog.dismiss();
			}
		});
		ll_audio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openThumbnailsToAttach(POSITION_VOICE);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void sendMessage(String textMessage) {
		rtDialog = ProgressDialog.show(CommunityPostActivity.this, "",
				getString(R.string.wait_feed), true);
		// postingMediaContent(textMessage);
		new getIdList().execute("");
		// postDataRequest request = new postDataRequest();
		// request.execute(groupname,""+BusinessProxy.sSelf.getUserId(),"Hello","","","","");
		// postDataOnServer(groupname,""+BusinessProxy.sSelf.getUserId(),"Hello","","","","");
	}

	private class getIdList extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// rtDialog = ProgressDialog.show(CommunityPostActivity.this, "",
			// getString(R.string.updating), true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			String img_file_id = null;
			try {
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
				if (mPicturePath != null && mPicturePath.size() > 0) {
					for (int i = 0; i < mPicturePath.size(); i++) {
						String fileId = Utilities.createMediaID(
								mPicturePath.get(i),
								Constants.ID_FOR_UPDATE_PROFILE);
						if (fileId.indexOf("}") != -1) {
							if (fileId.contains("message")) {
								final JSONObject jsonObject = new JSONObject(
										fileId);
								if (jsonObject.has("message"))
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											try {
												AppUtil.showTost(
														CommunityPostActivity.this,
														jsonObject
																.getString("message"));
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									});
							}
							return null;// fileId ;
						}
						// fileIdVec.add(fileId) ;
						if (fileId != null) {
							if (img_file_id == null)
								img_file_id = fileId + ",";
							else
								img_file_id = img_file_id + fileId + ",";
						}
					}
					imageServerId = img_file_id;
				}
				if (mVideoPath != null && mVideoPath.length() > 0) {

					String fileId = Utilities.createMediaID(mVideoPath,
							Constants.ID_FOR_UPDATE_PROFILE);
					if (fileId.indexOf("}") != -1) {
						if (fileId.contains("message")) {
							final JSONObject jsonObject = new JSONObject(fileId);
							if (jsonObject.has("message"))
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										try {
											AppUtil.showTost(
													CommunityPostActivity.this,
													jsonObject
															.getString("message"));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
						}
						return null;// fileId ;
					}
					// fileIdVec.add(fileId) ;
					if (fileId != null) {
						videoServerId = fileId;
						/*
						 * if (img_file_id == null) img_file_id = fileId + ",";
						 * else img_file_id = img_file_id + fileId+ ",";
						 */
					}

				}
				if (mVoicePath != null && mVoicePath.length() > 0) {

					String fileId = Utilities.createMediaID(mVoicePath,
							Constants.ID_FOR_UPDATE_PROFILE);
					if (fileId.indexOf("}") != -1) {
						if (fileId.contains("message")) {
							final JSONObject jsonObject = new JSONObject(fileId);
							if (jsonObject.has("message"))
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										try {
											AppUtil.showTost(
													CommunityPostActivity.this,
													jsonObject
															.getString("message"));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
						}
						return null;// fileId ;
					}
					// fileIdVec.add(fileId) ;
					if (fileId != null) {
						audioServerId = fileId;
						/*
						 * if (img_file_id == null) img_file_id = fileId + ",";
						 * else img_file_id = img_file_id + fileId+ ",";
						 */
					}

				}
			} catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(CommunityPostActivity.this,
								getString(R.string.something_went_wrong));// +"\n"+e.getLocalizedMessage())
																			// ;
					}
				});
			}
			return img_file_id;
		}

		@Override
		protected void onPostExecute(String response) {

			// closeDialog();

			try {
				postDataRequest request = new postDataRequest();

				request.execute(
						groupname,
						"" + BusinessProxy.sSelf.getUserId(),
						Base64.encodeToString(edt_posting_text.getText()
								.toString().getBytes(), Base64.DEFAULT), "",
						imageServerId, audioServerId, videoServerId);
				/*
				 * request.execute(groupname, "" +
				 * BusinessProxy.sSelf.getUserId(), new
				 * String(edt_posting_text.getText
				 * ().toString().getBytes("UTF-8")) , "", imageServerId,
				 * audioServerId, videoServerId);
				 */
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.onPostExecute(response);
		}
	}

	/*
	 * private void postingMediaContent(String textMessage) { // TODO
	 * Auto-generated method stub OutboxTblObject newRequest = new
	 * OutboxTblObject(1, Constants.FRAME_TYPE_VTOV,
	 * Constants.MSG_OP_VOICE_NEW); if (null != mVoicePath) {
	 * newRequest.mPayloadData.mVoiceType[0] = VoiceTypes.PCM_FORMAT;
	 * newRequest.mPayloadData.mVoice[0] = mVoicePath.getBytes();
	 * newRequest.mPayloadData.mPayloadTypeBitmap |=
	 * Payload.PAYLOAD_BITMAP_VOICE; } if (null != mVideoPath &&
	 * mVideoPath.length() > 0) { newRequest.mPayloadData.mVideoType[0] =
	 * VideoTypes.VIDEO_3GP_FORMAT; newRequest.mPayloadData.mVideo[0] =
	 * mVideoPath.getBytes(); newRequest.mPayloadData.mPayloadTypeBitmap |=
	 * Payload.PAYLOAD_BITMAP_VIDEO; } if (!"".equals(textMessage)) {
	 * newRequest.mPayloadData.mText[0] = textMessage.getBytes();
	 * newRequest.mPayloadData.mTextType[0] = 1;
	 * newRequest.mPayloadData.mPayloadTypeBitmap |=
	 * Payload.PAYLOAD_BITMAP_TEXT; } if (null != mPicturePath &&
	 * mPicturePath.size() > 0) { newRequest.mPayloadData.mPicture = new
	 * byte[mPicturePath.size()][]; newRequest.mPayloadData.mPictureType = new
	 * byte[mPicturePath.size()]; newRequest.mPayloadData.mPayloadTypeBitmap |=
	 * Payload.PAYLOAD_BITMAP_PICTURE; for (int i = 0; i < mPicturePath.size();
	 * i++) { CompressImage compressImage = new CompressImage(this);
	 * compressImage.checkRotation(false); String path =
	 * compressImage.compressImage(mPicturePath.get(i));
	 * newRequest.mPayloadData.mPicture[i] = ((String) path).getBytes();
	 * newRequest.mPayloadData.mPictureType[i] = PictureTypes.PICS_JPEG; } }
	 * newRequest.mDestContacts = new String[] { (groupname + "<g:"+ groupname +
	 * ">") }; if (replyToSender) { newRequest.mMessageId = replyMessageId;
	 * newRequest.mMessageOp = Constants.MSG_OP_REPLY; } // vector.add(entrys);
	 * //feed.entry.add(0, entrys); //Add this Entry to senderEntry so that, a
	 * check can be made at receiver side // senderEntry = entrys; if((null !=
	 * mVideoPath && mVideoPath.length() > 0)){ runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * if(viewHolder != null && viewHolder.progress_bar !=null ){
	 * viewHolder.progress_bar.setProgress(0);
	 * viewHolder.progress_bar.setSecondaryProgress(100);
	 * viewHolder.progress_bar.setVisibility(View.VISIBLE); } if(viewHolder !=
	 * null && viewHolder.download_circular_progress_percentage != null){
	 * viewHolder.download_circular_progress_percentage.setText("0%");
	 * viewHolder
	 * .download_circular_progress_percentage.setVisibility(View.VISIBLE); } }
	 * }); } hideShowNoMessageView(); mMainList.setSelection(feed.entry.size() -
	 * 1); mMediaAdapter.notifyDataSetChanged();
	 * mMainList.setStackFromBottom(true); mMainList.invalidateViews();
	 * 
	 * int ret = BusinessProxy.sSelf.sendToTransport(this, newRequest); switch
	 * (ret) { case Constants.ERROR_NONE: // CHAT_REFRESH_TIME =
	 * CHAT_MAX_REFRESH_TIME; // if((null != mVideoPath && mVideoPath.length() >
	 * 0)){ // runOnUiThread(new Runnable() { // // @Override // public void
	 * run() { // // TODO Auto-generated method stub //
	 * viewHolder.progress_bar.setProgress(0); //
	 * viewHolder.progress_bar.setSecondaryProgress(100); //
	 * viewHolder.progress_bar.setVisibility(View.VISIBLE); //
	 * viewHolder.download_circular_progress_percentage.setText("0%"); //
	 * viewHolder
	 * .download_circular_progress_percentage.setVisibility(View.VISIBLE); // }
	 * // }); // } // stopTimerToRefresh();
	 * ((com.rockerhieu.emojicon.EmojiconEditText)
	 * findViewById(R.id.edt_posting_text)).setText(""); mVoicePath = null;
	 * mVideoPath = null; mPicturePath.clear(); mPicturePathId.clear();
	 * replyMessageId = null; if (mMediaAdapter != null)
	 * mMediaAdapter.notifyDataSetChanged(); if (mMainList != null)
	 * getContent(mMainList.getFirstVisiblePosition()); break; case
	 * Constants.ERROR_OUTQUEUE_FULL: runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * if(mMediaAdapter != null){ mMediaAdapter.resetVideoUploaddInfeed();
	 * mMediaAdapter.notifyDataSetChanged(); } } });
	 * showSimpleAlert(getString(R.string.error),
	 * "Outbox full. Please try after some time!"); return; default:
	 * runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * if(mMediaAdapter != null){ mMediaAdapter.resetVideoUploaddInfeed();
	 * mMediaAdapter.notifyDataSetChanged(); } } }); } } //POST CODE SENDING
	 * //-----------------
	 */public class postDataRequest extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			// /deleteDialog = ProgressDialog.show(ChannelReportActivity.this,
			// "", getString(R.string.loading_dot), true);
			// deleteDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postDataOnServer(params[0], params[1], params[2],
					params[3], params[4], params[5], params[6]);
			return strData;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {
				// {"message":"Channel post created successfully.","status":"success","channelPostId":44}
				final JSONObject jmain;
				try {
					jmain = new JSONObject(result);
					if (jmain.has("message"))
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									AppUtil.showTost(
											CommunityPostActivity.this,
											jmain.getString("message"));
									CommunityTimeline.LOAD_DATA_MYSELF = 1;
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			Log.i("TAG", "DeleteRequest :: onPostExecute : result : " + result);
			closeDialog();
			finish();

		}
	}

	private String postDataOnServer(String gpName, String postedBy,
			String text, String hashtags, String imageFileIds,
			String audioFileId, String videoFileId) {
		String strData = null;
		String url = null;
		// http://www.yelatalkprod.com/tejas/feeds/api/channel/post
		url = "http://" + Urls.URL_LIVE + "/tejas/feeds/api/channel/post";
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);

		JSONObject listobj = new JSONObject();
		JSONObject listInvoice = new JSONObject();
		try {
			try {
				listInvoice.put("groupName",
						new String(gpName.trim().getBytes("UTF-8")));
				listInvoice.put("postedBy",
						new String(postedBy.getBytes("UTF-8")));
				// listInvoice.put("text", new String(text.getBytes("UTF-8")));
				if (!text.equals(""))
					listInvoice.put("text", text);
				listInvoice.put("hashtags",
						new String(hashtags.getBytes("UTF-8")));
				if (imageFileIds != null) {
					listInvoice.put("imageFileIds",
							new String(imageFileIds.getBytes("UTF-8")));
				} else {
					listInvoice.put("imageFileIds", "");
				}
				if (audioFileId != null) {
					listInvoice.put("audioFileId",
							new String(audioFileId.getBytes("UTF-8")));
				} else {
					listInvoice.put("audioFileId", "");
				}
				if (videoFileId != null) {
					listInvoice.put("videoFileId",
							new String(videoFileId.getBytes("UTF-8")));
				} else {
					listInvoice.put("videoFileId", "");
				}
				listobj.put("listInvoice", listInvoice);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				Log.v("listInvoice", "" + e);
				e.printStackTrace();
			}
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			Log.v("listInvoice", "" + e2);
			e2.printStackTrace();
		}

		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			String jsonStr;
			try {
				jsonStr = listobj.getString("listInvoice");
				httpPost.setEntity(new StringEntity(jsonStr));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// httpPost.setEntity(new UrlEncodedFormEntity(jsonStr));
			// httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,
			// "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);
			int responceCode = response.getStatusLine().getStatusCode();

			if (responceCode == 200) {
				InputStream ins = response.getEntity().getContent();
				// System.out.println("DATA = "+IOUtils.read(ins));
				strData = IOUtils.read(ins);
				ins.close();
			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}
		return strData;
	}

	// -----------------

	private void openThumbnailsToAttach(byte which) {
		Intent intent = new Intent();
		/*
		 * if(attachment_panel != null)
		 * attachment_panel.setVisibility(View.INVISIBLE);
		 */
		switch (which) {
		case POSITION_VOICE:
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, POSITION_VOICE);
			break;
		case POSITION_PICTURE_RT_CANVAS:
			intent = new Intent(this, RTCanvas.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
			// | Intent.FLAG_ACTIVITY_CLEAR_TOP
			// | Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.setData(Uri.parse("ddd")) ;
			startActivityForResult(intent, POSITION_PICTURE_RT_CANVAS);
			break;
		case POSITION_PICTURE:
		case POSITION_TALKING_PIC:
			/*
			 * if (mPicturePath != null && mPicturePath.size() > 0) { String[]
			 * data = new String[mPicturePath.size()]; for (int i = 0; i <
			 * mPicturePath.size(); i++) { data[i] = mPicturePath.get(i); }
			 * DataModel.sSelf.storeObject("3333", data); } if (mPicturePathId
			 * != null && mPicturePathId.size() > 0) { Integer[] dataId = new
			 * Integer[mPicturePathId.size()]; for (int i = 0; i <
			 * mPicturePathId.size(); i++) { dataId[i] = mPicturePathId.get(i);
			 * } DataModel.sSelf.storeObject("3333" + "ID", dataId); }
			 */
			intent = new Intent(this, KainatMultiImageSelection.class);
			// intent = new Intent(this, RocketalkMultipleSelectImage.class);
			intent.putExtra("MAX", 10);
			intent.putExtra("CAMERASHOW", 2);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
			// Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			if (which == POSITION_TALKING_PIC) {
				intent.putExtra("AUDIO_VISIBLITY", true);
				ImageWithAudio = true;
			} else {
				intent.putExtra("AUDIO_VISIBLITY", false);
				ImageWithAudio = false;
			}
			startActivityForResult(intent, POSITION_MULTI_SELECT_PICTURE);
			// intent.setType("image/*");
			// intent.setAction(Intent.ACTION_GET_CONTENT);
			// startActivityForResult(Intent.createChooser(intent,
			// "Select Picture"), POSITION_PICTURE);
			break;

		case POSITION_VIDEO:
			/*
			 * intent.setType("video/*");
			 * intent.setAction(Intent.ACTION_GET_CONTENT);
			 * startActivityForResult( Intent.createChooser(intent,
			 * "Select Video"), POSITION_VIDEO);
			 */
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_PICK);

			PackageManager pm = getPackageManager();
			List<ResolveInfo> allHandlers = pm.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			ApplicationInfo applicationInfo = null;
			for (ResolveInfo packageInfo : allHandlers) {
				applicationInfo = packageInfo.activityInfo.applicationInfo;

			}

			/*
			 * // open Gallery in Nexus plus All Google based ROMs if
			 * (doesPackageExist("" + applicationInfo)) intent.setClassName("" +
			 * applicationInfo, "com.android.gallery3d.app.Gallery");
			 * 
			 * // open Gallery in Sony Xperia android devices if
			 * (doesPackageExist("" + applicationInfo)) intent.setClassName("" +
			 * applicationInfo, "com.android.gallery3d.app.Gallery");
			 * 
			 * // open gallery in HTC Sense android phones if
			 * (doesPackageExist("" + applicationInfo)) intent.setClassName("" +
			 * applicationInfo, "com.htc.album.picker.PickerFolderActivity");
			 * 
			 * // open gallery in Samsung TouchWiz based ROMs if
			 * (doesPackageExist("" + applicationInfo)) intent.setClassName("" +
			 * applicationInfo, "com.cooliris.media.Gallery");
			 */

			startActivityForResult(intent, POSITION_VIDEO);

			break;
		}
	}

	private void openCamera() {
		/*
		 * cameraImagePath = null; File file = new
		 * File(Environment.getExternalStorageDirectory(), getRandomNumber() +
		 * ".jpg"); cameraImagePath = file.getPath(); Uri outputFileUri =
		 * Uri.fromFile(file); Intent i = new
		 * Intent("android.media.action.IMAGE_CAPTURE");
		 * i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		 * this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		 */

		String fileName = getRandomNumber() + ".jpg";

		// Create parameters for Intent with filename

		ContentValues values = new ContentValues();

		values.put(MediaStore.Images.Media.TITLE, fileName);

		values.put(MediaStore.Images.Media.DESCRIPTION,
				"Image capture by camera");

		// imageUri is the current activity attribute, define and save it for
		// later usage

		//
		//
		File file = new File(Environment.getExternalStorageDirectory(),
				getRandomNumber() + ".jpg");
		// if(capturedPath1==null)
		// capturedPath1 = file.getPath();
		// if(capturedPath2==null)
		// capturedPath2 = file.getPath();
		imageUri = Uri.fromFile(file);
		/*
		 * Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		 * i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); ((Activity)
		 * context).startActivityForResult(i, resultCode);
		 */

		/*
		 * imageUri = getContentResolver().insert(
		 * MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		 */

		/****
		 * EXTERNAL_CONTENT_URI : style URI for the "primary" external storage
		 * volume.
		 ****/

		// Standard Intent action that can be sent to have the camera
		// application capture an image and return it.

		Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		CameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(CameraIntent, POSITION_CAMERA_PICTURE);
	}

	private void openGallery(byte which) {
		Intent intent = new Intent();
		switch (which) {
		case POSITION_PICTURE:

			/*
			 * if (mPicturePath != null && mPicturePath.size() > 0) { String[]
			 * data = new String[mPicturePath.size()]; for (int i = 0; i <
			 * mPicturePath.size(); i++) { data[i] = mPicturePath.get(i); }
			 * DataModel.sSelf.storeObject("3333", data); } if (mPicturePathId
			 * != null && mPicturePathId.size() > 0) { Integer[] dataId = new
			 * Integer[mPicturePathId.size()]; for (int i = 0; i <
			 * mPicturePathId.size(); i++) { dataId[i] = mPicturePathId.get(i);
			 * } DataModel.sSelf.storeObject("3333" + "ID", dataId); }
			 */
			intent = new Intent(this, KainatMultiImageSelection.class);
			if (mPicturePath != null)
				intent.putExtra("MAX", 10 - (mPicturePath.size()));
			intent.putExtra("CAMERASHOW", 2);
			intent.putExtra("AUDIO_VISIBLITY", false);
			startActivityForResult(intent, POSITION_MULTI_SELECT_PICTURE);
			ImageWithAudio = false;
			// MediaChooser.setSelectionLimit(5);
			// MediaChooser.setSelectedMediaCount(0);
			// Intent intenta = new Intent(CommunityChatActivity.this,
			// BucketHomeFragmentActivity.class);
			// startActivity(intenta);
			break;

		}
	}

	public void openCameraForVedioRecording() {
		File videofile = null;
		try {
			videofile = File.createTempFile("temp", ".mp4", getCacheDir());
			// String videofileName = videofile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(Environment.getExternalStorageDirectory(), "rt"
				+ getRandomNumber() + ".mp4");

		if (BusinessProxy.sSelf.buildInfo.MODEL
				.equalsIgnoreCase(Constants.VIDEO_REC_PROB_ON_DEVICE2))
			videofile = file;

		cameraImagePath = file.getPath();
		Uri outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
		// intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,outputFileUri);//
		// Uri.fromFile(videofile));
		if (BusinessProxy.sSelf.buildInfo.MODEL
				.equalsIgnoreCase(Constants.VIDEO_REC_PROB_ON_DEVICE))
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					outputFileUri);// Uri.fromFile(videofile));
		else
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(videofile));
		// recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// intent.setType("image/*");
		intent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY,
				Constants.EXTRA_VIDEO_QUALITY);
		intent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, 0);
		// intent.putExtra("android.intent.extra.durationLimit",
		// Constants.VIDEO_RECORDING_DUERATION);
		// intent.putExtra("android.intent.extra.sizeLimit",
		// Constants.VIDEO_RECORDING_SIZE_LIMITE);

		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,
				Constants.VIDEO_RECORDING_DUERATION);
		intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,
				Constants.VIDEO_RECORDING_SIZE_LIMITE);
		startActivityForResult(intent, POSITION_VIDEO);
		// startRecoridng = true;

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// while(startRecoridng){
		// StatFs stat = new
		// StatFs(Environment.getExternalStorageDirectory().getPath());
		// long bytesAvailable = (long)stat.getBlockSize() *
		// (long)stat.getBlockCount();
		// long megAvailable = bytesAvailable / 1048576;
		// System.out.println("--------megAvailable-------"+megAvailable);
		// try{
		// Thread.sleep(1000);
		// }catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		//
		// }
		// }).start();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == -1)
			switch (requestCode) {
			case POSITION_VOICE:
				if (data.getData() != null) {
					mVoicePath = getPath(data.getData());
					if (mVoicePath != null)
						audioPlayer(mVoicePath);
					/*
					 * mVoicePath = getPath(data.getData()); if (mVoicePath !=
					 * null && mVoicePath.length() > 0 &&
					 * mVoicePath.contains("content://media")) { mVoicePath =
					 * getRealPathFromURI( getApplicationContext(),
					 * Uri.parse(mVoicePath));
					 * 
					 * } Audio_player = MediaPlayer.create(this,
					 * Uri.parse(mVoicePath));
					 * mediavoicePlayingDialog_progressbar.setMax(Audio_player
					 * .getDuration());
					 * media_play_layout.setVisibility(View.VISIBLE);
					 * llout_audio_player.setVisibility(View.VISIBLE);
					 * 
					 * //
					 * ((ImageView)findViewById(R.community_chat.sendButton)).
					 * performClick();
					 */}
				break;
			case POSITION_CAMERA_PICTURE:
				try {
					if (resultCode == RESULT_OK) {

						/*********** Load Captured Image And Data Start ****************/

						// String imageId = convertImageUriToFile(imageUri,
						// this);
						if (mPicturePath == null)
							mPicturePath = new Vector<String>();
						String path = "" + imageUri;
						path = path.replaceFirst("file://", "");
						mPicturePath.add(path);

						// Create and excecute AsyncTask to load capture image

						// new LoadImagesFromSDCard().execute(""+imageId);

						/*********** Load Captured Image And Data End ****************/
						disableAll();
						showImage();
						if (mPicturePath.size() < 10) {
							// img_camera.setImageResource(R.drawable.icon_picture_enable);
							ll_camera.setBackgroundColor(Color
									.parseColor("#ffffff"));
							img_camera.setClickable(true);

							// img_gallery.setImageResource(R.drawable.icon_gallery_enable);
							ll_gallery.setBackgroundColor(Color
									.parseColor("#ffffff"));
							img_gallery.setClickable(true);

						} else {
							/*
							 * img_camera
							 * .setImageResource(R.drawable.icon_picture_disable
							 * );
							 */
							ll_camera.setBackgroundColor(Color
									.parseColor("#BABFB8"));
							img_camera.setClickable(false);

							/*
							 * img_gallery
							 * .setImageResource(R.drawable.icon_gallery_disable
							 * );
							 */
							ll_gallery.setBackgroundColor(Color
									.parseColor("#BABFB8"));
							img_gallery.setClickable(false);
							// Toast.makeText(getApplicationContext(),
							// "Max Attached image is 10.",
							// Toast.LENGTH_LONG).show();
						}

					} else if (resultCode == RESULT_CANCELED) {

						Toast.makeText(this, " Picture was not taken ",
								Toast.LENGTH_SHORT).show();
					} else {

						Toast.makeText(this, " Picture was not taken ",
								Toast.LENGTH_SHORT).show();

					}
				} catch (Exception e) {
					// mPicturePath = cameraImagePath;
				}

				break;
			case POSITION_VIDEO:
				if (data.getData() != null) {
					try {
						mVideoPath = getPath(data.getData());
						File file = new File(mVideoPath);
						long size = file.length();

						if (size > (BusinessProxy.sSelf.MaxSizeData * 1024 * 1024)) {
							Toast.makeText(
									CommunityPostActivity.this,
									getString(R.string.max_attachment_reached_update),
									Toast.LENGTH_LONG).show();
							mVideoPath = null;
						} else {
							if (Utilities.getFileInputStream(mVideoPath) == null)
								mVideoPath = Utilities
										.getVideoLastVideoFile(this);

							Bitmap bmThumbnail;
							// MINI_KIND: 512 x 384 thumbnail
							bmThumbnail = ThumbnailUtils.createVideoThumbnail(
									mVideoPath, Thumbnails.MINI_KIND);
							llout_video_player.setVisibility(View.VISIBLE);
							img_thumb_video.setImageBitmap(bmThumbnail);
							disableAll();
						}
					} catch (Exception e) {
						mVideoPath = cameraImagePath;
					}

				}

				break;
			case POSITION_MULTI_SELECT_PICTURE:
				/*
				 * if(!isInternetOn()) return;
				 */
				// String[] all_path = data.getStringArrayExtra("all_path");
				// imagePathPos = (int[])
				// DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES+"IDINT");
				String s[] = (String[]) DataModel.sSelf
						.removeObject(DMKeys.MULTI_IMAGES);
				if (s != null) {
					if (mPicturePath == null)
						mPicturePath = new Vector<String>();
					for (int i = 0; i < s.length; i++) {
						try {
							mPicturePath.add(s[i]);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					s = null;
				}
				showImage();
				try {
					if (ImageWithAudio) {
						mVoicePath = (String) DataModel.sSelf
								.removeObject(DMKeys.VOICE_PATH);
						audioPlayer(mVoicePath);
						ImageWithAudio = false;
					}
				} catch (Exception e) {
				}
				disableAll();
				if (mPicturePath.size() < 10) {
					// img_camera.setImageResource(R.drawable.icon_picture_enable);
					ll_camera.setBackgroundColor(Color.parseColor("#ffffff"));
					img_camera.setClickable(true);
					/*
					 * img_gallery
					 * .setImageResource(R.drawable.icon_gallery_enable);
					 */
					ll_gallery.setBackgroundColor(Color.parseColor("#ffffff"));
					img_gallery.setClickable(true);
				}
				break;

			case POSITION_PICTURE_RT_CANVAS:
				if (!isInternetOn())
					return;
				String path;
				path = getPath(data.getData());
				if (mPicturePath == null)
					mPicturePath = new Vector<String>();
				mPicturePath.add(path);
				showImage();
				disableAll();

				break;
			}
	}

	public void audioPlayer(String mvpath) {
		mVoicePath = mvpath;
		/*
		 * if(mVoicePath.contains("/cache/")){ mVoicePath = null; }else{
		 */
		if (mVoicePath != null && mVoicePath.length() > 0
				&& mVoicePath.contains("content://media")) {
			mVoicePath = getRealPathFromURI(getApplicationContext(),
					Uri.parse(mVoicePath));

		}
		Audio_player = MediaPlayer.create(this, Uri.parse(mVoicePath));
		mediavoicePlayingDialog_progressbar.setMax(Audio_player.getDuration());
		media_play_layout.setVisibility(View.VISIBLE);
		llout_audio_player.setVisibility(View.VISIBLE);
		mediavoicePlayingDialog_progressbar.setMax(Audio_player.getDuration());
		disableAll();
		// }
		// ((ImageView)findViewById(R.community_chat.sendButton)).performClick();

	}

	private View.OnClickListener playerClickEvent = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.media_play:
				ImageView imageView1 = null;
				try {

					imageView1 = (ImageView) media_play_layout
							.findViewById(R.id.media_play);
					if (((String) v.getTag()).equals("PLAY")) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {

									// baradd.setTag(new Stats(object)) ;
									TextView tv = (TextView) media_play_layout
											.findViewById(R.id.streemStatus);
									tv.setTextColor(getResources().getColor(
											R.color.sub_heading));
									if (RTMediaPlayer.getUrl() != null)
										RTMediaPlayer.start();
									else {
										try {
											//
											RTMediaPlayer._startPlay(v.getTag()
													.toString());

										} catch (Exception e) {
											// e.print
										}
									}
								} catch (Exception e) {
								}
							}
						}).start();
						imageView1
								.setBackgroundResource(R.drawable.pause_icon_timeline);
						imageView1.setTag("PAUSE");

					} else if (((String) v.getTag()).equals("PAUSE")) {
						imageView1
								.setBackgroundResource(R.drawable.play_timeline);
						imageView1.setTag("PLAY");
						RTMediaPlayer.pause();
						imageView1 = (ImageView) media_play_layout
								.findViewById(R.id.media_pause_play);
						imageView1.setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {
				}
				break;
			case R.id.media_stop_play:
				media_play_layout.setVisibility(View.GONE);
				llout_audio_player.setVisibility(View.GONE);
				wakeLock.release();
				RTMediaPlayer.reset();
				RTMediaPlayer.clear();
				break;
			}
		}
	};

	private void openPlayScreen(final String url, boolean autoPlay,
			final LinearLayout ln) {
		RTMediaPlayer.setUrl(null);
		// tv.setTextColor(getResources().getColor(R.color.sub_heading));
		if (!autoPlay) {
			// imageView1.setTag(clickId+"^"+url);
			media_play.setTag("PLAY");
		//	media_play.setVisibility(View.INVISIBLE);
			// media_play.setOnClickListener(playerClickEvent);
		}
		RTMediaPlayer.setProgressBar(mediavoicePlayingDialog_progressbar);
		RTMediaPlayer.setMediaHandler(new VoiceMediaHandler() {

			@Override
			public void voiceRecordingStarted(String recordingPath) {
			}

			@Override
			public void voiceRecordingCompleted(String recordedVoicePath) {
				// TODO Auto-generated method stub

			}

			public void voicePlayStarted() {
				try {
					//ln.findViewById(R.id.streemStatus).setVisibility(View.GONE);
					media_play_layout.setVisibility(View.VISIBLE);
					llout_audio_player.setVisibility(View.VISIBLE);
					wakeLock.acquire();
					if (media_play != null) {
						media_play.setImageResource(R.drawable.pause_icon_timeline);
						media_play.setTag("PAUSE");
						media_play.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			public void voicePlayCompleted() {
				CommunityPostActivity.this.handler.post(new Runnable() {

					@Override
					public void run() {
						// closePlayScreen();
						// System.out.println("-----voicePlayCompleted-----");
						try {
							// enableViews();
							media_play
									.setImageResource(R.drawable.play_timeline);
							media_play.setTag("PLAY");
							RTMediaPlayer.reset();
							wakeLock.release();
							mediavoicePlayingDialog_progressbar.setVisibility(View.VISIBLE);
							mediavoicePlayingDialog_progressbar.setProgress(0);
							mediavoicePlayingDialog_progressbar.invalidate();
							// resetProgress();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
				// isFormDisplay = false ;
				// shouldStartDisplayTimer();
			}

			public void onError(int i) {
				try {
					BusinessProxy.sSelf.animPlay = false;
					// if (fullDialog != null && fullDialog.isShowing()) {
					// fullDialog.dismiss();
					// }
					// System.out.println("onerroe=======i");
					// TextView tv = (TextView)
					// ln.findViewById(R.id.streemStatus);
					// tv.setTextColor(getResources().getColor(R.color.red));
					// voiceIsPlaying = false;
					// tv.setText("Unable to play please try later!");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onDureationchanged(final long total, final long current) {
				CommunityPostActivity.this.handler.post(new Runnable() {

					@Override
					public void run() {
						try {

						} catch (Exception e) {
							// if(isAudio)
							RTMediaPlayer.reset();
						}
					}
				});
			}
		});
		//ln.findViewById(R.id.streemStatus).setVisibility(View.VISIBLE);
		// if(autoPlay)
		RTMediaPlayer._startPlay(url);
		ln.findViewById(R.id.media_play_layout).setVisibility(View.VISIBLE);
		llout_audio_player.setVisibility(View.VISIBLE);
		wakeLock.acquire();
	}

	public static String convertImageUriToFile(Uri imageUri, Activity activity) {

		Cursor cursor = null;
		String Path = null;
		int imageID = 0;

		try {

			/*********** Which columns values want to get *******/
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID,
					MediaStore.Images.Thumbnails._ID,
					MediaStore.Images.ImageColumns.ORIENTATION };

			cursor = activity.managedQuery(

			imageUri, // Get data for specific image URI
					proj, // Which columns to return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null // Order-by clause (ascending by name)

					);

			// Get Query Data

			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int columnIndexThumb = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
			int file_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

			// int orientation_ColumnIndex = cursor.
			// getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

			int size = cursor.getCount();

			/******* If size is 0, there are no images on the SD Card. *****/

			if (size == 0) {

				// imageDetails.setText("No Image");
			} else {

				int thumbID = 0;
				if (cursor.moveToFirst()) {

					/**************** Captured image details ************/

					/***** Used to show image on view in LoadImagesFromSDCard class ******/
					imageID = cursor.getInt(columnIndex);

					thumbID = cursor.getInt(columnIndexThumb);

					Path = cursor.getString(file_ColumnIndex);

					// String orientation =
					// cursor.getString(orientation_ColumnIndex);

					String CapturedImageDetails = " CapturedImageDetails : \n\n"
							+ " ImageID :"
							+ imageID
							+ "\n"
							+ " ThumbID :"
							+ thumbID + "\n" + " Path :" + Path + "\n";

					// // Show Captured Image detail on activity
					// imageDetails.setText( CapturedImageDetails );

				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		// Return Captured Image ImageID ( By this ImageID Image will load from
		// sdcard )

		return "" + Path;
	}

	private void disableAll() {
		// img_talkingpic.setImageResource(R.drawable.icon_talking_picture_disable);
		ll_talkingpic.setBackgroundColor(Color.parseColor("#BABFB8"));
		img_talkingpic.setClickable(false);
		// img_doodle.setImageResource(R.drawable.icon_doodle_disable);
		img_doodle.setClickable(false);
		ll_doodle.setBackgroundColor(Color.parseColor("#BABFB8"));
		// img_gallery.setImageResource(R.drawable.icon_gallery_disable);
		img_gallery.setClickable(false);
		ll_gallery.setBackgroundColor(Color.parseColor("#BABFB8"));
		// img_camera.setImageResource(R.drawable.icon_picture_disable);
		img_camera.setClickable(false);
		ll_camera.setBackgroundColor(Color.parseColor("#BABFB8"));
		// img_audio.setImageResource(R.drawable.icon_audio_recording_disable);
		img_audio.setClickable(false);
		ll_audio.setBackgroundColor(Color.parseColor("#BABFB8"));
		// ig_video.setImageResource(R.drawable.icon_video_disable);
		ig_video.setClickable(false);
		ll_video.setBackgroundColor(Color.parseColor("#BABFB8"));
	}

	private void EnableAll() {
		// img_talkingpic.setImageResource(R.drawable.icon_talking_picture_enable);
		ll_talkingpic.setBackgroundColor(Color.parseColor("#ffffff"));
		img_talkingpic.setClickable(true);
		// img_doodle.setImageResource(R.drawable.icon_doodle_enable);
		ll_doodle.setBackgroundColor(Color.parseColor("#ffffff"));
		img_doodle.setClickable(true);
		// img_gallery.setImageResource(R.drawable.icon_gallery_enable);
		ll_gallery.setBackgroundColor(Color.parseColor("#ffffff"));
		img_gallery.setClickable(true);
		// img_camera.setImageResource(R.drawable.icon_picture_enable);
		ll_camera.setBackgroundColor(Color.parseColor("#ffffff"));
		img_camera.setClickable(true);
		// img_audio.setImageResource(R.drawable.icon_audio_recording_enable);
		ll_audio.setBackgroundColor(Color.parseColor("#ffffff"));
		img_audio.setClickable(true);
		// ig_video.setImageResource(R.drawable.icon_video_enable);
		ll_video.setBackgroundColor(Color.parseColor("#ffffff"));
		ig_video.setClickable(true);
	}

	public static int maximumPositiveFrequency() {
		int localval = 0;
		int[] input11 = new int[] { 2, 30, 15, 10, 8, 25, 80 };
		// Write code here
		for (int i = 0; i < input11.length - 1; i++) {
			if (input11[i + 1] > input11[i])
				localval = localval + (input11[i + 1] - input11[i]);
		}
		return localval;
	}

	public void showImage() {
		ll_imageadd.setVisibility(View.VISIBLE);
		scrollView_img.setVisibility(View.VISIBLE);
		ll_imageadd.removeAllViews();
		if (mPicturePath != null && mPicturePath.size() > 0)
			for (int x = 0; x < mPicturePath.size(); x++) {

				/*
				 * ImageView image = new ImageView(CommunityPostActivity.this);
				 * image.setLayoutParams(new LayoutParams(250, 250)); //
				 * image.setBackgroundResource(R.drawable.ic_launcher);
				 */
				View hiddenInfo = getLayoutInflater().inflate(
						R.layout.imageview_with_close, ll_imageadd, false);
				ImageView image = (ImageView) hiddenInfo
						.findViewById(R.id.image_main);
				ImageView image_close = (ImageView) hiddenInfo
						.findViewById(R.id.image_close);
				image_close.setTag(x);
				hiddenInfo.setPadding(10, 0, 10, 0);
				image_close.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						if (mPicturePath != null && mPicturePath.size() == 1) {
							EnableAll();
						}
						mPicturePath.removeElementAt((Integer) v.getTag());
						showImage();
					}
				});
				try {
					/*
					 * Uri val =Uri.parse(new
					 * File(mPicturePath.get(x)).toString()); Bitmap imageBitmap
					 * =
					 * ThumbnailUtils.createVideoThumbnail(val.toString(),MediaStore
					 * .Images.Thumbnails.MINI_KIND);
					 */

					Bitmap imageBitmap = ThumbnailUtils.extractThumbnail(
							BitmapFactory.decodeFile(mPicturePath.get(x)),
							THUMBSIZE, THUMBSIZE);
					image.setImageBitmap(imageBitmap);
				} catch (Exception e) {
					Log.v("EX", "" + e);
					// image.setImageBitmap(BitmapFactory.decodeFile(mPicturePath.get(x)));
				}
				// ThumbImage.recycle();
				// image.setImageURI(Uri.parse(mPicturePath.get(x)));
				image.setTag(mPicturePath.get(x));
				ll_imageadd.addView(hiddenInfo);
				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(CommunityPostActivity.this,
								SlideShowScreen.class);
						// intent.putExtra("AUDIO_URL", result);
						intent.putExtra("IMAGE_ARR", mPicturePath
								.toArray(new String[mPicturePath.size()]));
						// slideBitmapArray = result;
						startActivity(intent);
					}
				});

			}
		// img_camera.setImageResource(R.drawable.icon_picture_enable);
		ll_camera.setBackgroundColor(Color.parseColor("#ffffff"));
		img_camera.setClickable(true);
		// img_gallery.setImageResource(R.drawable.icon_gallery_enable);
		ll_gallery.setBackgroundColor(Color.parseColor("#ffffff"));
		img_gallery.setClickable(true);
	}

	public String getPath(Uri uri) {
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return uri.getPath();
		}
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void showAudioDialog(final byte TYPE) {
		progress = 0;
		isCancelClick = false;
		dialog = new Dialog(CommunityPostActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.player_dialog);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// dialog.setCancelable(true);
		final Runnable wheelThread = new Runnable() {
			public void run() {
				wheelRunning = false;
				while (!wheelRunning) {
					if (progress < Constants.RECORDING_TIME) {
						isTimeOver = false;
						wheelProgress
								.incrementProgress(Constants.RECORDING_TIME);
						progress++;
						if (TYPE == PLAY_MODE)
							setPlayLeftTime(mVoiceMedia.getMediaDuration(),
									mVoiceMedia.getCurrentMediaTime());
						else if (TYPE == RECORDING_MODE) {
							// recordLeftTime((360 - progress)/3);
							recordLeftTime(Constants.RECORDING_TIME - progress);
						}
					} else {
						isTimeOver = true;
						wheelRunning = true;
						iCurrentState = UI_STATE_IDLE;
						CommunityPostActivity.this
								.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										stopRecording();
									}
								});
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				wheelRunning = true;
			}
		};

		wheelProgress = (AudioProgressWheel) dialog
				.findViewById(R.id.player_wheel);
		wheelProgress.setWheelType(AudioProgressWheel.RECORD_WHEEL);

		if (mRecordedVoicePath != null && TYPE == PLAY_MODE) {
			wheelProgress.resetCount();
			startPlaying(null);
			Thread s = new Thread(wheelThread);
			s.start();
			if (wheelProgress != null)
				wheelProgress.setBackgroundResource(R.drawable.profile_listen);
		} else {
			iCurrentState = UI_STATE_IDLE;
			wheelProgress.resetCount();
			startRecording(null);
			Thread s = new Thread(wheelThread);
			s.start();
			if (wheelProgress != null)
				wheelProgress.setBackgroundResource(R.drawable.profile_audio);
		}
		leftTimeView = (TextView) dialog.findViewById(R.id.left_time);

		Button cancelBt = (Button) dialog.findViewById(R.id.button_01);
		cancelBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isCancelClick = true;
				dialog.dismiss();
				mVoiceMedia.stop();
				payloadData = null;
				RTMediaPlayer.reset();
				isCancelClick = true;
				wakeLock.release();
			}
		});

		final Button doneReset = (Button) dialog.findViewById(R.id.button_02);
		if (TYPE == RECORDING_MODE) {
			doneReset.setText(getString(R.string.done));
		} else if (TYPE == PLAY_MODE) {
			doneReset.setText(getString(R.string.reset));
		}
		doneReset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isCancelClick = false;
				progress = 0;
				// TODO Auto-generated method stub
				if (doneReset.getText().toString()
						.equalsIgnoreCase(getString(R.string.done))) {
					wheelRunning = true;
					stopRecording();
					if (wheelProgress != null) {
						wheelProgress
								.setBackgroundResource(R.drawable.profile_audio);
						wheelProgress.resetCount();
					}
					// voiceBt.setImageResource(R.drawable.profile_listen);

					dialog.dismiss();
				} else if (doneReset.getText().toString()
						.equalsIgnoreCase(getString(R.string.reset))) {// if(TYPE
																		// ==
																		// PLAY_MODE){
					dialog.dismiss();
					wheelRunning = true;
					stopPlaying(v);
					if (wheelProgress != null)
						wheelProgress
								.setBackgroundResource(R.drawable.profile_listen);
					// voiceBt.setImageResource(R.drawable.profile_listen);
					File file = new File(mRecordedVoicePath);
					if (file.exists()) {
						file.delete();
					}
					// voiceBt.setImageResource(R.drawable.profile_audio);
					mRecordedVoicePath = null;
				}
				wakeLock.release();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (isCancelClick) {
					progress = 0;
					if (TYPE == RECORDING_MODE) {
						wheelRunning = true;
						stopRecording();
						if (wheelProgress != null)
							wheelProgress
									.setBackgroundResource(R.drawable.profile_audio);
						mRecordedVoicePath = null;
						// voiceBt.setImageResource(R.drawable.profile_audio);
						dialog.dismiss();
					} else if (TYPE == PLAY_MODE) {
						wheelRunning = true;
						stopPlaying(null);
						if (wheelProgress != null)
							wheelProgress
									.setBackgroundResource(R.drawable.profile_listen);
						dialog.dismiss();
					}
				}
				wheelProgress = null;
			}
		});
		dialog.show();
	}

	public void setPlayLeftTime(final int total, final int time) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				leftTimeView
						.setText(Utilities.converMiliSecond(time) + " secs");
			}
		});
	}

	public void recordLeftTime(final int time) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				leftTimeView.setText(String.format("%02d:%02d", (time / 60),
						(time % 60)) + " secs");
				// leftTimeView.setText(time+" secs");
			}
		});
	}

	private void startRecording(View v) {
		if (mVoiceMedia == null)
			mVoiceMedia = new VoiceMedia(CommunityPostActivity.this, this);
		mVoiceMedia.startRecording(Constants.RECORDING_TIME);
		iCurrentState = Constants.UI_STATE_RECORDING;
		Log.d("startRecording", "startRecording()");
	}

	private void stopRecording() {
		if (mVoiceMedia != null)
			mVoiceMedia.stopRec();
		iCurrentState = Constants.UI_STATE_IDLE;
		Log.i("stopRecording", "stopRecording()");
		if (!isCancelClick)
			audioPlayer(mVoiceMedia.getVoicePath());
	}

	private void startPlaying(View v) {
		if (mRecordedVoicePath != null) {
			mVoiceMedia.startNewPlaying(mRecordedVoicePath, null, false);
			// mVoiceMedia.startPlayingMedia(mRecordedVoicePath,
			// mediavoicePlayingDialog_progressbar, false);
			iCurrentState = Constants.UI_STATE_PLAYING;
		}
		Log.i("startPlaying", "Playing started..");
	}

	private void stopPlaying(View v) {
		if (mVoiceMedia != null)
			mVoiceMedia.stopVoicePlaying();
		iCurrentState = Constants.UI_STATE_IDLE;
		Log.i("stopPlaying", "stopPlaying()");
	}

	private void closeDialog() {
		if (rtDialog != null && rtDialog.isShowing())
			rtDialog.dismiss();
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "Title", null);
		return Uri.parse(path);
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks whether a hardware keyboard is available
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			if (smileyView != null && smileyView.isShown()) {
				smileyView.setVisibility(View.GONE);
			}
			// Toast.makeText(this, "keyboard visible",
			// Toast.LENGTH_SHORT).show();
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			if (smileyView != null && smileyView.isShown()) {
				smileyView.setVisibility(View.GONE);

				// Toast.makeText(this, "keyboard hidden",
				// Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onBackPressed() {
		backPressCheck();
	}
}
