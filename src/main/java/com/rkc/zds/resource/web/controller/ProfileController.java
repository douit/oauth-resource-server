package com.rkc.zds.resource.web.controller;

import com.rkc.zds.resource.model.FollowRelation;
import com.rkc.zds.resource.model.ProfileData;
import com.rkc.zds.resource.entity.ArticleFollowEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.exception.ResourceNotFoundException;
import com.rkc.zds.resource.repository.UserRepository;
import com.rkc.zds.resource.service.ProfileQueryService;
import com.rkc.zds.resource.service.impl.ProfileQueryServiceImpl;
import com.rkc.zds.resource.repository.ArticleFollowRepository;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping(path = "/api/profiles/{userName:.+}")
public class ProfileController {
    private ProfileQueryServiceImpl profileQueryService;
    private UserRepository userRepository;

    @Autowired
    ArticleFollowRepository followRepository;
    
    @Autowired
    public ProfileController(ProfileQueryServiceImpl profileQueryService, UserRepository userRepository) {
        this.profileQueryService = profileQueryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity getProfile(@PathVariable("userName") String userName) {
   	
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userLogin = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userLogin);
		
		UserEntity user = null;
		
		if(userDto.isPresent()) {
			user = userDto.get();
		}
		
    	return profileQueryService.findByUserName(userName, user)
            .map(this::profileResponse)
            .orElseThrow(ResourceNotFoundException::new);
    }

    //@PostMapping(path = "follow")
	@RequestMapping(value = "/follow", method = RequestMethod.POST, consumes = {
	"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })

    public ResponseEntity follow(@PathVariable("userName") String userName) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userLogin = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userLogin);
		
		UserEntity user = null;
		UserEntity target = null;
		
		if(userDto.isPresent()) {
			user = userDto.get();
		}
    	
    	Optional<UserEntity> targetDto = userRepository.findByUserName(userName);
    	
		if(targetDto.isPresent()) {
			target = targetDto.get();
		}    	

		ArticleFollowEntity follow = new ArticleFollowEntity();
		follow.setUserId(user.getId());
		follow.setFollowId(target.getId());
		
		followRepository.save(follow);
		
		return profileResponse(profileQueryService.findByUserName(userName, user).get());
		
    }

	@RequestMapping(value = "follow", method = RequestMethod.DELETE, consumes = {
	"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
    public ResponseEntity unfollow(@PathVariable("userName") String userName) {
 
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		KeycloakPrincipal principal=(KeycloakPrincipal)authentication.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        String userLogin = accessToken.getPreferredUsername();

		Optional<UserEntity> userDto = userRepository.findByUserName(userLogin);
		
		UserEntity user = null;
		UserEntity target = null;
		
		if(userDto.isPresent()) {
			user = userDto.get();
		}
    	
    	Optional<UserEntity> targetDto = userRepository.findByUserName(userName);
    	
		if(targetDto.isPresent()) {
			target = targetDto.get();
		}    	

		ArticleFollowEntity follow = followRepository.findByUserIdAndFollowId(user.getId(), target.getId());
		
		followRepository.delete(follow);
		
		return profileResponse(profileQueryService.findByUserName(userName, user).get());
/*
		Optional<UserDto> userOptional = userRepository.findByUserName(userName);
        if (userOptional.isPresent()) {
            UserDto target = userOptional.get();
            return userRepository.findRelation(user.getId(), target.getId())
                .map(relation -> {
                    userRepository.removeRelation(relation);
                    return profileResponse(profileQueryService.findByUserName(userName, user).get());
                }).orElseThrow(ResourceNotFoundException::new);
        } else {
            throw new ResourceNotFoundException();
        }
*/
    }

    private ResponseEntity profileResponse(ProfileData profile) {
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("profile", profile);
        }});
    }

}
