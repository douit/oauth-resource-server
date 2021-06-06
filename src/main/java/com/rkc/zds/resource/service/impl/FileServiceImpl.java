package com.rkc.zds.resource.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.ArticleTagArticleEntity;
import com.rkc.zds.resource.entity.ArticleTagEntity;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.FileEntity;
import com.rkc.zds.resource.entity.SkillEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.repository.FileRepository;
import com.rkc.zds.resource.service.FileService;
import com.rkc.zds.resource.service.SkillService;

@Service
public class FileServiceImpl implements FileService {

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManagerFactory entityManagerFactory;

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/*
	 * @Autowired
	 * 
	 * @Qualifier("solr") private EmbeddedSolrExample solr;
	 */
	@Autowired
	private FileRepository fileRepo;


	@Override
	public Page<FileEntity> findFiles(Pageable pageable) {

		Page<FileEntity> page = fileRepo.findAll(pageable);

		return page;
	}

	@Override
	public FileEntity saveFile(FileEntity file) throws Exception {

		FileEntity fileSaved = null;

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			fileSaved = fileRepo.save(file);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

		return fileSaved;
	}
/*
	@Override
	public Page<FileEntity> searchFiles(Pageable pageable, String search) throws Exception {

		EmbeddedSolrFile solr = EmbeddedSolrFile.getInstance();

		// EmbeddedSolrServer server = solr.getEmbeddedSolrServer();

		SolrDocumentList docList = solr.searchSolr(search);

		List<FileEntity> fileList = new ArrayList<FileEntity>();

		for (SolrDocument doc : docList) {

			String userId = doc.getFieldValue("userId").toString();
			userId = userId.substring(1, userId.length() - 1);

			String firstName = doc.getFieldValue("firstName").toString();
			firstName = firstName.substring(1, firstName.length() - 1);

			String lastName = doc.getFieldValue("lastName").toString();
			lastName = lastName.substring(1, lastName.length() - 1);

			String fileName = doc.getFieldValue("fileName").toString();
			fileName = fileName.substring(1, fileName.length() - 1);

			// FileEntity file = new FileEntity();

			// file.setUserId(firstName);
			// file.setFirstName(firstName);
			// file.setLastName(lastName);
			// file.setShortFileName(fileName);

			// List<FileEntity> tempList = findByFirstNameAndLastName(firstName,lastName);

			int id = Integer.parseInt(userId);
			List<FileEntity> tempList = findByUserId(id);

			for (FileEntity tempFile : tempList) {
				fileList.add(tempFile);
			}

		}

		// List<FileEntity> file

		int size = fileList.size();
		if (size == 0) {
			size = 1;
		}
		PageRequest pageRequest = PageRequest.of(0, size);

		PageImpl<FileEntity> page = new PageImpl<FileEntity>(fileList, pageRequest, size);

		return page;
	}
*/
	@Override
	public void updateFile(FileEntity file) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			fileRepo.saveAndFlush(file);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void deleteFile(Long id) {

		Optional<FileEntity> file = fileRepo.findById(id);

		FileEntity fileDto = null;

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			fileRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public FileEntity getFile(Long id) {

		Optional<FileEntity> file = fileRepo.findById(id);

		FileEntity fileDto = null;

		if (file.isPresent()) {
			fileDto = file.get();
			return fileDto;

		} else {
			return null;
		}
	}

	@Override
	public Page<FileEntity> searchFiles(Pageable pageable, Specification<FileEntity> spec) {
		return fileRepo.findAll(spec, pageable);
	}

	@Override
	public List<FileEntity> findByUserId(int userId) {
		return fileRepo.findByUserId(userId);
	}

}
