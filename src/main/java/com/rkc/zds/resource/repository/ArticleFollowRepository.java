package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rkc.zds.resource.entity.ArticleFollowEntity;

public interface ArticleFollowRepository extends JpaRepository<ArticleFollowEntity, Integer> {

	ArticleFollowEntity findByUserIdAndFollowId(Integer userId, Integer followId);
	
	List<ArticleFollowEntity> findByUserId(Integer userId);
	
	ArticleFollowEntity save(ArticleFollowEntity follow);
}
