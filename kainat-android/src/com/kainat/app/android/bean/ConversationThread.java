/*
 * Copyright (c) 2014 Jangobanana, Inc.
 * All rights reserved. Use is subject to license terms.
 */
package com.kainat.app.android.bean;

/**
 * @author Nagendra Kumar
 *
 */
public class ConversationThread {
	String participent_id  ;
	public String getParticipent_id() {
		return participent_id;
	}
	public void setParticipent_id(String participent_id) {
		this.participent_id = participent_id;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	public String getMsgText() {
		return msgText;
	}
	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getMessagestatus() {
		return messagestatus;
	}
	public void setMessagestatus(String messagestatus) {
		this.messagestatus = messagestatus;
	}
	String sender ;
	String conversationId ;
	String msgText ;
	String datetime ;
	String messagestatus ;
	
}
