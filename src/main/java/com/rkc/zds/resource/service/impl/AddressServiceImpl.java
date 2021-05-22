package com.rkc.zds.resource.service.impl;

import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.AddressEntity;

import com.rkc.zds.resource.repository.AddressRepository;
import com.rkc.zds.resource.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Autowired
	private AddressRepository addressRepo;
	
	@Override
	public List<AddressEntity> findAllByContactId(int contactId) {
		return addressRepo.findByContactId(contactId);
	}
	
	@Override
	public Page<AddressEntity> findAddress(Pageable pageable, int contactId) {

		Page<AddressEntity> page = addressRepo.findByContactId(pageable, contactId);

		return page;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void saveAddress(AddressEntity email) {

		addressRepo.save(email);
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateAddress(AddressEntity email) {

		addressRepo.saveAndFlush(email);

	}

	@Override
	public void deleteAddress(int id) {

		addressRepo.deleteById(id);

	}

	@Override
	public AddressEntity getAddress(int id) {

		Optional<AddressEntity> email = addressRepo.findById(id);
		if (email.isPresent())
			return email.get();
		else
			return null;
	}
}
