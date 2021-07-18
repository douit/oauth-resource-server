package com.rkc.zds.resource.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the PCM_CONTACT_EMAILS database table.
 * 
 */
@Entity
@Table(name="PCM_CONTACT_WEBSITES")
public class WebsiteEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer websiteId;

	@Column(name="CONTACT_ID")
	private int contactId;

	@Column(name="WEBSITE")
	private String website;
	
	@Column(name="WEBSITEKIND")
	private int websiteKind;

	public Integer getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(Integer websiteId) {
		this.websiteId = websiteId;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public int getWebsiteKind() {
		return websiteKind;
	}

	public void setWebsiteKind(int websiteKind) {
		this.websiteKind = websiteKind;
	}

	public WebsiteEntity() {
    }
}
    