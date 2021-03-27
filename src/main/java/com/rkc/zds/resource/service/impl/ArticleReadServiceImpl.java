package com.rkc.zds.resource.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.ArticleEntity;
import com.rkc.zds.resource.entity.ArticleFavoriteEntity;
import com.rkc.zds.resource.entity.ArticleTagArticleEntity;
import com.rkc.zds.resource.entity.ArticleTagEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.ArticleData;
import com.rkc.zds.resource.model.ArticleDataList;
import com.rkc.zds.resource.model.ProfileData;
import com.rkc.zds.resource.repository.ArticleFavoriteRepository;
import com.rkc.zds.resource.repository.ArticleRepository;
import com.rkc.zds.resource.repository.ArticleTagArticleRepository;
import com.rkc.zds.resource.repository.ArticleTagRepository;
import com.rkc.zds.resource.repository.UserRepository;
import com.rkc.zds.resource.rsql.CustomRsqlVisitor;
import com.rkc.zds.resource.service.ArticleReadService;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@Service("articleReadService")
@Qualifier("articleReadService")
public class ArticleReadServiceImpl implements ArticleReadService {

	@Autowired
	private ArticleRepository articleRepo;

	@Autowired
	private ArticleTagRepository articleTagRepo;

	@Autowired
	private ArticleTagArticleRepository articleTagArticleRepo;

