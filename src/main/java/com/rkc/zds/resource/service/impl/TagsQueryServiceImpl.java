package com.rkc.zds.resource.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rkc.zds.resource.service.TagReadService;

@Service
public class TagsQueryServiceImpl {
    private TagReadService tagReadService;

    public TagsQueryServiceImpl(TagReadService tagReadService) {
        this.tagReadService = tagReadService;
    }

    public List<String> allTags() {
        return tagReadService.all();
    }
}
