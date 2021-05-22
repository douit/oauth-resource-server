package com.rkc.zds.resource.service;

import javax.persistence.EntityManagerFactory;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.model.FollowRelation;

@Mapper
public interface UserMapper {
	
	public EntityManagerFactory getEntityManagerFactory();
	
    void insert(@Param("user") UserEntity user);

    UserEntity findByUserName(@Param("userName") String userName);
    
    UserEntity findByEmail(@Param("email") String email);

    UserEntity findById(@Param("id") String id);

    void update(@Param("user") UserEntity user);

    FollowRelation findRelation(@Param("userId") Integer userId, @Param("targetId") Integer targetId);

    void saveRelation(@Param("followRelation") FollowRelation followRelation);

    void deleteRelation(@Param("followRelation") FollowRelation followRelation);
}
