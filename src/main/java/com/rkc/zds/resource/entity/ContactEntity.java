package com.rkc.zds.resource.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Pcm Contacts generated by hbm2java
 */
@Entity
@Table(name = "PCM_CONTACTS")
public class ContactEntity implements java.io.Serializable {
	
    private static final long serialVersionUID = -6809049173391335091L;
	
	@Id
	@Column(name="CONTACT_ID", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "USER_ID")	
	private Integer userId;
	
	@Column(name = "OWNER_ID")	
	private Integer ownerId;	
	
	@Column(name="CREATED_AT")	
	private String createdAt;
	
	@Column(name="UPDATED_AT")	
	private String updatedAt;	

	@Column(name = "FULLNAME", length = 225)
	private String fullName;
	
	@Column(name = "FIRSTNAME", nullable = false, length = 225)
	private String firstName;

	@Column(name = "LASTNAME", nullable = false, length = 225)
	private String lastName;
	
	@Column(name = "GENDER", length = 25)
	private String gender;
	
	@Column(name="BIRTH_DATE")	
	private String birthdate;		
	
	@Column(name = "TITLE", length = 225)
	private String title;

	@Column(name = "COMPANY", length = 225)
	private String company;
	
	@Column(name = "IMAGE_URL", length = 225)
	private String imageURL;

	@Column(name = "LINKEDIN", length = 225)
	private String linkedin;

	@Column(name = "FACEBOOK", length = 225)
	private String facebook;
	
	@Column(name = "SKYPE", length = 225)
	private String skype;

	@Column(name = "TWITTER", length = 225)
	private String twitter;

	@Column(name = "NOTES", length = 225)
	private String notes;
	
	@Column(name = "PRESENCE_IMAGE_URL", length = 225)
	private String presenceImageUrl;
	
	@Column(name="ENABLED")	
	private Integer enabled;

	public ContactEntity() {
	}

	public ContactEntity(int id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public ContactEntity(
			int id, 
			String firstName, 
			String lastName, 
			String title, 
			String company) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.title = title;
		this.company = company;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}



	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
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
	
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}
	
	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
	        ContactEntity otherContact = (ContactEntity)other;
	        result = (id == (otherContact.id));
	    } // end else

	    return result;
	}

}
