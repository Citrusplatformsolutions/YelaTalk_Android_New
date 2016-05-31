package com.kainat.app.android.core;

import java.util.Hashtable;
import java.util.Vector;

import com.kainat.app.android.model.InboxMessage;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.format.ContentFormat;
import com.kainat.app.android.util.format.Payload;

public final class Proxy {

	public final static Proxy sSelf = new Proxy();

	public StringBuffer iRecordStatStr = new StringBuffer("");
	public boolean iRecordStat = true;
	private boolean iStaticStatOn = true;

	public String iCurrAgent;
	public String iKeyForAgent;
	public boolean iIsRTMLOpen;
	public String iCurrCardName;
	public String iCurrDisplayName;
	public String iPrevDisplayName;
	public boolean iAddToBackHistory = true;
	public boolean iDownloadTophone;
	public String iSaveDownload;
	public String iCurrAgentName;
	public Vector<String> iCardNameStr = new Vector<String>();
	public InboxMessage iCurrPayloadData;
	public Hashtable<Object, Object> iSyncCallData = new Hashtable<Object, Object>(50);// to cash rtml data

	public void recordScreenStatistics(String aStr, boolean aSep, boolean aStatic) {
		if (aStatic && !iStaticStatOn)
			return;
		int recCount = 0;
		// if(Flags.LOG_ENABLE)
		//     OtsLogger.LogTime("UiController::recordScreenStatistics()::[FINE] Entry", OtsLogger.SEVERE);
		if (iRecordStatStr == null || aStr == null || (aStr != null && aStr.length() == 0))
			return;
		if (aSep && iRecordStatStr.length() > 0) {
			recCount = (iRecordStatStr.toString().split("^")).length;
			if (recCount == Constants.STAT_COUNT) {
				// System.out.println("Stat Sent !! - "+iRecordStatStr.toString());
				if (sendNewTextMessage("STAT<a:userstatistics>", iRecordStatStr.toString(), false))
					iRecordStatStr.delete(0, iRecordStatStr.length());
				else {
					//if(Flags.LOG_ENABLE)
					//   OtsLogger.LogTime("UiController__sendStatRequest()__[WARNING] -- ****ERROR SENDING REQUEST****. RETURN VALUE = ", OtsLogger.SEVERE);
				}
			}
			if (iRecordStatStr.length() > 0)
				iRecordStatStr.append('^');
		}
		iRecordStatStr.append(aStr);
		//System.out.println("Stat -> " + iRecordStatStr.toString() + " and rec count - " + (recCount + 1));
	}

	public boolean sendNewTextMessage(String aDestination, String aMessage, boolean aShowAppropriateAlert) {
		boolean ret = false;
		OutboxTblObject newRequest = new OutboxTblObject(1, Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_VOICE_NEW);
		newRequest.mPayloadData.mText[0] = aMessage.getBytes();
		newRequest.mPayloadData.mTextType[0] = ContentFormat.TEXT_TXT;
		newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
		newRequest.mDestContacts = new String[] { aDestination };
		int response = BusinessProxy.sSelf.sendToTransport(newRequest);

		if (response == Constants.ERROR_NONE) {
			// if(Flags.LOG_ENABLE)
			//    OtsLogger.LogTime("CoreController_sendNewTextMessage()-[FIE] - message sent to-" + aDestination + "  content =" + aMessage, OtsLogger.SEVERE);
			ret = true;
		} else {
			//if(Flags.LOG_ENABLE)
			//   OtsLogger.LogTime("CoreController_sendNewTxtMessage()-[ERROR] - RETURNT VALUE = " + response + "  Destination=" + aDestination, OtsLogger.SEVERE);
		}
		if (!ret && aShowAppropriateAlert) {
			//String msg;
			//byte confirmationType = 0;
			switch (response) {
			case Constants.ERROR_OUTQUEUE_FULL:
				//                    msg = DMKeys.OUTBOX_FULL_ALERT;
				//confirmationType = Constants.CONFIRMATION_ERROR;
				break;
			case Constants.ERROR_CLIENT_NOT_LOGIN_YET:
				//                    msg = DMKeys.NW_BUSY_TRY_LATER;
				//confirmationType = Constants.CONFIRMATION_ALERT;
				break;
			default:
				//                    msg = DMKeys.NW_BUSY;
				//confirmationType = Constants.CONFIRMATION_ALERT;
				break;
			}
			// if(null != msg)              
			// AllDisplays.iSelf.showConfirmation(msg,
			//                                    null, TextData.OK, UiController.iUiController.getWidth() - 60);
		}
		return ret;
	}

	public String updateBackHistory(byte type) {
		String tempStr = null;
		int size = 0;
		switch (type) {
		case 1:// Add to back history
			if (!isCurrPagaAvailableInHistory() || iCurrDisplayName.indexOf("gm:") != -1) {
				if (iCurrDisplayName == null)
					return null;
				size = iCardNameStr.size();
				size--;//Actual size of vector starting from 0;
				if (size >= 0)
					iPrevDisplayName = (String) iCardNameStr.elementAt(size);
				if (iPrevDisplayName != null && !iPrevDisplayName.equals(iCurrDisplayName))
					iCardNameStr.addElement(iCurrDisplayName);
				else if (iPrevDisplayName != null && (iPrevDisplayName.equals(iCurrDisplayName) && size < 0))
					iCardNameStr.addElement(iCurrDisplayName);
				else if (iPrevDisplayName == null)
					iCardNameStr.addElement(iCurrDisplayName);
			}
			break;
		case 2: //Delete from back history
			size = iCardNameStr.size();
			if (size > 0) {
				size--;//Actual size of vector starting from 0;
				tempStr = (String) iCardNameStr.elementAt(size);
				iCurrDisplayName = tempStr;
				iCardNameStr.removeElementAt(size);
			}
			break;
		}
		return tempStr;
	}

	private boolean isCurrPagaAvailableInHistory() {
		boolean available = false;
		String cardStr = null;
		int size = iCardNameStr.size();
		if (size > 0) {
			size--;//Actual size of vector starting from 0;
			for (int i = size; i > 0; i--) {
				cardStr = (String) iCardNameStr.elementAt(i);
				if (cardStr.equals(iCurrDisplayName))
					return true;
			}
		} else
			return false;
		return available;
	}

}
