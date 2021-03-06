package com.rkc.zds.resource.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.ArticleEntity;
import com.rkc.zds.resource.entity.ArticleTagArticleEntity;
import com.rkc.zds.resource.entity.ArticleTagEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.ArticleData;
import com.rkc.zds.resource.model.ArticleDataList;
import com.rkc.zds.resource.model.ArticleFavoriteCount;
import com.rkc.zds.resource.model.ProfileData;
import com.rkc.zds.resource.repository.ArticleTagArticleRepository;
import com.rkc.zds.resource.repository.ArticleTagRepository;
import com.rkc.zds.resource.repository.UserRepository;
import com.rkc.zds.resource.service.ArticleFavoritesReadService;
import com.rkc.zds.resource.service.ArticleReadService;
import com.rkc.zds.resource.service.UserRelationshipQueryService;

@Service
@Qualifier("articleQueryService")
public class ArticleQueryServiceImpl {

	private ArticleReadService articleReadService;
	private UserRelationshipQueryService userRelationshipQueryService;
	private ArticleFavoritesReadService articleFavoritesReadService;

	@Autowired
	private ArticleTagRepository articleTagRepo;
	
	@Autowired
	private ArticleTagArticleRepository articleTagArticleRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	public ArticleQueryServiceImpl(ArticleReadService articleReadService,
			UserRelationshipQueryService userRelationshipQueryService,
			ArticleFavoritesReadService articleFavoritesReadService) {
		this.articleReadService = articleReadService;
		this.userRelationshipQueryService = userRelationshipQueryService;
		this.articleFavoritesReadService = articleFavoritesReadService;
	}

	public Optional<ArticleData> findById(Integer articleId, UserEntity user) {
		ArticleData articleData = articleReadService.findById(articleId);
		if (articleData == null) {
			return Optional.empty();
		} else {
			if (user != null) {
				fillExtraInfo(articleId, user, articleData);
			}
			return Optional.of(articleData);
		}
	}

	public ArticleDataList findRecentArticles(Pageable pageable, String tag, String author, String favoritedBy,
			UserEntity currentUser) {
		List<String> articleIds = articleReadService.queryArticles(pageable, tag, author, favoritedBy);
		// int articleCount = articleReadService.countArticle(tag, author, favoritedBy);
		int articleCount = articleIds.size();
		if (articleIds.size() == 0) {
			return new ArticleDataList(new ArrayList<>(), articleCount);
		} else {
			List<ArticleData> articles = articleReadService.findArticles(pageable, articleIds);
			fillExtraInfo(articles, currentUser);
			return new ArticleDataList(articles, articleCount);
		}
	}

	private void fillExtraInfo(List<ArticleData> articles, UserEntity currentUser) {
		setFavoriteCount(articles);
		if (currentUser != null) {
			setIsFavorite(articles, currentUser);
			setIsFollowingAuthor(articles, currentUser);
		}
	}

	private void setIsFollowingAuthor(List<ArticleData> articles, UserEntity currentUser) {
		Set<Integer> followingAuthors = userRelationshipQueryService.followingAuthors(currentUser.getId(),
				articles.stream().map(articleData1 -> articleData1.getProfileData().getId()).collect(toList()));
		articles.forEach(articleData -> {
			if (followingAuthors.contains(articleData.getProfileData().getId())) {
				articleData.getProfileData().setFollowing(true);
			}
		});
	}

	private void setFavoriteCount(List<ArticleData> articles) {
		List<ArticleFavoriteCount> favoritesCounts = articleFavoritesReadService
				.articlesFavoriteCount(articles.stream().map(ArticleData::getId).collect(toList()));
		Map<Integer, Integer> countMap = new HashMap<>();
		favoritesCounts.forEach(item -> {
			countMap.put(item.getId(), item.getCount());
		});
//        articles.forEach(articleData -> articleData.setFavoritesCount(countMap.get(articleData.getId())));

		for (ArticleData articleData : articles) {
			Integer test = countMap.get(articleData.getId());
			articleData.setFavoritesCount(countMap.get(articleData.getId()));

		}

	}

