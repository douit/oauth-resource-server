package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rkc.zds.resource.entity.WebsiteEntity;

public interface WebsiteRepository extends JpaRepository<WebsiteEntity, Integer> {
  
	Page<WebsiteEntity> findByContactId(Pageable pageable, int contactId);

	List<WebsiteEntity> findByContactId(int contactId);
	       
}
