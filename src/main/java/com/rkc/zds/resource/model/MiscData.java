package com.rkc.zds.resource.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MiscData {
	
	@JsonProperty("MISC")	
	private String MISC;
	
	public String getMISC() {
		return MISC;
	}

	public void setMISC(String misc) {
		MISC = misc;
	}

	@JsonFormat(shape=JsonFormat.Shape.ARRAY)
	private List<String> misc;
	
	public List<String> getMisc() {
		return misc;
	}

	public void setCredibility(List<String> misc) {
		this.misc = misc;
	}
}