	private void setIsFavorite(List<ArticleData> articles, UserEntity currentUser) {
		Set<Integer> favoritedArticles = articleFavoritesReadService.userFavorites(
				articles.stream().map(articleData -> articleData.getId()).collect(toList()), currentUser);

		if (!favoritedArticles.isEmpty()) {
			articles.forEach(articleData -> {
				if (favoritedArticles.contains(articleData.getId())) {
					articleData.setFavorited(true);
				}
			});
		}
	}

	private void fillExtraInfo(Integer articleId, UserEntity user, ArticleData articleData) {
		articleData.setFavorited(articleFavoritesReadService.isUserFavorite(user.getId(), articleId));
		articleData.setFavoritesCount(articleFavoritesReadService.articleFavoriteCount(articleId));
		articleData.getProfileData().setFollowing(
				userRelationshipQueryService.isUserFollowing(user.getId(), articleData.getProfileData().getId()));
	}

	public ArticleDataList findUserFeed(Pageable pageable, UserEntity user) {
		List<Integer> followedUsers = userRelationshipQueryService.followedUsers(user.getId());
		if (followedUsers.size() == 0) {
			return new ArticleDataList(new ArrayList<>(), 0);
		} else {
			ArticleDataList articles = articleReadService.findArticlesOfAuthors(pageable, followedUsers);
			fillExtraInfo(articles.getList(), user);
			//int count = articleReadService.countFeedSize(followedUsers);
			return new ArticleDataList(articles.getList(), articles.getCount());
		}
	}

	public Page<ArticleEntity> findArticles(Pageable pageable, String tag, String author, String favoritedBy, UserEntity userDto) {
		
		return articleReadService.findAll(pageable);
	}

	public ArticleDataList convertToArticleData(Page<ArticleEntity> pageList) {
		ArticleDataList articles = new ArticleDataList(new ArrayList<>(), 0);
		List<ArticleData> articleDatas = new ArrayList<>();
		ArticleData data = null;
		UserEntity user = null;
		ProfileData profile = null;
		
		for(ArticleEntity articleDto:pageList.toList()) {
			data = new ArticleData();

			data.setId(articleDto.getId());
			data.setBody(articleDto.getBody());
			data.setTitle(articleDto.getTitle());
			data.setCreatedAt(articleDto.getCreatedAt());
			data.setUpdatedAt(articleDto.getUpdatedAt());
			data.setDescription(articleDto.getDescription());
			data.setFavorited(false);
			data.setSlug(articleDto.toSlug(articleDto.getTitle()));

			// List<ArticleTagArticleDto> tagDtoList = articleDto.getTagList();
			List<ArticleTagArticleEntity> tagDtoList = articleTagArticleRepo.findByArticleId(articleDto.getId());
			List<String> tagList = new ArrayList<String>();
			Optional<ArticleTagEntity> tag = null;
			ArticleTagEntity tagDto = null;
			for (ArticleTagArticleEntity articleTag : tagDtoList) {

				tag = articleTagRepo.findById(articleTag.getTagId());

				if (tag.isPresent()) {
					tagDto = tag.get();
					tagList.add(tagDto.getName());
				}

			}

			data.setTagList(tagList);
			
			Integer userId = articleDto.getUserId();

			Optional<UserEntity> userDto = userRepo.findById(userId);

			if (userDto.isPresent()) {
				user = userDto.get();

				profile = new ProfileData(user.getId(), user.getUserName(), user.getBio(), user.getImage(), true);

				data.setProfileData(profile);
			}

			articleDatas.add(data);
			
		}
		
		fillExtraInfo(articleDatas, user);
		articles.setArticleDatas(articleDatas);
		return articles;
	}
}
