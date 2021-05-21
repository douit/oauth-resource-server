package com.rkc.zds.resource.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeData {
	
	@JsonProperty("education_and_training")
	List<EducationData> educationData;
	
	@JsonProperty("work_experience")
	List<WorkData> workData;
	
	@JsonProperty("skills")
	List<SkillsData> skillsData;

	@JsonProperty("basics")
	BasicsData basicsData;

	@JsonIgnore
	@JsonProperty("summary")
	SummaryData summaryData;

	//@JsonIgnore
	//@JsonProperty("credibility")
	//CredibilityData credibilityData;
	
	public List<EducationData> getEducationData() {
		return educationData;
	}
	
	public void setEducationData(List<EducationData> educationData) {
		this.educationData = educationData;
	}

	public List<WorkData> getWorkData() {
		return workData;
	}

	public void setWorkData(List<WorkData> workData) {
		this.workData = workData;
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
	/*
	public SummaryData getSummaryData() {
		return summaryData;
	}
	
	public void setSummaryData(SummaryData summaryData) {
		this.summaryData = summaryData;
	}

	public CredibilityData getCredibilityData() {
		return credibilityData;
	}

	public void setCredibilityData(CredibilityData credibilityData) {
		this.credibilityData = credibilityData;
	}
	*/
}