	@Autowired
	private ArticleFavoriteRepository articleFavoriteRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public ArticleData findById(Integer id) {
		Optional<ArticleEntity> article = articleRepo.findById(id);

		ArticleData data = null;
		ProfileData profile = null;
		ArticleEntity articleDto = null;

		if (article.isPresent()) {
			articleDto = article.get();
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
				UserEntity user = userDto.get();

				profile = new ProfileData(user.getId(), user.getUserName(), user.getBio(), user.getImage(), true);

				data.setProfileData(profile);
			}
		}
		return data;
	}

	@Override
	public ArticleData findBySlug(String slug) {
		Optional<ArticleEntity> article = articleRepo.findBySlug(slug);

		ArticleData data = null;
		ProfileData profile = null;
		ArticleEntity articleDto = null;

		if (article.isPresent()) {
			articleDto = article.get();
			data = new ArticleData();

			data.setId(articleDto.getId());
			data.setBody(articleDto.getBody());
			data.setTitle(articleDto.getTitle());
			data.setCreatedAt(articleDto.getCreatedAt());
			data.setUpdatedAt(articleDto.getUpdatedAt());
			data.setDescription(articleDto.getDescription());
			data.setFavorited(false);
			data.setSlug(articleDto.toSlug(articleDto.getTitle()));

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
				UserEntity user = userDto.get();

				profile = new ProfileData(user.getId(), user.getUserName(), user.getBio(), user.getImage(), true);

				data.setProfileData(profile);
			}
		}
		return data;
	}

	@Override
	public List<String> queryArticles(Pageable pageable, String tag, String author, String favoritedBy) {

		List<String> list = new ArrayList<String>();

		if (tag != null) {
			ArticleTagEntity tagDto = articleTagRepo.findByName(tag);
			List<ArticleTagArticleEntity> articles = null;

			if (tagDto != null) {
				articles = articleTagArticleRepo.findByTagId(tagDto.getId());
				for (ArticleTagArticleEntity element : articles) {
					list.add(element.getArticleId().toString());
				}
				return list;
			}
		}

		List<ArticleEntity> articleDtoList = null;
		UserEntity userDto = null;
		if (author != null) {
			Optional<UserEntity> userDtoTemp = userRepo.findByUserName(author);
			if (userDtoTemp.isPresent()) {
				userDto = userDtoTemp.get();

				articleDtoList = articleRepo.findByUserId(userDto.getId());

				for (ArticleEntity article : articleDtoList) {

					list.add(article.getId().toString());

				}

				return list;
			}
		}

		List<ArticleFavoriteEntity> favoriteList = null;
		if (favoritedBy != null) {
			Optional<UserEntity> userDtoTemp = userRepo.findByUserName(favoritedBy);
			if (userDtoTemp.isPresent()) {
				userDto = userDtoTemp.get();

				favoriteList = articleFavoriteRepo.findByUserId(userDto.getId());

				for (ArticleFavoriteEntity favorite : favoriteList) {

					list.add(favorite.getArticleId().toString());

				}

				return list;

			}

		}

		Page<ArticleEntity> page = articleRepo.findAll(pageable);

		articleDtoList = page.getContent();

		for (ArticleEntity element : articleDtoList) {
			list.add(element.getId().toString());
		}

		return list;
	}

	public Page<ArticleEntity> searchArticles(Pageable pageable, Specification<ArticleEntity> spec) {
		return articleRepo.findAll(spec, pageable);
	}

	@Override
	public int countArticle(String tag, String author, String favoritedBy) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ArticleData> findArticles(Pageable pageable, List<String> articleIds) {
		List<ArticleData> list = new ArrayList<ArticleData>();

		ArticleData data = null;
		ProfileData profile = null;

		Optional<ArticleEntity> articleDtoTemp = null;

		ArticleEntity articleDto = null;

		for (String articleIdString : articleIds) {

			articleDtoTemp = articleRepo.findById(Integer.parseInt(articleIdString));

			if (articleDtoTemp.isPresent()) {
				data = new ArticleData();

				articleDto = articleDtoTemp.get();
				data.setId(articleDto.getId());
				data.setBody(articleDto.getBody());
				data.setTitle(articleDto.getTitle());
				data.setCreatedAt(articleDto.getCreatedAt());
				data.setUpdatedAt(articleDto.getUpdatedAt());
				data.setDescription(articleDto.getDescription());
				data.setFavorited(false);
				data.setSlug(articleDto.getSlug());

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
					UserEntity user = userDto.get();

					profile = new ProfileData(user.getId(), user.getUserName(), user.getBio(), user.getImage(), true);

					data.setProfileData(profile);
				}

				list.add(data);

			}
		}

		return list;
	}

	@Override
	public ArticleDataList findArticlesOfAuthors(Pageable pageable, List<Integer> authors) {

		List<ArticleData> list = new ArrayList<ArticleData>();
		List<ArticleEntity> articleDtoList = null;
		ArticleData data = null;
		ProfileData profile = null;

		String search = "";
		for (int i = 0; i < authors.size(); i++) {
			Integer id = authors.get(i);
			if (i == 0) {
				search = "userId==" + id;
			} else {
				search += " or userId==" + id;
			}
		}

		Node rootNode = new RSQLParser().parse(search);
		Specification<ArticleEntity> spec = rootNode.accept(new CustomRsqlVisitor<ArticleEntity>());

		Page<ArticleEntity> page = articleRepo.findAll(spec, pageable);

		for (ArticleEntity articleDto : page.getContent()) {
			data = new ArticleData();

			data.setId(articleDto.getId());
			data.setBody(articleDto.getBody());
			data.setTitle(articleDto.getTitle());
			data.setCreatedAt(articleDto.getCreatedAt());
			data.setUpdatedAt(articleDto.getUpdatedAt());
			data.setDescription(articleDto.getDescription());
			data.setFavorited(false);
			data.setSlug(articleDto.getSlug());

			Integer userId = articleDto.getUserId();

			Optional<UserEntity> userDto = userRepo.findById(userId);

			if (userDto.isPresent()) {
				UserEntity user = userDto.get();

				profile = new ProfileData(user.getId(), user.getUserName(), user.getBio(), user.getImage(), true);

				data.setProfileData(profile);
			}

			list.add(data);
		}
		int size = (int) page.getTotalElements();
		ArticleDataList articleList = new ArticleDataList(list, size);

		return articleList;
	}

	@Override
	public int countFeedSize(List<Integer> authors) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<ArticleEntity> findAll(Pageable pageable) {
		return articleRepo.findAll(pageable);
	}

	@Override
	public Page<ArticleEntity> findByUserId(Pageable pageable, Integer author) {
		return articleRepo.findByUserId(pageable, author);
	}

	@Override
	public Page<ArticleEntity> findFavorites(Pageable pageable, Integer id) {

		Page<ArticleFavoriteEntity> favoritesPage = articleFavoriteRepo.findPageByUserId(pageable, id);

		List<ArticleEntity> articleDtoList = new ArrayList<ArticleEntity>();
		
		for(ArticleFavoriteEntity element:favoritesPage.getContent()) {
			Optional<ArticleEntity> articleDtoTemp = articleRepo.findById(element.getArticleId());
			if (articleDtoTemp.isPresent()) {
				
				ArticleEntity article = articleDtoTemp.get();

				articleDtoList.add(article);
			}
		}
			
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

		PageImpl<ArticleEntity> page = new PageImpl<ArticleEntity>(articleDtoList, pageRequest,
				favoritesPage.getTotalElements());		

		return page;
	}

	@Override
	public Page<ArticleEntity> findByTag(Pageable pageable, String tag) {
		ArticleTagEntity tagDto = articleTagRepo.findByName(tag);
		
		Page<ArticleTagArticleEntity> articleTagPage = articleTagArticleRepo.findByTagId(pageable, tagDto.getId());

		List<ArticleEntity> articleDtoList = new ArrayList<ArticleEntity>();
		
		for(ArticleTagArticleEntity articleTag:articleTagPage.getContent()) {
			Optional<ArticleEntity> articleDtoTemp = articleRepo.findById(articleTag.getArticleId());
			if (articleDtoTemp.isPresent()) {
				
				ArticleEntity article = articleDtoTemp.get();

				articleDtoList.add(article);
			}
		}
		
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

		PageImpl<ArticleEntity> page = new PageImpl<ArticleEntity>(articleDtoList, pageRequest,
				articleTagPage.getTotalElements());		

		return page;
	}

}
