package com.rkc.zds.resource.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rkc.zds.resource.entity.ArticleTagArticleEntity;
import com.rkc.zds.resource.entity.ArticleTagEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.exception.NoAuthorizationException;
import com.rkc.zds.resource.exception.ResourceNotFoundException;
import com.rkc.zds.resource.repository.ArticleRepository;
import com.rkc.zds.resource.repository.ArticleTagArticleRepository;
import com.rkc.zds.resource.repository.ArticleTagRepository;
import com.rkc.zds.resource.repository.UserRepository;
import com.rkc.zds.resource.service.AuthorizationService;
import com.rkc.zds.resource.service.impl.AuthorizationServiceImpl;
import com.rkc.zds.resource.service.impl.TagsQueryServiceImpl;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(path = "/api/tags")
public class TagsController {
		
	@Autowired
	UserRepository userRepository;

	@Autowired
	ArticleTagRepository tagRepository;
	
	@Autowired
	ArticleTagArticleRepository tagArticleRepository;
	
	@Autowired	
	ArticleRepository articleRepository;
	
    private TagsQueryServiceImpl tagsQueryService;

    @Autowired
    public TagsController(TagsQueryServiceImpl tagsQueryService) {
        this.tagsQueryService = tagsQueryService;
    }

    @GetMapping
    public ResponseEntity getTags() {
        return ResponseEntity.ok(new HashMap<String, Object>() {
        	
			private static final long serialVersionUID = 1252138647889335832L;

		{
            put("tags", tagsQueryService.allTags());
        }});
    }
    
	@RequestMapping(value = "{tag:.+}/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteTag(@PathVariable("tag") String tag, @PathVariable("id") Integer id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userName = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userName);
		
		UserEntity user = null;
		
		if(userDto.isPresent()) {
			user = userDto.get();
		}
		
		final UserEntity temp = user;
				
		ArticleTagEntity tagDto = tagRepository.findByName(tag);
		
		ArticleTagArticleEntity tagArticle = tagArticleRepository.findByTagIdAndArticleId(tagDto.getId(), id);
		
		//return articleRepository.findBySlug(slug).map(article -> {
		return articleRepository.findById(id).map(article -> {
			if (!AuthorizationServiceImpl.canWriteArticle(temp, article)) {
				throw new NoAuthorizationException();
			}
			tagArticleRepository.delete(tagArticle);
			
			//if there are no articles with this tag, delete tag
			List<ArticleTagArticleEntity> tagArticleList = new ArrayList<ArticleTagArticleEntity>();
			tagArticleList = tagArticleRepository.findByTagId(tagDto.getId());
			if(tagArticleList.size()==0) {
				tagRepository.delete(tagDto);
			}
			
			return ResponseEntity.noContent().build();
		}).orElseThrow(ResourceNotFoundException::new);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteTagX(@PathVariable("id") Integer id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userName = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userName);
		
		UserEntity user = null;
		
		if(userDto.isPresent()) {
			user = userDto.get();
		}
		
		final UserEntity temp = user;
		
		String tag = "";
		
		ArticleTagEntity tagDto = tagRepository.findByName(tag);
		
		ArticleTagArticleEntity tagArticle = tagArticleRepository.findByTagIdAndArticleId(tagDto.getId(), id);
		
		//return articleRepository.findBySlug(slug).map(article -> {
		return articleRepository.findById(id).map(article -> {
			if (!AuthorizationServiceImpl.canWriteArticle(temp, article)) {
				throw new NoAuthorizationException();
			}
			tagArticleRepository.delete(tagArticle);
			
			//if there are no articles with this tag, delete tag
			List<ArticleTagArticleEntity> tagArticleList = new ArrayList<ArticleTagArticleEntity>();
			tagArticleList = tagArticleRepository.findByTagId(tagDto.getId());
			if(tagArticleList.size()==0) {
				tagRepository.delete(tagDto);
			}
			return ResponseEntity.noContent().build();
		}).orElseThrow(ResourceNotFoundException::new);
	}
}
