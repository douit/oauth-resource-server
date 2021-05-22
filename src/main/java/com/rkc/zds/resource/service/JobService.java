package com.rkc.zds.resource.service;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.rkc.zds.resource.entity.JobEntity;
import com.rkc.zds.resource.entity.JobEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.util.SearchCriteria;

public interface JobService {
	
	public EntityManagerFactory getEntityManagerFactory();
	
    List<JobEntity> findAll();
    
    Page<JobEntity> findJobs(Pageable pageable);
    
	Page<JobEntity> searchJobs(Pageable pageable, Specification<JobEntity> spec);

	//Page<JobEntity> searchJobs(Pageable pageable, String search) throws Exception;
    
    Page<JobEntity> searchJobsByJobTitle(String jobTitle);

    Page<JobEntity> searchJobsByJobCompany(String jobCompany);

    List<JobEntity> searchJobsByJobTitleAndJobCompany(String lastName, String firsttName);
    
    Page<JobEntity> searchJobs(Pageable pageable, List<SearchCriteria> params);
    
    //Page<JobEntity> findFilteredJobs(Pageable pageable, int groupId);
	
    JobEntity getJob(int id);  
    
    public JobEntity saveJob(JobEntity job);
      
    public void updateJob(JobEntity job);
 
	void deleteJob(int id);
	
	List<JobEntity> findByJobTitleAndJobCompany(String jobTile, String jobCompany);
	
	List<JobEntity> findByUserId(int userId);
}
