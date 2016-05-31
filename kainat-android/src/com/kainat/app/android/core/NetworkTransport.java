package com.kainat.app.android.core;

import java.util.Timer;

import android.util.Log;

import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.engine.HttpConnectionEngine;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.FrameEngine;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Urls;

public final class NetworkTransport implements NetworkEngineNotifier, OnSchedularListener {

	private static final String TAG = NetworkTransport.class.getSimpleName();
	final public static byte HTTP_ENGINES = 3;
	final public static byte INIT = 1;
	final public static byte CONFIGURE = 2;
	final public static byte DISCONNECTED = 3;
	final public static byte CONNECTED = 4;
	final public static byte STARTSESSION = 5;
	final public static byte EXECUTESPEECH = 6;
	final public static byte TRANSPORT_READING = 7;
	final public static byte TRANSPORT_WRITING = 8;

	private static NetworkTransport sSelf;
	private HttpConnectionEngine mHttpEngine[];
	private boolean log = false;
	/**
	 * The transaction ID is being used to send the request This is basically having a value of 2 when the login of the user is in the progress.
	 */
	
	public int iSentTransId[];
	public int iSentOpCode[];
	private boolean mBusy[];
	public int iRetryCount = 0;
	private OutboxTblObject mRequest[];
	public int iNetState[] ;//= INIT;
//	public int lastAsyncFirstProcessedId = -1;
//    public int lastAsyncSecondProcessedId = -1;
    
	private Timer iTimeOutTimer;
	private boolean iIsSyncCall;
//	private byte nextHttpEngine = 0;
    private byte currentHttpEngine = 0;
	private static final int TIME_OUT_TASK = 1 << 1;

	private NetworkTransport() {
		try {
			createNetInstance();
			if (mHttpEngine != null)
	        {
	            iSentTransId = new int[mHttpEngine.length];
	            iSentOpCode = new int[mHttpEngine.length];
	            mBusy = new boolean[mHttpEngine.length];
	            iNetState = new int[mHttpEngine.length];
	            for(byte i=0; i<iNetState.length; i++)
	                iNetState[i] = INIT;
	            mRequest = new OutboxTblObject[mHttpEngine.length];
	        }
			configureNetGateway();
		} catch (Exception e) {
			if (log)
				Log.e(TAG, "--NetworkTransport- INSTANCE CREATION FAILED", e);
		}
	}

	public static NetworkTransport getInstance() {
		if (null != sSelf) {
			return sSelf;
		}
		synchronized (NetworkTransport.class) {
			if (null == sSelf) {
				sSelf = new NetworkTransport();
			}
		}
		return sSelf;
	}
	 public HttpConnectionEngine getHttpInstance(byte i)
	    {
	        return mHttpEngine[i];
	    }
	public void close() {
		if (null != mHttpEngine) {
			for (byte i = 0; i < mHttpEngine.length; i++)
            {
                if (mHttpEngine[i] != null){
                   // iNetState[i]=INIT;
                    mBusy[i]=false;
                   // CoreController.iSelf.setSyncCall(false);
                    mHttpEngine[i].cancelOperation("NetworkTransport__close()");
                    releaseNetInstance(i);
                }
			
			}
		}
	}

	private  boolean isBusy(byte mRequestNo) {
		return mBusy[mRequestNo];
	}

	private synchronized void setBusy(boolean busy, byte mRequestNo) {
		if(log)
			if (Logger.MULTICHANEL){
				Log.w(TAG, "--------------------setBusy-------------------busy"+busy);	
				Log.w(TAG, "--------------------setBusy-------------------mRequestNo"+mRequestNo);	
			}
		this.mBusy[mRequestNo] = busy;
	}

	/**
     * 
     */
	public synchronized void cancelOperation() {
		if (mHttpEngine != null)
            for (byte i = 0; i < mHttpEngine.length; i++)
            {
				if ((mBusy[i] && !getSyncCall()) || (mBusy[i] && getSyncCall() && i == 2)) {
					if (Logger.ENABLE)
						Logger.debug(TAG, "--cancelOperation--[INFO] -- CANCEL THE OPERATION ");
					mHttpEngine[i].cancelOperation("NetworkTransport__cancelOperation()");
					 if(iNetState[i] != INIT)
						 iNetState[i] = DISCONNECTED;
					if (null != mRequest)
						mRequest[i].close();
					mRequest[i] = null;
//					lastAsyncFirstProcessedId = -1;
//                    lastAsyncSecondProcessedId = -1;
					setBusy(false,i);
					iSentOpCode[i] = 0;
					iSentTransId[i] = 0;
				}
		}
	}

