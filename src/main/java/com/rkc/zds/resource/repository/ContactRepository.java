package com.rkc.zds.resource.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.ContactEntity;

public interface ContactRepository extends JpaRepository<ContactEntity, Integer>, JpaSpecificationExecutor<ContactEntity> {
  
	Page<ContactEntity> findByLastNameIgnoreCaseLike(Pageable pageable, String lastName);
	 
}
