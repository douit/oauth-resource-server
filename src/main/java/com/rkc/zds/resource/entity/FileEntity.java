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
//import com.rkc.zds.dashboard.pcm.model.FileData;

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
 * The persistent class for the PCM_USER_FILE database table.
 * 
 */
@Entity(name = "FileEntity")
@Table(name = "PCM_USER_FILE")
public class FileEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
	@Column(name = "FILE_ID")
    @GeneratedValue
    private Long fileId;

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

	@Column(name = "JSON_FILE")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String jsonFile;

	@Column(name = "TEXT_FILE")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String textFile;

	
	@Column(name = "HTML_FILE")
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String htmlFile;
	
	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	
	public String getTextFile() {
		return textFile;
	}

	public void setTextFile(String textFile) {
		this.textFile = textFile;
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

	public String getJsonFile() {
		return jsonFile;
	}

	public void setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
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
	
	public String getHtmlFile() {
		return htmlFile;
	}

	public void setHtmlFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}

	public FileEntity() {
	}
	
}
