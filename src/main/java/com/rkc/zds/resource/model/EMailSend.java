package com.rkc.zds.resource.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class EMailSend {
	  private String emailId;
	  private String emailSubjectTxt = "Contact Request";
	  private String emailList = "richard.campion@zdslogic.com";
	  private String emailFirstName;
	  private String emailFromAddress;
	  private String emailMsgTxt;
	  private List<MultipartFile> emailFiles;
	  
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getEmailSubjectTxt() {
		return emailSubjectTxt;
	}
	public void setEmailSubjectTxt(String emailSubjectTxt) {
		this.emailSubjectTxt = emailSubjectTxt;
	}
	public String getEmailList() {
		return emailList;
	}
	public void setEmailList(String emailList) {
		this.emailList = emailList;
	}
	public String getEmailFirstName() {
		return emailFirstName;
	}
	public void setEmailFirstName(String emailFirstName) {
		this.emailFirstName = emailFirstName;
	}
	public String getEmailFromAddress() {
		return emailFromAddress;
	}
	public void setEmailFromAddress(String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}
	public String getEmailMsgTxt() {
		return emailMsgTxt;
	}
	public void setEmailMsgTxt(String emailMsgTxt) {
		this.emailMsgTxt = emailMsgTxt;
	}
	public List<MultipartFile> getEmailFiles() {
		return emailFiles;
	}
	public void setEmailFiles(List<MultipartFile> emailFiles) {
		this.emailFiles = emailFiles;
	}


}
