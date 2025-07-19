package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philokun.stardustbackend.mapper.VideoCollectMapper;
import com.philokun.stardustbackend.model.entity.VideoCollect;
import com.philokun.stardustbackend.service.VideoFavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VideoFavoriteServiceImpl extends ServiceImpl<VideoCollectMapper, VideoCollect>
        implements VideoFavoriteService {
    @Override
    public boolean favorite(String userId, String videoId) {
        if (hasFavorited(userId, videoId))
            return false;
        VideoCollect favorite = new VideoCollect();
        favorite.setUserId(userId);
        favorite.setVideoId(videoId);
        favorite.setCreateTime(LocalDateTime.now());
        return this.save(favorite);
    }

    @Override
    public boolean unfavorite(String userId, String videoId) {
        return this.remove(new QueryWrapper<VideoCollect>().eq("user_id", userId).eq("video_id", videoId));
    }

    @Override
    public boolean hasFavorited(String userId, String videoId) {
        return this.count(new QueryWrapper<VideoCollect>().eq("user_id", userId).eq("video_id", videoId)) > 0;
    }

    @Override
    public List<String> listFavoriteVideoIds(String userId) {
        return this.lambdaQuery().eq(VideoCollect::getUserId, userId)
                .list().stream().map(VideoCollect::getVideoId).toList();
    }

    @Override
    public int countByVideoId(String videoId) {
        return (int) this.count(new QueryWrapper<VideoCollect>().eq("video_id", videoId));
    }
}