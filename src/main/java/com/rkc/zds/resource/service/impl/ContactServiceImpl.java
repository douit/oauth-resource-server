package com.rkc.zds.resource.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.AddressEntity;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.entity.GroupMemberEntity;
import com.rkc.zds.resource.entity.PhoneEntity;
import com.rkc.zds.resource.entity.WebsiteEntity;
import com.rkc.zds.resource.repository.AddressRepository;
import com.rkc.zds.resource.repository.ContactRepository;
import com.rkc.zds.resource.repository.EMailRepository;
import com.rkc.zds.resource.repository.GroupMemberRepository;
import com.rkc.zds.resource.repository.PhoneRepository;
import com.rkc.zds.resource.repository.WebsiteRepository;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.util.SearchCriteria;

@Service
public class ContactServiceImpl implements ContactService {
	private static final int PAGE_SIZE = 50;

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	@Autowired
	private ContactRepository contactRepo;

	@Autowired
	private GroupMemberRepository groupMemberRepo;

	@Autowired
	private EMailRepository eMailRepo;

	@Autowired
	private PhoneRepository phoneRepo;

	@Autowired
	private AddressRepository addressRepo;
	
	@Autowired
	private WebsiteRepository websiteRepo;

	@Override
	public List<ContactEntity> findAll() {
		return contactRepo.findAll();
	}

	@Override
	public Page<ContactEntity> findContacts(Pageable pageable) {

		return contactRepo.findAll(pageable);
	}

	@Override
	public Page<ContactEntity> findFilteredContacts(Pageable pageable, int groupId) {

		List<ContactEntity> contacts = contactRepo.findAll();

		List<GroupMemberEntity> memberList = groupMemberRepo.findByGroupId(groupId);

		List<ContactEntity> testList = new ArrayList<ContactEntity>();

		List<ContactEntity> filteredList = new ArrayList<ContactEntity>();

		// build member list of Contacts
		Optional<ContactEntity> contact;
		for (GroupMemberEntity element : memberList) {
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
	public ContactEntity getContact(int id) {

		Optional<ContactEntity> contact = contactRepo.findById(id);
		if (contact.isPresent())
			return contact.get();
		else
			return null;
	}

	@Override
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateContact(ContactEntity contact) {
		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();
			contactRepo.saveAndFlush(contact);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public Page<ContactEntity> searchContactsByFullName(String fullName) {

		final PageRequest pageRequest = PageRequest.of(0, 10, sortByNameASC());

		return contactRepo.findByFullNameIgnoreCaseLike(pageRequest, "%" + fullName + "%");
	}
	
	@Override
	public Page<ContactEntity> searchContactsByLastName(String lastName) {

		final PageRequest pageRequest = PageRequest.of(0, 10, sortByNameASC());

		return contactRepo.findByLastNameIgnoreCaseLike(pageRequest, "%" + lastName + "%");
	}

	@Override
	public List<ContactEntity> searchContactsByLastNameAndFirstName(String lastName, String firstName) {

		// final PageRequest pageRequest = PageRequest.of(0, 10, sortByNameASC());

		return contactRepo.findByLastNameIgnoreCaseLikeAndFirstNameIgnoreCaseLike("%" + lastName + "%",
				"%" + firstName + "%");
	}

	@Override
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ContactEntity saveContact(ContactEntity contact) {
		
		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		ContactEntity result = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			result = contactRepo.save(contact);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteContact(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			// delete the addresses
			List<AddressEntity> addressList = addressRepo.findByContactId(id);
			for (AddressEntity email : addressList) {
				addressRepo.delete(email);
			}

			// delete the emails
			List<EMailEntity> emailList = eMailRepo.findByContactId(id);
			for (EMailEntity email : emailList) {
				eMailRepo.delete(email);
			}

			// delete the phones
			List<PhoneEntity> phoneList = phoneRepo.findByContactId(id);
			for (PhoneEntity phone : phoneList) {
				phoneRepo.delete(phone);
			}
			
			// delete the websites
			List<WebsiteEntity> websiteList = websiteRepo.findByContactId(id);
			for (WebsiteEntity website : websiteList) {
				websiteRepo.delete(website);
			}

			// delete the contact
			contactRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private Sort sortByNameASC() {
		return Sort.by(Sort.Direction.ASC, "lastName");
	}

	@Override
	public Page<ContactEntity> searchContacts(Pageable pageable, List<SearchCriteria> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<ContactEntity> searchContacts(Pageable pageable, Specification<ContactEntity> spec) {
		return contactRepo.findAll(spec, pageable);
	}

}
