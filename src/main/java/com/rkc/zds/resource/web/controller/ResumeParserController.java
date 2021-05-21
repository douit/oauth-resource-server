package com.rkc.zds.resource.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkc.zds.resource.entity.ResumeEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.ResumeData;
import com.rkc.zds.resource.service.ResumeParserService;
import com.rkc.zds.resource.service.ResumeService;
import com.rkc.zds.resource.service.UserService;

@CrossOrigin(origins = {"http://localhost:8089", "http://localhost:4200"})
@RestController
@RequestMapping(value = "/api")
public class ResumeParserController {

	@Autowired
	private ResumeParserService parserService;
	
	@Autowired
	ResumeService resumeService;

	@Autowired
	UserService userService;
	
    @PostMapping("/uploadResume")
    public ResponseWrapper uploadResume(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file) {
      	
    	KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
		KeycloakPrincipal<?> principal = (KeycloakPrincipal) token.getPrincipal();
		KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
		AccessToken accessToken = session.getToken();
		String userName = accessToken.getPreferredUsername();
		
		UserEntity user = userService.findByUserName(userName);
		
    	ResponseWrapper responseWrapper = null;
		try {
			responseWrapper = parserService.parseResume(request,file);
		} catch (Exception ex) {
			responseWrapper = new ResponseWrapper();
			responseWrapper.setMessage(ex.getMessage());
			responseWrapper.setStatus(500);
			ex.printStackTrace();
		}
		
		ResumeEntity resumeDTO = new ResumeEntity();
		resumeDTO.setUserId(user.getId());
		resumeDTO.setFirstName(user.getFirstName());
		resumeDTO.setLastName(user.getLastName());
		resumeDTO.setJsonResume((String) responseWrapper.getJsonData());
		resumeDTO.setHtmlResume((String) responseWrapper.getHtmlData());
		resumeDTO.setOriginalFileName(responseWrapper.getOriginalFileName());
		resumeDTO.setShortFileName(responseWrapper.getShortFileName());
		resumeDTO.setHtmlFileName(responseWrapper.getHtmlFileName());
		resumeDTO.setTextResume(responseWrapper.getTextData());
		resumeDTO.setPdfFileName(responseWrapper.getPdfFileName());
		
		try {
			ResumeEntity savedResume = resumeService.saveResume(resumeDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseWrapper;
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseWrapper handleMultipartException(Exception ex) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.setJsonData("No file uploaded");
		responseWrapper.setMessage("Please upload Resume!!");
		responseWrapper.setStatus(400);
		return responseWrapper;
	}

}
