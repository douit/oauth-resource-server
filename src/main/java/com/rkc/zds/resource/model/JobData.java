package com.rkc.zds.resource.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobData {
	
	@JsonProperty("education_and_training")
	List<EducationData> educationData;
	
	@JsonProperty("work_experience")
	List<WorkData> workData;
	
	@JsonProperty("skills")
	List<SkillsData> skillsData;

	@JsonProperty("basics")
	BasicsData basicsData;

	public List<EducationData> getEducationData() {
		return educationData;
	}

	public void setEducationData(List<EducationData> educationData) {
		this.educationData = educationData;
	}
	
	public List<SkillsData> getSkillsData() {
		return skillsData;
	}

	public void setSkillsData(List<SkillsData> skillsData) {
		this.skillsData = skillsData;
	}
	
	public BasicsData getBasicsData() {
		return basicsData;
	}

	public void setBasicsData(BasicsData basicsData) {
		this.basicsData = basicsData;
	}

}
