package com.rkc.zds.resource.service;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.rkc.zds.resource.web.controller.ResponseWrapper;

public interface ResumeParserService {
	
	public EntityManagerFactory getEntityManagerFactory();

	ResponseWrapper parseResume(HttpServletRequest request, MultipartFile file);

}
