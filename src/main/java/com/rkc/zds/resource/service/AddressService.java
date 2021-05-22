package com.rkc.zds.resource.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rkc.zds.resource.entity.AddressEntity;

public interface AddressService {

	public EntityManager getEntityManager();
	
	List<AddressEntity> findAllByContactId(int contactId);
	
    Page<AddressEntity> findAddress(Pageable pageable, int contactId);
       
    AddressEntity getAddress(int id);  
   
    public void saveAddress(AddressEntity address);
        
    public void updateAddress(AddressEntity address);
      
	void deleteAddress(int id);

}
