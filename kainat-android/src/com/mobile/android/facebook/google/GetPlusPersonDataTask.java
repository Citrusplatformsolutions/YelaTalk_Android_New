/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobile.android.facebook.google;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.kainat.app.android.util.ResponseError;

/**
 * This AsyncTask is used to fetch the signed in user's Google+ profile data.
 * 09-07 13:38:57.265: W/GetPlusPersonDataTask(21692): Error HTTP/1.1 404 Not Found when requesting person data from https://www.googleapis.com/plus/v1/people/me

 */
public abstract class GetPlusPersonDataTask extends AsyncTask<Void, Void, JSONObject> {
    private static final String TAG = GetPlusPersonDataTask.class.getSimpleName();
    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private static final String PLUS_PEOPLE_ME = "https://www.googleapis.com/plus/v1/people/me";

    private final Context mContext;
    private final String mAccountName;

    public GetPlusPersonDataTask(Context context, String accountName) {
        this.mContext = context;
        this.mAccountName = accountName;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        // Since we've already connected a PlusClient with the
        // Scopes.PLUS_PROFILE we will almost certainly be able to get an
        // auth token. The only case in which this won't hold is if the user
        // has since revoked permission.
        try {
            return fetchPersonData(true);
        } catch (UserRecoverableAuthException e) {
            if (!isCancelled()) {
                onUserRecoverAuthException(e);
            }
        } catch (IOException e) {
            Log.e(TAG, "Transient error when requesting an OAuth access token", e);
            return null;
        } catch (GoogleAuthException e) {
            Log.e(TAG, "Fatal error when requesting an OAuth access token", e);
            return null;
        }
        return null;
    }

    private JSONObject fetchPersonData(boolean retry)
            throws UserRecoverableAuthException, IOException, GoogleAuthException {
        // The scopes here must match those specified in SignInActivity, so we
        // refer to the same constant. The order of the scopes and the spacing
        // between them needs to match as well.
        String accessToken = GoogleAuthUtil.getToken(mContext, mAccountName,
                com.kainat.app.android.engine.Auth.SCOPE_STRING);
        if (accessToken == null) {
            Log.e(TAG, "A valid OAuth access token is required to request person data");
            return null;
        }
//        DefaultHttpClient client = DefaultHttpClient.newInstance("Android");
        DefaultHttpClient client = new DefaultHttpClient();
        /*client.getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("rocketalkconnect@gmail.com", "onetouch"));*/
        HttpGet request = new HttpGet(PLUS_PEOPLE_ME);
        request.setHeader("User-Agent", "Google+ Android Sample");
        request.setHeader("Authorization", "OAuth " + accessToken);
      
//        request.setRequestProperty("Authorization", "Bearer " + accessToken);

        try {
            HttpResponse response = client.execute(request);
            
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HttpStatus.SC_OK) {
                Log.w(TAG, "Error " + status + " when requesting person data from " +
                        request.getURI().toString());
                if (status.getStatusCode() == HttpStatus.SC_UNAUTHORIZED && retry) {
                    GoogleAuthUtil.invalidateToken(mContext, accessToken);
                  //  onUserRecoverAuthException(new UserRecoverableAuthException(status.getReasonPhrase()+" : "+status.getStatusCode(),null));//status.getStatusCode()
                    return fetchPersonData(false);
                }
//                return null;
                return  new JSONObject(ResponseError._404_GP);
            }

            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            // Read the response into a buffered stream
            int readBytes = 0;
            byte[] buffer = new byte[IO_BUFFER_SIZE];
            while ((readBytes = inputStream.read(buffer)) != -1) {
                content.write(buffer, 0, readBytes);
            }

            String jsonData = new String(content.toByteArray(), "UTF-8");
            System.out.println("Google Plus user info : "+jsonData);
            JSONObject personObj = new JSONObject(jsonData);

            return personObj;
        } catch (IOException e) {
            Log.e(TAG, "Errors when communicating with the Google+ API", e);
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse the json response from the Google+ API", e);
        } finally {
            client.clearRequestInterceptors();//lose();
        }

        return null;
    }

    protected abstract void onUserRecoverAuthException(UserRecoverableAuthException e);
}