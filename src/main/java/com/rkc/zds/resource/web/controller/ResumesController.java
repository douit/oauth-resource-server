package com.rkc.zds.resource.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkc.zds.resource.entity.ResumeEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.ResumeData;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;
import com.rkc.zds.resource.service.ResumeService;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

import com.rkc.zds.resource.model.IgnoreSetValueMixIn;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api")
public class ResumesController {
	
	@Autowired
	ResumeService resumeService;
	
	@RequestMapping(value = "/resumes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ResumeEntity>> findAllResumes(Pageable pageable, HttpServletRequest req) {
		Page<ResumeEntity> page = resumeService.findResumes(pageable);
		ResponseEntity<Page<ResumeEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/resumes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResumeEntity> getResume(@PathVariable Long id, HttpServletRequest req) {
		ResumeEntity resume = resumeService.getResume(id);
		
		ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		
        //mapper.getSerializationConfig().addMixInAnnotations(SummaryData.class, IgnoreSetValueMixIn.class);
		
		ResumeData tempData = new ResumeData();
		
		String jsonResume = resume.getJsonResume();
		
		try {
			tempData = mapper.readValue(jsonResume, ResumeData.class);
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
		
		resume.setResumeData(tempData);
		return new ResponseEntity<>(resume, HttpStatus.OK);
	}
	//resume/download/${id}`;
	
	@RequestMapping(value = "/resumes/download/{id}",method = RequestMethod.GET, produces="application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	public @ResponseBody byte[] getDoc(@PathVariable Long id, HttpServletRequest req) throws IOException {
		
		ResumeEntity resume = resumeService.getResume(id);
		
		int userId = resume.getUserId();
		
		String userIdString=String.valueOf(userId); 
		
		String firstName = resume.getFirstName();
		
		String lastName = resume.getLastName();
		
		String shortFileName = resume.getShortFileName();
		
		String path = "/_/data/resumes/storage/";
		
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
	
	@RequestMapping(value = "/resume/resume", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void createResume(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		ResumeEntity resumeDTO = new ResumeEntity();
		try {
			resumeDTO = mapper.readValue(jsonString, ResumeEntity.class);
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
			resumeService.saveResume(resumeDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/resume/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteResume(@PathVariable Long id) {
		resumeService.deleteResume(id);
		return Long.toString(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/resume/resume", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateResume(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		ResumeEntity resume = new ResumeEntity();
		try {
			resume = mapper.readValue(jsonString, ResumeEntity.class);
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

		resumeService.updateResume(resume);

	}

	@RequestMapping(value = "/resumes/search/text", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ResumeEntity>> findAllByText(Pageable pageable,
			@RequestParam(value = "search") String search) {
		//Node rootNode = new RSQLParser().parse(search);
		//Specification<ResumeEntity> spec = rootNode.accept(new CustomRsqlVisitor<ResumeEntity>());
		// return dao.findAll(spec);
		Page<ResumeEntity> page = null;
		try {
			page = resumeService.searchResumes(pageable, search);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(page, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/resumes/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ResumeEntity>> findAllByRsql(Pageable pageable,
			@RequestParam(value = "search") String search) {
		Node rootNode = new RSQLParser().parse(search);
		Specification<ResumeEntity> spec = rootNode.accept(new CustomRsqlVisitor<ResumeEntity>());
		// return dao.findAll(spec);
		Page<ResumeEntity> page = resumeService.searchResumes(pageable, spec);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}
}
