package com.rkc.zds.resource.service;

import javax.persistence.EntityManagerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rkc.zds.resource.entity.WebsiteEntity;

public interface WebsiteService {
	
	public EntityManagerFactory getEntityManagerFactory();
	
    Page<WebsiteEntity> findWebsites(Pageable pageable, int contactId);
    
    WebsiteEntity getWebsite(int id);  
    
    public void saveWebsite(WebsiteEntity phone);
      
    public void updateWebsite(WebsiteEntity phone);
 
	void deleteWebsite(int id);
}
