package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rkc.zds.resource.entity.GroupMemberEntity;

public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Integer> {
  
	Page<GroupMemberEntity> findByGroupId(Pageable pageable, int groupId);

	List<GroupMemberEntity> findByGroupId(int groupId);
	       
}
