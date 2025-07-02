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

    @PostMapping("/upload-chunk")
    public R<Boolean> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("index") int index,
            @RequestParam("total") int total,
            @RequestParam("userId") String userId,
            @RequestParam("fileName") String fileName) {
        File chunkDir = new File("/tmp/video_chunks/" + userId + "/" + fileName);
        if (!chunkDir.exists()) chunkDir.mkdirs();
        File chunkFile = new File(chunkDir, index + ".part");
        try {
            file.transferTo(chunkFile);
        } catch (Exception e) {
            return R.error(400, "分片保存失败: " + e.getMessage());
        }
        return R.success("分片上传成功", true);
    }

    @PostMapping("/merge-chunks")
    public R<String> mergeChunks(
            @RequestParam("userId") String userId,
            @RequestParam("fileName") String fileName,
            @RequestParam("total") int total,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description) {
        File chunkDir = new File("/tmp/video_chunks/" + userId + "/" + fileName);
        File mergedFile = new File(chunkDir, "merged_" + fileName);
        try (FileOutputStream fos = new FileOutputStream(mergedFile)) {
            for (int i = 0; i < total; i++) {
                File chunkFile = new File(chunkDir, i + ".part");
                Files.copy(chunkFile.toPath(), fos);
                chunkFile.delete();
            }
        } catch (Exception e) {
            return R.error(400, "合并失败: " + e.getMessage());
        }
        // TODO: 调用视频上传逻辑，将mergedFile作为视频源文件，生成封面并上传到MinIO
        // 合并后可删除chunkDir
        return R.success("合并成功", "merged_" + fileName);
    }

}