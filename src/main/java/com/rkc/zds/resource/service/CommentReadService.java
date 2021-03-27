package com.rkc.zds.resource.service;

import com.rkc.zds.resource.entity.ArticleCommentEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.CommentData;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentReadService {
    CommentData findById(@Param("id") Integer id, UserEntity user);

    List<CommentData> findByArticleId(@Param("articleId") Integer articleId);

    Optional<ArticleCommentEntity> findByArticleIdAndUserId(Integer articleId, Integer userId);
}
