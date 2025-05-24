package com.philokun.stardustbackend.service;

import com.philokun.stardustbackend.model.entity.Video;
import com.philokun.stardustbackend.model.vo.video.VideoVO;

import java.util.List;

public interface VideoService {

    /**
     * 上传视频
     * @param video 视频实体
     * @return 是否成功
     */
    boolean uploadVideo(Video video);

    /**
     * 根据ID获取视频详情
     * @param videoId 视频ID
     * @return 视频VO对象
     */
    VideoVO getVideoById(Long videoId);

    /**
     * 获取用户发布的视频列表
     * @param userId 用户ID
     * @return 视频VO列表
     */
    List<VideoVO> listVideosByUserId(Long userId);

    // 可以根据需要添加其他视频相关的业务方法
} 