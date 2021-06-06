package com.rkc.zds.resource.service;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.util.SearchCriteria;

//@Service
public interface ContactService {

	public EntityManagerFactory getEntityManagerFactory();
	
    List<ContactEntity> findAll();
    
    Page<ContactEntity> findContacts(Pageable pageable);

    Page<ContactEntity> searchContactsByLastName(String lastName);
    
    List<ContactEntity> searchContactsByLastNameAndFirstName(String lastName, String firsttName);
    
    Page<ContactEntity> searchContacts(Pageable pageable, List<SearchCriteria> params);
    
	Page<ContactEntity> searchContacts(Pageable pageable, Specification<ContactEntity> spec);

    Page<ContactEntity> findFilteredContacts(Pageable pageable, int groupId);
       
    ContactEntity getContact(int id);    
     
    public ContactEntity saveContact(ContactEntity contact);
       
    public void updateContact(ContactEntity contact);

	void deleteContact(int id);

}
