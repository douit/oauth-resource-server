package com.rkc.zds.resource.service;

import com.rkc.zds.resource.entity.ArticleCommentEntity;
import com.rkc.zds.resource.entity.ArticleEntity;
import com.rkc.zds.resource.entity.UserEntity;

public class AuthorizationService {
    public static boolean canWriteArticle(UserEntity user, ArticleEntity article) {
        return user.getId().equals(article.getUserId());
    }

    public static boolean canWriteComment(UserEntity user, ArticleEntity article, ArticleCommentEntity comment) {
        return user.getId().equals(article.getUserId()) || user.getId().equals(comment.getUserId());
    }
}
