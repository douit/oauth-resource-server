package com.rkc.zds.resource.service;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.web.controller.ResponseWrapper;

public interface ResumeParserService {
	
	public EntityManagerFactory getEntityManagerFactory();

	ResponseWrapper parseResume(UserEntity user, HttpServletRequest request, MultipartFile file) throws Exception;

}
