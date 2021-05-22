package com.rkc.zds.resource.service;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.UserContactEntity;

public interface UserContactsService {
	
	public EntityManagerFactory getEntityManagerFactory();
	
    Page<UserContactEntity> findUserContacts(Pageable pageable, int userId);

    Page<ContactEntity> findFilteredContacts(Pageable pageable, int userId);    

    List<UserContactEntity> findAllUserContacts(int userId);
    
    List<UserContactEntity> getAllUserContacts();

    public void addUserContact(UserContactEntity userContact);
    
    public void saveUserContact(UserContactEntity userContact);
    
    public UserContactEntity findUserContact(int id);
    
	void deleteUserContact(int id);
}
