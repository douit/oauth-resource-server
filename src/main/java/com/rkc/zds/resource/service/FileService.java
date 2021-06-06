package com.rkc.zds.resource.service;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.rkc.zds.resource.entity.FileEntity;
import com.rkc.zds.resource.entity.UserEntity;

public interface FileService {
	
	public EntityManagerFactory getEntityManagerFactory();
	
    Page<FileEntity> findFiles(Pageable pageable);
    
    FileEntity getFile(Long id);  
    
    public FileEntity saveFile(FileEntity file) throws Exception;
      
    public void updateFile(FileEntity file);
 
	void deleteFile(Long id);
	
	Page<FileEntity> searchFiles(Pageable pageable, Specification<FileEntity> spec);

	//Page<FileEntity> searchFiles(Pageable pageable, String search) throws Exception;
	
	List<FileEntity> findByUserId(int userId);
}
