package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.ResumeEntity;
import com.rkc.zds.resource.entity.ResumeSkillEntity;

public interface ResumeSkillRepository extends JpaRepository<ResumeSkillEntity, Long>, JpaSpecificationExecutor<ResumeSkillEntity>{
  
	Page<ResumeSkillEntity> findByResumeId(Pageable pageable, Long resumeId);

	List<ResumeSkillEntity> findByResumeId(Long resumeId);
	       
}
