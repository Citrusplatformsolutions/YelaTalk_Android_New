package com.kainat.app.android.engine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.kainat.app.android.R;
import com.kainat.app.android.bean.User;
import com.kainat.app.android.facebook.AsyncFacebookRunner;
import com.kainat.app.android.facebook.BaseRequestListener;
import com.kainat.app.android.facebook.Facebook;
import com.kainat.app.android.facebook.Login;
import com.kainat.app.android.facebook.SessionEvents;
import com.kainat.app.android.facebook.SessionEvents.AuthListener;
import com.kainat.app.android.facebook.SessionEvents.LogoutListener;
import com.kainat.app.android.inf.AuthInf;
import com.kainat.app.android.util.Constants;
import com.mobile.android.facebook.google.GetPlusPersonDataTask;

public class Auth implements ConnectionCallbacks, OnConnectionFailedListener {

	private static Auth self;
	public static Facebook mFacebook;
	AsyncFacebookRunner mAsyncRunner;
	AuthInf authInf;
	private static final String TAG = Auth.class.getSimpleName();
	private static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
	private static final int REQUEST_CODE_RESOLVE_FAILURE = 9000;
	private static final int REQUEST_CODE_RESOLVE_MISSING_GP = 9001;

	// Key to save if Google+ API is called.
	private static final String KEY_HAS_CALLED_GOOGLE_PLUS_API_ENDPOINT = "hasCalledGooglePlusApiEndpoint";

	private static final String OAUTH2_PREFIX = "oauth2:";

	/** OAuth 2.0 scope for writing a moment to the user's Google+ history. */
	private static final String PLUS_WRITE_MOMENT = "https://www.googleapis.com/auth/plus.moments.write";

	public static final String[] SCOPES = new String[] { Scopes.PLUS_PROFILE,
			PLUS_WRITE_MOMENT };
	public static final String SCOPE_STRING = OAUTH2_PREFIX
			+ TextUtils.join(" ", SCOPES);

	private ProgressDialog mConnectionProgressDialog;
//	public PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	// private TextView mSignInStatus;

	private boolean mHasCalledGooglePlusApiEndpoint;

	Activity activity;
	private GetPlusPersonDataTask mAsyncTask;

	public static final int TWITTER_LOGIN_RESULT_CODE_SUCCESS = 1;
	public static final int TWITTER_LOGIN_RESULT_CODE_FAILURE = 2;

	public static final String TWITTER_CONSUMER_KEY = "twitter_consumer_key";
	public static final String TWITTER_CONSUMER_SECRET = "twitter_consumer_secret";

	private WebView twitterLoginWebView;
	private ProgressDialog mProgressDialog;
	public String twitterConsumerKey;
	public String twitterConsumerSecret;

	// private static Twitter twitter;
	// private static RequestToken requestToken;

	public static Auth getInstance() {
		if (self == null) {
			self = new Auth();
		}
		return self;
	}

	public void setListener(AuthInf authInf) {
		if (self != null)
			self.authInf = authInf;
		else
			getInstance().authInf = authInf;
	}

