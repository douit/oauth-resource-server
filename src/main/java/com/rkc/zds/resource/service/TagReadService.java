package com.rkc.zds.resource.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

//@Mapper
public interface TagReadService {
    List<String> all();
}
