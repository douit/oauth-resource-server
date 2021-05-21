package com.rkc.zds.resource.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.rkc.zds.resource.entity.ResumeEntity;
import com.rkc.zds.resource.entity.UserEntity;

public interface ResumeService {
	
    Page<ResumeEntity> findResumes(Pageable pageable);
    
    ResumeEntity getResume(Long id);  
    
    public ResumeEntity saveResume(ResumeEntity resume) throws Exception;
      
    public void updateResume(ResumeEntity resume);
 
	void deleteResume(Long id);
	
	Page<ResumeEntity> searchResumes(Pageable pageable, Specification<ResumeEntity> spec);

	Page<ResumeEntity> searchResumes(Pageable pageable, String search) throws Exception;
	
	List<ResumeEntity> findByUserId(int userId);
}
