package com.rkc.zds.resource.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CredibilityData {
	
	@JsonProperty("CREDIBILITY")	
	private String CREDIBILITY;
	
	public String getCREDIBILITY() {
		return CREDIBILITY;
	}

	public void setCREDIBILITY(String cred) {
		CREDIBILITY = cred;
	}

	@JsonFormat(shape=JsonFormat.Shape.ARRAY)
	private List<String> credibility;
	
	public List<String> getCredibility() {
		return credibility;
	}

	public void setCredibility(List<String> credibility) {
		this.credibility = credibility;
	}
}