	public void setSyncCall(boolean aBool) {
		iIsSyncCall = aBool;
	}
	public boolean  getSyncCall() {
		return iIsSyncCall;
	}
	 public synchronized boolean isBusy()
	    {
	        boolean isBusy = true;
	         if (mHttpEngine != null)
	            for (byte i = 0; i < mHttpEngine.length-1; i++){
	                if(!mBusy[i])
	                    return  false;
	            }
	        return isBusy;
	    }
	     public synchronized boolean isASyncBusy()
	    {
	        boolean isBusy = false;
	         if (mHttpEngine != null)
	            for (byte i = 0; i < mHttpEngine.length; i++){
	                if(mBusy[i])
	                    return  true;
	            }
	        return isBusy;
	    }
	    
	    public byte[] putRequestforAutoInboxLoad(OutboxTblObject newRequest, byte mRequestNo) {
	 		
	    	byte[] otsFrame = FrameEngine.sSelf.parseScriptAndCreateOtsFrame(newRequest);
	 		return otsFrame;
	 	}
	/**
	 * @param newRequest
	 */
	public synchronized int putRequest(OutboxTblObject newRequest, byte mRequestNo) {
		
		if (newRequest == null) {
			if(log)
				if (Logger.MULTICHANEL){
					Log.i(TAG,"--------------------putRequest----mRequestNo---------"+mRequestNo);	
				}
			
			mBusy[mRequestNo] = false;
			if (log)
				Log.i(TAG, "--putRequest--[INFO] - REQUEST OBJECT NULL");
		}
		if (mBusy[mRequestNo])
			return Constants.ERROR_BUSY;
		if (null != mRequest[mRequestNo])
			mRequest[mRequestNo].close();
		mRequest[mRequestNo] = null;
		mRequest[mRequestNo] = newRequest;
		if(mRequest[mRequestNo] !=null)
			mRequest[mRequestNo].httpEngineIndex = mRequestNo;
		if (log)
			Log.i(TAG, "--putRequest::[INFO] - REQUEST OBJECT =" + mRequest);
		return Constants.ERROR_NONE;
	}
	/**
	 * @return
	 */
	public int sendRequest(boolean mIcu, OutboxTblObject reqObj) {
		if (log)
			Log.i(TAG, "sendRequest--[INFO]  -- ENTRY ");
		
		int ret = Constants.ERROR_NONE;
//		setBusy(true);
		byte nextHttpEngine = 0;
		byte busyCount = 0;
		 while(isBusy(nextHttpEngine)) {
//           System.out.println("while : "+nextHttpEngine);
           if(busyCount>HTTP_ENGINES)
               break;
           else{
              if(nextHttpEngine<mHttpEngine.length-1)
                  nextHttpEngine++;
              else
                  nextHttpEngine = 0;
              busyCount++;
           }
      }
		 if(log)
			 if (Logger.MULTICHANEL){
					Log.i(TAG,"PSS_2------------isBusy(0)-"+isBusy((byte)0));
					Log.i(TAG,"PSS_3------------isBusy(1)-"+isBusy((byte)1));
					Log.i(TAG,"PSS_4------------sendRequest---nextHttpEngine-"+nextHttpEngine);	
			 }
		if(isBusy(nextHttpEngine))return Constants.ERROR_BUSY;
		{
			if (log)
				Log.w(TAG, "OtsTransport::sendRequest():[INFO] REQUEST OBJECT IS ALREADY SET "+mIcu);
			if(mIcu){               
//               currentHttpEngine = 0;
//               nextHttpEngine = 0;
				putRequest(reqObj,nextHttpEngine);
               setBusy(true, nextHttpEngine);
           }else {
               return 0;
           }
		}
		cancelTimer();
		ret = netSend(nextHttpEngine);
		if (ret != Constants.ERROR_NONE) {
//			setBusy(false,nextHttpEngine);
			if (log)
				Log.w(TAG, "OtsTransport::sendReqeust():[WARNING]-- ****RETURN VALUE = " + ret + "  NETWORK STATE = " + this.iNetState);
			createErrorResponse(Constants.ERROR_NET,nextHttpEngine);
		}
		BusinessProxy.sSelf.networkStateChanged(iNetState[nextHttpEngine], mRequest[nextHttpEngine].mOp[0]);
//		if (log)
//			Log.w(TAG, "OtsTransport::sendRequest():[INFO]  -- EXIT");
		return ret;
	}
	/**
	 * @return
	 */
	public int sendRequest(boolean mIcu) {
		if (log)
			Log.i(TAG, "sendRequest--[INFO]  -- ENTRY");
		int outboxRecCount = 0;
		int ret = Constants.ERROR_NONE;
//		setBusy(true);
		byte nextHttpEngine = 0;
		byte busyCount = 0;
		 while(isBusy(nextHttpEngine)) {
//           System.out.println("while : "+nextHttpEngine);
           if(busyCount>HTTP_ENGINES-1)
               break;
           else{
              if(nextHttpEngine<mHttpEngine.length-2)
                  nextHttpEngine++;
              else
                  nextHttpEngine = 0;
              busyCount++;
           }
      }
		 if(log)
			 if (Logger.MULTICHANEL){
					Log.i(TAG,"PSS_2------------isBusy(0)-"+isBusy((byte)0));	
					Log.i(TAG,"PSS_3------------isBusy(1)-"+isBusy((byte)1));	
					Log.i(TAG,"PSS_4------------sendRequest---nextHttpEngine-"+nextHttpEngine);	
			 }
		  if(isBusy(nextHttpEngine) && !mIcu)return ret;
		if (!mIcu && !isBusy(nextHttpEngine)) {
			try {
				OutboxTblObject reqObj = new OutboxTblObject(1);
				outboxRecCount = BusinessProxy.sSelf.getRecordCount(DBEngine.OUTBOX_TABLE);
				if (log)
					Log.i(TAG, "PSS -sendRequest--[INFO] Outbox Count = " + outboxRecCount);
				
//				 mRequest[nextHttpEngine] = null;
				if (outboxRecCount > 0) {
					// Retrieve A New Request from out-box queue.
//					ret = reqObj.getRecords(true, true, 0, DBEngine.OUTBOX_TABLE);
				
					 ret = reqObj.getRecords(true, true, 0, nextHttpEngine, DBEngine.OUTBOX_TABLE);
					  if (ret != Constants.ERROR_NONE)
	                    {
//	                         switch(nextHttpEngine){
//	                            case 0:
//	                                 lastAsyncFirstProcessedId = -1;
//	                                break;
//	                            case 1:
//	                                 lastAsyncSecondProcessedId = -1;
//	                                break;
//	                        }
						  if (log)
								Log.i(TAG, "sendRequest--[INFO] Outbox ret nextHttpEngine = " + ret+" , "+nextHttpEngine);
	                         reqObj = null;
	                        if(ret == Constants.ERROR_DB)
	                            return Constants.ERROR_NONE;
	                        return ret;
	                    }
					if (reqObj.mIdList[0] > BusinessProxy.sSelf.getTransactionId())
						BusinessProxy.sSelf.setTransactionId(reqObj.mIdList[0]);
					reqObj.httpEngineIndex = nextHttpEngine;
                    currentHttpEngine = nextHttpEngine;
                    setBusy(true,nextHttpEngine);
                    mRequest[reqObj.httpEngineIndex] = null;
					mRequest[nextHttpEngine] = reqObj;
					if(log)
						if (Logger.MULTICHANEL){
							Log.i(TAG,"###################################mRequest########################");	
							Log.i(TAG,"nextHttpEngine: "+nextHttpEngine);	
							Log.i(TAG,"reqObj[0]: "+reqObj.mIdList[0]);	
							Log.i(TAG,"mOp[0]: "+reqObj.mOp[0]);
							Log.i(TAG,"mOp[0]: "+"mHeader[0]: "+reqObj.mHeader[0]);
						}
					
					
				} else {
//					mRequest[reqObj.httpEngineIndex] = null;
//					setBusy(false,nextHttpEngine);
					return Constants.ERROR_OUTQUEUE_EMPTY;
				}
			} catch (Exception ex) {
				if (log)
					Log.e(TAG, "--sendReqeust--[ERROR(1)] FETCH NEW REQUEST FAILED: " + ex, ex);
				return Constants.ERROR_DB;
			}
		} else {
			if (log)
				Log.i(TAG, "OtsTransport::sendRequest():[INFO] REQUEST OBJECT IS ALREADY SET "+mIcu);
			if(mIcu){
               currentHttpEngine = 2;
               nextHttpEngine = 2;
               setBusy(true, nextHttpEngine);
           }else {
               return 0;
           }
		}
		cancelTimer();
		ret = netSend(nextHttpEngine);
		if (ret != Constants.ERROR_NONE) {
//			setBusy(false,nextHttpEngine);
			if (log)
				Log.w(TAG, "OtsTransport::sendReqeust():[WARNING]-- ****RETURN VALUE = " + ret + "  NETWORK STATE = " + this.iNetState);
			createErrorResponse(Constants.ERROR_NET,nextHttpEngine);
		}
		BusinessProxy.sSelf.networkStateChanged(iNetState[nextHttpEngine], mRequest[nextHttpEngine].mOp[0]);
//		if (log)
//			Log.i(TAG, "OtsTransport::sendRequest():[INFO]  -- EXIT");
		return ret;
	}

