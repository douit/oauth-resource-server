package com.rkc.zds.resource.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the PCM_USER_RESUME database table.
 * 
 */
@Entity
@Table(name = "PCM_SKILL")
public class SkillEntity {
	
	@Id
	@Column(name = "SKILL_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long skillId;
	
	@Column(name="SKILL_NAME")	
	private String skillName;

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

}
