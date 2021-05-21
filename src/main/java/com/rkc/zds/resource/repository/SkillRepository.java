package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.SkillEntity;

public interface SkillRepository extends JpaRepository<SkillEntity, Integer>, JpaSpecificationExecutor<SkillEntity>{
  
	Page<SkillEntity> findBySkillId(Pageable pageable, int skillId);

	Page<SkillEntity> findBySkillName(Pageable pageable, String skillName);

	List<SkillEntity> findBySkillId(int skillId);
	
	List<SkillEntity> findBySkillName(String skillName);
	
	Page<SkillEntity> findBySkillNameIgnoreCaseLike(Pageable pageable, String skillName);
	
}
