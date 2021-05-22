package com.rkc.zds.resource.service;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.rkc.zds.resource.entity.SkillEntity;
import com.rkc.zds.resource.entity.SkillEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.util.SearchCriteria;

public interface SkillService {

	public EntityManagerFactory getEntityManagerFactory();
	
    List<SkillEntity> findAll();
    
    Page<SkillEntity> findSkills(Pageable pageable);
    
	Page<SkillEntity> searchSkills(Pageable pageable, Specification<SkillEntity> spec);

	//Page<SkillEntity> searchSkills(Pageable pageable, String search) throws Exception;
    
    Page<SkillEntity> searchSkillsBySkillName(String skillTitle);

    Page<SkillEntity> searchSkills(Pageable pageable, List<SearchCriteria> params);
    
    SkillEntity getSkill(int id);  
    
    public SkillEntity saveSkill(SkillEntity skill);
      
    public void updateSkill(SkillEntity skill);
 
	void deleteSkill(int id);
	
}
