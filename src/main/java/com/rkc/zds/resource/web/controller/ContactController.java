package com.rkc.zds.resource.web.controller;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import com.rkc.zds.resource.dto.UserContactElementDTO;
import com.rkc.zds.resource.entity.AddressEntity;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.entity.WebsiteEntity;

import com.rkc.zds.resource.model.EMailSend;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.PcmEMailService;
import com.rkc.zds.resource.service.PhoneService;
import com.rkc.zds.resource.service.AddressService;
import com.rkc.zds.resource.service.WebsiteService;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api/contact")
// @PreAuthorize("isAuthenticated()") 
public class ContactController {

	final static Logger LOG = LoggerFactory.getLogger(ContactController.class);

	private static final String DEFAULT_PAGE_DISPLAYED_TO_USER = "0";

	private int contactId = 0;

	@Autowired
	ContactService contactService;

	@Autowired
	PcmEMailService emailService;

	@Autowired
	PhoneService phoneService;

	@Autowired
	AddressService addressService;

	@Autowired
	WebsiteService websiteService;

//	@Autowired
//	private MessageSource messageSource;

	@Value("10")
	private int maxResults;

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ContactEntity>> findAllContacts(Pageable pageable, HttpServletRequest req) {

		/*
		 * // Temp -Reset all Contacts to emabled
		 * 
		 * List<ContactEntity> list = contactService.findAll();
		 * 
		 * for (ContactEntity contact : list) {
		 * 
		 * contact.setEnabled(1);
		 * 
		 * contactService.saveContact(contact);
		 * 
		 * }
		 */
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

//EMails
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

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
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

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/email/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteEmail(@PathVariable int id) {
		emailService.deleteEMail(id);
		return Integer.toString(id);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
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

	@RequestMapping(value = "/email/system/send", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void sendSystemEmailToContacts(@RequestBody String jsonString) {

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

		List<ContactEntity> contactList = contactService.findAll();

		for (ContactEntity contact : contactList) {

			if (contact.getEnabled() == 1) {

				List<EMailEntity> emailList = emailService.findAllByContactId(contact.getId());

				for (EMailEntity email : emailList) {

					// EMailEntity temp = email.getEmail();
					emailSend.setEmailList(email.getEmail());

					emailService.sendEMail(emailSend);

				}
			}
		}
	}

	// Address
	@RequestMapping(value = "/address/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<AddressEntity>> findAddresss(@PathVariable int contactId, Pageable pageable,
			HttpServletRequest req) {
		Page<AddressEntity> page = addressService.findAddress(pageable, contactId);
		ResponseEntity<Page<AddressEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/address/address/{addressId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AddressEntity> getAddress(@PathVariable int addressId) {
		AddressEntity address = addressService.getAddress(addressId);
		return new ResponseEntity<>(address, HttpStatus.OK);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/address/address", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void createAddress(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		AddressEntity addressDTO = new AddressEntity();
		try {
			addressDTO = mapper.readValue(jsonString, AddressEntity.class);
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

		addressService.saveAddress(addressDTO);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/address/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteAddress(@PathVariable int id) {
		addressService.deleteAddress(id);
		return Integer.toString(id);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/address/address", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateAddress(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		AddressEntity address = new AddressEntity();
		try {
			address = mapper.readValue(jsonString, AddressEntity.class);
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

		addressService.updateAddress(address);

	}

//Phones
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

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
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

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/phone/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deletePhone(@PathVariable int id) {
		phoneService.deletePhone(id);
		return Integer.toString(id);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
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

	// Websites
	@RequestMapping(value = "/website/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<WebsiteEntity>> findWebsites(@PathVariable int contactId, Pageable pageable,
			HttpServletRequest req) {
		Page<WebsiteEntity> page = websiteService.findWebsites(pageable, contactId);
		ResponseEntity<Page<WebsiteEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/website/website/{websiteId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WebsiteEntity> getWebsite(@PathVariable int websiteId) {
		WebsiteEntity website = websiteService.getWebsite(websiteId);
		return new ResponseEntity<>(website, HttpStatus.OK);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/website/website", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void createWebsite(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		WebsiteEntity websiteDTO = new WebsiteEntity();
		try {
			websiteDTO = mapper.readValue(jsonString, WebsiteEntity.class);
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

		websiteService.saveWebsite(websiteDTO);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/website/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteWebsite(@PathVariable int id) {
		websiteService.deleteWebsite(id);
		return Integer.toString(id);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/website/website", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateWebsite(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		WebsiteEntity website = new WebsiteEntity();
		try {
			website = mapper.readValue(jsonString, WebsiteEntity.class);
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

		websiteService.updateWebsite(website);

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

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
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

		Timestamp stamp = new Timestamp(new Date().getTime());
		
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
		//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// This is the format Angular needs
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String dateString  = dateFormat.format(stamp);
		
		contactDTO.setCreatedAt(dateString);
		contactDTO.setUpdatedAt(dateString);
		
		ContactEntity dto = contactService.saveContact(contactDTO);
		this.contactId = dto.getId();
		// post();
		return dto;
	}

	@MessageMapping("/contacts/create")
	@SendTo("/topic/contacts/created")
	public String save(String post) {
		return post;
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateContact(@RequestBody String jsonString) {
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

		Timestamp stamp = new Timestamp(new Date().getTime());
		
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
		//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// This is the format Angular needs
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String dateString  = dateFormat.format(stamp);
		
		//contactDTO.setCreatedAt(dateString);
		contactDTO.setUpdatedAt(dateString);
		
		contactService.updateContact(contactDTO);

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

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/updateContactsFromFacebook", method = RequestMethod.GET)
	public void updateContactsFromFacebook() {

		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(
					"/_/data/facebook/facebook-richardkcampion-json/friends_and_followers/friends.json"));

			// A JSON object. Key value pairs are unordered. JSONObject supports
			// java.util.Map interface.
			JSONObject jsonObject = (JSONObject) obj;

			// A JSON array. JSONObject supports java.util.List interface.
			JSONArray companyList = (JSONArray) jsonObject.get("friends_v2");

			// An iterator over a collection. Iterator takes the place of Enumeration in the
			// Java Collections Framework.
			// Iterators differ from enumerations in two ways:
			// 1. Iterators allow the caller to remove elements from the underlying
			// collection during the iteration with well-defined semantics.
			// 2. Method names have been improved.
			Iterator<JSONObject> iterator = companyList.iterator();
			while (iterator.hasNext()) {
				// {"name":"Eric King","timestamp":1623859076}

				Object entry = iterator.next();

				// typecasting obj to JSONObject
				JSONObject jo = (JSONObject) entry;

				// getting firstName and lastName
				String name = (String) jo.get("name");

				String[] nameParts = name.split(" ");

				String firstName = nameParts[0];

				String lastName = "";

				for (int i = 1; i < nameParts.length; i++) {

					lastName += nameParts[i];
					if (i < nameParts.length - 1) {
						lastName += " ";
					}

				}

				List<ContactEntity> contacts = contactService.searchContactsByLastNameAndFirstName(lastName, firstName);

				if (contacts.size() == 0) {

					System.out.println("firstname:" + firstName + " lastName:" + lastName);

					ContactEntity contactDTO = new ContactEntity();

					contactDTO.setCompany("Undefined");
					contactDTO.setEnabled(1);
					contactDTO.setFacebook("");
					contactDTO.setFirstName(firstName);
					// contactDTO.setId(null)
					contactDTO.setImageURL("https://www.zdslogic.com/download/user.png");
					contactDTO.setLastName(lastName);
					contactDTO.setLinkedin("");
					contactDTO.setNotes("");
					contactDTO.setPresenceImageUrl("");
					contactDTO.setSkype("");
					contactDTO.setTitle("Undefined");
					contactDTO.setTwitter("");
					contactDTO.setUserId(0);

					ContactEntity dto = contactService.saveContact(contactDTO);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