	private synchronized void createErrorResponse(int aErr,byte mRequestNo) {
		if(log)
			if (Logger.MULTICHANEL)
				Log.w(TAG, "PSS_15 --createErrorResponse--[INFO] ERROR IN NETWORK WITH CODE mRequestNo = " + aErr +" , "+mRequestNo);
		
		if (log)
			Log.w(TAG, "--createErrorResponse--[INFO] ERROR IN NETWORK WITH CODE = " + aErr);
		ResponseObject res = new ResponseObject();
		res.setSentTransactionId(iSentTransId[mRequestNo]);
		res.setSentOp(iSentOpCode[mRequestNo]);
		res.setError(aErr);
		iNetState[mRequestNo] = DISCONNECTED;
		++iRetryCount;
		BusinessProxy.sSelf.enqueueResponse(res);
//		setBusy(false,mRequestNo);
		BusinessProxy.sSelf.networkStateChanged(this.iNetState[mRequestNo], iSentOpCode[mRequestNo]);
		synchronized (this) {
			iSentOpCode[mRequestNo] = 0;
			iSentTransId[mRequestNo] = 0;
		}
	}

	private void createNetInstance() throws Exception {
		if (log)
			Log.i(TAG, "--createNetInstance--[FINE] Entry");
		this.mHttpEngine = HttpConnectionEngine.getInstance();
		if (null == this.mHttpEngine)
			throw new Exception("HTTP INSTANCE FAILED");
		if (log)
			Log.i(TAG, "--createNetInstance--[FINE] EXIT");
	}

