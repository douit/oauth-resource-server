package com.rkc.zds.resource.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the PCM_GROUP_MEMBERS database table.
 * 
 */
@Entity
@Table(name="PCM_USER_CONTACTS")
public class UserContactEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="USER_ID")
	private int userId;

	@Column(name="CONTACT_ID")
	private int contactId;

	@Column(name = "FIRSTNAME", nullable = false, length = 100)
	private String firstName;

	@Column(name = "LASTNAME", nullable = false, length = 100)
	private String lastName;

	@Column(name = "TITLE", length = 100)
	private String title;

	@Column(name = "COMPANY", length = 100)
	private String company;

	@Column(name = "PRESENCE_IMAGE_URL", length = 100)
	private String presenceImageUrl;
	
	@Column(name="ENABLED")	
	private Integer enabled;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getContactId() {
		return this.contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPresenceImageUrl() {
		return this.presenceImageUrl;
	}

	public void setPresenceImageUrl(String presenceImageUrl) {
		this.presenceImageUrl = presenceImageUrl;
	}
	
	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	
    public UserContactEntity() {
    }
	
	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 53 * hash + (this.id);
	    return hash;
	}

	@Override
	public boolean equals(Object other) {
	    boolean result;
	    if((other == null) || (getClass() != other.getClass())){
	        result = false;
	    } // end if
	    else{
	        UserContactEntity otherContact = (UserContactEntity)other;
	        result = (id == (otherContact.id));
	    } // end else

	    return result;
	}
}
