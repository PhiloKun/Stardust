package com.philokun.stardustbackend.controller;

import com.philokun.stardustbackend.service.VideoService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.philokun.stardustbackend.common.R;
import com.philokun.stardustbackend.model.dto.video.VideoPublishRequest;
import com.philokun.stardustbackend.model.vo.video.VideoPublishVO;

/**
 * 视频控制器
 * 处理视频相关的HTTP请求
 */
@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    /**
     * 视频服务
     */
    private final VideoService videoService;

    /**
     * 上传视频
     * @param request 视频发布请求参数
     * @return 视频发布结果
     */
    @PostMapping("/upload")
    public R<VideoPublishVO> uploadVideo(VideoPublishRequest request) {
        VideoPublishVO videoPublishVO = videoService.uploadVideo(request);
        return R.success("视频上传成功", videoPublishVO);
    }

}