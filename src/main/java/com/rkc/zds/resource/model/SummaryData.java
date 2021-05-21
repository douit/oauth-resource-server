package com.rkc.zds.resource.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryData {
	
	@JsonIgnore
	@JsonProperty("SUMMARY")	
	private String SUMMARY;

	@JsonIgnore
	public String getSUMMARY() {
		return SUMMARY;
	}
	@JsonIgnore
	public void setSUMMARY(String ed) {
		SUMMARY = ed;
	}
	
	@JsonIgnore
	@JsonFormat(shape=JsonFormat.Shape.ARRAY)
	private List<String> summaryList;
	
	@JsonIgnore	
	public List<String> getSummary() {
		return summaryList;
	}
	@JsonIgnore
	public void setSummary(List<String> summary) {
		this.summaryList = summary;
	}
}