	public void configureNetGateway() throws Exception {
		if (log)
			Log.i(TAG, "--configureNetGateway():[FINE] Entry");
		int ret = 0;
		
		if (ret != HttpConnectionEngine.RESPONSE_TYPE_SUCCESS) {
			throw new Exception("HTTP CONFIGURE FAILED");
		}
		 if (mHttpEngine != null)
             for (byte i = 0; i < mHttpEngine.length; i++)
             {
                 if (mHttpEngine[i] != null)
                 {
//                	 ret = mHttpEngine[i].netConfigure(this, YelatalkApplication.clientProperty.getProperty(ClientProperty.SERVER_MAIN_ADDRESS),
//                			 YelatalkApplication.clientProperty.getProperty(ClientProperty.SERVER_FALLBACK_ADDRESS),i);
                	 
                	 ret = mHttpEngine[i].netConfigure(this, Urls.SERVER_MAIN_ADDRESS, Urls.SERVER_FALLBACK_ADDRESS, i);
        			 if (ret != HttpConnectionEngine.RESPONSE_TYPE_SUCCESS) {
        					throw new Exception("HTTP CONFIGURE FAILED");
        				}
                 }
             }
	}

	synchronized private int netStart(byte mRequestNo) {
		int retVal = Constants.ERROR_NONE;
		if (iNetState[mRequestNo] == INIT) {
			try {
				configureNetGateway();
			} catch (Exception ex) {
				if (log)
					Log.e(TAG, "--netStart--[ERROR] VECLIENT CONFIGURE FAILED ::" + ex, ex);
				return -1;
			}
		} else {
			retVal = startSession(mRequestNo);
		}
		return retVal;
	}

