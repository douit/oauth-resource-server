package com.rkc.zds.resource.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NameData {
	
	@JsonProperty("firstName")
	String firstName;

	@JsonProperty("surname")
	String surname;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}

}
