package com.rkc.zds.resource.web.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkc.zds.resource.entity.ArticleCommentEntity;
import com.rkc.zds.resource.entity.ArticleEntity;
import com.rkc.zds.resource.entity.ArticleFavoriteEntity;
import com.rkc.zds.resource.entity.ArticleTagArticleEntity;
import com.rkc.zds.resource.entity.ArticleTagEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.exception.NoAuthorizationException;
import com.rkc.zds.resource.exception.ResourceNotFoundException;
import com.rkc.zds.resource.model.ArticleData;
import com.rkc.zds.resource.repository.ArticleCommentRepository;
import com.rkc.zds.resource.repository.ArticleFavoriteRepository;
import com.rkc.zds.resource.repository.ArticleRepository;
import com.rkc.zds.resource.repository.ArticleTagArticleRepository;
import com.rkc.zds.resource.repository.ArticleTagRepository;
import com.rkc.zds.resource.repository.UserRepository;
import com.rkc.zds.resource.service.ArticleQueryService;
import com.rkc.zds.resource.service.AuthorizationService;

@CrossOrigin(origins = "http://www.zdslogic-development.com:4200")
@RestController
@RequestMapping(path = "/api/articles/")
public class ArticleController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ArticleTagRepository tagRepository;

	@Autowired
	ArticleTagArticleRepository tagArticleRepository;
	
	@Autowired
	ArticleCommentRepository articleCommentRepository;

	@Autowired
	ArticleFavoriteRepository favoritesRepository;
	
	@Autowired
	private ArticleQueryService articleQueryService;

	private ArticleRepository articleRepository;

	@Autowired
	public ArticleController(ArticleQueryService articleQueryService, ArticleRepository articleRepository) {
		this.articleQueryService = articleQueryService;
		this.articleRepository = articleRepository;
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> article(@PathVariable("id") Integer id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userName = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userName);

		UserEntity user = null;

		if (userDto.isPresent()) {
			user = userDto.get();
		}

		return articleQueryService.findById(id, user)
				.map(articleData -> ResponseEntity.ok(articleResponse(articleData)))
				.orElseThrow(ResourceNotFoundException::new);
	}

	// @PutMapping
	// puts are not working, 415 error, switched to post
	@RequestMapping(value = "{id}", method = RequestMethod.POST, consumes = { "application/json" }, produces = {
			"application/json" })
	public ResponseEntity<?> updateArticle(@RequestBody String jsonString) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userName = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userName);

		UserEntity user = null;

		if (userDto.isPresent()) {
			user = userDto.get();
		}

		ArticleEntity article = new ArticleEntity();

		ObjectMapper mapper = new ObjectMapper();

		try {
			article = mapper.readValue(jsonString, ArticleEntity.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (article.getTags() != null) {
			processTags(article);
		}

		Optional<ArticleEntity> articleTemp = articleRepository.findById(article.getId());

		ArticleEntity articleDto = null;
		if (articleTemp.isPresent()) {
			articleDto = articleTemp.get();

			articleDto.setBody(article.getBody());
			articleDto.setTitle(article.getTitle());
			articleDto.setDescription(article.getDescription());
			Timestamp stamp = new Timestamp(new Date().getTime());

			articleDto.setUpdatedAt(stamp);
			articleDto = articleRepository.save(articleDto);
		}

		final Integer articleId = article.getId();

		return ResponseEntity.ok(articleResponse(articleQueryService.findById(articleId, user).get()));

	}

	private void processTags(ArticleEntity article) {

		String tags = article.getTags();
		String[] array = tags.split("\\s+");
		ArticleTagEntity tagDto = null;
		ArticleTagArticleEntity tagArticleDto = null;

		List<ArticleTagArticleEntity> articleTagList = null;

		for (String tag : array) {
			if (!tag.equals("")) {
				tagDto = tagRepository.findByName(tag);
				if (tagDto == null) {
					tagDto = new ArticleTagEntity();
					tagDto.setName(tag);
					tagDto = tagRepository.save(tagDto);
				}
				if (tagDto != null) {
					tagArticleDto = tagArticleRepository.findByTagIdAndArticleId(tagDto.getId(), article.getId());
					if (tagArticleDto == null) {
						tagArticleDto = new ArticleTagArticleEntity();
						tagArticleDto.setTagId(tagDto.getId());
						tagArticleDto.setArticleId(article.getId());
						tagArticleDto = tagArticleRepository.save(tagArticleDto);
					}
				}
			}
		}
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteArticle(@PathVariable("id") Integer id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userName = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userName);

		UserEntity user = null;

		if (userDto.isPresent()) {
			user = userDto.get();
		}

		final UserEntity temp = user;

		return articleRepository.findById(id).map(article -> {
			if (!AuthorizationService.canWriteArticle(temp, article)) {
				throw new NoAuthorizationException();
			}

			deleteTagsForArticle(article);
			
			deleteCommentsForArticle(article);
			
			deleteFavoritesForArticle(article);

			articleRepository.delete(article);
			return ResponseEntity.noContent().build();
		}).orElseThrow(ResourceNotFoundException::new);
	}

	private void deleteCommentsForArticle(ArticleEntity article) {
		List<ArticleCommentEntity> list = articleCommentRepository.findByArticleId(article.getId());
		
		for(ArticleCommentEntity comment:list) {
			articleCommentRepository.delete(comment);
		}		
	}

	private void deleteFavoritesForArticle(ArticleEntity article) {
		List<ArticleFavoriteEntity> list = favoritesRepository.findByArticleId(article.getId());
		
		for(ArticleFavoriteEntity favorite:list) {
			favoritesRepository.delete(favorite);
		}		
	}
	
	private void deleteTagsForArticle(ArticleEntity article) {
		List<ArticleTagArticleEntity> articleTagList = tagArticleRepository.findByArticleId(article.getId());
		Optional<ArticleTagEntity> tagDtoOpt = null;
		ArticleTagEntity tagDto = null;

		for (ArticleTagArticleEntity articleTag : articleTagList) {
			tagArticleRepository.delete(articleTag);

			List<ArticleTagArticleEntity> list = tagArticleRepository.findByTagId(articleTag.getTagId());

			if (list.size() == 0) {
				tagDtoOpt = tagRepository.findById(articleTag.getTagId());
				if (tagDtoOpt.isPresent()) {
					tagDto = tagDtoOpt.get();
					tagRepository.delete(tagDto);
				}
			}
		}
	}

	private Map<String, Object> articleResponse(ArticleData articleData) {
		return new HashMap<String, Object>() {

			private static final long serialVersionUID = -1587818607546012077L;

			{
				put("article", articleData);
			}
		};
	}
}
