package com.rkc.zds.resource.service;

//import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.UserEntity;

import java.util.Optional;

//@Service
public interface JwtService {
    String toToken(UserEntity user);

    Optional<String> getSubFromToken(String token);
}
