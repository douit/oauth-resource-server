package com.rkc.zds.resource.service;

import javax.persistence.EntityManagerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.rkc.zds.resource.entity.GroupEntity;
import com.rkc.zds.resource.entity.GroupMemberEntity;

public interface GroupService {
	
	public EntityManagerFactory getEntityManagerFactory();

    Page<GroupEntity> findGroups(Pageable pageable);

    Page<GroupEntity> searchGroups(String name);
    
	Page<GroupEntity> searchGroups(Pageable pageable, Specification<GroupEntity> spec);

    GroupEntity getGroup(int id);    

    Page<GroupMemberEntity> findGroupMembers(int id); 
    
    public void saveGroup(GroupEntity group);

    public void updateGroup(GroupEntity group);

	void deleteGroup(int id);

}
