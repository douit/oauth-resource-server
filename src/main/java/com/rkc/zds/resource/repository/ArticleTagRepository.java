package com.rkc.zds.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.ArticleTagEntity;

public interface ArticleTagRepository extends JpaRepository<ArticleTagEntity, Integer>, JpaSpecificationExecutor<ArticleTagEntity> {
	
	ArticleTagEntity findByName(String tag);


}
