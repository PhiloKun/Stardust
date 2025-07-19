package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philokun.stardustbackend.mapper.VideoLikeMapper;
import com.philokun.stardustbackend.model.entity.VideoLike;
import com.philokun.stardustbackend.service.VideoLikeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoLikeServiceImpl extends ServiceImpl<VideoLikeMapper, VideoLike> implements VideoLikeService {
    @Override
    public boolean like(String userId, String videoId) {
        if (hasLiked(userId, videoId))
            return false;
        VideoLike like = new VideoLike();
        like.setUserId(userId);
        like.setVideoId(videoId);
        like.setCreateTime(LocalDateTime.now());
        return this.save(like);
    }

    @Override
    public boolean unlike(String userId, String videoId) {
        return this.remove(new QueryWrapper<VideoLike>().eq("user_id", userId).eq("video_id", videoId));
    }

    @Override
    public boolean hasLiked(String userId, String videoId) {
        return this.count(new QueryWrapper<VideoLike>().eq("user_id", userId).eq("video_id", videoId)) > 0;
    }
}