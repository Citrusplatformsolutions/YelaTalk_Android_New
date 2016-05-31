package com.kainat.app.android.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;

import com.kainat.app.android.engine.HttpConnectionEngine;

import android.util.Log;

/**
 * This class handles the HTTP connection handling. Just make an instance of this class, set the data like header and/or post data and make connection and read response
 * 
 * @author Vishal Kumar
 */
public class HttpConnectionHelper {
	
	String TAG ="HttpConnectionHelper" ;
	public boolean isCancel = false;
	HttpConnectionEngine httpEngine;
	/**
	 * variable to store http input stream
	 */
	private InputStream is;
	/**
	 * Variable to store url Connection
	 */
	private HttpURLConnection httpURLConnection;
	/**
	 * Variable to store http connection output stream object
	 */
	private OutputStream os;
	/**
	 * Hashtable to store headers
	 */
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	/**
	 * Variable to store post data
	 */
	private byte[] postData;
	/**
	 * URL used to make http connection
	 */
	private String serverPath;
	/**
	 * method to store the http request type get/post Default value set as GET
	 */
	private String requestMethod = HttpGet.METHOD_NAME;
	/**
	 * This variable stores the connect timeout over the url connection
	 */
	private int connectTimeout = 60;
	/**
	 * This variable store the connection read timout
	 */
	private int readTimeout = 60;

	/**
	 * constructor URL used to make http connection
	 * 
	 * @param path
	 *            URL path to hit
	 */
	public void setParent(HttpConnectionEngine http_engine) {
		httpEngine = http_engine;
	}
	/**
	 * constructor URL used to make http connection
	 * 
	 * @param path
	 *            URL path to hit
	 */
	public HttpConnectionHelper(String path) {
		serverPath = path;
	}

	
	/**
	 * set the http post data
	 * 
	 * @param postData
	 *            data to be sent in post request as string
	 */
	public void setPostData(String postData) {
		try {
			this.postData = postData.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			this.postData = postData.getBytes();
		}
	}

	/**
	 * set the http post data
	 * 
	 * @param postData
	 *            data to be sent in post request as string
	 */
	public void setPostData(byte[] postData) {
		this.postData = postData;
	}

	/**
	 * This method set the http headers
	 * 
	 * @param name
	 *            name of the header
	 * @param value
	 *            value of the header
	 */
	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	/**
	 * method to make http request and get the input stream
	 * 
	 * @return http connection's input stream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		try {
			if (httpURLConnection == null) {
				makeHttpRequest();
			}
			is = httpURLConnection.getInputStream();
		} catch (IOException e) {
			throw e;
		}
		return is;
	}

	/**
	 * method to read http response's header's
	 * 
	 * @param httpHeader
	 *            name of the http header
	 * @return http header's value
	 * @throws IOException
	 */
	public String getHttpHeader(String httpHeader) throws IOException {
		if (httpURLConnection == null) {
			makeHttpRequest();
		}
		return httpURLConnection.getHeaderField(httpHeader);
	}

	/**
	 * This method can be used to get all the header fields found after hitting URL
	 * 
	 * @return Hastable with entry as key and value of headers
	 * @throws IOException
	 *             When request is not made and while sending request
	 */

	public Hashtable<String, String> getAllHttpHeaders() throws IOException {
		if (httpURLConnection == null) {
			makeHttpRequest();
		}
		Hashtable<String, String> retVal = null;
		Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
		if (!headers.isEmpty()) {
			retVal = new Hashtable<String, String>();
			Set<String> keys = headers.keySet();
			Collection<List<String>> val = headers.values();
			Iterator<String> keyIter = keys.iterator();
			Iterator<List<String>> valIter = val.iterator();
			String key;
			String value;
			List<String> valueList;
			Iterator<String> valueIterator;
			while (keyIter.hasNext()) {
				key = keyIter.next();
				valueList = valIter.next();
				valueIterator = valueList.iterator();
				if (valueIterator.hasNext())
					value = valueIterator.next();
				else
					value = null;
				retVal.put(key, value);
			}
		}
		return retVal;
	}

	/**
	 * Method to read http response code
	 * 
	 * @return http response code
	 * @throws IOException
	 */
	public int getResponseCode() throws IOException {
		try{
		if (httpURLConnection == null) {
			makeHttpRequest();
		}
		if(isCancel)
			return 404;
		}catch (Exception e) {
			// TODO: handle exception
		}
		if (httpURLConnection == null)
		 return -1;
		
		return httpURLConnection.getResponseCode();
	}

	/**
	 * method to read http response message
	 * 
	 * @return http response message
	 * @throws IOException
	 */
	public String getResponseMessage() throws IOException {

		if (httpURLConnection == null) {
			makeHttpRequest();
		}
		if(isCancel)
			return "Request cancel";
		
		return httpURLConnection.getResponseMessage();
	}

	/**
	 * This method is basically used for writing headers in the Http connection
	 * 
	 * @param urlConn
	 *            Connection of the url
	 * @param headers
	 *            Header to be set for that url connection
	 */
	private void writeHeaders(URLConnection urlConn, Hashtable<String, String> headers) {
		if (headers != null && headers.size() > 0) {
			Enumeration<String> keyEnum = headers.keys();
			Enumeration<String> valEnum = headers.elements();
			while (keyEnum.hasMoreElements()) {
				urlConn.addRequestProperty(keyEnum.nextElement(), valEnum.nextElement());
			}
		}
	}

