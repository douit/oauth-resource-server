package com.rkc.zds.resource.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.SkillEntity;
import com.rkc.zds.resource.repository.SkillRepository;
import com.rkc.zds.resource.entity.SkillEntity;

import com.rkc.zds.resource.service.SkillService;
import com.rkc.zds.resource.util.SearchCriteria;

@Service
public class SkillServiceImpl implements SkillService {
	
	@Autowired
	private SkillRepository skillRepo;
	
	@Override
	public List<SkillEntity> findAll() {
		return skillRepo.findAll();
	}

	@Override
	public Page<SkillEntity> findSkills(Pageable pageable) {
		Page<SkillEntity> page = skillRepo.findAll(pageable);

		return page;
	}

	@Override
	public Page<SkillEntity> searchSkills(Pageable pageable, Specification<SkillEntity> spec) {
		return skillRepo.findAll(spec, pageable);

	}
/*
	@Override
	public Page<SkillEntity> searchSkillsBySkillName(Pageable pageable,String skillName) {
		return skillRepo.findBySkillName(pageable,skillName);

	}
*/
	@Override
	public Page<SkillEntity> searchSkills(Pageable pageable, List<SearchCriteria> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SkillEntity getSkill(int id) {
		Optional<SkillEntity> skill = skillRepo.findById(id);
		if(skill.isPresent())
			return skill.get();
		else
			return null;
	}

	@Override
	public SkillEntity saveSkill(SkillEntity skill) {
		return skillRepo.save(skill);
	}

	@Override
	public void updateSkill(SkillEntity skill) {
		skillRepo.saveAndFlush(skill);
		
	}

	@Override
	public void deleteSkill(int id) {
		skillRepo.deleteById(id);
		
	}

	@Override
	public Page<SkillEntity> searchSkillsBySkillName(String skillName) {
		// TODO Auto-generated method stub
		return null;
	}

}
