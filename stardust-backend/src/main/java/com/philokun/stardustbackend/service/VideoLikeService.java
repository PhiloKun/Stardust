package com.philokun.stardustbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.philokun.stardustbackend.model.entity.VideoLike;

public interface VideoLikeService extends IService<VideoLike> {
    boolean like(String userId, String videoId);

    boolean unlike(String userId, String videoId);

    boolean hasLiked(String userId, String videoId);
}