package com.rkc.zds.resource.service.impl;

import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.email.SendMailUsingAuthentication;
import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.model.EMailSend;
import com.rkc.zds.resource.repository.EMailRepository;
import com.rkc.zds.resource.service.PcmEMailService;

@Service
public class PcmEMailServiceImpl implements PcmEMailService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	@Autowired
	private EMailRepository eMailRepo;

	@Override
	public List<EMailEntity> findAllByContactId(int contactId) {
		return eMailRepo.findByContactId(contactId);
	}

	@Override
	public Page<EMailEntity> findEMails(Pageable pageable, int contactId) {

		Page<EMailEntity> page = eMailRepo.findByContactId(pageable, contactId);

		return page;
	}

	@Override
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void saveEMail(EMailEntity email) {
		
		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();
			eMailRepo.save(email);
			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateEMail(EMailEntity email) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			eMailRepo.saveAndFlush(email);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void deleteEMail(int id) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			eMailRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public EMailEntity getEMail(int id) {

		Optional<EMailEntity> email = eMailRepo.findById(id);
		if (email.isPresent())
			return email.get();
		else
			return null;
	}

	@Override
	public void sendEMail(EMailSend emailSend) {
		SendMailUsingAuthentication smtpMailSender = new SendMailUsingAuthentication();
		try {
			String[] array = emailSend.getEmailList().split(",");
			smtpMailSender.postMail(array, emailSend.getEmailSubjectTxt(), emailSend.getEmailMsgTxt(),
					emailSend.getEmailFromAddress());

		} catch (MessagingException e) {
			e.printStackTrace();

		}
	}

}
