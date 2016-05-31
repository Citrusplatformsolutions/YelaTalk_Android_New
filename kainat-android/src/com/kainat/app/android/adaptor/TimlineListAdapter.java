package com.kainat.app.android.adaptor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kainat.util.UtilDateTime;
import com.kainat.app.android.CommunityTimeline;
import com.kainat.app.android.ProfileViewActivity;
import com.kainat.app.android.R;
import com.kainat.app.android.bean.ChannelPostAPI;
import com.kainat.app.android.bean.ChannelPostAPIGetFeed;
import com.kainat.app.android.bean.MediaContentUrl;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.helper.CommunityChatDB;
import com.kainat.app.android.uicontrol.SlideShowScreen;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.rockerhieu.emojicon.EmojiconTextView;

public class TimlineListAdapter extends BaseAdapter {

	List<ChannelPostAPI> mList;
	LayoutInflater mInflater;
	Context mContext;
	SparseBooleanArray mSparseBooleanArray;
	ChannelPostAPIGetFeed channelPostForm;
	ViewHolder viewHolder = null;
	ProgressDialog rtDialog;
	ImageLoader imageLoader = ImageLoader.getInstance();
	private static final String TAG = "TimeLineAdapter";
	private static final int CONTENT_ID_VIDEO_DOWNLOAD = 6001;
	private static final int CONTENT_ID_VIDEO_PLAY = 7001;
	private static final int CONTENT_ID_Audio_DOWNLOAD = 8001;
	private static final int CONTENT_ID_Audio_PLAY = 9001;
	private static final int CONTENT_ID_IMAGE_LONG = 1502;
	private static final int CONTENT_ID_Audio_LONG = 1503;
	private static final int CONTENT_ID_Text_LONG = 1504;
	private static final int CONTENT_ID_image_LONG = 1505;
	private static final int CONTENT_ID_profile_LONG = 1506;
	private static final int CONTENT_ID_crestetime_LONG = 1507;
	int IsAdmin;
	TimlineListAdapter TLA;
	ListView listview;
	DisplayImageOptions optionss = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.imagedefault_tl)
			.showImageOnFail(R.drawable.imagedefault_tl)
			.showImageOnLoading(R.drawable.imagedefault_tl)
			.postProcessor(new BitmapProcessor() {
				@Override
				public Bitmap process(Bitmap bmp) {
					return Bitmap.createScaledBitmap(bmp, 300, 300, false);
				}
			}).build();

	public TimlineListAdapter(Context context,
			ChannelPostAPIGetFeed channelPostForm, int IsAdmin) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mSparseBooleanArray = new SparseBooleanArray();
		this.channelPostForm = channelPostForm;
		mList = channelPostForm.getChannelPostAPIList();
		this.IsAdmin = IsAdmin;

	}

	private void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub

		menu.setHeaderTitle(mContext.getResources().getString(R.string.option));
		String[] menuItems = mContext.getResources().getStringArray(
				R.array.timeline_option);
		for (int i = 0; i < menuItems.length; i++) {
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		// AdapterView.AdapterContextMenuInfo info =
		// (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = mContext.getResources().getStringArray(
				R.array.timeline_option);
		// String menuItemName = menuItems[menuItemIndex];
		// String listItemName = Countries[info.position];
		switch (menuItemIndex) {
		case 1:

			break;

		default:
			item.collapseActionView();
			break;
		}
		// TextView text = (TextView)findViewById(R.id.footer);
		// text.setText(String.format("Selected %s for item %s", menuItemName,
		// listItemName));
		return true;
	}

	@Override
	public int getCount() {
		return mList.size();
		// return mList.size();
	}

	public void refreshMe(TimlineListAdapter TLA, ListView listview) {
		this.TLA = TLA;
		this.listview = listview;
	}

	@Override
	public ChannelPostAPI getItem(int position) {
		if (mList != null)
			return mList.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View listItemView = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();

			listItemView = LayoutInflater.from(mContext).inflate(
					R.layout.row_community_timline, null);

			viewHolder.txt_username = (TextView) listItemView
					.findViewById(R.id.txt_username);
			viewHolder.txt_feeddetail = (EmojiconTextView) listItemView
					.findViewById(R.id.txt_feeddetail);
			viewHolder.txt_createdtime = (TextView) listItemView
					.findViewById(R.id.txt_createdtime);
			viewHolder.img_userprofile = (ImageView) listItemView
					.findViewById(R.id.img_userprofile);
			viewHolder.img_thumburlfeed = (ImageView) listItemView
					.findViewById(R.id.img_thumburlfeed);
			viewHolder.ll_imageadd_show = (LinearLayout) listItemView
					.findViewById(R.id.ll_imageadd_show);
			viewHolder.ll_audiolayout = (LinearLayout) listItemView
					.findViewById(R.id.inboxLayout_imageLayout_voice_media_play_layout_static);
			viewHolder.ll_media_play_static = (ImageView) listItemView
					.findViewById(R.id.ll_media_play_static);
			viewHolder.img_audio_download_now = (ImageView) listItemView
					.findViewById(R.id.img_audio_download_now);

			viewHolder.seekbar = (SeekBar) listItemView
					.findViewById(R.id.ll_mediavoicePlayingDialog_progressbar_static);
			viewHolder.video_img = (ImageView) listItemView
					.findViewById(R.id.video_img);
			viewHolder.videoview_ll = (LinearLayout) listItemView
					.findViewById(R.id.videoview_ll);
			viewHolder.video_view = (RelativeLayout) listItemView
					.findViewById(R.id.video_view);
			viewHolder.video_download_down = (ImageView) listItemView
					.findViewById(R.id.video_download_down);

			viewHolder.download_arrow = ((ImageView) listItemView
					.findViewById(R.id.video_download_down));
			viewHolder.ll_video_play_static = ((ImageView) listItemView
					.findViewById(R.id.ll_video_play_static));

			viewHolder.progress_bar = ((ProgressBar) listItemView
					.findViewById(R.id.download_circular_progress));
			viewHolder.download_circular_progress_percentage = ((TextView) listItemView
					.findViewById(R.id.download_circular_progress_percentage));
			viewHolder.audio_progressbar = ((ProgressBar) listItemView
					.findViewById(R.id.audio_progressbar));
			viewHolder.audio_progress_percentage = ((TextView) listItemView
					.findViewById(R.id.audio_progress_percentage));

			// listItemView =
			// mInflater.inflate(R.layout.row_community_timline,null);
			listItemView.setTag(viewHolder);
		} else {
			listItemView = (View) convertView;
			viewHolder = (ViewHolder) listItemView.getTag();
		}
		viewHolder.channelPostAPI = getItem(position);
		viewHolder.img_thumburlfeed.setVisibility(View.GONE);
		viewHolder.txt_username.setText(viewHolder.channelPostAPI
				.getPostedByUser().getName());
		try {
			if (viewHolder.channelPostAPI.getText() != null
					&& !viewHolder.channelPostAPI.getText().equals("")) {
				byte[] messageText = Base64.decode(
						viewHolder.channelPostAPI.getText(), Base64.DEFAULT);
				if ((new String(messageText, "utf-8")).length() == 0) {
					viewHolder.txt_feeddetail.setEmojiconSize(50);
				} else

					viewHolder.txt_feeddetail.setText(new String(messageText,
							"utf-8"));
			} else {
				viewHolder.txt_feeddetail.setVisibility(View.VISIBLE);
				viewHolder.txt_feeddetail.setText("");
			}

			viewHolder.txt_feeddetail.setId(CONTENT_ID_Text_LONG);
			viewHolder.txt_feeddetail.setTag(""
					+ viewHolder.channelPostAPI.getChannelPostId());
				viewHolder.txt_feeddetail.setOnLongClickListener(viewHolder);
		} catch (Exception e) {

		}

		try {
			viewHolder.txt_createdtime.setText(UtilDateTime.getTimePassed(""
					+ viewHolder.channelPostAPI.getCreatedDate()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		imageLoader.displayImage(viewHolder.channelPostAPI.getPostedByUser()
				.getThumbUrl(), viewHolder.img_userprofile, optionss);
		viewHolder.img_userprofile.setTag(""
				+ viewHolder.channelPostAPI.getPostedByUser().getUserId());
		viewHolder.txt_username.setTag(""
				+ viewHolder.channelPostAPI.getPostedByUser().getUserId());
		viewHolder.txt_username.setId(CONTENT_ID_profile_LONG);
		viewHolder.img_userprofile.setId(CONTENT_ID_image_LONG);
		viewHolder.txt_createdtime.setId(CONTENT_ID_crestetime_LONG);
		viewHolder.txt_createdtime.setTag(viewHolder.channelPostAPI
				.getChannelPostId());
		viewHolder.txt_createdtime.setOnLongClickListener(viewHolder);
		// viewHolder.img_userprofile.setOnLongClickListener(viewHolder);
		viewHolder.txt_username.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					String userName = (String) v.getTag();
					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, userName);
					Intent intent = new Intent((Activity) mContext,
							ProfileViewActivity.class);
					intent.putExtra("USERID", userName);
					intent.putExtra("CallFrom", 1);
					mContext.startActivity(intent);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		viewHolder.img_userprofile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					String userName = (String) v.getTag();
					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, userName);
					Intent intent = new Intent((Activity) mContext,
							ProfileViewActivity.class);
					intent.putExtra("USERID", userName);
					intent.putExtra("CallFrom", 1);
					mContext.startActivity(intent);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		viewHolder.ll_imageadd_show.setId(CONTENT_ID_IMAGE_LONG);
		viewHolder.ll_imageadd_show.setOnLongClickListener(viewHolder);
		if (viewHolder.channelPostAPI.getImageMediaContentUrlList() != null
				&& viewHolder.channelPostAPI.getImageMediaContentUrlList()
						.size() > 0) {

			viewHolder.ll_imageadd_show.setVisibility(View.VISIBLE);
			viewHolder.ll_imageadd_show.setTag(""
					+ viewHolder.channelPostAPI.getChannelPostId());
			showImage(viewHolder.ll_imageadd_show,
					viewHolder.channelPostAPI.getImageMediaContentUrlList(),
					viewHolder.channelPostAPI.getChannelPostId());
		} else {
			viewHolder.ll_imageadd_show.setVisibility(View.GONE);
		}
		// =====================================================================
		// VIDEO
		if (viewHolder.channelPostAPI.getVideoMediaContentUrlList() != null
				&& viewHolder.channelPostAPI.getVideoMediaContentUrlList()
						.size() > 0) {
			try {
				viewHolder.video_view.setVisibility(View.VISIBLE);
				viewHolder.video_img.setVisibility(View.VISIBLE);
				imageLoader.displayImage(viewHolder.channelPostAPI
						.getVideoMediaContentUrlList().get(0).getThumbUrl(),
						viewHolder.video_img, optionss);
				viewHolder.progress_bar.setId((int) viewHolder.channelPostAPI
						.getChannelPostId());
				viewHolder.video_download_down.setTag(viewHolder.channelPostAPI
						.getVideoMediaContentUrlList().get(0).getContentUrl());
				String[] strarr = new String[2];
				strarr[0] = viewHolder.channelPostAPI
						.getVideoMediaContentUrlList().get(0).getContentUrl();
				strarr[1] = "" + viewHolder.channelPostAPI.getChannelPostId();
				viewHolder.video_img.setTag(strarr);
				viewHolder.download_circular_progress_percentage
						.setId(((int) viewHolder.channelPostAPI
								.getChannelPostId()) + 800);
				String urlstr = viewHolder.channelPostAPI
						.getVideoMediaContentUrlList().get(0).getContentUrl();
				String fileName = urlstr.substring(urlstr.lastIndexOf('/') + 1,
						urlstr.length());
				// viewHolder.video_img.setTag(urlstr);
				if (isFileExists(fileName)) {
					viewHolder.video_download_down.setVisibility(View.GONE);
					viewHolder.ll_video_play_static.setVisibility(View.VISIBLE);
				} else {
					viewHolder.video_download_down.setVisibility(View.VISIBLE);
					viewHolder.video_download_down.setTag(urlstr);
				}
			} catch (Exception e) {
				Log.v("Error", "" + e);
			}
		} else {
			viewHolder.video_view.setVisibility(View.GONE);
		}

		// =======================================================================
		// AUDIO PART
		if (viewHolder.channelPostAPI.getAudioMediaContentUrlList() != null
				&& viewHolder.channelPostAPI.getAudioMediaContentUrlList()
						.size() > 0) {
			viewHolder.ll_audiolayout.setId(CONTENT_ID_Audio_LONG);
			viewHolder.ll_audiolayout.setTag(""
					+ viewHolder.channelPostAPI.getChannelPostId());
			viewHolder.ll_audiolayout.setOnLongClickListener(viewHolder);
			viewHolder.ll_audiolayout.setVisibility(View.VISIBLE);
			String audioUrl = viewHolder.channelPostAPI
					.getAudioMediaContentUrlList().get(0).getContentUrl();
			if (isFileExists(audioUrl.substring(audioUrl.lastIndexOf("/") + 1))) {
				viewHolder.ll_media_play_static.setVisibility(View.VISIBLE);
				viewHolder.audio_progress_percentage.setVisibility(View.GONE);
				viewHolder.audio_progressbar.setVisibility(View.GONE);
				viewHolder.img_audio_download_now.setVisibility(View.GONE);

			} else {
				viewHolder.ll_media_play_static.setVisibility(View.GONE);
				viewHolder.img_audio_download_now.setVisibility(View.VISIBLE);
			}
			viewHolder.ll_media_play_static.setTag(audioUrl);
			viewHolder.ll_media_play_static.setId(CONTENT_ID_Audio_PLAY);
			viewHolder.ll_media_play_static.setOnClickListener(viewHolder);
			viewHolder.img_audio_download_now.setTag(audioUrl);
			viewHolder.img_audio_download_now.setId(CONTENT_ID_Audio_DOWNLOAD);
			viewHolder.img_audio_download_now.setOnClickListener(viewHolder);

		} else {
			viewHolder.ll_audiolayout.setVisibility(View.GONE);
		}
		viewHolder.video_download_down.setId(CONTENT_ID_VIDEO_DOWNLOAD);
		viewHolder.video_download_down.setOnClickListener(viewHolder);
		viewHolder.video_img.setId(CONTENT_ID_VIDEO_PLAY);
		viewHolder.video_img.setOnClickListener(viewHolder);
		viewHolder.video_img.setOnLongClickListener(viewHolder);
		return listItemView;
	}

	private void listPlayAudio(final ImageView play_btn, final SeekBar seekbar,
			String url) {
		RTMediaPlayer.setUrl(url);
		RTMediaPlayer.setProgressBar(seekbar);
		RTMediaPlayer.setMediaHandler(new VoiceMediaHandler() {

			@Override
			public void voiceRecordingStarted(String recordingPath) {
				// TODO Auto-generated method stub

			}

			@Override
			public void voiceRecordingCompleted(String recordedVoicePath) {
				// TODO Auto-generated method stub

			}

			@Override
			public void voicePlayStarted() {
				// TODO Auto-generated method stub
				play_btn.setTag("PAUSE");
				play_btn.setBackgroundResource(R.drawable.pause_icon_timeline);
			}

			@Override
			public void voicePlayCompleted() {
				// TODO Auto-generated method stub
				play_btn.setTag("PLAY");
				play_btn.setBackgroundResource(R.drawable.play_timeline);
				seekbar.setProgress(0);
				seekbar.invalidate();
			}

			@Override
			public void onError(int i) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDureationchanged(long total, long current) {

			}

		});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};

	public void showImage(LinearLayout llout, List<MediaContentUrl> list,
			long contentid) {
		llout.setVisibility(View.VISIBLE);
		llout.removeAllViews();
		final String[] stockArr = new String[list.size()];
		;
		if (list != null && list.size() > 0)
			for (int x = 0; x < list.size(); x++) {

				/*
				 * ImageView image = new ImageView(CommunityPostActivity.this);
				 * image.setLayoutParams(new LayoutParams(250, 250)); //
				 * image.setBackgroundResource(R.drawable.ic_launcher);
				 */
				stockArr[x] = list.get(x).getContentUrl();
				ImageView image = new ImageView(mContext);
				LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(300,300);
				image.setLayoutParams(parms);
				// image.setTag(list.get(x).getContentId());
				image.setPadding(5, 5, 5, 5);
				image.setId(CONTENT_ID_IMAGE_LONG);
				image.setTag("" + contentid);
				image.setOnLongClickListener(viewHolder);
				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent((Activity) mContext,
								SlideShowScreen.class);
						// intent.putExtra("AUDIO_URL", result);
						intent.putExtra("IMAGE_ARR", stockArr);
						// slideBitmapArray = result;
						mContext.startActivity(intent);
					}
				});
				// rotateImage(list.get(x).getThumbUrl());
				imageLoader.displayImage(list.get(x).getThumbUrl(), image,
						optionss);
				// image.setBackgroundResource(R.drawable.ic_launcher);
				llout.addView(image);

				/*
				 * View hiddenInfo =
				 * getLayoutInflater().inflate(R.layout.imageview_with_close,
				 * ll_imageadd, false); ImageView image =
				 * (ImageView)hiddenInfo.findViewById(R.id.image_main);
				 * 
				 * image.setImageURI(Uri.parse(mPicturePath.get(x)));
				 * image.setTag(mPicturePath.get(x));
				 * ll_imageadd.addView(hiddenInfo);
				 */
			}

	}

	//
	public static Bitmap rotateImage(String path) {
		int orientation = 1;
		try {
			ExifInterface exifJpeg = new ExifInterface(path);
			orientation = exifJpeg.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			// // orientation =
			// Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(path, bfo);
		if (orientation != ExifInterface.ORIENTATION_NORMAL) {
			int width = bm.getWidth();
			int height = bm.getHeight();
			Matrix matrix = new Matrix();
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}
			return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		}

		return bm;
	}

	//
	public void mp3load(final String urlStr) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					// URL url = new URL(urlStr);
					// String urlstr =
					// "http://www.yelatalkprod.com/rtMediaServer/get/1_1_7_G_A_A5_eebz4q0ipz.mp3";
					URL url = new URL(urlStr);
					HttpURLConnection c = (HttpURLConnection) url
							.openConnection();
					c.setRequestMethod("GET");
					c.setDoOutput(true);
					c.connect();

					String dir = Environment.getExternalStorageDirectory()
							.getPath();
					String fullPath = new StringBuffer(dir).append('/')
							.append("YelaTalk").append('/').toString();
					/*
					 * String PATH = Environment.getExternalStorageDirectory() +
					 * "/download/";
					 */
					String fileName = urlStr.substring(
							urlStr.lastIndexOf('/') + 1, urlStr.length());
					Log.v("path is", "PATH: " + fullPath);
					File file = new File(fullPath);
					file.mkdirs();

					// String fileName = "test.mp3";

					File outputFile = new File(file, fileName);
					FileOutputStream fos = new FileOutputStream(outputFile);

					InputStream is = c.getInputStream();

					byte[] buffer = new byte[1024];
					int len1 = 0;
					while ((len1 = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len1);
					}
					fos.close();
					is.close();
				} catch (Exception e) {
					Log.v("Exception :-", "" + e);
				}

			}
		}).start();
	}

	class ViewHolder implements OnClickListener, OnLongClickListener {
		ChannelPostAPI channelPostAPI;
		TextView txt_username;
		TextView txt_createdtime;
		EmojiconTextView txt_feeddetail;
		ImageView img_userprofile;
		ImageView img_thumburlfeed;
		ImageView video_img;
		LinearLayout ll_imageadd_show;
		LinearLayout ll_audiolayout;
		LinearLayout videoview_ll;
		ImageView ll_media_play_static;
		ImageView ll_video_play_static;
		ImageView img_audio_download_now;
		SeekBar seekbar;
		RelativeLayout video_view;
		ImageView video_download_down;
		ImageView download_arrow;
		ProgressBar progress_bar;
		TextView download_circular_progress_percentage;
		ProgressBar audio_progressbar;
		TextView audio_progress_percentage;
		boolean status = false;
		String AudioStatus = "PLAY";

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case CONTENT_ID_Audio_DOWNLOAD:
				LoadAudioFromURL audioDownLoaderTask = new LoadAudioFromURL(0);
				audioDownLoaderTask.execute((String) v.getTag());
				break;
			case CONTENT_ID_Audio_PLAY:
				//TLA.notifyDataSetChanged();
				if(AudioStatus.equals("PLAY")){
				String urliss = (String) v.getTag();
				String fileName = urliss.substring(urliss.lastIndexOf("/") + 1);
				String fullPathAudio = new StringBuffer(Environment
						.getExternalStorageDirectory().getPath()).append('/')
						.append("YelaTalk").append('/').append(fileName)
						.toString();
				openPlayScreen(fullPathAudio, true, null);
				}else
				{
					((Activity) mContext).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ll_media_play_static
									.setBackgroundResource(R.drawable.play_timeline);
							AudioStatus = "PLAY";	
							seekbar.setVisibility(View.VISIBLE);
							seekbar.setProgress(0);
							seekbar.invalidate();
							RTMediaPlayer.reset();
						}
					});
					
				}
				break;
			case CONTENT_ID_VIDEO_DOWNLOAD:
				/*
				 * if(videoDownLoaderTask != null &&
				 * !videoDownLoaderTask.isCancelled()){
				 * videoDownLoaderTask.cancel(true); Log.i(TAG,
				 * "ViewHolder :: onClick : CONTENT_ID_VIDEO_CANCEL - true"); }
				 */
				LoadVideoFromURL videoDownLoaderTask = new LoadVideoFromURL(0);
				videoDownLoaderTask.execute((String) v.getTag());
				break;
			case CONTENT_ID_VIDEO_PLAY:

				// strarr
				String[] urlis = (String[]) v.getTag();
				//
				// mp3load(urlis);
				try {
					// TLA.notifyDataSetChanged();

					String fileNames = urlis[0].substring(urlis[0]
							.lastIndexOf("/") + 1);
					String fullPathVideo = new StringBuffer(Environment
							.getExternalStorageDirectory().getPath())
							.append('/').append("YelaTalk").append('/')
							.append(fileNames).toString();
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(urlis[0]);
					String mimeType = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension(extension);
					File file = new File(fullPathVideo);
					Intent player = new Intent(Intent.ACTION_VIEW,
							Uri.fromFile(file));
					if (mimeType != null && mimeType.equalsIgnoreCase("3gp")) {
						player.setDataAndType(Uri.fromFile(file), "video/3gpp");
					} else {
						player.setDataAndType(Uri.fromFile(file), mimeType);
					}
					mContext.startActivity(player);

				} catch (Exception e) {

				}
				break;
			}

		}

		private class LoadVideoFromURL extends
				AsyncTask<String, Integer, String> {
			int index;

			LoadVideoFromURL(int index) {
				this.index = index;
			}

			@Override
			protected void onCancelled(String result) {
				Log.i("", "LoadVideoFromURL :: onCancelled : " + result);

				super.onCancelled(result);
			}

			@Override
			protected void onPostExecute(String file_path) {
				try {

					TLA.notifyDataSetChanged();
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(file_path);
					String mimeType = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension(extension);
					File file = new File(file_path);
					// Uri uri = Uri.parse(aURL);
					// videoPlayer(uri,true);
					Intent player = new Intent(Intent.ACTION_VIEW,
							Uri.fromFile(file));
					// Toast.makeText(this,
					// "extension: " + extension + " mimeType: " + mimeType,
					// Toast.LENGTH_LONG).show();
					// player.setDataAndType(uri, "video/3gpp");
					if (mimeType != null && mimeType.equalsIgnoreCase("3gp")) {
						player.setDataAndType(Uri.fromFile(file), "video/3gpp");
					} else {
						player.setDataAndType(Uri.fromFile(file), mimeType);
					}
					mContext.startActivity(player);

				} catch (Exception e) {

				}
				download_circular_progress_percentage.setVisibility(View.GONE);
				progress_bar.setVisibility(View.GONE);
				viewHolder.ll_video_play_static.setVisibility(View.VISIBLE);
			}

			@Override
			protected void onPreExecute() {
				progress_bar.setVisibility(View.VISIBLE);
				video_download_down.setVisibility(View.GONE);
				download_circular_progress_percentage
						.setVisibility(View.VISIBLE);
			}

			@Override
			protected String doInBackground(String... params) {
				String fullPath = null;
				String fileName = null;
				String data_url = params[0];
				int count;
				String extension;
				// TODO Auto-generated method stub
				try {

					extension = MimeTypeMap.getFileExtensionFromUrl(data_url);
					if (extension == null || extension.trim().length() <= 0) {
						try {
							extension = data_url.substring(data_url
									.lastIndexOf(".") + 1);
						} catch (Exception e) {
							extension = "mp4";
						}
					}
					// Extract File name from the URL
					if (data_url.lastIndexOf('/') != -1
							&& data_url.lastIndexOf('.') != -1) {
						if (data_url.lastIndexOf('.') > data_url
								.lastIndexOf('/'))
							fileName = data_url.substring(
									data_url.lastIndexOf("/") + 1,
									data_url.lastIndexOf("."));
						else
							fileName = data_url.substring(data_url
									.lastIndexOf("=") + 1);
						File folder = new File(
								Environment.getExternalStorageDirectory()
										+ "/YelaTalk");
						if (!folder.exists()) {
							folder.mkdir();
						}
						// fullPath = new
						// StringBuffer(Environment.getExternalStorageDirectory().getPath()).append('/').append(fileName).append('.').append(extension).toString();
						fullPath = new StringBuffer(Environment
								.getExternalStorageDirectory().getPath())
								.append('/').append("YelaTalk").append('/')
								.append(fileName).append('.').append(extension)
								.toString();
						// Check if filename exists
						if (isFileExists(data_url.substring(data_url
								.lastIndexOf("/") + 1))) {
							// Return the same path
							return fullPath;
						}
					}

					URL url = new URL(data_url);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					// if(data_url.lastIndexOf(".mp3") != -1)
					{
						connection.setRequestProperty("RT-APP-KEY", ""
								+ BusinessProxy.sSelf.getUserId());
						connection.setRequestProperty("RT-DEV-KEY", ""
								+ BusinessProxy.sSelf.getClientId());
					}
					connection.connect();
					// getting file length
					int lenghtOfFile = connection.getContentLength();
					Log.d(TAG,
							"LoadVideoFromURL :: doInBackground : Length of File : "
									+ lenghtOfFile);
					// input stream to read file - with 8k buffer
					InputStream input = new BufferedInputStream(
							url.openStream(), 8192);
					// Output stream to write file
					OutputStream output = new FileOutputStream(fullPath);
					byte data[] = new byte[4096];
					long total = 0;
					while ((count = input.read(data)) != -1) {
						if (isCancelled()) {
							output.close();
							break;
						}
						total += count;
						publishProgress((int) ((total * 100) / lenghtOfFile));
						output.write(data, 0, count);
					}
					// flushing output
					output.flush();
					// closing streams
					output.close();
					input.close();
				} catch (Exception ex) {
					// resetVideoDownloadInfeed();
				}
				return fullPath;
			}

			/**
			 * Updating progress bar
			 * */
			protected void onProgressUpdate(Integer... progress) {
				updateStatus(index, progress[0]);
			}
		}

		private void updateStatus(int index, int Status) {

			progress_bar.setVisibility(View.VISIBLE);
			progress_bar.setProgress(Status);
			download_circular_progress_percentage.setVisibility(View.VISIBLE);
			download_circular_progress_percentage.setText("" + Status + "%");
		}

		// Audio Download work

		private class LoadAudioFromURL extends
				AsyncTask<String, Integer, String> {
			int index;

			LoadAudioFromURL(int index) {
				this.index = index;
			}

			@Override
			protected void onCancelled(String result) {
				Log.i("", "LoadVideoFromURL :: onCancelled : " + result);

				super.onCancelled(result);
			}

			@Override
			protected void onPostExecute(String file_path) {
				audio_progressbar.setVisibility(View.GONE);
				ll_media_play_static.setVisibility(View.VISIBLE);
				audio_progress_percentage.setVisibility(View.GONE);
			}

			@Override
			protected void onPreExecute() {
				audio_progressbar.setVisibility(View.VISIBLE);
				img_audio_download_now.setVisibility(View.GONE);
				audio_progress_percentage.setVisibility(View.VISIBLE);
			}

			@Override
			protected String doInBackground(String... params) {
				String fullPath = null;
				String fileName = null;
				String data_url = params[0];
				int count;
				String extension;
				try {

					extension = MimeTypeMap.getFileExtensionFromUrl(data_url);
					if (extension == null || extension.trim().length() <= 0) {
						try {
							extension = data_url.substring(data_url
									.lastIndexOf(".") + 1);
						} catch (Exception e) {
							extension = "mp3";
						}
					}
					if (data_url.lastIndexOf('/') != -1
							&& data_url.lastIndexOf('.') != -1) {
						if (data_url.lastIndexOf('.') > data_url
								.lastIndexOf('/'))
							fileName = data_url.substring(
									data_url.lastIndexOf("/") + 1,
									data_url.lastIndexOf("."));
						else
							fileName = data_url.substring(data_url
									.lastIndexOf("=") + 1);
						File folder = new File(
								Environment.getExternalStorageDirectory()
										+ "/YelaTalk");
						if (!folder.exists()) {
							folder.mkdir();
						}
						fullPath = new StringBuffer(Environment
								.getExternalStorageDirectory().getPath())
								.append('/').append("YelaTalk").append('/')
								.append(fileName).append('.').append(extension)
								.toString();
						if (isFileExists(data_url.substring(data_url
								.lastIndexOf("/") + 1))) {
							return fullPath;
						}
					}

					URL url = new URL(data_url);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					{
						connection.setRequestProperty("RT-APP-KEY", ""
								+ BusinessProxy.sSelf.getUserId());
						connection.setRequestProperty("RT-DEV-KEY", ""
								+ BusinessProxy.sSelf.getClientId());
					}
					connection.connect();
					int lenghtOfFile = connection.getContentLength();
					Log.d(TAG,
							"LoadVideoFromURL :: doInBackground : Length of File : "
									+ lenghtOfFile);
					// input stream to read file - with 8k buffer
					InputStream input = new BufferedInputStream(
							url.openStream(), 8192);
					// Output stream to write file
					OutputStream output = new FileOutputStream(fullPath);
					byte data[] = new byte[4096];
					long total = 0;
					while ((count = input.read(data)) != -1) {
						if (isCancelled()) {
							output.close();
							break;
						}
						total += count;
						publishProgress((int) ((total * 100) / lenghtOfFile));
						output.write(data, 0, count);
					}
					// flushing output
					output.flush();
					// closing streams
					output.close();
					input.close();
				} catch (Exception ex) {
					// resetVideoDownloadInfeed();
				}
				return fullPath;
			}

			/**
			 * Updating progress bar
			 * */
			protected void onProgressUpdate(Integer... progress) {
				updateStatusaudio(index, progress[0]);
			}
		}

		private void updateStatusaudio(int index, int Status) {

			audio_progressbar.setVisibility(View.VISIBLE);
			audio_progressbar.setProgress(Status);
			audio_progress_percentage.setVisibility(View.VISIBLE);
			audio_progress_percentage.setText("" + Status + "%");
		}

		//
		// ---------------- PLAY AUDIO
		//
		private void openPlayScreen(final String url, boolean autoPlay,
				final LinearLayout ln) {
			RTMediaPlayer.setUrl(null);
			// tv.setTextColor(getResources().getColor(R.color.sub_heading));
			if (!autoPlay) {
				// imageView1.setTag(clickId+"^"+url);
				// ll_media_play_static.setTag("PLAY");
				// ll_media_play_static.setVisibility(View.INVISIBLE);
				// media_play.setOnClickListener(playerClickEvent);
			}
			RTMediaPlayer.setProgressBar(seekbar);
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
						ll_audiolayout.setVisibility(View.VISIBLE);
						AudioStatus = "PAUSE";	
						//ll_media_play_static.setTag("PAUSE");
						ll_media_play_static
								.setBackgroundResource(R.drawable.pause_icon_timeline);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				public void voicePlayCompleted() {
					try {
						// enableViews();
						((Activity) mContext).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ll_media_play_static
										.setBackgroundResource(R.drawable.play_timeline);
								AudioStatus = "PAUSE";	
								//ll_media_play_static.setTag("PLAY");
								seekbar.setVisibility(View.VISIBLE);
								seekbar.setProgress(0);
								seekbar.invalidate();
								RTMediaPlayer.reset();
							}
						});
					} catch (Exception e) {
						// TODO: handle exception
						Log.e("Exception", "" + e);
					}
				}

				public void onError(int i) {
					try {
						BusinessProxy.sSelf.animPlay = false;
						ll_media_play_static
								.setBackgroundResource(R.drawable.play_timeline);
						seekbar.setVisibility(View.VISIBLE);
						seekbar.setProgress(0);
					} catch (Exception e) {
					}
				}

				@Override
				public void onDureationchanged(long total, long current) {
					// TODO Auto-generated method stub

				}
			});
			RTMediaPlayer._startPlay(url);
		}

		@Override
		public boolean onLongClick(View arg0) {
			// TODO Auto-generated method stub

			switch (arg0.getId()) {
			case CONTENT_ID_Audio_LONG:
			case CONTENT_ID_crestetime_LONG:
			case CONTENT_ID_image_LONG:
			case CONTENT_ID_profile_LONG:
			case CONTENT_ID_Text_LONG:
			case CONTENT_ID_VIDEO_PLAY:
			case CONTENT_ID_IMAGE_LONG:
				String Id_ = null;
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						mContext);
				if (arg0.getId() == CONTENT_ID_VIDEO_PLAY) {
					String[] urlis = (String[]) arg0.getTag();
					Id_ = urlis[1];
				} else {
					Id_ = (String) arg0.getTag();
				}

				CharSequence colors[] = new CharSequence[] { "Delete",
						"Cancel", };

				final String abc = Id_;
				builder.setTitle(mContext.getResources().getString(
						R.string.option));
				builder.setItems(colors, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// the user clicked on colors[which]
						if (which == 0) {

							new AsyncTaskRunner("" + abc).execute();
							// asyn call
						} else {
							// builder.cancel();
						}
					}
				});
				
				if ((IsAdmin == 1)
						|| (((int) channelPostAPI.getPostedByUser()
								.getUserId()) == BusinessProxy.sSelf.getUserId()))
				builder.show();

				break;
			}
			return false;
		}

		// DELETE WORK HERE
		// DATE 20-05-2016
		// MANOJ SINGH

		private class AsyncTaskRunner extends AsyncTask<String, String, String> {

			private String channelPostId;

			AsyncTaskRunner(String channelPostId) {
				this.channelPostId = channelPostId;
			}

			@Override
			protected String doInBackground(String... params) {
				String responseStr = null;
				try {
					// http://www.yelatalkprod.com/tejas/feeds/api/channel/post/delete?channelPostId=36
					String url = "http://"
							+ Urls.TEJAS_HOST
							+ "/tejas/feeds/api/channel/post/delete?channelPostId="
							+ channelPostId;

					HttpClient httpClient = new DefaultHttpClient();
					// Creating HTTP Post
					HttpPost httpPost = new HttpPost(url);
					String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword());
					httpPost.addHeader("RT-APP-KEY", appKeyValue);

					try {
						HttpResponse response = httpClient.execute(httpPost);
						int responceCode = response.getStatusLine()
								.getStatusCode();

						if (responceCode == 200) {
							InputStream ins = response.getEntity().getContent();
							// System.out.println("DATA = "+IOUtils.read(ins));
							responseStr = IOUtils.read(ins);
							ins.close();
						}
						// writing response to log
					} catch (ClientProtocolException e) {
						// writing exception to log
						e.printStackTrace();
						return responseStr;
					} catch (IOException e) {
						// writing exception to log
						e.printStackTrace();
						return responseStr;
					}
					return responseStr;
				} catch (Exception e) {
					Log.v("", "" + e);
				}
				return responseStr;
			}

			@Override
			protected void onPostExecute(String result) {
				// execution of result of Long time consuming operation
				if (rtDialog != null && rtDialog.isShowing())
					rtDialog.dismiss();

				// {"status":"success","message":"Channel Post deleted by poster successfully"}
				if (result != null) {
					final JSONObject jmain;
					try {
						jmain = new JSONObject(result);

						if (jmain.has("status")) {
							String jmainStr = jmain.getString("status");
							if (jmainStr.equals("success")) {
								CommunityChatDB CCB = new CommunityChatDB(
										mContext);
								CCB.TImelineMessageDeletion(channelPostId);

								if (jmain.has("message")) {
									Toast.makeText(mContext,
											jmain.getString("message"),
											Toast.LENGTH_LONG).show();
									mList.remove(channelPostAPI);
									TLA.notifyDataSetChanged();
								}
							} else {
								if (jmain.has("ytErrors")) {
									JSONArray jSONArray = jmain
											.getJSONArray("ytErrors");
									for (int i = 0; i < jSONArray.length(); i++) {
										JSONObject nameObjectw = jSONArray
												.getJSONObject(0);
										String desc = nameObjectw.optString(
												"message").toString();
										Toast.makeText(mContext, desc,
												Toast.LENGTH_LONG).show();
									}
								}
							}
						} else {
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			protected void onPreExecute() {

				rtDialog = ProgressDialog.show(mContext, "",
						mContext.getString(R.string.refreshing_list), true);
			}
		}
	}

	private boolean isFileExists(String file_name) {
		String dir = Environment.getExternalStorageDirectory().getPath();
		String fullPath = new StringBuffer(dir).append('/').append("YelaTalk")
				.append('/').append(file_name).toString();
		return new File(fullPath).isFile();
	}

}