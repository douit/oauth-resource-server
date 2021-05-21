package com.rkc.zds.resource.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SkillsData {
	@JsonProperty("SKILLS")	
	private String SKILLS_HEADING;
	

	public String getSKILLS_HEADING() {
		return SKILLS_HEADING;
	}

	public void setSKILLS_HEADING(String sKILLS_HEADING) {
		SKILLS_HEADING = sKILLS_HEADING;
	}

	@JsonFormat(shape=JsonFormat.Shape.ARRAY)
	private List<String> skills;
	
	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
}