	private int startSession(byte mRequestNo) {
		int ret = Constants.ERROR_NONE;
		if (log)
			Log.i(TAG, "--veClientStartSession():[INFO]");
		iNetState[mRequestNo] = STARTSESSION;
		iTimeOutTimer = new Timer();
		iTimeOutTimer.schedule(new OtsSchedularTask(this, null, mRequestNo), 119 * 1000);
		ret = mHttpEngine[mRequestNo].startSession(mRequestNo);
		if (ret != Constants.ERROR_NONE)
			cancelTimer();
		return ret;
	}

	private synchronized int netSend(byte mRequestNo) {
		
		if(log)
			if (Logger.MULTICHANEL){
				Log.i(TAG,"---------------------net send mRequestNo"+mRequestNo);
		 }
		
		if (log)
			Log.i(TAG, "--netSend--[INFO] -- Entry. And Request Object = " + mRequest);
		int retVal = Constants.ERROR_NONE;
		if (mRequest == null) {
			return -1;
		}
		iSentOpCode[mRequestNo] = mRequest[mRequestNo].mOp[0];
		iSentTransId[mRequestNo] = mRequest[mRequestNo].mIdList[0];
		
		if(log)
			if (Logger.MULTICHANEL){
				Log.i(TAG,"PSS_5---------------------net send mRequestNo"+mRequestNo);
		 }
		if ((retVal = sendOnNetwork(mRequest[mRequestNo])) != 0) {
			return retVal;
		}
		iNetState[mRequestNo] = NetworkTransport.EXECUTESPEECH;
		return 0;
	}

	private int sendOnNetwork(OutboxTblObject aRequestData) {
		int ret = 0;
		byte[] otsFrame = FrameEngine.sSelf.parseScriptAndCreateOtsFrame(aRequestData);
		if (null == otsFrame || otsFrame.length < 12) {
			if (log)
				Log.i(TAG, "PSS_6--sendOnNetwork-- ERROR  -- FRAME = " + otsFrame + "    Having ERROR ");
			return Constants.ERROR_NOT_FOUND;
		}
		
		if (log)
			Log.i(TAG, "--sendOnNetwork-- Frame Length -  " + otsFrame.length);
		ret = veClientExecuteSpeech(otsFrame, aRequestData);
		return ret;
	}

	//--------------------------------------------------------------------------------------------------   
	private int veClientExecuteSpeech(byte[] aOtsFrame, OutboxTblObject aRequestData) {
		int ret = 0;
		try {
			if (log)
				Log.i(TAG, "veClientExecuteSpeech--[INFO]: FRAME = " + aRequestData.mHeader[0]);
			int x = aRequestData.mHeader[0].indexOf("TIME");
			String str = "";
			if (x >= 0) {
				str = aRequestData.mHeader[0].substring(0, x);
				String str1 = aRequestData.mHeader[0].substring(x, aRequestData.mHeader[0].indexOf(',', x));
				str1 = aRequestData.mHeader[0].substring(x + str1.length() + 1);
				aRequestData.mHeader[0] = str + str1;
				if (log)
					Log.i(TAG, "--veClientExecuteSpeech--[INFO]: FRAME AFTER TIME = " + aRequestData.mHeader[0]);
			}
			iTimeOutTimer = new Timer();
//			if (iIsSyncCall)
//				iTimeOutTimer.schedule(new OtsSchedularTask(this, new Integer(TIME_OUT_TASK), aRequestData.httpEngineIndex), 44 * 1000);
//			else
				iTimeOutTimer.schedule(new OtsSchedularTask(this, new Integer(TIME_OUT_TASK), aRequestData.httpEngineIndex), 299 * 1000);
			this.mHttpEngine[aRequestData.httpEngineIndex].setRequestType(aOtsFrame[8], aOtsFrame[9],aRequestData.mIdList[0]);
			ret = this.mHttpEngine[aRequestData.httpEngineIndex].executeResourceData(aOtsFrame, aRequestData.httpEngineIndex);
			if (ret != Constants.ERROR_NONE) {
				cancelTimer();
				if (log)
					Log.i(TAG, "--veClientExecuteSpeech -- HttpEngine return =" + ret);
			}
		} catch (Exception ex) {
			cancelTimer();
			if (log)
				Log.e(TAG, "--veClientExecuteSpeech--[ERROR(1)] EXECUTE SPEECH FAILED :: " + ex, ex);
			ret = Constants.ERROR_NOT_FOUND;
		}
		return ret;
	}

