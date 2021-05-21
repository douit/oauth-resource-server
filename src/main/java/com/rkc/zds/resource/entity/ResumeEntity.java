package com.rkc.zds.resource.entity;

import java.io.Serializable;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rkc.zds.resource.model.ResumeData;

/*
@Entity(name = "Post")
@Table(name = "post")
public class Post {
 
    @Id
    @GeneratedValue
    private Long id;
 
    private String title;
 
    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PostComment> comments = new ArrayList<>();
 
    //Constructors, getters and setters removed for brevity
 
    public void addComment(PostComment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
 
    public void removeComment(PostComment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}
*/
/**
 * The persistent class for the PCM_USER_RESUME database table.
 * 
 */
@Entity(name = "ResumeEntity")
@Table(name = "PCM_USER_RESUME")
public class ResumeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
	@Column(name = "RESUME_ID")
    @GeneratedValue
    private Long resumeId;

	@Column(name = "USER_ID")
	private int userId;

	@Column(name = "FIRSTNAME")
	private String firstName;

	@Column(name = "LASTNAME")
	private String lastName;

	@Column(name = "ORIGINAL_FILE_NAME")
	private String originalFileName;

	@Column(name = "SHORT_FILE_NAME")
	private String shortFileName;

	@Column(name = "PDF_FILE_NAME")
	private String pdfFileName;

	@Column(name = "HTML_FILE_NAME")
	private String htmlFileName;

	@Column(name = "JSON_RESUME")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String jsonResume;

	@Column(name = "TEXT_RESUME")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String textResume;

	public Long getResumeId() {
		return resumeId;
	}

	public void setResumeId(Long resumeId) {
		this.resumeId = resumeId;
	}
/*
	@OneToMany(cascade = {CascadeType.ALL, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "resumeId", referencedColumnName = "RESUME_ID")
	@ElementCollection(targetClass=ResumeSkillEntity.class)
    @JsonProperty("skillsList")
    private List<ResumeSkillEntity> skillsList = new ArrayList<ResumeSkillEntity>();

	public List<ResumeSkillEntity> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<ResumeSkillEntity> skillsList) {
		this.skillsList = skillsList;
	}
*/
	
	//@OneToMany(mappedBy = "resumeEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
	//@OneToMany(mappedBy = "resumeEntity", cascade = {CascadeType.ALL, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
	@OneToMany(mappedBy = "resumeEntity", cascade = {CascadeType.ALL, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    //@JoinColumn(name = "resumeId", referencedColumnName = "resumeId")
	@ElementCollection(targetClass=ResumeSkillEntity.class)
    @JsonProperty("skills")
	private List<ResumeSkillEntity> skills = new ArrayList<ResumeSkillEntity>();
	public List<ResumeSkillEntity> getSkills() {
		return skills;
	}

	public void setSkills(List<ResumeSkillEntity> skills) {
		this.skills = skills;
	}

	//private List<ResumeSkillEntity> skills = new ArrayList<>();
	public void addResumeSkillEntity(ResumeSkillEntity resumeSkill) {
		skills.add(resumeSkill);
		resumeSkill.setResumeEntity(this);
	}

	public void removeResumeSkillEntity(ResumeSkillEntity resumeSkill) {
		skills.remove(resumeSkill);
		resumeSkill.setResumeEntity(null);
	}

	public String getTextResume() {
		return textResume;
	}

	public void setTextResume(String textResume) {
		this.textResume = textResume;
	}

	@Transient
	private ResumeData resumeData;

	@Transient
	private String htmlResume;

	public ResumeData getResumeData() {
		return resumeData;
	}

	public void setResumeData(ResumeData parsed) {
		this.resumeData = parsed;
	}


	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJsonResume() {
		return jsonResume;
	}

	public void setJsonResume(String jsonResume) {
		this.jsonResume = jsonResume;
	}

	public String getHtmlResume() {
		return htmlResume;
	}

	public void setHtmlResume(String htmlResume) {
		this.htmlResume = htmlResume;
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

	public ResumeEntity() {
	}
	
}
