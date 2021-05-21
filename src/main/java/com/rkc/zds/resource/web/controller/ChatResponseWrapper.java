package com.rkc.zds.resource.web.controller;

import com.rkc.zds.resource.model.ChatParticipant;

public class ChatResponseWrapper {
	
	ChatParticipant participant;

	public ChatParticipant getParticipant() {
		return participant;
	}

	public void setParticipant(ChatParticipant participant) {
		this.participant = participant;
	}

}
