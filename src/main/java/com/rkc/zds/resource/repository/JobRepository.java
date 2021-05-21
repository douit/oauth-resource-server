package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Integer>, JpaSpecificationExecutor<JobEntity>{
  
	Page<JobEntity> findByJobId(Pageable pageable, int jobId);

	List<JobEntity> findByJobId(int jobId);
	
	List<JobEntity> findByJobTitleAndJobCompany(String jobTitle, String jobCompany);
	
	List<JobEntity> findByUserId(int userId);	

	Page<JobEntity> findByJobTitleIgnoreCaseLike(Pageable pageable, String lastName);
	
	List<JobEntity> findByJobCompanyIgnoreCaseLikeAndJobTitleIgnoreCaseLike(String lastName, String firstName);
	
}
