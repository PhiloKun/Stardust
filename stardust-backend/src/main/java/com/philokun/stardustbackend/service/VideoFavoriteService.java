package com.philokun.stardustbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.philokun.stardustbackend.model.entity.VideoFavorite;

import java.util.List;

public interface VideoFavoriteService extends IService<VideoFavorite> {
    boolean favorite(String userId, String videoId);

    boolean unfavorite(String userId, String videoId);

    boolean hasFavorited(String userId, String videoId);

    List<String> listFavoriteVideoIds(String userId);
}