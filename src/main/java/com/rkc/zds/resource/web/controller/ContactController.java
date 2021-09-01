package com.rkc.zds.resource.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.opencsv.CSVReader;
import com.rkc.zds.resource.dto.UserContactElementDTO;
import com.rkc.zds.resource.entity.AddressEntity;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.entity.WebsiteEntity;

import com.rkc.zds.resource.model.EMailSend;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;

import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.PcmEMailService;
import com.rkc.zds.resource.service.PhoneService;
import com.rkc.zds.resource.service.UserService;
import com.rkc.zds.resource.service.AddressService;
import com.rkc.zds.resource.service.WebsiteService;
import com.rkc.zds.resource.service.impl.FileStorageServiceImpl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api/contact")
// @PreAuthorize("isAuthenticated()") 
public class ContactController {

	private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

	private static final String DEFAULT_PAGE_DISPLAYED_TO_USER = "0";

	private int contactId = 0;

	@Autowired
	private FileStorageServiceImpl fileStorageService;

	@Autowired
	UserService userService;

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
/*
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
*/
	@PostMapping(value = "/email/send", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public void sendEmail(@RequestPart("jsonString") String jsonString, @RequestPart("file") List<MultipartFile> file) {

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
		
		emailSend.setEmailFiles(file);

		emailService.sendEMail(emailSend);

	}

	@PostMapping(value = "/email/system/send", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public void sendSystemEmailToContacts(@RequestPart("jsonString") String jsonString, @RequestPart("file") List<MultipartFile> file) {
	
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
		
		emailSend.setEmailFiles(file);

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

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
		// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// This is the format Angular needs
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String dateString = dateFormat.format(stamp);

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

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
		// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// This is the format Angular needs
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String dateString = dateFormat.format(stamp);

		// contactDTO.setCreatedAt(dateString);
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

	@RequestMapping(value = "/updateContactsDates", method = RequestMethod.GET)
	public void updateContactsDates() {

		String myDate = "2014/01/01 12:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(myDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long millis = date.getTime();
		Timestamp stamp = new Timestamp(millis);

		// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
		// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// This is the format Angular needs
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String dateString = dateFormat.format(stamp);

		List<ContactEntity> list = contactService.findAll();

		for (ContactEntity contactDTO : list) {
			contactDTO.setCreatedAt(dateString);
			contactDTO.setUpdatedAt(dateString);

			ContactEntity contact = contactService.saveContact(contactDTO);

		}
		System.out.println("updateContactsDates:Done");
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/updateFullNameRunner", method = RequestMethod.GET)
	public void updateFullName() {

		PopulateFullNameRunner populateRunner = new PopulateFullNameRunner(this.contactService);

		Thread thread1 = new Thread(populateRunner);
		thread1.start();

	}

	@PostMapping("/updateMyContactsFromFLinkedIn")
	public void updateMyContactsFromFLinkedIn(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("file") MultipartFile file) {

		KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
		KeycloakPrincipal<?> principal = (KeycloakPrincipal) token.getPrincipal();
		KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
		AccessToken accessToken = session.getToken();
		String userName = accessToken.getPreferredUsername();

		UserEntity user = userService.findByUserName(userName);

		String fileName = fileStorageService.storeFile(request, file);

		String filePrefix = "/_/servers/www/www.zdslogic.com/html/data/files/uploads/";

		String fullFileName = filePrefix + fileName;

		int rowCount = 0;

		try {

			BufferedReader reader = new BufferedReader(new FileReader(fullFileName), 65536);

			String lineIn = null;

			String[] lineInArray;

			String id = "";
			String fullName = "";
			String firstName = "";
			String lastName = "";
			String email = "";
			String phoneNumber = "";
			String linkedIn = "";
			String title = "";
			String company = "";
			String companyPhone = "";
			String website1 = "";
			String website2 = "";
			String facebook = "";
			String twitter = "";
			String website3 = "";
			String country = "";
			String city = "";
			boolean inBounds;
			int index = 0;

			String test = "";
			boolean isAscii = false;

			// skip over first n line(s)
			// lineInArray = reader.readNext();
			// readWriteFileAccess.seek(lastKnownPosition);
			// lineIn = readWriteFileAccess.readLine();
			for (int i = 0; i < 4; i++) {
				lineIn = reader.readLine();
			}

			String originalStr = "";
			while ((lineIn = reader.readLine()) != null) {

				// lineInArray = lineIn.split(",");
				lineInArray = lineIn.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

				if (lineInArray.length == 6) {
					if (0 < lineInArray.length) {
						firstName = lineInArray[0];
						originalStr = firstName;
						if (originalStr.startsWith("\"")) {
							originalStr = originalStr.substring(1, originalStr.length());
						}
						if (originalStr.endsWith("\"")) {
							originalStr = originalStr.substring(0, originalStr.length() - 1);
						}
						if (firstName.contains("null"))
							firstName = "";
						else
							firstName = originalStr.trim();
					}

					if (1 < lineInArray.length) {
						lastName = lineInArray[1];
						originalStr = lastName;
						if (originalStr.startsWith("\"")) {
							originalStr = originalStr.substring(1, originalStr.length());
						}
						if (originalStr.endsWith("\"")) {
							originalStr = originalStr.substring(0, originalStr.length() - 1);
						}
						if (lastName.contains("null"))
							lastName = "";
						else
							lastName = originalStr.trim();
					}

					if (2 < lineInArray.length) {
						email = lineInArray[2];
						originalStr = email;
						if (originalStr.startsWith("\"")) {
							originalStr = originalStr.substring(1, originalStr.length());
						}
						if (originalStr.endsWith("\"")) {
							originalStr = originalStr.substring(0, originalStr.length() - 1);
						}
						if (email.contains("null"))
							email = "";
						else
							email = originalStr.trim();
					}

					if (3 < lineInArray.length) {
						company = lineInArray[3];
						originalStr = company;
						if (originalStr.startsWith("\"")) {
							originalStr = originalStr.substring(1, originalStr.length());
						}
						if (originalStr.endsWith("\"")) {
							originalStr = originalStr.substring(0, originalStr.length() - 1);
						}
						if (company.contains("null"))
							company = "";
						else
							company = originalStr.trim();
					}

					if (4 < lineInArray.length) {
						title = lineInArray[4];
						originalStr = title;
						if (originalStr.startsWith("\"")) {
							originalStr = originalStr.substring(1, originalStr.length());
						}
						if (originalStr.endsWith("\"")) {
							originalStr = originalStr.substring(0, originalStr.length() - 1);
						}
						if (title.contains("null"))
							title = "";
						else
							title = originalStr.trim();
					}

					fullName = firstName + " " + lastName;

					logger.info(id + "," + fullName + "," + email + "," + phoneNumber + "," + linkedIn + "," + title
							+ "," + company + "," + companyPhone + "," + website1 + "," + website2 + "," + facebook
							+ "," + twitter + "," + website3 + "," + country + "," + city);

					test = firstName + ", " + lastName;

					isAscii = CharMatcher.ascii().matchesAllOf(test);

					if (lastName.equalsIgnoreCase("Forand")) {
						System.out.println(firstName + " " + lastName);
					}

					if (isAscii) {
						// System.out.println("firstname:" + firstName + " lastName:" + lastName);
						System.out.println(id + ", " + firstName + ", " + lastName);

						List<ContactEntity> list = contactService.searchContactsByLastNameAndFirstName(lastName,
								firstName);

						if (list.size() == 0) {
							ContactEntity contactDTO = new ContactEntity();
							contactDTO.setCompany(company);
							contactDTO.setEnabled(1);

							contactDTO.setFacebook(facebook);
							contactDTO.setFirstName(firstName);
							contactDTO.setFullName(firstName + " " + lastName);
							// contactDTO.setId(null)
							contactDTO.setImageURL("https://www.zdslogic.com/download/user.png");
							contactDTO.setLastName(lastName);
							contactDTO.setLinkedin(linkedIn);
							contactDTO.setNotes("");
							contactDTO.setOwnerId(user.getId());
							contactDTO.setPresenceImageUrl("");
							contactDTO.setSkype("");
							contactDTO.setTitle(title);
							contactDTO.setTwitter(twitter);
							contactDTO.setUserId(0);

							Timestamp stamp = new Timestamp(new Date().getTime());

							// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
							// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
							// This is the format Angular needs
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
							String dateString = dateFormat.format(stamp);

							contactDTO.setCreatedAt(dateString);
							contactDTO.setUpdatedAt(dateString);

							ContactEntity contact = contactService.saveContact(contactDTO);

						} else if (list.size() == 1) {

							ContactEntity contact = list.get(0);

							contact.setCompany(company);
							contact.setTitle(title);

							Timestamp stamp = new Timestamp(new Date().getTime());

							// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm.ss");
							// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
							// This is the format Angular needs
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
							String dateString = dateFormat.format(stamp);

							// contact.setCreatedAt(dateString);
							contact.setUpdatedAt(dateString);

							contactService.updateContact(contact);

						}

						rowCount++;
					}
				} else {
					logger.info(lineIn);
				}
			}

		} catch (IndexOutOfBoundsException e) {
			// Output expected IndexOutOfBoundsExceptions.
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("rowCount:" + rowCount);
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/updateContactsFromLinkedIn", method = RequestMethod.GET)
	public void updateContactsFromLinkedIn() {

		PopulateLinkedinDataRunner populateRunner = new PopulateLinkedinDataRunner(this.contactService,

				this.emailService,

				this.phoneService,

				this.addressService,

				this.websiteService);

		Thread thread5 = new Thread(populateRunner);
		thread5.start();

	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/updateFullName", method = RequestMethod.GET)
	public void setFullName() {

		List<ContactEntity> contacts = contactService.findAll();
		for (ContactEntity entity : contacts) {

			entity.setFullName(entity.getFirstName() + " " + entity.getLastName());

			ContactEntity dto = contactService.saveContact(entity);
		}

		System.out.println("setFullName: Done");
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/updateOwnerId", method = RequestMethod.GET)
	public void updateOwnerId() {

		List<ContactEntity> contacts = contactService.findAll();
		for (ContactEntity entity : contacts) {

			entity.setOwnerId(41);

			ContactEntity dto = contactService.saveContact(entity);
		}

		System.out.println("setFullName: Done");
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
					contactDTO.setFullName(name);
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
