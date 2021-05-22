package com.rkc.zds.resource.service;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.rkc.zds.resource.dto.LoginDto;
import com.rkc.zds.resource.entity.AuthorityEntity;
import com.rkc.zds.resource.entity.UserEntity;
import com.rkc.zds.resource.exception.UserAlreadyExistException;

public interface UserService {
	
	public EntityManagerFactory getEntityManagerFactory();
	
    Page<UserEntity> findUsers(Pageable pageable);
    
	UserEntity findByUserName(String userName);
    
	UserEntity findById(Integer id);

	List<UserEntity> getUsers();
	
    UserEntity getUser(int id);  
	  
    public void updateUser(UserEntity user);
    
	void deleteUser(int id);
    
    public void saveUser(UserEntity user);

	UserEntity registerNewUserAccount(UserEntity accountDto) throws UserAlreadyExistException;

	Page<UserEntity> searchUsers(Pageable pageable, Specification<UserEntity> spec);

	UserEntity changePassword(LoginDto loginDTO, HttpServletRequest request, HttpServletResponse response);

	Page<AuthorityEntity> findAuthorities(Pageable pageable, String userName);

	AuthorityEntity getAuthority(int id);
	
    public void saveAuthority(AuthorityEntity role);
    
    public void updateAuthority(AuthorityEntity authority);

	void deleteAuthority(int id);

}