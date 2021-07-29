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

import com.rkc.zds.resource.entity.WebsiteEntity;
import com.rkc.zds.resource.repository.WebsiteRepository;
import com.rkc.zds.resource.service.WebsiteService;

@Service
public class WebsiteServiceImpl implements WebsiteService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	@Autowired
	private WebsiteRepository websiteRepo;

	@Override
	public Page<WebsiteEntity> findWebsites(Pageable pageable, int contactId) {

		Page<WebsiteEntity> page = websiteRepo.findByContactId(pageable, contactId);

		return page;
	}

	@Override
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void saveWebsite(WebsiteEntity website) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			websiteRepo.save(website);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateWebsite(WebsiteEntity website) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			websiteRepo.saveAndFlush(website);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Override
	public void deleteWebsite(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			websiteRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public WebsiteEntity getWebsite(int id) {

		Optional<WebsiteEntity> website = websiteRepo.findById(id);
		if (website.isPresent())
			return website.get();
		else
			return null;
	}

}
