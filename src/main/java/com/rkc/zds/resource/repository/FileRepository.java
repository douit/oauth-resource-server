package com.rkc.zds.resource.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rkc.zds.resource.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity>{
  
	Page<FileEntity> findByFileId(Pageable pageable, Long fileId);

	List<FileEntity> findByFileId(Long fileId);
	
	List<FileEntity> findByUserId(int userId);	
	       
}
