package com.kainat.app.android.engine;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

import com.kainat.app.android.core.NetworkEngineNotifier;
import com.kainat.app.android.core.NetworkTransport;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;

public class HttpConnectionEngine implements Runnable, OnSchedularListener {

	private enum EngineState {
		StateInit(0), StateReady(5), StateHittingServer(10), StateReading(15), StateWriting(20);

		EngineState(int state) {
		}
	};

	private static final String TAG = HttpConnectionEngine.class.getSimpleName();
	private static HttpConnectionEngine sSelf[];

	public static final byte RESPONSE_TYPE_SUCCESS = 0;
	public static final byte RESPONSE_TYPE_ERROR = -1;
	private static final int CHUNK_LENGTH = 32 * 1024;
	private int mFrameType, mRequestOp,mMessageId;
	public static  String mServerAddress, mFallbackServerAddress;
	private NetworkEngineNotifier mParent;
	private Thread mThread;
	private byte[] mRequestData;
	private byte[] mResponseData;
	private boolean mIsRunning;
	private EngineState mEngineState = EngineState.StateInit;
	private byte mRequestObjNo = 0;
	private String name = "" ;
	private HttpConnectionEngine(String name) {
		mIsRunning = true;
		mThread = new Thread(this);
		mThread.start();
		this.name = name ;

	}

//	public static HttpConnectionEngine getInstance() {
//		if (null != sSelf) {
//			return sSelf;
//		}
//		synchronized (HttpConnectionEngine.class) {
//			if (null == sSelf) {
//				sSelf = new HttpConnectionEngine();
//			}
//		}
//		return sSelf;
//	}
	public static HttpConnectionEngine[] getInstance()
    {
        if (null == sSelf)
        {
        	sSelf = new HttpConnectionEngine[NetworkTransport.HTTP_ENGINES];
            for(byte i = 0; i<sSelf.length; i++) {
            	sSelf[i] = new HttpConnectionEngine("connection "+i);
//            	sSelf[i].iRequestData = null;
//            	sSelf[i].iMainThread = new Thread(iSelf[i]);
//            	sSelf[i].iMainThread.start();
            }
        }
        return sSelf;
    }
	/**
	 * @param caller
	 */
	public void cancelOperation(String caller) {
		if (Logger.ENABLE) {
			Logger.warning(TAG, "PSS--cancelOperation-- CALLER = " + caller + "  State = " + mEngineState);
		}
//		System.out.println("PSS---cancelOperation-----------mEngineState : "+mEngineState);
		
		if (mEngineState != EngineState.StateReady) {
			if (Logger.ENABLE) {
				Logger.warning(TAG, "PSS--cancelOperation-- Interrupting");
			}
			if(helper != null)
			{
//				System.out.println("PSS-----helper---------mEngineState : "+mEngineState);
				helper.isCancel = true ;
			}

		}
	}

	/**
     * 
     */
	public void delete() {
		cancelOperation(HttpConnectionEngine.class.getSimpleName());
		sSelf = null;
	}

	public int netConfigure(NetworkEngineNotifier networkTransport, String server, String fallbackServer, byte mRequestNo) {
		this.mParent = networkTransport;
		this.mServerAddress = server;
		this.mFallbackServerAddress = fallbackServer;
		return RESPONSE_TYPE_SUCCESS;
	}

	/**
	 * @return
	 */
	public int startSession(byte mRequestNo) {
		return 0;
	}

	public void setRequestType(byte frameType, byte requestOp,int mMessageId) {
		this.mFrameType = frameType;
		this.mRequestOp = requestOp;
		this.mMessageId = mMessageId ;
	}

	public void close() {
		mIsRunning = false;
		notifyThread();
	}

