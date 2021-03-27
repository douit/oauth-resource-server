package com.rkc.zds.resource.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rkc.zds.resource.entity.ArticleFavoriteEntity;

public interface ArticleFavoriteRepository extends JpaRepository<ArticleFavoriteEntity, Integer> {
	ArticleFavoriteEntity save(ArticleFavoriteEntity articleFavorite);

	Optional<ArticleFavoriteEntity> findByArticleIdAndUserId(Integer articleId, Integer userId);
	
	List<ArticleFavoriteEntity> findByArticleId(Integer articleId);

	List<ArticleFavoriteEntity> findByUserId(Integer id);

	Page<ArticleFavoriteEntity> findPageByUserId(Pageable pageable, Integer id);
}
