package com.rkc.zds.resource.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.UserContactEntity;
import com.rkc.zds.resource.repository.ContactRepository;
import com.rkc.zds.resource.repository.UserContactsRepository;
import com.rkc.zds.resource.service.UserContactsService;

@Service
public class UserContactsServiceImpl implements UserContactsService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	@Autowired
	private ContactRepository contactRepo;

	@Autowired
	private UserContactsRepository userContactsRepo;

	@Override
	public Page<UserContactEntity> findUserContacts(Pageable pageable, int id) {

		Page<UserContactEntity> page = userContactsRepo.findByUserId(pageable, id);

		return page;
	}

	public List<UserContactEntity> getAllUserContacts() {
		List<UserContactEntity> list = userContactsRepo.findAll();
		return list;
	}

	@Override
	public List<UserContactEntity> findAllUserContacts(int userId) {

		List<UserContactEntity> list = userContactsRepo.findByUserId(userId);

		return list;
	}

	@Override
	public UserContactEntity findUserContact(int id) {

		UserContactEntity userContact = userContactsRepo.findById(id);

		return userContact;
	}

	@Override
	public Page<ContactEntity> findFilteredContacts(Pageable pageable, int userId) {

		List<ContactEntity> contacts = contactRepo.findAll();

		List<UserContactEntity> userContactsList = userContactsRepo.findByUserId(userId);

		List<ContactEntity> testList = new ArrayList<ContactEntity>();

		List<ContactEntity> filteredList = new ArrayList<ContactEntity>();

		// build member list of Contacts
		Optional<ContactEntity> contact;
		for (UserContactEntity element : userContactsList) {
			contact = contactRepo.findById(element.getContactId());
			testList.add(contact.get());
		}

		// check member list of Contacts
		for (ContactEntity element : contacts) {
			// if the contact is in the members list, ignore it
			if (!testList.contains(element)) {
				filteredList.add(element);
			}
		}

		int size = filteredList.size();
		if (size == 0) {
			size = 1;
		}

		PageRequest pageRequest = PageRequest.of(0, size);

		PageImpl<ContactEntity> page = new PageImpl<ContactEntity>(filteredList, pageRequest, size);

		return page;
	}

	@Override
	public void addUserContact(UserContactEntity userContact) {
		// checking for duplicates
		List<UserContactEntity> list = userContactsRepo.findByUserId(userContact.getUserId());

		// return if duplicate found
		for (UserContactEntity element : list) {
			if (element.getContactId() == userContact.getContactId()) {
				return;
			}
		}

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			userContactsRepo.save(userContact);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void saveUserContact(UserContactEntity userContact) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			userContactsRepo.save(userContact);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void deleteUserContact(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			userContactsRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
