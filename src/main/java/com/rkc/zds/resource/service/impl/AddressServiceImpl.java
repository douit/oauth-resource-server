package com.rkc.zds.resource.service.impl;

import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.AddressEntity;

import com.rkc.zds.resource.repository.AddressRepository;
import com.rkc.zds.resource.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	@Autowired
	private AddressRepository addressRepo;

	@Override
	public List<AddressEntity> findAllByContactId(int contactId) {
		return addressRepo.findByContactId(contactId);
	}

	@Override
	public Page<AddressEntity> findAddress(Pageable pageable, int contactId) {

		Page<AddressEntity> page = addressRepo.findByContactId(pageable, contactId);

		return page;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void saveAddress(AddressEntity address) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		
		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			addressRepo.save(address);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateAddress(AddressEntity address) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		
		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			addressRepo.saveAndFlush(address);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void deleteAddress(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		
		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			addressRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Override
	public AddressEntity getAddress(int id) {

		Optional<AddressEntity> email = addressRepo.findById(id);
		if (email.isPresent())
			return email.get();
		else
			return null;
	}
}
