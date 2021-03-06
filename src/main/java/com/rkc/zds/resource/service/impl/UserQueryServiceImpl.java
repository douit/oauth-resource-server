package com.rkc.zds.resource.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rkc.zds.resource.model.UserData;
import com.rkc.zds.resource.service.UserReadService;

@Service
public class UserQueryServiceImpl  {
    private UserReadService userReadService;

    public UserQueryServiceImpl(UserReadService userReadService) {
        this.userReadService = userReadService;
    }

    public Optional<UserData> findById(Integer integer) {
        return Optional.ofNullable(userReadService.findById(integer));
    }
}

