package com.kainat.app.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.facebook.AsyncFacebookRunner;
import com.kainat.app.android.facebook.BaseDialogListener;
import com.kainat.app.android.facebook.BaseRequestListener;
import com.kainat.app.android.facebook.Facebook;
import com.kainat.app.android.facebook.FacebookError;
import com.kainat.app.android.facebook.Login;
import com.kainat.app.android.facebook.SessionEvents;
import com.kainat.app.android.facebook.SessionEvents.AuthListener;
import com.kainat.app.android.facebook.SessionEvents.LogoutListener;
import com.kainat.app.android.facebook.SessionStore;
import com.kainat.app.android.facebook.Util;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;

public class FbConfigActivity extends UIActivityManager {
//	public static final String APP_ID = "167162466662454";

	// public static final String APP_ID = "275717542543914";
	// private String[] mPermissions;
	boolean fbFlag = true;
	Dialog lDialogProgress;
//	private Facebook mFacebook;
	private Context mFacebookContext;
	private AsyncFacebookRunner mAsyncRunner;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFacebookContext = this;
		setContentView(R.layout.postfbconfig);
		init();
		((TextView) findViewById(R.id.configtext))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						FacebookSession();
					}
				});
		((ToggleButton) findViewById(R.id.toggleButton1))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (((ToggleButton) findViewById(R.id.toggleButton1))
								.isChecked()) {
							fbFlag = true;
							// ((ToggleButton)findViewById(R.id.toggleButton1)).setChecked(false);
						} else {
							fbFlag = false;
							// ((ToggleButton)findViewById(R.id.toggleButton1)).setChecked(true);
						}
					}
				});

		((Button) findViewById(R.id.postfbScreen_doneButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						setResult(RESULT_OK) ;
						finish() ;
					}
				});
	}

	public void init() {
		Login login = new Login(FbConfigActivity.this);
		if(BusinessProxy.sSelf.mFacebook==null)
		BusinessProxy.sSelf.mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(BusinessProxy.sSelf.mFacebook);

		// SessionStore.restore(mFacebook,mFacebookContext);
		SessionEvents.addAuthListener(new SampleAuthConfermListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		login.init(FbConfigActivity.this, BusinessProxy.sSelf.mFacebook);
		// SessionStore.restore(mFacebook, MediaVideoPlayer.this);
	}

	public void FacebookSession() {
		Login login = new Login(FbConfigActivity.this);
		BusinessProxy.sSelf.mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(BusinessProxy.sSelf.mFacebook);

		// SessionStore.restore(mFacebook,mFacebookContext);
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		login.init(FbConfigActivity.this, BusinessProxy.sSelf.mFacebook);
		// SessionStore.restore(mFacebook, MediaVideoPlayer.this);

	}

	public class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			try {

				FbConfigActivity.this.runOnUiThread(new Runnable() {
					public void run() {
					}
				});

			} catch (Exception e) {
				System.out.println("Ëxception in facebook dailog==" + e);
			}

		}

		@Override
		public void onAuthFail(String error) {
			// TODO Auto-generated method stub

		}

	}

	public class SampleAuthConfermListener implements AuthListener {

		public void onAuthSucceed() {
			try {

				FbConfigActivity.this.runOnUiThread(new Runnable() {
					public void run() {

						((ToggleButton) findViewById(R.id.toggleButton1))
								.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.configtext))
								.setVisibility(View.GONE);
					}
				});

			} catch (Exception e) {
				System.out.println("Ëxception in facebook dailog==" + e);
			}

		}

		@Override
		public void onAuthFail(String error) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		BusinessProxy.sSelf.mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class SampleLogoutListener implements LogoutListener {
		public void onLogoutBegin() {
			// mText.setText("Logging out...");
		}

		public void onLogoutFinish() {
			// mText.setText("You have logged out! ");
			SessionStore.clear(FbConfigActivity.this);
		}
	}

	public class SampleRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			try {
				// process the response here: executed in background thread
				Log.d("Facebook-Example", "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String name = json.getString("name");

				FbConfigActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						// mText.setText("Hello there, " + name + "!");
					}
				});
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}
	}

	public class SampleUploadListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			try {

				Log.d("Facebook-Example", "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String src = json.getString("src");

				FbConfigActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						// mText.setText("Hello there, photo has been uploaded at \n"
						// + src);
					}
				});
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}
	}

	public class WallPostRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			Log.d("Facebook-Example", "Got response: " + response);
			String message = "<empty>";
			try {
				JSONObject json = Util.parseJson(response);
				message = json.getString("message");
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
			final String text = "Your Wall Post: " + message;
			FbConfigActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					// mText.setText(text);

					// SessionStore.restore(mFacebook, MediaVideoPlayer.this);
				}
			});

		}
	}

	public class WallPostDeleteListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			if (response.equals("true")) {
				Log.d("Facebook-Example", "Successfully deleted wall post");
				FbConfigActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						// mDeleteButton.setVisibility(View.INVISIBLE);
						// mText.setText("Deleted Wall Post");
					}
				});
			} else {
				Log.d("Facebook-Example", "Could not delete wall post");
			}
		}
	}

	public class SampleDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values) {
			try {
				final String postId = values.getString("post_id");
				if (postId != null) {
					Log.d("Facebook-Example", "Dialog Success! post_id="
							+ postId);

					mAsyncRunner.request(postId, new WallPostRequestListener());

				} else {
					Log.d("Facebook-Example", "No wall post made");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private boolean createFacebookId(
			final Hashtable<String, String> postParams, final byte[] imageData,
			final boolean update) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				String url = null;
				// if (update) {

				url = "http://" + Urls.TEJAS_HOST
						+ "/tejas/feeds/api/media/create";
				// } else {
				// url = "http://" + TEJAS_HOST +
				// "/tejas/feeds/api/group/create";in
				// }
				try {
					String response = uploadPhoto(url, postParams, imageData);
					dismissAnimationDialog();
					final JSONObject jsonObject = new JSONObject(response);
					final String status = jsonObject.getString("status");
					if (status.equals("error")) {
						try {
							showSimpleAlert("Error",
									jsonObject.getString("message"));
						} catch (JSONException e) {
							showSimpleAlert("Error", e.toString());
						}
					} else {
						try {
							showSimpleAlert("Success",
									jsonObject.getString("message"),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									});
						} catch (JSONException e) {
							showSimpleAlert("Error", e.toString());
						}
					}

				} catch (Exception e1) {
					e1.printStackTrace();
					dismissAnimationDialog();
					showSimpleAlert("Error", e1.toString());
					runOnUiThread(new Runnable() {
						public void run() {
							// chked_status.setTextColor(Color.BLACK);
							// chked_status.setText("Check Available!");
						}
					});
				}
			}
		}, 10);
		return true;
	}

	private String uploadPhoto(String urlStr,
			Hashtable<String, String> postParams, byte[] imageData)
			throws Exception {
		String BOUNDRY = "0xKhTmLbOuNdArY";
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			// This is the standard format for a multipart request
			byte messageByteArray[] = new byte[0];
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					"\r\n--".getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					BOUNDRY.getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					"\r\n".getBytes("UTF-8"));
			for (Iterator<String> iterator = postParams.keySet().iterator(); iterator
					.hasNext();) {
				String key = iterator.next();
				String value = postParams.get(key);

				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Disposition: form-data; charset=utf-8; name=\""
								.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						key.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\"\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						value.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n".getBytes("UTF-8"));
			}
			if (imageData != null) {
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Disposition: form-data; charset=utf-8; name=\"data\""
								.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Type: application/octet-stream"
								.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						imageData);
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"--\r\n".getBytes("UTF-8"));
			}

			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; charset=utf-8; boundary=" + BOUNDRY);

			conn.addRequestProperty("RT-APP-KEY",
					"" + BusinessProxy.sSelf.getUserId());
			conn.setRequestProperty("RT-APP-KEY",
					"" + BusinessProxy.sSelf.getUserId());

			conn.addRequestProperty("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());
			conn.setRequestProperty("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());

			writeToFile(new String(messageByteArray));

			// Send the body
			OutputStream dataOS = conn.getOutputStream();
			dataOS.write(messageByteArray);
			dataOS.flush();
			dataOS.close();

			// Ensure we got the HTTP 200 response code
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new Exception(String.format(
						"Received the response code %d from the URL %s",
						responseCode, url));
			}

			// Read the response
			is = conn.getInputStream();
			return IOUtils.read(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					// do nothing
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private static void writeToFile(String stacktrace) {
		File file = new File("/sdcard/mediaFb_post.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					true));
			writer.write(stacktrace);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dismissAnimationDialog() {
		runOnUiThread(new Runnable() {
			public void run() {

				if (null != lDialogProgress && lDialogProgress.isShowing()) {
					lDialogProgress.dismiss();
					lDialogProgress = null;
				}

			}
		});
	}

	/*
	 * protected final void showAlertMessage(final String title, final String
	 * message, final int[] buttonTypes, final String[] buttonTexts, final
	 * DialogInterface.OnClickListener[] buttonListeners) {
	 * 
	 * runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * showCustomMessage(title, message,buttonTypes,buttonTexts,buttonListeners)
	 * ; } });
	 * 
	 * }
	 */
	/*
	 * public synchronized final void showSimpleAlert(final String title, final
	 * String message) { runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { showSimpleAlert(title, message, null); }
	 * });
	 * 
	 * }
	 */

	/*
	 * public synchronized final void showSimpleAlert(final String title, final
	 * String message, final DialogInterface.OnClickListener listener) { if
	 * (null == message) { return; } if (lDialog != null && lDialog.isShowing())
	 * { return; }
	 * 
	 * runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * showCustomMessage(title,message,null,null,null); } });
	 * 
	 * }
	 */

	Dialog lDialog = null;

	public void showCustomMessage(String pTitle, final String pMsg,
			final int[] buttonTypes, final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners) {
		try {

			if (lDialog != null && lDialog.isShowing()) {
				lDialog.dismiss();
				lDialog = null;
			}

			lDialog = new Dialog(this,
					android.R.style.Theme_Translucent_NoTitleBar) {
				public void onBackPressed() {
					if (null == buttonTypes)
						if (lDialog == null && lDialog.isShowing())
							lDialog.dismiss();
				};
			};
			lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			lDialog.setContentView(R.layout.r_okcanceldialogview);
			((TextView) lDialog.findViewById(R.id.dialog_title))
					.setText(pTitle);
			((TextView) lDialog.findViewById(R.id.dialog_message))
					.setText(pMsg);
			LinearLayout buttonContainer = ((LinearLayout) lDialog
					.findViewById(R.id.buttonContainer));// .setText(pMsg);
			LinearLayout con = ((LinearLayout) lDialog.findViewById(R.id.con));
			// View convertView =
			// LayoutInflater.from(this).inflate(R.layout.button, null);
			// Button b = ((Button)
			// convertView.findViewById(R.id.button));//.setText(pMsg);.findViewById(R.id.dialog_message)).setText(pMsg);
			//
			// android:layout_width="0dip"
			// android:background="@drawable/custom_button1"
			// android:layout_weight="0.5"
			// android:layout_gravity="left"
			// android:textColor = "@color/white"
			// android:text="Cancel"
			// android:textStyle="bold"
			// android:layout_marginLeft="10dip"
			// android:id="@+id/cancel"

			android.widget.LinearLayout.LayoutParams param = new android.widget.LinearLayout.LayoutParams(
					0, (int) Utilities.convertDpToPixel(40, this), 0.5f);
			param.leftMargin = (int) Utilities.convertDpToPixel(5, this);
			param.rightMargin = (int) Utilities.convertDpToPixel(5, this);
			/*
			 * Button button = new Button(this); button.setLayoutParams(param) ;
			 * button.setText("sdfsdf") ; button.setTextColor(0xffffffff);
			 * button.setHeight(25) ;
			 * 
			 * button.setBackgroundResource(R.drawable.custom_button1) ;
			 * buttonContainer.addView(button);
			 * 
			 * button = new Button(this); button.setLayoutParams(param) ;
			 * button.setText("sdfsdf") ; button.setTextColor(0xffffffff);
			 * button.setHeight(25) ;
			 * button.setBackgroundResource(R.drawable.custom_button1) ;
			 * buttonContainer.addView(button);
			 */

			if (null != buttonTypes) {
				for (int i = 0; i < buttonTypes.length; i++) {
					// lDialog.setButton(buttonTypes[i], buttonTexts[i],
					// buttonListeners[i]);

					Button button = new Button(this);
					button.setLayoutParams(param);
					button.setText(buttonTexts[i]);
					button.setTextColor(0xffffffff);
					button.setId(i);
					// button.setPadding((int) Utilities.convertDpToPixel(10,
					// this), 0, 0, 0);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							runOnUiThread(new Runnable() {
								public void run() {
									cancelAlert();
									lDialog.dismiss();
									int btnIndex = v.getId();
									try {
										// if(buttonTypes[v.getId()] != 0)
										btnIndex = buttonTypes[v.getId()];
									} catch (Exception e) {
										// TODO: handle exception
									}
									if (buttonListeners[v.getId()] != null)
										buttonListeners[v.getId()].onClick(
												null, btnIndex);

								}
							});

						}
					});
					button.setBackgroundResource(R.drawable.custom_button1);
					buttonContainer.addView(button);
				}
			} else {
				Button button = new Button(this);
				button.setLayoutParams(param);
				button.setText("Ok");
				button.setTextColor(0xffffffff);
				// button.setHeight(Utilities.convertDpToPixel(40, this)) ;
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// buttonListeners[v.getId()].onClick(null, v.getId());
						cancelAlert();
						lDialog.dismiss();
					}
				});
				button.setBackgroundResource(R.drawable.custom_button1);
				buttonContainer.addView(button);

			}

			Utilities.startAnimition(this, con, R.anim.grow_from_midddle);
			lDialog.show();
		} catch (Exception e) {
			System.out
					.println("##################### EXCEPTION CATCHED ############################");
			e.printStackTrace();
		}

	}

	public void cancelAlert() {

	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onBackPressed() {
		if(onBackPressedCheck())return;
		super.onBackPressed();
	}
}
