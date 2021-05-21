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
import com.rkc.zds.resource.entity.JobEntity;
import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.EMailSend;
import com.rkc.zds.resource.model.JobData;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.service.JobService;
import com.rkc.zds.resource.service.PcmEMailService;
import com.rkc.zds.resource.service.PhoneService;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(value = "/api/job")
public class JobsController {

	@Autowired
	JobService jobService;

	@Autowired
	ContactService contactService;

	@Autowired
	PcmEMailService emailService;

	@Autowired
	PhoneService phoneService;

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<JobEntity>> findAllJobs(Pageable pageable, HttpServletRequest req) {
		Page<JobEntity> page = jobService.findJobs(pageable);
		ResponseEntity<Page<JobEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	/*
	 * @RequestMapping(value = "/jobs/{id}", method = RequestMethod.GET, produces =
	 * MediaType.APPLICATION_JSON_VALUE) public ResponseEntity<JobEntity>
	 * getJob(@PathVariable int id, HttpServletRequest req) { JobEntity job =
	 * jobService.getJob(id);
	 * 
	 * ObjectMapper mapper = new ObjectMapper();
	 * //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
	 * true); JobData tempData = new JobData(); try { tempData =
	 * mapper.readValue(job.getJsonJob(), JobData.class); } catch
	 * (JsonParseException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (JsonMappingException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * job.setJobData(tempData); try { jobService.saveJob(job); } catch (Exception
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * return new ResponseEntity<>(job, HttpStatus.OK); }
	 * 
	 */
	// job/download/${id}`;

	/*
	 * @RequestMapping(value = "/jobs/download/{id}",method = RequestMethod.GET,
	 * produces=
	 * "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	 * public @ResponseBody byte[] getDoc(@PathVariable int id, HttpServletRequest
	 * req) throws IOException {
	 * 
	 * JobEntity job = jobService.getJob(id);
	 * 
	 * int userId = job.getUserId();
	 * 
	 * String userIdString=String.valueOf(userId);
	 * 
	 * String firstName = job.getFirstName();
	 * 
	 * String lastName = job.getLastName();
	 * 
	 * String shortFileName = job.getShortFileName();
	 * 
	 * String path = "/_/data/jobs/storage/";
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
	public ResponseEntity<JobEntity> getJob(@PathVariable int id, HttpServletRequest req) {
		JobEntity job = jobService.getJob(id);
		return new ResponseEntity<>(job, HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<JobEntity>> findAllByRsql(Pageable pageable,
			@RequestParam(value = "search") String search) {
		Node rootNode = new RSQLParser().parse(search);
		Specification<JobEntity> spec = rootNode.accept(new CustomRsqlVisitor<JobEntity>());
		// return dao.findAll(spec);
		Page<JobEntity> page = jobService.searchJobs(pageable, spec);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public JobEntity createJob(@RequestBody String jsonString) {

		ObjectMapper mapper = new ObjectMapper();

		JobEntity jobDTO = new JobEntity();
		try {
			jobDTO = mapper.readValue(jsonString, JobEntity.class);
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
		/*
		 * try { jobService.saveJob(jobDTO); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		Timestamp stamp = new Timestamp(new Date().getTime());
		jobDTO.setCreatedAt(stamp);
		jobDTO.setUpdatedAt(stamp);

		jobDTO.setUserId(41);
		jobDTO.setContactId(8);

		JobEntity dto = jobService.saveJob(jobDTO);
		// this.contactId = dto.getId();
		// post();
		return dto;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteJob(@PathVariable int id) {
		jobService.deleteJob(id);
		return Integer.toString(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.PUT, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public void updateJob(@RequestBody String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		JobEntity jobDTO = new JobEntity();
		try {
			jobDTO = mapper.readValue(jsonString, JobEntity.class);
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
		jobDTO.setUpdatedAt(stamp);
		jobService.updateJob(jobDTO);

	}

	/*
	 * @RequestMapping(value = "/jobs/search/text", method = RequestMethod.GET,
	 * produces = MediaType.APPLICATION_JSON_VALUE) public
	 * ResponseEntity<Page<JobEntity>> findAllByText(Pageable pageable,
	 * 
	 * @RequestParam(value = "search") String search) { //Node rootNode = new
	 * RSQLParser().parse(search); //Specification<JobEntity> spec =
	 * rootNode.accept(new CustomRsqlVisitor<JobEntity>()); // return
	 * dao.findAll(spec); Page<JobEntity> page = null; try { page =
	 * jobService.searchJobs(pageable, search); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } return new
	 * ResponseEntity<>(page, HttpStatus.OK); }
	 */
	/*
	 * @RequestMapping(value = "/jobs/search", method = RequestMethod.GET, produces
	 * = MediaType.APPLICATION_JSON_VALUE) public ResponseEntity<Page<JobEntity>>
	 * findAllByRsql(Pageable pageable,
	 * 
	 * @RequestParam(value = "search") String search) { Node rootNode = new
	 * RSQLParser().parse(search); Specification<JobEntity> spec =
	 * rootNode.accept(new CustomRsqlVisitor<JobEntity>()); // return
	 * dao.findAll(spec); Page<JobEntity> page = jobService.searchJobs(pageable,
	 * spec); return new ResponseEntity<>(page, HttpStatus.OK); }
	 */

	// Email
	@RequestMapping(value = "/email/{jobId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<EMailEntity>> findEMails(@PathVariable int jobId, Pageable pageable,
			HttpServletRequest req) {

		JobEntity jobDTO = jobService.getJob(jobId);

		int contactId = jobDTO.getContactId();

		Page<EMailEntity> page = emailService.findEMails(pageable, contactId);
		ResponseEntity<Page<EMailEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/email/email/{emailId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EMailEntity> getEmail(@PathVariable int emailId) {
		EMailEntity email = emailService.getEMail(emailId);
		return new ResponseEntity<>(email, HttpStatus.OK);
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

	// Phone
	@RequestMapping(value = "/phone/{jobId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<PhoneEntity>> findPhones(@PathVariable int jobId, Pageable pageable,
			HttpServletRequest req) {

		JobEntity jobDTO = jobService.getJob(jobId);

		int contactId = jobDTO.getContactId();

		Page<PhoneEntity> page = phoneService.findPhones(pageable, contactId);
		ResponseEntity<Page<PhoneEntity>> response = new ResponseEntity<>(page, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/phone/phone/{phoneId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PhoneEntity> getPhone(@PathVariable int phoneId) {
		PhoneEntity phone = phoneService.getPhone(phoneId);
		return new ResponseEntity<>(phone, HttpStatus.OK);
	}
}
