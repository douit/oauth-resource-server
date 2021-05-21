package com.rkc.zds.resource.web.controller;

public class UploadChatFileResponse {
    private int type;
    private int toId;
    private String message;
    private String mimeType;
    private long fileSizeInBytes;
    private String downloadUrl;
    
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getToId() {
		return toId;
	}
	public void setToId(int toId) {
		this.toId = toId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public long getFileSizeInBytes() {
		return fileSizeInBytes;
	}
	public void setFileSizeInBytes(long fileSizeInBytes) {
		this.fileSizeInBytes = fileSizeInBytes;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	public UploadChatFileResponse(int type, int toId, String message, String mimeType, long fileSizeInBytes,
			String downloadUrl) {
		super();
		this.type = type;
		this.toId = toId;
		this.message = message;
		this.mimeType = mimeType;
		this.fileSizeInBytes = fileSizeInBytes;
		this.downloadUrl = downloadUrl;
	}
    


}
