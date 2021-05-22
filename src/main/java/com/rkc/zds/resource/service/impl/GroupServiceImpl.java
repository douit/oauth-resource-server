package com.rkc.zds.resource.service.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.rkc.zds.resource.entity.GroupEntity;
import com.rkc.zds.resource.entity.GroupMemberEntity;
import com.rkc.zds.resource.repository.GroupMemberRepository;
import com.rkc.zds.resource.repository.GroupRepository;
import com.rkc.zds.resource.service.GroupService;

@Service
public class GroupServiceImpl implements GroupService {
	private static final int PAGE_SIZE = 50;

	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Autowired
	private GroupRepository groupRepo;

	@Autowired
	private GroupMemberRepository groupMemberRepo;
	
	@Override
	public Page<GroupEntity> findGroups(Pageable pageable) {

		return groupRepo.findAll(pageable);
	}

	@Override
	public GroupEntity getGroup(int id) {
		
		Optional<GroupEntity> group = groupRepo.findById(id);
		
		return group.get();

	}

	@Override
	public Page<GroupMemberEntity> findGroupMembers(int id) {

		final PageRequest pageRequest = PageRequest.of(0, 10, sortByNameASC());

		Page<GroupMemberEntity> page = groupMemberRepo.findByGroupId(pageRequest, id );

		return page;
	}
	
	@Override
	public Page<GroupEntity> searchGroups(String name) {

		final PageRequest pageRequest = PageRequest.of(0, 10, sortByNameASC());

		return groupRepo.findByGroupNameLike(pageRequest, "%" + name + "%");
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void saveGroup(GroupEntity group) {
		groupRepo.save(group);
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateGroup(GroupEntity group) {
		groupRepo.saveAndFlush(group);
	}

	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteGroup(int groupId) {
		
		//delete all group members for this group prior to deleting group.		
		List<GroupMemberEntity> list = groupMemberRepo.findByGroupId(groupId);
		
		for(GroupMemberEntity element : list){
			groupMemberRepo.delete(element);
		}
		
		groupRepo.deleteById(groupId);
	}

	private Sort sortByNameASC() {
		return Sort.by(Sort.Direction.ASC, "groupName");
	}
	
	@Override
	public Page<GroupEntity> searchGroups(Pageable pageable, Specification<GroupEntity> spec) {
		return groupRepo.findAll(spec, pageable);
	}
}