	/**
	 * Method to make http request
	 * 
	 * @throws IOException
	 */
	private void makeHttpRequest() throws IOException {
		URL url = new URL(this.serverPath);
//		Log.d(TAG, " ++ makeHttpRequest : "+serverPath);
		httpURLConnection = (HttpURLConnection) url.openConnection();
		// IF POST DATA IS NOT NULL THEN OBVIOUSLY REQUEST WILL BE OF POST TYPE
		if (null != this.postData)
			setRequestMethod(HttpPost.METHOD_NAME);
		
		// SETTING THE REQUEST METHOD HERE - GET/POST/HEAD
		httpURLConnection.setRequestMethod(requestMethod);

		// SET THE HEADERS IF AVAILABLE
		if (headers != null && headers.size() > 0) {
			writeHeaders(httpURLConnection, headers);
		}
		// TRYING TO CONNECT AFTER SETTING REQUEST METHOD AND HEADERS
		//httpURLConnection.connect();
		int MAX_DATA_CHUNK = 1024 * 32;
		httpURLConnection.setConnectTimeout(connectTimeout * 1000);
		httpURLConnection.setReadTimeout(readTimeout * 1000);
		httpURLConnection.setChunkedStreamingMode(MAX_DATA_CHUNK);

		// OPEN OUTPUT STREAM AND WRITE BUFFER DATA IF THE REQUEST TYPE IS POST
		if (postData != null) {
			if (!httpURLConnection.getDoOutput()) {
				httpURLConnection.setDoOutput(true);
			}
			
			os = httpURLConnection.getOutputStream();
//			os.write(postData);
			int total_data = postData.length;
			int dataLength = postData.length;
           int  iTotalBytesDone = 0;
           float percentage = 0;
            while (dataLength > 0)
            {
                //<editor-fold desc="--Writing in chunks to O/P stream--">
                if (isCancel)
                {
                    break;
                }
                if (dataLength >= MAX_DATA_CHUNK)
                {
                    if (null != os){
                    	os.write(postData, iTotalBytesDone, MAX_DATA_CHUNK);
                    }
                    iTotalBytesDone += MAX_DATA_CHUNK;
                    dataLength -= MAX_DATA_CHUNK;
                }
                else
                {
                    if (null != os)
                    	os.write(postData, iTotalBytesDone, dataLength);
                    iTotalBytesDone += dataLength;
                    dataLength = 0;
                }
                percentage = (float)iTotalBytesDone/total_data;
                if(httpEngine != null){
                	httpEngine.onChunkDataWriteCallback((int)(100 * percentage));
//                	Log.v(TAG, "onChunkDataWriteCallback : Total Data : "+(100 * percentage));
                }
            }
		}
	}

	/**
	 * This method can be used to set connect time out. The default connect time out is 60
	 * 
	 * @param connectTimeout
	 *            Connect timeout in seconds
	 */

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * This method can be used to set read timeout. The default read timeout is 60.
	 * 
	 * @param readTimeout
	 *            Read time out in seconds
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * method to close all connection, streams and reset state
	 */
	public void close() {
//		System.out.println("PSS close------------------------- connection disconnect");
		try {
			if (is != null) {
				is.close();
//				System.out.println("PSS close1------------------------- connection disconnect");
			}
		} catch (Exception e) {
//			System.out.println("PSS close2------------------------- connection disconnect");
		} finally {
//			System.out.println("PSS close3------------------------- connection disconnect");
			is = null;
		}
		try {
			if (os != null) {
				os.close();
			}
		} catch (Exception e) {
		} finally {
			os = null;
		}
		try {
//			System.out.println("PSS close4------------------------- connection disconnect httpURLConnection "+httpURLConnection);
			if (null != httpURLConnection) {
				httpURLConnection.disconnect();
//				System.out.println("PSS close------------------------- connection disconnect");
			}
		} catch (Exception e) {
//			System.out.println("PSS close------------------------- connection disconnect "+e.toString());
			e.printStackTrace() ;
		} finally {
			httpURLConnection = null;
		}
	}
	/**
	 * method to close all connection, streams and reset state
	 */
	public void closeConnection() {
		try {
//			System.out.println("PSS close4------------------------- connection disconnect httpURLConnection "+httpURLConnection);
			if (null != httpURLConnection) {
				httpURLConnection.disconnect();
//				System.out.println("PSS close------------------------- connection disconnect");
			}
		} catch (Exception e) {
//			System.out.println("PSS close------------------------- connection disconnect "+e.toString());
			e.printStackTrace() ;
		} finally {
			httpURLConnection = null;
		}
	}
	/**
	 * @return Returns the requestMethod.
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * The requestMethod to set.
	 * 
	 * @param requestMethod
	 * @throws If
	 *             request method is not standard
	 */
	public void setRequestMethod(String requestMethod) throws IllegalArgumentException {
		if (HttpPost.METHOD_NAME.equals(requestMethod)) {
			this.requestMethod = HttpPost.METHOD_NAME;
		} else if (HttpGet.METHOD_NAME.equals(requestMethod)) {
			this.requestMethod = HttpGet.METHOD_NAME;
		} else if (HttpHead.METHOD_NAME.equals(requestMethod)) {
			this.requestMethod = HttpHead.METHOD_NAME;
		} else
			throw new IllegalArgumentException("Not a standard request method");
	}

	/**
	 * @return Returns the serverPath.
	 */
	public String getServerPath() {
		return serverPath;
	}

	/**
	 * To set the new server path
	 * 
	 * @param serverPath
	 */
	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}
}
