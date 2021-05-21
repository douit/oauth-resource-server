package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rkc.zds.resource.entity.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {
  
	Page<AddressEntity> findByContactId(Pageable pageable, int contactId);

	List<AddressEntity> findByContactId(int contactId);
	       
}
