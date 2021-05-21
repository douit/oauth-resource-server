package com.rkc.zds.resource.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkData {
	@JsonProperty("date_start")
	private String date_start;

	@JsonProperty("date_end")
	private String date_end;
	
	@JsonProperty("jobtitle")
	private String jobtitle;
	
	@JsonProperty("text")
	private String text;	
	
	@JsonProperty("organization")
	private String organization;

	public String getDate_start() {
		return date_start;
	}

	public void setDate_start(String date_start) {
		this.date_start = date_start;
	}

	public String getDate_end() {
		return date_end;
	}

	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}

	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
}
