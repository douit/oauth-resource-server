package com.rkc.zds.resource.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;
import com.rkc.zds.resource.service.GroupService;
import com.rkc.zds.resource.entity.GroupEntity;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api/group")
public class GroupController {

	final static Logger LOG = LoggerFactory.getLogger(GroupController.class);

	private static final String DEFAULT_PAGE_DISPLAYED_TO_USER = "0";

	@Autowired
	GroupService groupService;

	@Autowired
	private MessageSource messageSource;

	@Value("10")
	private int maxResults;

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<GroupEntity>> findAllGroups(Pageable pageable, HttpServletRequest req) {
		Page<GroupEntity> page = groupService.findGroups(pageable);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GroupEntity> getGroup(@PathVariable int id, HttpServletRequest req) {
		GroupEntity group = groupService.getGroup(id);
		return new ResponseEntity<>(group, HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<GroupEntity>> findAllByRsql(Pageable pageable, @RequestParam(value = "search") String search) {
	    Node rootNode = new RSQLParser().parse(search);
	    Specification<GroupEntity> spec = rootNode.accept(new CustomRsqlVisitor<GroupEntity>());
		Page<GroupEntity> page = groupService.searchGroups(pageable, spec);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void createGroup(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		GroupEntity groupDTO = new GroupEntity();
		try {
			groupDTO = mapper.readValue(jsonString, GroupEntity.class);
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

		groupService.saveGroup(groupDTO);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateGroup(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		GroupEntity group = new GroupEntity();
		try {
			group = mapper.readValue(jsonString, GroupEntity.class);
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

		groupService.updateGroup(group);

	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteGroup(@PathVariable int id) {
		groupService.deleteGroup(id);
		return Integer.toString(id);
	}	
}
