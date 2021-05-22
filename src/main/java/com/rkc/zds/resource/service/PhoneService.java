package com.rkc.zds.resource.service;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rkc.zds.resource.entity.PhoneEntity;

public interface PhoneService {
	
	public EntityManager getEntityManager();
	
    Page<PhoneEntity> findPhones(Pageable pageable, int contactId);
    
    PhoneEntity getPhone(int id);  
    
    public void savePhone(PhoneEntity phone);
      
    public void updatePhone(PhoneEntity phone);
 
	void deletePhone(int id);
}
