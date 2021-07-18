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

// import com.rkc.zds.EmbeddedSolrResume;

import com.rkc.zds.resource.entity.ArticleTagArticleEntity;
import com.rkc.zds.resource.entity.ArticleTagEntity;
import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.ResumeEntity;
import com.rkc.zds.resource.entity.ResumeSkillEntity;
import com.rkc.zds.resource.entity.SkillEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.repository.ResumeRepository;
import com.rkc.zds.resource.repository.ResumeSkillRepository;
import com.rkc.zds.resource.service.ResumeService;
import com.rkc.zds.resource.service.SkillService;

@Service
public class ResumeServiceImpl implements ResumeService {

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
	private ResumeRepository resumeRepo;

	@Autowired
	private SkillService skillService;

	@Autowired
	private ResumeSkillRepository resumeSkillRepo;

	@Override
	public Page<ResumeEntity> findResumes(Pageable pageable) {

		Page<ResumeEntity> page = resumeRepo.findAll(pageable);

		return page;
	}

	@Override
	public ResumeEntity saveResume(ResumeEntity resume) throws Exception {

		ResumeEntity resumeSaved = null;

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			resumeSaved = resumeRepo.save(resume);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}

		File source = new File(resume.getOriginalFileName());

		String solrFilePath = "/_/data/resumes/storage/" + resume.getUserId() + "_" + resume.getFirstName() + "_"
				+ resume.getLastName() + "_" + resume.getShortFileName();

		File dest = new File(solrFilePath);

		try {
			FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
/*
		EmbeddedSolrResume solr = EmbeddedSolrResume.getInstance();

		EmbeddedSolrServer server = solr.getEmbeddedSolrServer();

		solr.reindexSolr();
*/

		List<SkillEntity> skills = skillService.findAll();

		for (SkillEntity skill : skills) {
			// boolean found = resumeSaved.getTextResume().matches("(?i).*" +
			// skill.getSkillName() + ".*");

			boolean found = resumeSaved.getTextResume().toLowerCase().contains(skill.getSkillName().toLowerCase());
			if (found) {

				ResumeSkillEntity resumeSkill = new ResumeSkillEntity();

				resumeSkill.setResumeId(resumeSaved.getResumeId());
				resumeSkill.setSkillId(skill.getSkillId());
				resumeSkill.setSkillName(skill.getSkillName());

				try {
					tx = em.getTransaction();
					tx.begin();
					
					resumeSkillRepo.save(resumeSkill);

					tx.commit();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
		
		//put the resume into elastic search

		return resumeSaved;
	}

	@Override
	public Page<ResumeEntity> searchResumes(Pageable pageable, String search) throws Exception {

		// EmbeddedSolrResume solr = EmbeddedSolrResume.getInstance();

		// EmbeddedSolrServer server = solr.getEmbeddedSolrServer();

		// SolrDocumentList docList = solr.searchSolr(search);

		List<ResumeEntity> resumeList = new ArrayList<ResumeEntity>();
/*
		for (SolrDocument doc : docList) {

			String userId = doc.getFieldValue("userId").toString();
			userId = userId.substring(1, userId.length() - 1);

			String firstName = doc.getFieldValue("firstName").toString();
			firstName = firstName.substring(1, firstName.length() - 1);

			String lastName = doc.getFieldValue("lastName").toString();
			lastName = lastName.substring(1, lastName.length() - 1);

			String fileName = doc.getFieldValue("fileName").toString();
			fileName = fileName.substring(1, fileName.length() - 1);

			// ResumeEntity resume = new ResumeEntity();

			// resume.setUserId(firstName);
			// resume.setFirstName(firstName);
			// resume.setLastName(lastName);
			// resume.setShortFileName(fileName);

			// List<ResumeEntity> tempList = findByFirstNameAndLastName(firstName,lastName);

			int id = Integer.parseInt(userId);
			List<ResumeEntity> tempList = findByUserId(id);

			for (ResumeEntity tempResume : tempList) {
				resumeList.add(tempResume);
			}

		}
*/
		// List<ResumeEntity> resume

		int size = resumeList.size();
		if (size == 0) {
			size = 1;
		}
		PageRequest pageRequest = PageRequest.of(0, size);

		PageImpl<ResumeEntity> page = new PageImpl<ResumeEntity>(resumeList, pageRequest, size);

		return page;
	}

	@Override
	public void updateResume(ResumeEntity resume) {

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			resumeRepo.saveAndFlush(resume);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void deleteResume(Long id) {

		Optional<ResumeEntity> resume = resumeRepo.findById(id);

		ResumeEntity resumeDto = null;

		EntityManagerFactory emf = getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			if (resume.isPresent()) {
				resumeDto = resume.get();

				List<ResumeSkillEntity> skillsList = resumeSkillRepo.findByResumeId(resumeDto.getResumeId());

				for (ResumeSkillEntity skill : skillsList) {
					resumeSkillRepo.delete(skill);
				}
			}

			resumeRepo.deleteById(id);

			tx.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public ResumeEntity getResume(Long id) {

		Optional<ResumeEntity> resume = resumeRepo.findById(id);

		ResumeEntity resumeDto = null;

		if (resume.isPresent()) {
			resumeDto = resume.get();
			// List<ArticleTagArticleDto> tagDtoList = articleDto.getTagList();
			List<ResumeSkillEntity> skillsList = resumeSkillRepo.findByResumeId(resumeDto.getResumeId());

			resumeDto.setSkills(skillsList);

			return resumeDto;

		} else {
			return null;
		}
	}

	@Override
	public Page<ResumeEntity> searchResumes(Pageable pageable, Specification<ResumeEntity> spec) {
		return resumeRepo.findAll(spec, pageable);
	}

	@Override
	public List<ResumeEntity> findByUserId(int userId) {
		return resumeRepo.findByUserId(userId);
	}

}
