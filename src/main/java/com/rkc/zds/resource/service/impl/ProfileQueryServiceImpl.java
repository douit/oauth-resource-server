package com.rkc.zds.resource.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.ProfileData;
import com.rkc.zds.resource.model.UserData;
import com.rkc.zds.resource.service.UserReadService;
import com.rkc.zds.resource.service.UserRelationshipQueryService;

//Component
@Service
public class ProfileQueryServiceImpl {
    private UserReadService userReadService;
    private UserRelationshipQueryService userRelationshipQueryService;

    @Autowired
    public ProfileQueryServiceImpl(UserReadService userReadService, UserRelationshipQueryService userRelationshipQueryService) {
        this.userReadService = userReadService;
        this.userRelationshipQueryService = userRelationshipQueryService;
    }

    public Optional<ProfileData> findByUserName(String userName, UserEntity currentUser) {
        UserData userData = userReadService.findByUserName(userName);
        if (userData == null) {
            return Optional.empty();
        } else {
            ProfileData profileData = new ProfileData(
                userData.getId(),
                userData.getUserName(),
                userData.getBio(),
                userData.getImage(),
                userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId()));
            return Optional.of(profileData);
        }
    }
}
