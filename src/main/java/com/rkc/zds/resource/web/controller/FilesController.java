package com.rkc.zds.resource.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkc.zds.resource.entity.FileEntity;
import com.rkc.zds.resource.entity.FileEntity;
import com.rkc.zds.resource.entity.UserEntity;

import com.rkc.zds.resource.rsql.CustomRsqlVisitor;
import com.rkc.zds.resource.service.FileService;
import com.rkc.zds.resource.service.FileParserService;
import com.rkc.zds.resource.service.FileService;
import com.rkc.zds.resource.service.UserService;
import com.rkc.zds.resource.service.impl.FileStorageServiceImpl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

import com.rkc.zds.resource.model.IgnoreSetValueMixIn;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api")
public class FilesController {
	
	@Autowired
	FileService fileService;
	
	@Autowired
	private FileParserService parserService;

	@Autowired
	UserService userService;
	
    @Autowired
    private FileStorageServiceImpl fileStorageService;
	
	@RequestMapping(value = "/files", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<FileEntity>> findAllFiles(Pageable pageable, HttpServletRequest req) {
		Page<FileEntity> page = fileService.findFiles(pageable);
		ResponseEntity<Page<FileEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FileEntity> getFile(@PathVariable Long id, HttpServletRequest req) {
		
		FileEntity file = fileService.getFile(id);
		
		return new ResponseEntity<>(file, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/files/download/{id}",method = RequestMethod.GET, produces="application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	public @ResponseBody byte[] getDoc(@PathVariable Long id, HttpServletRequest req) throws IOException {
		
		FileEntity fileEntity = fileService.getFile(id);
		
		int userId = fileEntity.getUserId();
		
		String userIdString=String.valueOf(userId); 
		
		String firstName = fileEntity.getFirstName();
		
		String lastName = fileEntity.getLastName();
		
		String shortFileName = fileEntity.getShortFileName();
		
		String path = "/_/data/files/storage/";
		
		String inputFileName = userIdString + "_"
				+ firstName + "_"
				+ lastName + "_"
				+ shortFileName;
				
		
		File file = new File(path + inputFileName);
		
	    //File file = userService.getDocx();
		
	    FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    byte[] doc = IOUtils.toByteArray(fis);
	    
	    return doc;
	}
	
	@RequestMapping(value = "/file/file", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void createFile(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		FileEntity fileDTO = new FileEntity();
		try {
			fileDTO = mapper.readValue(jsonString, FileEntity.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			fileService.saveFile(fileDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/file/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteFile(@PathVariable Long id) {
		fileService.deleteFile(id);
		return Long.toString(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/file/file", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateFile(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		FileEntity file = new FileEntity();
		try {
			file = mapper.readValue(jsonString, FileEntity.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fileService.updateFile(file);

	}
	
/*
	@RequestMapping(value = "/files/search/text", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<FileEntity>> findAllByText(Pageable pageable,
			@RequestParam(value = "search") String search) {

		Page<FileEntity> page = null;
		try {
			page = fileService.searchFiles(pageable, search);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(page, HttpStatus.OK);
	}
*/
	
	@RequestMapping(value = "/files/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<FileEntity>> findAllByRsql(Pageable pageable,
			@RequestParam(value = "search") String search) {
		Node rootNode = new RSQLParser().parse(search);
		Specification<FileEntity> spec = rootNode.accept(new CustomRsqlVisitor<FileEntity>());
		// return dao.findAll(spec);
		Page<FileEntity> page = fileService.searchFiles(pageable, spec);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}
	
    @PostMapping("/files/uploadFile")
    public ResponseWrapper uploadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file) {
      	
    	KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
		KeycloakPrincipal<?> principal = (KeycloakPrincipal) token.getPrincipal();
		KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
		AccessToken accessToken = session.getToken();
		String userName = accessToken.getPreferredUsername();
		
		UserEntity user = userService.findByUserName(userName);
		
    	ResponseWrapper responseWrapper = null;
		try {
			responseWrapper = parserService.parseFile(request,file);
		} catch (Exception ex) {
			responseWrapper = new ResponseWrapper();
			responseWrapper.setMessage(ex.getMessage());
			responseWrapper.setStatus(500);
			ex.printStackTrace();
		}
		
		FileEntity fileDTO = new FileEntity();
		fileDTO.setUserId(user.getId());
		fileDTO.setFirstName(user.getFirstName());
		fileDTO.setLastName(user.getLastName());
		fileDTO.setJsonFile((String) responseWrapper.getJsonData());
		fileDTO.setHtmlFile((String) responseWrapper.getHtmlData());
		fileDTO.setOriginalFileName(responseWrapper.getOriginalFileName());
		fileDTO.setShortFileName(responseWrapper.getShortFileName());
		fileDTO.setHtmlFileName(responseWrapper.getHtmlFileName());
		fileDTO.setTextFile(responseWrapper.getTextData());
		fileDTO.setPdfFileName(responseWrapper.getPdfFileName());
		
		try {
			FileEntity savedFile = fileService.saveFile(fileDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseWrapper;
	}
}
