package com.rkc.zds.resource.model;

import java.lang.annotation.Repeatable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EducationData {
//@Repeatable	
	//@JsonProperty("Education")	
	@JsonProperty("EDUCATION")	
	private String EDUCATION;

	@JsonProperty("Education")
	private String educationTwo;
	
//	public String getEducation() {
///		return EDUCATION;
//	}

	/*
	public void setEducation(String education) {
		this.EDUCATION = education;
	}
	 */

	public String getEducationTwo() {
		return EDUCATION;
	}

	public void setEducationTwo(String educationTwo) {
		this.EDUCATION = educationTwo;
	}

	public String getEDUCATION() {
		return EDUCATION;
	}

	public void setEDUCATION(String ed) {
		EDUCATION = ed;
	}

	@JsonFormat(shape=JsonFormat.Shape.ARRAY)
	private List<String> education_and_training;
	
	public List<String> getEducation_and_training() {
		return education_and_training;
	}

	public void setEducation_and_training(List<String> education_and_training) {
		this.education_and_training = education_and_training;
	}
}
