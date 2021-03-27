package com.rkc.zds.resource.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.GroupMemberEntity;
import com.rkc.zds.resource.repository.ContactRepository;
import com.rkc.zds.resource.repository.GroupMemberRepository;
import com.rkc.zds.resource.service.ContactService;
import com.rkc.zds.resource.util.SearchCriteria;

@Service
public class ContactServiceImpl implements ContactService {
	private static final int PAGE_SIZE = 50;

    @PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
	private ContactRepository contactRepo;

	@Autowired
	private GroupMemberRepository groupMemberRepo;
	
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
			contact= contactRepo.findById(element.getContactId());
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
		if(size == 0) {
			size = 1;
		}
		
		PageRequest pageRequest = PageRequest.of(0, size);

		PageImpl<ContactEntity> page = new PageImpl<ContactEntity>(filteredList, pageRequest, size);

		return page;
	}

	@Override
	public ContactEntity getContact(int id) {
	
		Optional<ContactEntity> contact = contactRepo.findById(id);
		if(contact.isPresent())
			return contact.get();
		else
			return null;
	}

	@Override
	public Page<ContactEntity> searchContacts(String name) {

		final PageRequest pageRequest = PageRequest.of(0, 10, sortByNameASC());

		return contactRepo.findByLastNameIgnoreCaseLike(pageRequest, "%" + name + "%");
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ContactEntity saveContact(ContactEntity contact) {

		return contactRepo.save(contact);
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateContact(ContactEntity contact) {

		contactRepo.saveAndFlush(contact);
	}

	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteContact(int id) {

		contactRepo.deleteById(id);
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
