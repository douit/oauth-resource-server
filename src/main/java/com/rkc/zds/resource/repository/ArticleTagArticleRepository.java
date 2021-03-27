package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.ArticleTagArticleEntity;

public interface ArticleTagArticleRepository extends JpaRepository<ArticleTagArticleEntity, Integer>, JpaSpecificationExecutor<ArticleTagArticleEntity> {

	List<ArticleTagArticleEntity> findByTagId(Integer id);

	ArticleTagArticleEntity findByTagIdAndArticleId(Integer tagId, Integer articleId);

	List<ArticleTagArticleEntity> findByArticleId(Integer articleId);

	Page<ArticleTagArticleEntity> findByTagId(Pageable pageable, Integer id);

}
