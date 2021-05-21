package com.rkc.zds.resource.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

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
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.entity.SkillEntity;
import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.EMailSend;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.SkillService;
import com.rkc.zds.resource.service.PcmEMailService;
import com.rkc.zds.resource.service.PhoneService;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api/skill")
public class SkillsController {

	@Autowired
	SkillService skillService;

	@Autowired
	ContactService contactService;

	@Autowired
	PcmEMailService emailService;

	@Autowired
	PhoneService phoneService;

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<SkillEntity>> findAllSkills(Pageable pageable, HttpServletRequest req) {
		Page<SkillEntity> page = skillService.findSkills(pageable);
		ResponseEntity<Page<SkillEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	/*
	 * @RequestMapping(value = "/skills/{id}", method = RequestMethod.GET, produces =
	 * MediaType.APPLICATION_JSON_VALUE) public ResponseEntity<SkillEntity>
	 * getSkill(@PathVariable int id, HttpServletRequest req) { SkillEntity skill =
	 * skillService.getSkill(id);
	 * 
	 * ObjectMapper mapper = new ObjectMapper();
	 * //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
	 * true); SkillData tempData = new SkillData(); try { tempData =
	 * mapper.readValue(skill.getJsonSkill(), SkillData.class); } catch
	 * (JsonParseException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (JsonMappingException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * skill.setSkillData(tempData); try { skillService.saveSkill(skill); } catch (Exception
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * return new ResponseEntity<>(skill, HttpStatus.OK); }
	 * 
	 */
	// skill/download/${id}`;

	/*
	 * @RequestMapping(value = "/skills/download/{id}",method = RequestMethod.GET,
	 * produces=
	 * "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	 * public @ResponseBody byte[] getDoc(@PathVariable int id, HttpServletRequest
	 * req) throws IOException {
	 * 
	 * SkillEntity skill = skillService.getSkill(id);
	 * 
	 * int userId = skill.getUserId();
	 * 
	 * String userIdString=String.valueOf(userId);
	 * 
	 * String firstName = skill.getFirstName();
	 * 
	 * String lastName = skill.getLastName();
	 * 
	 * String shortFileName = skill.getShortFileName();
	 * 
	 * String path = "/_/data/skills/storage/";
	 * 
	 * String inputFileName = userIdString + "_" + firstName + "_" + lastName + "_"
	 * + shortFileName;
	 * 
	 * 
	 * File file = new File(path + inputFileName);
	 * 
	 * //File file = userService.getDocx();
	 * 
	 * FileInputStream fis = null; try { fis = new FileInputStream(file); } catch
	 * (FileNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * byte[] doc = IOUtils.toByteArray(fis);
	 * 
	 * return doc; }
	 */

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SkillEntity> getSkill(@PathVariable int id, HttpServletRequest req) {
		SkillEntity skill = skillService.getSkill(id);
		return new ResponseEntity<>(skill, HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<SkillEntity>> findAllByRsql(Pageable pageable,
			@RequestParam(value = "search") String search) {
		Node rootNode = new RSQLParser().parse(search);
		Specification<SkillEntity> spec = rootNode.accept(new CustomRsqlVisitor<SkillEntity>());
		// return dao.findAll(spec);
		Page<SkillEntity> page = skillService.searchSkills(pageable, spec);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public SkillEntity createSkill(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		SkillEntity skillDTO = new SkillEntity();
		try {
			skillDTO = mapper.readValue(jsonString, SkillEntity.class);
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

		SkillEntity dto = skillService.saveSkill(skillDTO);
		// this.contactId = dto.getId();
		// post();
		return dto;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteSkill(@PathVariable int id) {
		skillService.deleteSkill(id);
		return Integer.toString(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateSkill(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		SkillEntity skillDTO = new SkillEntity();
		try {
			skillDTO = mapper.readValue(jsonString, SkillEntity.class);
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

		skillService.updateSkill(skillDTO);

	}




}
