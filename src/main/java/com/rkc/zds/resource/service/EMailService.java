package com.rkc.zds.resource.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rkc.zds.resource.entity.EMailEntity;
import com.rkc.zds.resource.model.EMailSend;

public interface EMailService {
	
	List<EMailEntity> findAllByContactId(int contactId);
	
    Page<EMailEntity> findEMails(Pageable pageable, int contactId);
       
    EMailEntity getEMail(int id);  
   
    public void saveEMail(EMailEntity email);
        
    public void updateEMail(EMailEntity email);
      
	void deleteEMail(int id);

	void sendEMail(EMailSend emailSend);
}
