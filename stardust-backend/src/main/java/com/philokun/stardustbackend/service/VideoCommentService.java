package com.philokun.stardustbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.philokun.stardustbackend.model.entity.VideoComment;

public interface VideoCommentService extends IService<VideoComment> {
    boolean addComment(String userId, String videoId, String content, Long parentId);
}