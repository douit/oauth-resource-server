package com.rkc.zds.resource.service;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.util.SearchCriteria;

import org.springframework.context.annotation.Bean;

@Service
public interface ContactService {

    List<ContactEntity> findAll();
    
    Page<ContactEntity> findContacts(Pageable pageable);

    Page<ContactEntity> searchContacts(String name);
    
    Page<ContactEntity> searchContacts(Pageable pageable, List<SearchCriteria> params);
    
	Page<ContactEntity> searchContacts(Pageable pageable, Specification<ContactEntity> spec);

    Page<ContactEntity> findFilteredContacts(Pageable pageable, int groupId);
       
    ContactEntity getContact(int id);    
     
    public ContactEntity saveContact(ContactEntity contact);
       
    public void updateContact(ContactEntity contact);

	void deleteContact(int id);

}
