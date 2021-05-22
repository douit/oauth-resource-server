package com.rkc.zds.resource.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.ArticleCommentEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.CommentData;
import com.rkc.zds.resource.service.CommentReadService;
import com.rkc.zds.resource.service.UserRelationshipQueryService;

@Service
public class CommentQueryServiceImpl {
    private CommentReadService commentReadService;
    private UserRelationshipQueryService userRelationshipQueryService;

    public CommentQueryServiceImpl(CommentReadService commentReadService, UserRelationshipQueryService userRelationshipQueryService) {
        this.commentReadService = commentReadService;
        this.userRelationshipQueryService = userRelationshipQueryService;
    }

    public Optional<CommentData> findById(Integer id, UserEntity user) {
        CommentData commentData = commentReadService.findById(id, user);
        if (commentData == null) {
            return Optional.empty();
        } else {
            commentData.getProfileData().setFollowing(
                userRelationshipQueryService.isUserFollowing(
                    user.getId(),
                    commentData.getProfileData().getId()));
        }
        return Optional.ofNullable(commentData);
    }

    public List<CommentData> findByArticleId(Integer articleId, UserEntity user) {
        List<CommentData> comments = commentReadService.findByArticleId(articleId);
/*
        if (comments.size() > 0 && user != null) {
            Set<String> followingAuthors = userRelationshipQueryService.followingAuthors(user.getId(), comments.stream().map(commentData -> commentData.getProfileData().getId()).collect(Collectors.toList()));
            comments.forEach(commentData -> {
                if (followingAuthors.contains(commentData.getProfileData().getId())) {
                    commentData.getProfileData().setFollowing(true);
                }
            });
        }
*/
        return comments;
    }

	public Optional<ArticleCommentEntity> findByArticleIdAndUserId(Integer articleId , Integer userId) {
		return commentReadService.findByArticleIdAndUserId(articleId, userId);
	}
}
