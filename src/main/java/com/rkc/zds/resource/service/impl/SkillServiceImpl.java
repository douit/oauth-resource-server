package com.rkc.zds.resource.service.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

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
	 * @Override public Page<SkillEntity> searchSkillsBySkillName(Pageable
	 * pageable,String skillName) { return
	 * skillRepo.findBySkillName(pageable,skillName);
	 * 
	 * }
	 */
	@Override
	public Page<SkillEntity> searchSkills(Pageable pageable, List<SearchCriteria> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SkillEntity getSkill(int id) {
		Optional<SkillEntity> skill = skillRepo.findById(id);
		if (skill.isPresent())
			return skill.get();
		else
			return null;
	}

	@Override
	public SkillEntity saveSkill(SkillEntity skill) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		SkillEntity result = null;
		try {
			tx = em.getTransaction();
			tx.begin();

			result = skillRepo.save(skill);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	@Override
	public void updateSkill(SkillEntity skill) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			skillRepo.saveAndFlush(skill);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void deleteSkill(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			skillRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public Page<SkillEntity> searchSkillsBySkillName(String skillName) {
		// TODO Auto-generated method stub
		return null;
	}

}
