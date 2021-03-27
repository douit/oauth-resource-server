package com.rkc.zds.resource.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rkc.zds.resource.entity.ArticleCommentEntity;

public interface  ArticleCommentRepository extends JpaRepository<ArticleCommentEntity, Integer> {

	ArticleCommentEntity save(ArticleCommentEntity comment);

    Optional<ArticleCommentEntity> findByArticleIdAndUserId(Integer articleId, Integer id);

	List<ArticleCommentEntity> findByArticleId(Integer articleId);

	Optional<ArticleCommentEntity> findByArticleIdAndId(Integer id, Integer commentId);

}
