package com.rkc.zds.resource.entity;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The persistent class for the PCM_RESUME_SKILL database table.
 * 
 */

@Entity(name = "ResumeSkillEntity")
@Table(name = "PCM_RESUME_SKILL")
public class ResumeSkillEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @Id
	@Column(name="ID", unique = true)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
 
	@Column(name = "RESUME_ID")
	//@JsonProperty("resumeId")
    private Long resumeId;
	
	@Column(name = "SKILL_ID")
	//@JsonProperty("skillId")
	private Long skillId;
 
	@Column(name = "SKILL_NAME")
	//@JsonProperty("skillId")
	private String skillName;
	
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getResumeId() {
		return resumeId;
	}

	public void setResumeId(Long resumeId) {
		this.resumeId = resumeId;
	}

	public Long getSkillId() {
		return skillId;
	}

	public void setSkillId(Long skillId) {
		this.skillId = skillId;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    private ResumeEntity  resumeEntity;
    
	public ResumeEntity getResumeEntity() {
		return resumeEntity;
	}

	public void setResumeEntity(ResumeEntity resumeEntity) {
		this.resumeEntity = resumeEntity;
	}

    //Constructors, getters and setters removed for brevity
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResumeSkillEntity )) return false;
        return id != null && id.equals(((ResumeSkillEntity) o).getId());
    }
 
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
/*
@Entity(name = "ResumeSkillEntity")
//@Table(name = "PCM_RESUME_SKILL", catalog = "pcm", uniqueConstraints = @UniqueConstraint(columnNames = { "RESUME_ID", "SKILL_ID" }))
@Table(name = "PCM_RESUME_SKILL")
public class ResumeSkillEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID", unique = true)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonProperty("id")
	private Long id;
	
    @ManyToOne(fetch = FetchType.EAGER)
	private ResumeEntity resumeEntity;

	public ResumeEntity getResumeEntity() {
		return this.resumeEntity;
	}
	
    public void setResumeEntity(ResumeEntity resumeDto) {
        this.resumeEntity = resumeDto;
    }
    
	@Column(name = "RESUME_ID")
	@JsonProperty("resumeId")
    private Long resumeId;
	
	@Column(name = "SKILL_ID")
	@JsonProperty("skillId")
	private Long skillId;

    public ResumeSkillEntity() {
    }
    
	public ResumeSkillEntity(Long id, 
		Long resumeId, Long skillId) {
		this.id = id;
		this.resumeId = resumeId;
		this.skillId = skillId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getResumeId() {
		return resumeId;
	}

	public void setResumeId(Long resumeId) {
		this.resumeId = resumeId;
	}

	public Long getSkillId() {
		return skillId;
	}

	public void setSkillId(Long skillId) {
		this.skillId = skillId;
	}
    	


}
*/