	private void createResponseAndEnqueue(byte[] data, byte mResponseNo) {
		iRetryCount = 0;
		ResponseObject res = new ResponseObject();
		res.iHttpEngineIndex = mResponseNo;
		res.setResponseData(data);
		res.setSentTransactionId(this.iSentTransId[mResponseNo]);
		res.setSentOp(this.iSentOpCode[mResponseNo]);
		if(res.mResponseData.length < 9){
//			System.out.println("======================================================================");
//			System.out.println(new String(res.mResponseData));
//			System.out.println("======================================================================");
		} else {
			res.setResponseCode(res.mResponseData[9]);
		}
		res.setError(Constants.ERROR_NONE);
		this.iSentOpCode[mResponseNo] = 0;
		this.iSentTransId[mResponseNo] = 0;
		if (res.getResponseCode() != Constants.RES_TYPE_CONGESTION) {
			BusinessProxy.sSelf.enqueueResponse(res);
		} else {
			if (log)
				Log.e(TAG, "--createResponseAndEnqueue():[WARNING] -- ****GOT CONGESTION FROM SERVER****", null);
		}
//		setBusy(false,mResponseNo);//nagendra kumar
	}

	private void releaseNetInstance(byte mRequestNo) {
		mHttpEngine[mRequestNo].delete();
	}

	private void cancelTimer() {
		if (null != iTimeOutTimer)
			iTimeOutTimer.cancel();
		iTimeOutTimer = null;
	}

	private void notifySuccess(int aApi, byte[] aResponse, byte mResponseNo) {
		if (log)
			Log.i(TAG, "PSS_14 --notifySuccess--[INFO] API CODE = " + aApi);
		int ret = 0;
		cancelTimer();
		switch (aApi) {
		case NetworkEngineNotifier.API_NET_CONFIGUREED:
			iNetState[mResponseNo] = NetworkTransport.DISCONNECTED;
			ret = netStart(mResponseNo);
			BusinessProxy.sSelf.networkStateChanged(this.iNetState[mResponseNo], iSentOpCode[mResponseNo]);
			break;
		case NetworkEngineNotifier.API_NET_STARTED:
			// iNetState = OtsTransport.CONNECTED;
			ret = netSend(mResponseNo);
			BusinessProxy.sSelf.networkStateChanged(this.iNetState[mResponseNo], iSentOpCode[mResponseNo]);
			break;
		case NetworkEngineNotifier.API_DATA_EXECUTED:
			iNetState[mResponseNo] = NetworkTransport.CONNECTED;
			createResponseAndEnqueue(aResponse, mResponseNo);
			break;
		default:
			ret = -1;
			if (log)
				Log.i(TAG, "PSS_141 --notifySuccess([])::[ERROR] - case not handled for = " + aApi);
			break;
		}
		if (ret != 0) {
			if (mRequest != null) {
				iSentOpCode[mResponseNo] = mRequest[mResponseNo].mOp[0];
				iSentTransId[mResponseNo] = mRequest[mResponseNo].mIdList[0];
			}
			createErrorResponse(Constants.ERROR_NET, mResponseNo);
		}
	}

	public void networkAPINotification(int retCode, byte[] data, byte mResponseNo) {
		// FIXME - Check this for Android
		// CoreController.iSelf.cancelTimer();
		
		if(log)
			if (Logger.MULTICHANEL){
				Log.i(TAG,"----- networkAPINotification:  retCode:"+retCode+" mResponseNo:"+mResponseNo);
		 }
		try {
			notifySuccess(retCode, data, mResponseNo);
		} catch (Throwable ex) {
			if (log)
				Log.e(TAG, "--networkAPINotification():SELF " + ex.toString(), ex);
		}
	}

	public void networkStateChanged(int newState, String str) {

	}

	public void onTaskCallback(Object parameter, byte mRequestNo) {
		int task = ((Integer) parameter).intValue();
		switch (task) {
		case TIME_OUT_TASK:
			mHttpEngine[mRequestNo].cancelOperation("OtsTransport__executeTask[TIMEOUT]");
			createErrorResponse(Constants.ERROR_NET, mRequestNo);
			break;
		}
	}

	@Override
	public void networkDataChange(int data) {
		// TODO Auto-generated method stub
		BusinessProxy.sSelf.networkDataLoadChange(data);
	}

}
