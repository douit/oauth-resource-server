package com.rkc.zds.resource.service.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.repository.PhoneRepository;
import com.rkc.zds.resource.service.PhoneService;

@Service
public class PhoneServiceImpl implements PhoneService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	@Autowired
	private PhoneRepository phoneRepo;

	@Override
	public Page<PhoneEntity> findPhones(Pageable pageable, int contactId) {

		Page<PhoneEntity> page = phoneRepo.findByContactId(pageable, contactId);

		return page;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void savePhone(PhoneEntity phone) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			phoneRepo.save(phone);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updatePhone(PhoneEntity phone) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			phoneRepo.saveAndFlush(phone);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Override
	public void deletePhone(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			phoneRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public PhoneEntity getPhone(int id) {

		Optional<PhoneEntity> phone = phoneRepo.findById(id);
		if (phone.isPresent())
			return phone.get();
		else
			return null;
	}

}
