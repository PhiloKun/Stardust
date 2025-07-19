package com.philokun.stardustbackend.service;

import com.philokun.stardustbackend.model.dto.video.VideoPublishRequest;
import com.philokun.stardustbackend.model.vo.video.VideoInfoVO;
import com.philokun.stardustbackend.model.vo.video.VideoPublishVO;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VideoService {

    /**
     * 上传视频
     * 
     * @param request 视频发布请求
     * @return 是否上传成功
     */
    public VideoPublishVO uploadVideo(VideoPublishRequest request);

    /**
     * 根据ID获取视频详情
     * 
     * @param videoId 视频ID
     * @return 视频VO对象
     */
    public VideoInfoVO getVideoById(String videoId);

    /**
     * 获取用户发布的视频列表
     * 
     * @param userId 用户ID
     * @return 视频VO列表
     */
    public List<VideoInfoVO> listVideosByUserId(String userId);

    /**
     * 获取所有视频列表（推荐页）
     * 
     * @return 视频VO列表
     */
    public List<VideoInfoVO> listAllVideos();

    /**
     * 根据ID批量获取视频详情
     * 
     * @param videoIds 视频ID列表
     * @return 视频VO列表
     */
    public List<VideoInfoVO> getVideosByIds(List<String> videoIds);

    /**
     * 删除视频（仅限本人）
     * @param videoId 视频ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteVideoById(String videoId, String userId);
}