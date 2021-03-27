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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.model.EMailSend;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.EMailService;
import com.rkc.zds.resource.service.PhoneService;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api/contact")
public class ContactController {

	final static Logger LOG = LoggerFactory.getLogger(ContactController.class);

	private static final String DEFAULT_PAGE_DISPLAYED_TO_USER = "0";

	private int contactId = 0;

	@Autowired
	ContactService contactService;

	@Autowired
	EMailService emailService;

	@Autowired
	PhoneService phoneService;

//	@Autowired
//	private MessageSource messageSource;

	@Value("10")
	private int maxResults;

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ContactEntity>> findAllContacts(Pageable pageable, HttpServletRequest req) {
		Page<ContactEntity> page = contactService.findContacts(pageable);
		ResponseEntity<Page<ContactEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/group/{groupId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ContactEntity>> findFilteredContacts(@PathVariable int groupId, Pageable pageable,
			HttpServletRequest req) {
		Page<ContactEntity> page = contactService.findFilteredContacts(pageable, groupId);
		ResponseEntity<Page<ContactEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/email/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<EMailEntity>> findEMails(@PathVariable int contactId, Pageable pageable,
			HttpServletRequest req) {
		Page<EMailEntity> page = emailService.findEMails(pageable, contactId);
		ResponseEntity<Page<EMailEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/email/email/{emailId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EMailEntity> getEmail(@PathVariable int emailId) {
		EMailEntity email = emailService.getEMail(emailId);
		return new ResponseEntity<>(email, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/email/email", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void createEmail(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		EMailEntity emailDTO = new EMailEntity();
		try {
			emailDTO = mapper.readValue(jsonString, EMailEntity.class);
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

		emailService.saveEMail(emailDTO);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/email/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteEmail(@PathVariable int id) {
		emailService.deleteEMail(id);
		return Integer.toString(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/email/email", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateEMail(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		EMailEntity email = new EMailEntity();
		try {
			email = mapper.readValue(jsonString, EMailEntity.class);
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

		emailService.updateEMail(email);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/email/send", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void sendEmail(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		EMailSend emailSend = new EMailSend();
		try {
			emailSend = mapper.readValue(jsonString, EMailSend.class);
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

		emailService.sendEMail(emailSend);
	}

	@RequestMapping(value = "/phone/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<PhoneEntity>> findPhones(@PathVariable int contactId, Pageable pageable,
			HttpServletRequest req) {
		Page<PhoneEntity> page = phoneService.findPhones(pageable, contactId);
		ResponseEntity<Page<PhoneEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/phone/phone/{phoneId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PhoneEntity> getPhone(@PathVariable int phoneId) {
		PhoneEntity phone = phoneService.getPhone(phoneId);
		return new ResponseEntity<>(phone, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/phone/phone", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void createPhone(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		PhoneEntity phoneDTO = new PhoneEntity();
		try {
			phoneDTO = mapper.readValue(jsonString, PhoneEntity.class);
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

		phoneService.savePhone(phoneDTO);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/phone/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deletePhone(@PathVariable int id) {
		phoneService.deletePhone(id);
		return Integer.toString(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/phone/phone", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updatePhone(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		PhoneEntity phone = new PhoneEntity();
		try {
			phone = mapper.readValue(jsonString, PhoneEntity.class);
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

		phoneService.updatePhone(phone);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContactEntity> getContact(@PathVariable int id, HttpServletRequest req) {
		ContactEntity contact = contactService.getContact(id);
		return new ResponseEntity<>(contact, HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ContactEntity>> findAllByRsql(Pageable pageable,
			@RequestParam(value = "search") String search) {
		Node rootNode = new RSQLParser().parse(search);
		Specification<ContactEntity> spec = rootNode.accept(new CustomRsqlVisitor<ContactEntity>());
		// return dao.findAll(spec);
		Page<ContactEntity> page = contactService.searchContacts(pageable, spec);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public ContactEntity createContact(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		ContactEntity contactDTO = new ContactEntity();
		try {
			contactDTO = mapper.readValue(jsonString, ContactEntity.class);
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

		ContactEntity dto = contactService.saveContact(contactDTO);
		this.contactId = dto.getId();
		//post();
		return dto;
	}

	@MessageMapping("/contacts/create")
	@SendTo("/topic/contacts/created")
	public String save(String post) {
		return post;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateContact(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		ContactEntity contact = new ContactEntity();
		try {
			contact = mapper.readValue(jsonString, ContactEntity.class);
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

		contactService.updateContact(contact);

	}
	
	@MessageMapping("/contacts/update")
	@SendTo("/topic/contacts/updated")
	public String update(String post) {
		return post;
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteContact(@PathVariable int id) {
		contactService.deleteContact(id);
		return Integer.toString(id);
	}
	
	@MessageMapping("/contacts/delete")
	@SendTo("/topic/contacts/deleted")
	public String delete(String post) {
		return post;
	}
}