	public void onCreateSaveInstanceState(Activity context,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mHasCalledGooglePlusApiEndpoint = savedInstanceState
					.getBoolean(KEY_HAS_CALLED_GOOGLE_PLUS_API_ENDPOINT);
		}

	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_HAS_CALLED_GOOGLE_PLUS_API_ENDPOINT,
				mHasCalledGooglePlusApiEndpoint);

	}

	public void onStart(Activity context) {

	}

	public void initGooglePlus(final Activity context, Bundle savedInstanceState) {

//		mConnectionProgressDialog = new ProgressDialog(context);
//		mConnectionProgressDialog.setMessage("Loading...");// context.getString(R.string.signing_in_status));
//		mConnectionProgressDialog.setOwnerActivity(context);
//		mConnectionProgressDialog.setCancelable(false);
//		mConnectionProgressDialog.setCanceledOnTouchOutside(false);
//		if (mPlusClient != null) {
//			if (mPlusClient.isConnected()) {
//				mPlusClient.clearDefaultAccount();
//				try {
//					mPlusClient.disconnect();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
//		}
//		// mPlusClient.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
//		// @Override
//		// public void onAccessRevoked(ConnectionResult status) {
//		// // mPlusClient is now disconnected and access has been revoked.
//		// // Trigger app logic to comply with the developer policies
//		// }
//		// });
//		if (mPlusClient == null) {
//
//			context.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//
//					// mPlusClient = new PlusClient.Builder(this, this, this)
//					// .setVisibleActivities("http://schemas.google.com/AddActivity",
//					// "http://schemas.google.com/BuyActivity")
//					// .build();
//
////					mPlusClient = new	 PlusClient(context, Auth.this, Auth.this,
////							SCOPES);
////					mPlusClient.connect();
//
//				}
//			});
//		}
//		activity = context;
//		if (mPlusClient != null && !mPlusClient.isConnected()) {
//			if (mConnectionResult == null) {
//				// mConnectionProgressDialog.show();
//				mPlusClient.connect();
//			} else {
//				try {
//					mConnectionResult.startResolutionForResult(context,
//							REQUEST_CODE_RESOLVE_FAILURE);
//				} catch (SendIntentException e) {
//					mConnectionResult = null;
//					mPlusClient.connect();
//				}
//			}
//		} else {
//		}

		// if (!mPlusClient.isConnected()) {
		// // Show the dialog as we are now signing in.
		// mConnectionProgressDialog.show();
		// // Make sure that we will start the resolution (e.g. fire the
		// // intent and pop up a dialog for the user) for any errors
		// // that come in.
		// // mResolveOnFail = true;
		// // We should always have a connection result ready to resolve,
		// // so we can start that process.
		// if (mConnectionResult != null) {
		// startResolution();
		// } else {
		// // If we don't have one though, we can start connect in
		// // order to retrieve one.
		// mPlusClient.connect();
		// }
		// }
	}

	public void initFB(Activity context) {
		try {
			com.kainat.app.android.facebook.Login login = new Login(
					context);
			if (Auth.getInstance().mFacebook == null)
				Auth.getInstance().mFacebook = new Facebook(
						Constants.FACEBOOK_APP_ID);

			mAsyncRunner = new AsyncFacebookRunner(Auth.getInstance().mFacebook);
			SessionEvents.cleanListener();
			SessionEvents.addAuthListener(new SampleAuthConfermListener());
			SessionEvents.addLogoutListener(new SampleLogoutListener());
			login.init(context, Auth.getInstance().mFacebook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fbLogout() {
		try {
			SessionEvents.onLogoutBegin();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public class SampleAuthConfermListener implements AuthListener {

		public void onAuthSucceed() {
			try {
				if (authInf != null)
					authInf.onAuthSucceed();
				requestUserData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAuthFail(String error) {
			System.out.println("----------Auth on auth fail-----" + error);
			if (authInf != null)
				authInf.onAuthFail(error);
		}

	}

	public class SampleLogoutListener implements LogoutListener {
		public void onLogoutBegin() {
			if (authInf != null)
				authInf.onLogoutBegin();
		}

		public void onLogoutFinish() {
			if (authInf != null)
				authInf.onLogoutFinish();
		}
	}

	public void requestUserData() {
		try {
			if (mFacebook != null && mFacebook.isSessionValid()) {

				Bundle params = new Bundle();
				params.putString(
						"fields",
						"name,first_name,last_name,link,location,gender,verified,languages, picture,email,birthday");
				mAsyncRunner = new AsyncFacebookRunner(mFacebook);
				// me/friends?fields=id,name,installed
				mAsyncRunner.request("me", params, new UserRequestListener());
				//
			}
		} catch (Exception e) {
		}
	}

	public void getFriendListWhoUsingThisApp() {

		try {
			if (mFacebook != null && mFacebook.isSessionValid()) {

				Bundle params = new Bundle();
				// params.putString("fields", "");
				mAsyncRunner = new AsyncFacebookRunner(mFacebook);
				// me/friends?fields=id,name,installed
				params.putString("method", "fql.query");
				params.putString(
						"query",
						"SELECT name,uid,pic_square FROM user WHERE is_app_user AND uid IN (SELECT uid2 FROM friend WHERE uid1 = me())");
				// mAsyncRunner.request("me/friends", params, new
				// UserRequestListener());
				mAsyncRunner.request("me/friends", params,
						new graphApiRequestListener());
			}
		} catch (Exception e) {
		}

	}

	public class graphApiRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			System.out.println("-----getFriendListWhoUsingThisApp-------------"
					+ response);
		}

	}

	public class UserRequestListener extends BaseRequestListener {
		String name;
		String url;

		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				// System.out.println("Facebook user info : " + response);
				// System.out.println("Facebook user Token : "+mFacebook.getAccessToken());
				// System.out.println("Facebook user App ID : "+mFacebook.getAppId());
				jsonObject = new JSONObject(response);
				User user = User.getInstance();
				User.getInstance().clean();

				if (jsonObject.has("picture")) {

					final String picURL = jsonObject.getString("picture");
					if (!picURL.startsWith("http")) {
						JSONObject jsonObjectPic = new JSONObject(picURL);
						user.profilePic = jsonObjectPic.getString("data");
						jsonObjectPic = new JSONObject(user.profilePic);
						user.profilePic = jsonObjectPic.getString("url");
					}
				}
				user.LOGIN_VIA = user.LOGIN_FACEBOOK;
				if (jsonObject.has("id")) {
					user.userId = jsonObject.getString("id");
				}
				if (jsonObject.has("name")) {
					user.name = jsonObject.getString("name");
				}
				if (jsonObject.has("first_name")) {
					user.firstName = jsonObject.getString("first_name");
				}
				if (jsonObject.has("last_name")) {
					user.lastName = jsonObject.getString("last_name");
				}
				if (jsonObject.has("link")) {
					user.link = jsonObject.getString("link");
				}
				if (jsonObject.has("username")) {
					user.userNameL = jsonObject.getString("username");
				}
				if (jsonObject.has("gender")) {
					user.gender = jsonObject.getString("gender");
				}
				if (jsonObject.has("timezone")) {
					user.timezone = jsonObject.getString("timezone");
				}
				if (jsonObject.has("locale")) {
					user.locale = jsonObject.getString("locale");
				}
				if (jsonObject.has("verified")) {
					user.verified = jsonObject.getString("verified");
				}
				if (jsonObject.has("updated_time")) {
					System.out.println("updated_time : "
							+ jsonObject.getString("updated_time"));
				}
				if (jsonObject.has("email")) {
					user.email = jsonObject.getString("email");
				}
				if (jsonObject.has("birthday")) {
					user.birthday = jsonObject.getString("birthday");
				}

				if (jsonObject.has("hometown")) {
					JSONObject hometown = jsonObject.getJSONObject("hometown");
					if (jsonObject.has("id")) {
						user.homeTownId = hometown.getString("id");
					}
					if (jsonObject.has("name")) {
						user.homeTownName = hometown.getString("name");
					}
				}
				if (jsonObject.has("location")) {
					if (jsonObject.has("location")) {
						JSONObject location = jsonObject
								.getJSONObject("location");
						user.locationName = jsonObject.getString("location");

						if (location.has("id")) {
							user.locationId = location.getString("id");
						}
						if (location.has("name")) {
							user.locationName = location.getString("name");
						}
					}
				}

				if (user != null)
					System.out.println(user.toString());

			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

	}

	// ////////////////////////////
	public void onActivityResult(int requestCode, int responseCode,
			Intent intent, Activity activity) {
		this.activity = activity;
		// 09-07 12:34:52.045: I/System.out(14474):
		// --------onActivityResult----requestCode : 100 responseCode :0
		if (mConnectionProgressDialog != null
				&& mConnectionProgressDialog.isShowing())
			mConnectionProgressDialog.dismiss();
		System.out.println("-------auth-onActivityResult----requestCode : "
				+ requestCode + " responseCode :" + responseCode);
		switch (requestCode) {
		case 32665:
			if (Auth.getInstance().mFacebook != null)
				Auth.getInstance().mFacebook.authorizeCallback(requestCode,
						requestCode, intent);
			break;
		case REQUEST_CODE_RESOLVE_FAILURE:
//			if (responseCode == activity.RESULT_OK) {
//
//				if (mConnectionProgressDialog != null
//						&& mConnectionProgressDialog.isShowing())
//					mConnectionProgressDialog.dismiss();
//
//				mConnectionProgressDialog = new ProgressDialog(activity);
//				mConnectionProgressDialog.setMessage(activity
//						.getString(R.string.signing_in_status));
//				mConnectionProgressDialog.setOwnerActivity(activity);
//				mConnectionProgressDialog.setCancelable(false);
//				mConnectionProgressDialog.setCanceledOnTouchOutside(false);
//				// mConnectionProgressDialog.show();
//
//				mConnectionResult = null;
////				mPlusClient = new PlusClient(activity, this, this, SCOPES);
//				mPlusClient.connect();
//			} else {
//				Log.e(TAG, "Unable to recover from a connection failure.");
//			}
			break;
		case REQUEST_CODE_RESOLVE_MISSING_GP:
//			if (responseCode == activity.RESULT_OK) {
//
//				if (mConnectionProgressDialog != null
//						&& mConnectionProgressDialog.isShowing())
//					mConnectionProgressDialog.dismiss();
//
//				mConnectionProgressDialog = new ProgressDialog(activity);
//				mConnectionProgressDialog.setMessage(activity
//						.getString(R.string.signing_in_status));
//				mConnectionProgressDialog.setOwnerActivity(activity);
//				mConnectionProgressDialog.setCancelable(false);
//				mConnectionProgressDialog.setCanceledOnTouchOutside(false);
//				// mConnectionProgressDialog.show();
//
////				mPlusClient = new PlusClient(activity, this, this, SCOPES);
//				mPlusClient.connect();
//			} else {
//				Log.e(TAG, "Unable to install Google Play services.");
//			}
			break;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult status) {
//		System.out.println("Auth-------onConnectionFailed : "
//				+ status.getErrorCode());
//		resetAccountState();
//		// if (mConnectionProgressDialog.isShowing())
//		{
//			// The user clicked the button already and we are showing a spinner
//			// progress dialog. We dismiss the progress dialog and start to
//			// resolve connection errors.
//
//			if (mConnectionProgressDialog.isShowing())
//				mConnectionProgressDialog.dismiss();
//
//			if (status.hasResolution()) {
//				try {
//					status.startResolutionForResult(activity,
//							REQUEST_CODE_RESOLVE_FAILURE);
//				} catch (SendIntentException e) {
//					mPlusClient.connect();
//				}
//			}
//		}
//
//		// Save the intent so that we can start an activity when the user clicks
//		// the button.
//		mConnectionResult = status;

		// FragmentManager fragmentManager = getSupportFragmentManager();
		// if (!status.hasResolution()
		// &&
		// GooglePlayServicesUtil.isUserRecoverableError(status.getErrorCode())
		// && fragmentManager.findFragmentByTag(TAG_ERROR_DIALOG_FRAGMENT) ==
		// null) {
		// DialogFragment fragment = new
		// GooglePlayServicesErrorDialogFragment();
		// Bundle args = new Bundle();
		// args.putInt(GooglePlayServicesErrorDialogFragment.ARG_ERROR_CODE,
		// status.getErrorCode());
		// args.putInt(GooglePlayServicesErrorDialogFragment.ARG_REQUEST_CODE,
		// REQUEST_CODE_RESOLVE_MISSING_GP);
		// fragment.setArguments(args);
		// fragment.show(fragmentManager, TAG_ERROR_DIALOG_FRAGMENT);
		// }
	}

	public static String emil = "";

//	@Override
	public void onConnected() {
//		// mSignInStatus.setText(activity.getString(R.string.signed_in_status));
//		// System.out.println("Auth-------onConnected : "
//		// + mHasCalledGooglePlusApiEndpoint);
//		// mConnectionProgressDialog.dismiss();
//		// mPlusClient.
//		// Use the connection to make a call.
//		if (mAsyncTask == null)// && !mHasCalledGooglePlusApiEndpoint)
//		{
//			String accountName = mPlusClient.getAccountName();
//			User.getInstance().clean();
//			User.getInstance().email = accountName;// + "@gmail.com";
//			emil = accountName;
//			System.out.println("-----onConnected accountName: " + accountName);
//			// accountName = "nagendra42@gmail.com" ;
//			mAsyncTask = new GetPlusPersonDataTask(activity.getBaseContext(),
//					accountName) {
//				@Override
//				protected void onUserRecoverAuthException(
//						final UserRecoverableAuthException e) {
//					// This case will be very rare since we've already
//					// authenticated by
//					// connecting the PlusClient to Google Play services.
//					activity.startActivityForResult(e.getIntent(),
//							REQUEST_CODE_RESOLVE_FAILURE);
//					if (mConnectionProgressDialog.isShowing())
//						mConnectionProgressDialog.dismiss();
//					System.out
//							.println("-----------onUserRecoverAuthException : "
//									+ e.getLocalizedMessage());
//				}
//
//				@Override
//				protected void onPostExecute(JSONObject person) {
//					String msg = null;
//					System.out.println("-----------onPostExecute : ");
//					try {
//						if (person.has("message")) {
//							msg = person.getString("message");
//						}
//						if (person != null && !person.has("status")) {
//							try {
//								final String name = person.optString(
//										"displayName", "unknown");
//								User user = User.getInstance();
//								user.LOGIN_VIA = user.LOGIN_GOOGLE_PLUS;
//
//								if (person.has("id")) {
//									user.userId = person.getString("id");
//								}
//								if (person.has("displayName")) {
//									user.displayName = person
//											.getString("displayName");
//								}
//								if (person.has("kind")) {
//									user.kind = person.getString("kind");
//								}
//								if (person.has("objectType")) {
//									user.objectType = person
//											.getString("objectType");
//								}
//								if (person.has("url")) {
//									user.url = person.getString("url");
//								}
//								if (person.has("isPlusUser")) {
//									user.isPlusUser = person
//											.getString("isPlusUser");
//								}
//								if (person.has("gender")) {
//									user.gender = person.getString("gender");
//								}
//
//								if (person.has("id")) {
//									user.userId = person.getString("id");
//								}
//								if (person.has("name")) {
//									JSONObject nameObject = person
//											.getJSONObject("name");
//									if (nameObject.has("givenName")) {
//										user.firstName = nameObject
//												.getString("givenName");
//									}
//									if (nameObject.has("familyName")) {
//										user.lastName = nameObject
//												.getString("familyName");
//									}
//								}
//								if (person.has("image")) {
//									JSONObject nameObject = person
//											.getJSONObject("image");
//									if (nameObject.has("url")) {
//										user.profilePic = nameObject
//												.getString("url");
//									}
//								}
//
//								if (person.has("verified")) {
//									user.verified = person
//											.getString("verified");
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							// System.out.println(User.getInstance().toString());
//							// mSignInStatus.setText(greeting);
//						}
//
//						mHasCalledGooglePlusApiEndpoint = person != null;
//						mAsyncTask = null;
//						if (mConnectionProgressDialog.isShowing())
//							mConnectionProgressDialog.dismiss();
//						if (person != null && !person.has("status")) {
//							if (authInf != null)
//								authInf.onAuthSucceed();
//						} else {
//							if (authInf != null)
//								authInf.onAuthFail(msg);
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			};
//
//			mAsyncTask.execute();
//		}
	}

	/**
	 * Resets any pending calls to Google+ API and the user's sign-in status.
	 */
	private void resetAccountState() {
		// Reset cached state of any previous account that was signed in.
		mHasCalledGooglePlusApiEndpoint = false;
		if (mAsyncTask != null) {
			mAsyncTask.cancel(false);
			mAsyncTask = null;
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
