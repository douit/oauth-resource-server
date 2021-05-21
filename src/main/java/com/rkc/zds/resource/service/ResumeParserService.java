package com.rkc.zds.resource.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.rkc.zds.resource.web.controller.ResponseWrapper;

public interface ResumeParserService {

	ResponseWrapper parseResume(HttpServletRequest request, MultipartFile file);

}
