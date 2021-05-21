package com.rkc.zds.resource.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

//"basics":{"gender":"male","phone":["401-608-2548"],"name":{"firstName":"Richard","surname":"Campion"},"email":["richard.campion@yahoo.com"]},
public class BasicsData {
	
	@JsonProperty("gender")		
	String gender;
	
	@JsonProperty("phone")	
	List<String> phone;
	
	@JsonProperty("email")
	List<String> email;

	@JsonProperty("name")
	NameData nameData;
	
	@JsonProperty("firstName")
	String firstName;

	@JsonProperty("surname")
	String surname;
	
	//@JsonProperty("address")
	//String address;
	
	@JsonFormat(shape=JsonFormat.Shape.ARRAY)
	private List<String> address;

	@JsonFormat(shape=JsonFormat.Shape.ARRAY)
	private List<String> url;
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
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

	public List<String> getPhone() {
		return phone;
	}
	public void setPhone(List<String> phone) {
		this.phone = phone;
	}
	public List<String> getEmail() {
		return email;
	}
	public void setEmail(List<String> email) {
		this.email = email;
	}	
	public NameData getNameData() {
		return nameData;
	}
	public void setNameData(NameData nameData) {
		this.nameData = nameData;
	}
	
	public List<String> getAddress() {
		return address;
	}

	public void setAddress(List<String> address) {
		this.address = address;
	}
	public List<String> getUrl() {
		return url;
	}
	public void setUrl(List<String> url) {
		this.url = url;
	}	

}
