package com.philokun.stardustbackend.controller;

import com.philokun.stardustbackend.service.VideoService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.philokun.stardustbackend.common.R;
import com.philokun.stardustbackend.model.dto.video.VideoPublishRequest;
import com.philokun.stardustbackend.model.vo.video.VideoPublishVO;
import com.philokun.stardustbackend.model.vo.video.VideoInfoVO;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 视频控制器
 * 处理视频相关的HTTP请求
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    /**
     * 视频服务
     */
    private final VideoService videoService;

    /**
     * 上传视频
     * 
     * @param request 视频发布请求参数
     * @return 视频发布结果
     */
    @PostMapping("/upload")
    public R<VideoPublishVO> uploadVideo(VideoPublishRequest request) {
        VideoPublishVO videoPublishVO = videoService.uploadVideo(request);
        return R.success("视频上传成功", videoPublishVO);
    }

    /**
     * 获取所有视频列表（推荐页）
     */
    @GetMapping("/list")
    public R<List<VideoInfoVO>> listVideos() {
        List<VideoInfoVO> videoList = videoService.listAllVideos();
        return R.success("获取视频列表成功", videoList);
    }

    /**
     * 获取指定用户的视频列表
     */
    @GetMapping("/user/{userId}")
    public R<List<VideoInfoVO>> listVideosByUser(@PathVariable String userId) {
        List<VideoInfoVO> videoList = videoService.listVideosByUserId(userId);
        return R.success("获取用户视频列表成功", videoList);
    }

    /**
     * 根据ID获取视频详情
     */
    @GetMapping("/{id}")
    public R<VideoInfoVO> getVideoById(@PathVariable String id) {
        VideoInfoVO videoInfoVO = videoService.getVideoById(id);
        if (videoInfoVO == null) {
            return R.error(404, "视频不存在");
        }
        return R.success("获取视频详情成功", videoInfoVO);
    }

    /**
     * 批量获取视频详情
     */
    @PostMapping("/batch")
    public R<List<VideoInfoVO>> getVideosByIds(@RequestBody List<String> videoIds) {
        List<VideoInfoVO> videos = videoService.getVideosByIds(videoIds);
        return R.success(videos);
    }

    /**
     * 删除视频（仅限本人）
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteVideo(@PathVariable String id, @RequestHeader("userId") String userId) {
        boolean success = videoService.deleteVideoById(id, userId);
        if (success) {
            return R.success("删除成功", true);
        } else {
            return R.error(403, "无权删除该视频或视频不存在", false);
        }
    }

}