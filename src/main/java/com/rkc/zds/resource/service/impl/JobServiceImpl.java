package com.rkc.zds.resource.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

//import com.rkc.zds.EmbeddedSolrJob;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.JobEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.repository.JobRepository;
import com.rkc.zds.resource.service.JobService;
import com.rkc.zds.resource.util.SearchCriteria;

@Service
public class JobServiceImpl implements JobService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	/*
	@Autowired
	@Qualifier("solr")
	private EmbeddedSolrExample solr;
	*/
	
	@Autowired
	private JobRepository jobRepo;

	@Override
	public List<JobEntity> findAll() {
		return jobRepo.findAll();
	}
	
	@Override
	public Page<JobEntity> findJobs(Pageable pageable) {

		Page<JobEntity> page = jobRepo.findAll(pageable);

		return page;
	}
/*
	@Override
	public void saveJob(JobEntity job) throws Exception {

		jobRepo.save(job);
		
		File source = new File(job.getOriginalFileName());
		
		String solrFilePath = "/_/data/jobs/storage/"
				+ job.getUserId() + "_" 				
				+ job.getFirstName() + "_" 
				+ job.getLastName() + "_" 
				+ job.getShortFileName();
		
		File dest = new File(solrFilePath);
		
		try {
			FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EmbeddedSolrJob solr = EmbeddedSolrJob.getInstance();
		
		EmbeddedSolrServer server = solr.getEmbeddedSolrServer();
		
		solr.reindexSolr();
	}
*/
	
/*
	@Override
	public Page<JobEntity> searchJobs(Pageable pageable, String search) throws Exception {
		
		EmbeddedSolrJob solr = EmbeddedSolrJob.getInstance();
		
		//EmbeddedSolrServer server = solr.getEmbeddedSolrServer();
		
		SolrDocumentList docList = solr.searchSolr(search);
		
		List<JobEntity> jobList = new ArrayList<JobEntity>();
	
		for(SolrDocument doc : docList) {

			String userId = doc.getFieldValue("userId").toString();
			userId = userId.substring(1, userId.length() - 1);
			
			String firstName = doc.getFieldValue("firstName").toString();
			firstName = firstName.substring(1, firstName.length() - 1);
			
			String lastName = doc.getFieldValue("lastName").toString();
			lastName = lastName.substring(1, lastName.length() - 1);
			
			String fileName = doc.getFieldValue("fileName").toString();
			fileName = fileName.substring(1, fileName.length() - 1);
			
			//JobEntity job = new JobEntity();

			//job.setUserId(firstName);
			//job.setFirstName(firstName);
			//job.setLastName(lastName);
			//job.setShortFileName(fileName);
			
			// List<JobEntity> tempList = findByFirstNameAndLastName(firstName,lastName);
			
			int id =Integer.parseInt(userId); 
			List<JobEntity> tempList = findByUserId(id);
						
			for(JobEntity tempJob:tempList) {
				jobList.add(tempJob);
			}
						
		}
		
		//List<JobEntity> job
		
		
		int size = jobList.size();
		if(size == 0) {
			size = 1;
		}		
		PageRequest pageRequest = PageRequest.of(0, size);

		PageImpl<JobEntity> page = new PageImpl<JobEntity>(jobList, pageRequest, size);

		return page;
	}
*/
	
	@Override
	public JobEntity getJob(int id) {
	
		Optional<JobEntity> job = jobRepo.findById(id);
		if(job.isPresent())
			return job.get();
		else
			return null;
	}

	@Override
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public JobEntity saveJob(JobEntity job) {

		return jobRepo.save(job);
		
	}
	
	@Override
	public void updateJob(JobEntity job) {

		jobRepo.saveAndFlush(job);
		
	}
	
	@Override
	public void deleteJob(int id) {

		jobRepo.deleteById(id);

	}
	
	@Override
	public Page<JobEntity> searchJobs(Pageable pageable, Specification<JobEntity> spec) {
		return jobRepo.findAll(spec, pageable);
	}

	@Override
	public List<JobEntity> findByJobTitleAndJobCompany(String jobTitle, String jobCompany) {
		return jobRepo.findByJobTitleAndJobCompany(jobTitle, jobCompany);
	}

	@Override
	public List<JobEntity> findByUserId(int userId) {
		return jobRepo.findByUserId(userId);
	}

	@Override
	public Page<JobEntity> searchJobsByJobTitle(String jobTitle) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Page<JobEntity> searchJobsByJobCompany(String jobCompany) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<JobEntity> searchJobsByJobTitleAndJobCompany(String lastName, String firsttName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<JobEntity> searchJobs(Pageable pageable, List<SearchCriteria> params) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
