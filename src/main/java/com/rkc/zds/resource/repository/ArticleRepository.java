package com.rkc.zds.resource.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.ArticleEntity;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer>, JpaSpecificationExecutor<ArticleEntity> {

    ArticleEntity save(ArticleEntity article);

    Optional<ArticleEntity> findById(String id);

    Optional<ArticleEntity> findBySlug(String slug);
    
    List<ArticleEntity> findByUserId(Integer userId);

    Page<ArticleEntity> findByUserId(Pageable page, Integer author);
}