	/**
	 * @param otsFrame
	 * @return
	 */
	public int executeResourceData(final byte[] frame, final byte mRequestNo) {
//		if(Logger.MULTICHANEL)
//			System.out.println("PSS_8-------------EngineState : "+mEngineState);
		if (mEngineState != EngineState.StateReady) {
			new Thread(new Runnable() {

				public void run() {
					while (mEngineState != EngineState.StateReady) {
						try {
//							if(Logger.MULTICHANEL)
//							System.out.println("PSS_9-------------EngineState : "+mEngineState+" tname: "+name);
							
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
					mRequestObjNo = mRequestNo;
					mRequestData = frame;
					notifyThread();
				}
			}).start();
		} else {
//			if(Logger.MULTICHANEL)
//				System.out.println("PSS_99-------------EngineState : "+mEngineState);
			mRequestObjNo = mRequestNo;
			this.mRequestData = frame;
			notifyThread();
		}
		return RESPONSE_TYPE_SUCCESS;
	}

	private void notifyThread() {
		synchronized (this) {
			notify();
		}
	}
	HttpConnectionHelper helper =null ;
	public void run() {
		while (mIsRunning) {
			try {
				synchronized (this) {
					mEngineState = EngineState.StateReady;
					wait();
				}
				boolean isRetry = false;
				String serverAddress = this.mServerAddress;
//				if(SettingData.sSelf !=null && SettingData.sSelf.isIm())
//								System.out.println("Going to hit Address - " + serverAddress);
//				System.out.println("FRAME TYPE AT HTTP REQUEST===========================" + mFrameType);
//				System.out.println("OPERATION AT HTTP REQUEST===========================" + mRequestOp);
				boolean fail = false;
				while (true) {
					try {
						if (Logger.MULTICHANEL){
							Logger.debug(TAG, "PSS_10 send message id   :   "+mMessageId);							
						}
						
						long sTime = System.currentTimeMillis() ;
						helper = new HttpConnectionHelper(serverAddress);
						helper.setParent(this);
						mEngineState = EngineState.StateHittingServer;
						helper.setRequestMethod(HttpPost.METHOD_NAME);
						helper.setHeader("Content-Type", "application/octet-stream");
						helper.setHeader("Content-Encoding", "gzip");
						helper.setHeader("Request-Type", mFrameType + "_" + mRequestOp);
						helper.setHeader("Content-Length", Integer.toString(this.mRequestData.length));
						
//						System.out.println("Request-Type"+ mFrameType + "_" + mRequestOp);
						
//						System.out.println(ClientProperty.RT_PARAMS);
						if (mFrameType == 1 && (mRequestOp == 1 || mRequestOp == 7)){
							helper.setHeader("RT-Params", ClientProperty.RT_PARAMS);
//							if(Logger.ENABLE)
								Logger.debug(TAG, "-----------ClientProperty.RT_PARAMS------"+ClientProperty.RT_PARAMS);
						}
						
//						Logger.debugOP(System.currentTimeMillis()+mFrameType + "_" + mRequestOp + "_",this.mRequestData);
						
//						 if (Flags.FILE_LOG_ENABLE_SACH)
//			                    OtsLogger.LogOPFileTime(iFrameType+"_"+iRequestOp,iRequestData, OtsLogger.SEVERE);
//						System.out.println("---------------------request file size------------"+this.mRequestData.length);
//						String str = new String(mRequestData);
//						                        System.out.println("Request byte Data - "+str);
						mEngineState = EngineState.StateWriting;
						helper.setPostData(this.mRequestData);
						helper.setConnectTimeout(30);
						helper.setReadTimeout(120);
						int responseCode = helper.getResponseCode();
//						System.out.println("-------------responseCode---"+responseCode);
						if (Logger.MULTICHANEL){
							Logger.debug(TAG, "PSS_11 send message id   :   "+mMessageId);
							Logger.debug(TAG, "PSS_12 responseCode   :   "+responseCode);
						
						}
//						System.out.println("---------------------request responseCode------------"+responseCode);
						
//						System.out.println();
						if (Logger.ENABLE)
							Logger.info(TAG, "--run()--[INFO] - ResponseCode = " + responseCode);
						switch (responseCode) {
						case HttpURLConnection.HTTP_MOVED_PERM:
						case HttpURLConnection.HTTP_MOVED_TEMP:
						case HttpURLConnection.HTTP_SEE_OTHER:
						case HttpURLConnection.HTTP_NOT_MODIFIED:
						case HttpURLConnection.HTTP_USE_PROXY:
						case HttpURLConnection.HTTP_MULT_CHOICE:
							mEngineState = EngineState.StateReading;
							String location = helper.getHttpHeader("Location");
							if (location != null) {
								if (location.charAt(location.length() - 1) == '/') {
									location = location + "rocketalk/im";
								} else {
									location = location + "/rocketalk/im";
								}
								if (Logger.ENABLE)
									Logger.info(TAG, "--run()--[WARNING] - iWebAddressToHit CHANGED = " + location);
							} else {
								if (Logger.ENABLE)
									Logger.warning(TAG, "--run--[WARNING] - 'location' header value");
							}
							continue;
						case HttpURLConnection.HTTP_OK:
						case HttpURLConnection.HTTP_ACCEPTED:
							//System.out.println("Response Time: ==================" + new Date());
							
							String contentEncoding = helper.getHttpHeader("Content-Encoding");
							mEngineState = EngineState.StateReading;
							InputStream inputStream = null;
							if (null == contentEncoding) {
								inputStream = helper.getInputStream();
							} else if (contentEncoding.equals("gzip")) {
								inputStream = new GZIPInputStream(helper.getInputStream());
							}
							ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
							byte[] chunk = new byte[CHUNK_LENGTH];
							int len;
							//long start = System.currentTimeMillis();
							while (-1 != (len = inputStream.read(chunk)) && !helper.isCancel) {
								buffer.append(chunk, 0, len);
//								System.out.println("------buffer----------"+buffer.length());
							}
							if(!helper.isCancel){
								//long end = System.currentTimeMillis();
								//System.out.println("Read Time=================" + (end - start));
								this.mResponseData = buffer.toByteArray();
								//System.out.println("Response Data:::::::::: " + new String(this.mResponseData));
								Timer tmr = new Timer();
								tmr.schedule(new OtsSchedularTask(this, null, mRequestObjNo), 5);
								if (Logger.MULTICHANEL){
									Logger.debug(TAG, "send message id   :   " + mMessageId);
									Logger.debug(TAG, "time taken   :   :   " + +((System.currentTimeMillis()-sTime)/1000) + " second");
								}
								
							}
							if(helper.isCancel){
								this.mResponseData = null ;
								Timer tmr = new Timer();
								tmr.schedule(new OtsSchedularTask(this, null, mRequestObjNo), 5);
							}
							break;
						default:
							if (Logger.ENABLE) {
								Logger.warning(TAG, "run - Some error Response code - " + responseCode);
							}
							Timer tmr = new Timer();
							tmr.schedule(new OtsSchedularTask(this, null, mRequestObjNo), 5);
							break;
						}
					} catch (SocketTimeoutException stx) {
						stx.printStackTrace();
						if (Logger.MULTICHANEL)
							Logger.debug(TAG, "--name " + name);
						fail = false;
						fail = true;
					} catch (UnknownHostException uhex) {
						if (Logger.MULTICHANEL)
							Logger.debug(TAG, "--name " + name);
						uhex.printStackTrace();
						fail = true;
					} catch (ConnectTimeoutException conex) {
						if (Logger.MULTICHANEL)
							Logger.debug(TAG, "--name " + name);
						conex.printStackTrace();
						fail = true;
					}
					catch (OutOfMemoryError conex) {
						if (Logger.MULTICHANEL)
							Logger.debug(TAG, "--name " + name);
						conex.printStackTrace();
//						System.out.println("-------------out of memotr http connection engine-----");
						fail = true;
						helper.isCancel = true ;
						Constants.ERROR = "This content is too large to view!" ;
						Timer tmr = new Timer();
						tmr.schedule(new OtsSchedularTask(this, null, mRequestObjNo), 5);						
					}
					if(!helper.isCancel){
					if (fail) {
						if (Logger.MULTICHANEL)
							Logger.debug(TAG, "--name fail" + name);
						fail = false;
						if (!isRetry) {
							serverAddress = this.mServerAddress.equals(this.mServerAddress) ? this.mFallbackServerAddress : this.mServerAddress;
							isRetry = true;
							if (Logger.ENABLE) {
								Logger.info(TAG, "run-- switching to server = " + serverAddress);
							}
							continue;
						} else {
							throw new ConnectException("Could not connect to even fallback server");
						}
					}
					}
					break;
				}
			} catch (InterruptedException im) {
				im.printStackTrace();
				if (Logger.MULTICHANEL)
					Logger.debug(TAG, "--name" + name);
				if (Logger.ENABLE)
					Logger.warning(TAG, "run-- interrupted " + im.getMessage());
			} catch (Exception ex) {
				if (Logger.MULTICHANEL)
					Logger.debug(TAG, "--name" + name);
				ex.printStackTrace();
				if (Logger.ENABLE)
					Logger.error(TAG, "run-- ERROR -- " + ex.getMessage(), ex);
				Timer tmr = new Timer();
				tmr.schedule(new OtsSchedularTask(this, null, mRequestObjNo), 5);
			}
		}
	}
	
	public void onChunkDataWriteCallback(int data){
		if(mParent != null)
			mParent.networkDataChange(data);
	}

	public void onTaskCallback(Object parameter, byte objectNo) {
		try {
			
			if (null != mParent) {
				if (null != mResponseData) {
					if (Logger.MULTICHANEL){
						Logger.debug(TAG, "PSS_13 onTaskCallback API_DATA_EXECUTED  objectNo :   "+objectNo);							
					}
					this.mParent.networkAPINotification(NetworkEngineNotifier.API_DATA_EXECUTED, mResponseData, objectNo);
				} else {
			//		System.out.println("name  :   "+name);
			//		System.out.println("----- RESPONSE_TYPE_ERROR:  "+objectNo);
					if (Logger.MULTICHANEL){
						Logger.debug(TAG, "PSS_131 onTaskCallback API_DATA_EXECUTED  objectNo :   "+objectNo);							
					}
					this.mParent.networkAPINotification(RESPONSE_TYPE_ERROR, null, objectNo);
				}
			}
		} catch (Throwable th) {
			if (Logger.ENABLE) {
				Logger.error(TAG, "PSS_132 --onTaskCallback-- ERROR WHILE HANDLING DATA BY PARENT", th);
			}
		} finally {
			mResponseData = null;
			mRequestData = null;
		}
	}
}
