package com.rkc.zds.resource.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rkc.zds.resource.entity.ContactEntity;
import com.rkc.zds.resource.entity.GroupMemberEntity;

public interface GroupMemberService {
    Page<GroupMemberEntity> findGroupMembers(Pageable pageable, int groupId);
    
    List<GroupMemberEntity> findAllMembers(int groupId);

    Page<ContactEntity> findFilteredContacts(Pageable pageable, int groupId);  
     
    public void saveGroupMember(GroupMemberEntity groupMember);    
  
	void deleteGroupMember(int id);
}
