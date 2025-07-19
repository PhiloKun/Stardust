package com.philokun.stardustbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.philokun.stardustbackend.model.entity.VideoShare;

public interface VideoShareService extends IService<VideoShare> {
    boolean share(String userId, String videoId, String platform);
}