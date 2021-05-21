package com.rkc.zds.resource.web.controller;

public class ResponseWrapper {

	private Integer status;
	private String jsonData;
	private String htmlData;
	private String textData;
	private String originalFileName;
	private String shortFileName;	
	private String pdfFileName;
	private String htmlFileName;
	private String message;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String data) {
		this.jsonData = data;
	}
	
	public String getHtmlData() {
		return htmlData;
	}

	public void setHtmlData(String htmlData) {
		this.htmlData = htmlData;
	}
	
	public String getTextData() {
		return textData;
	}

	public void setTextData(String textData) {
		this.textData = textData;
	}	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	
	public String getShortFileName() {
		return shortFileName;
	}

	public void setShortFileName(String shortFileName) {
		this.shortFileName = shortFileName;
	}
	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	public String getHtmlFileName() {
		return htmlFileName;
	}

	public void setHtmlFileName(String htmlFileName) {
		this.htmlFileName = htmlFileName;
	}

}
