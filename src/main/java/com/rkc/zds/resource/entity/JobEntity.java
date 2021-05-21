package com.rkc.zds.resource.entity;

import java.io.Serializable;
import java.sql.Clob;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the PCM_JOB database table.
 * 
 */
@Entity
@Table(name = "PCM_JOB")
public class JobEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer jobId;

	@Column(name = "USER_ID")
	private int userId;

	@Column(name = "CONTACT_ID")
	private int contactId;
		
	@Column(name="CREATED_AT")	
	private Timestamp createdAt;
	
	@Column(name="UPDATED_AT")	
	private Timestamp updatedAt;
	
	@Column(name="JOB_TITLE")	
	private String jobTitle;

	@Column(name="JOB_COMPANY")	
	private String jobCompany;
	
	@Column(name="JOB_DESCRIPTION")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
	private String jobDescription;

	@Column(name="JOB_DESCRIPTION_ORIGINAL")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
	private String jobDescriptionOriginal;

	@Column(name="BODY")	
	private String body;
	
	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getJobCompany() {
		return jobCompany;
	}

	public void setJobCompany(String jobCompany) {
		this.jobCompany = jobCompany;
	}
	
	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	
	public String getJobDescriptionOriginal() {
		return jobDescriptionOriginal;
	}

	public void setJobDescriptionOriginal(String jobDescriptionOriginal) {
		this.jobDescriptionOriginal = jobDescriptionOriginal;
	}
	


	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public JobEntity() {
	}
}
