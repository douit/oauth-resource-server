package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.ResumeEntity;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long>, JpaSpecificationExecutor<ResumeEntity>{
  
	Page<ResumeEntity> findByResumeId(Pageable pageable, Long resumeId);

	List<ResumeEntity> findByResumeId(Long resumeId);
	
	List<ResumeEntity> findByUserId(int userId);	
	       
}
