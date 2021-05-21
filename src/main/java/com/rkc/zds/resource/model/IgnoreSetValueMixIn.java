package com.rkc.zds.resource.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public abstract class IgnoreSetValueMixIn {    
	  @JsonIgnore public abstract String getSUMMARY();
	  @JsonIgnore public abstract String setSUMMARY();
	}
