package com.rkc.zds.resource.model;

public class ChatParticipant {
	int participantType;
	int id;
	int status;
	String avatar;
	String displayName;
	
	public int getParticipantType() {
		return participantType;
	}
	public void setParticipantType(int participantType) {
		this.participantType = participantType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}

