package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philokun.stardustbackend.mapper.VideoShareMapper;
import com.philokun.stardustbackend.model.entity.VideoShare;
import com.philokun.stardustbackend.service.VideoShareService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoShareServiceImpl extends ServiceImpl<VideoShareMapper, VideoShare> implements VideoShareService {
    @Override
    public boolean share(String userId, String videoId, String platform) {
        VideoShare share = new VideoShare();
        share.setUserId(userId);
        share.setVideoId(videoId);
        share.setPlatform(platform);
        share.setCreateTime(LocalDateTime.now());
        return this.save(share);
    }
}