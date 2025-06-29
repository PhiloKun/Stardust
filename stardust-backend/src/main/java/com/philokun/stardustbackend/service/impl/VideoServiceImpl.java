package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philokun.stardustbackend.mapper.VideoMapper;
import com.philokun.stardustbackend.model.dto.video.VideoPublishRequest;
import com.philokun.stardustbackend.model.entity.Video;
import com.philokun.stardustbackend.model.vo.video.VideoInfoVO;
import com.philokun.stardustbackend.model.vo.video.VideoPublishVO;
import com.philokun.stardustbackend.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.philokun.stardustbackend.utils.MinioUtils;
import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    private final VideoMapper videoMapper;
    private final MinioUtils minioUtils;

    @Override
    public VideoPublishVO uploadVideo(VideoPublishRequest request) {
        System.out.println(request.getTags());


        String videoUrl;
        try {
            // Get file details from the request
            MultipartFile videoFile = request.getVideoFile();
            if (videoFile == null || videoFile.isEmpty()) {
                throw new RuntimeException("视频文件不能为空");
            }

            // Generate a unique object name (e.g., using UUID and original file extension)
            String originalFilename = videoFile.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") ?
                                   originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            // 使用用户ID和UUID组合生成唯一的文件名，便于管理和追踪
            String objectName = String.format("videos/%s/%s%s", request.getUserId(), java.util.UUID.randomUUID().toString(), fileExtension);

            // Get input stream and content type
            InputStream inputStream = videoFile.getInputStream();
            String contentType = videoFile.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                 contentType = "application/octet-stream"; // Default content type
            }

            // Upload file to MinIO
            videoUrl = minioUtils.uploadFile(inputStream, objectName, contentType);

        } catch (MinioException e) {
            // Log the exception and throw a meaningful runtime exception
            e.printStackTrace(); // Consider using a logger instead
            throw new RuntimeException("视频上传到存储服务失败: " + e.getMessage());
        } catch (java.io.IOException e) {
             e.printStackTrace(); // Consider using a logger instead
             throw new RuntimeException("读取视频文件失败: " + e.getMessage());
        }

        Video video = new Video();
        // Copy common properties
        BeanUtils.copyProperties(request, video);

        // Manually set fields that are not directly copied or require transformation
        video.setVideoUrl(videoUrl);
        video.setTags(request.getTags() != null && !request.getTags().isEmpty() ? String.join(",", request.getTags()) : "");
        video.setStatus(0); // Set initial status, e.g., 0 for pending review

        // Insert video into database
        boolean success = this.save(video); // Using MyBatis-Plus ServiceImpl's save method

        if (!success) {
            // Handle insertion failure, maybe throw an exception
             throw new RuntimeException("视频保存失败");
        }

        // Prepare and return VideoPublishVO
        VideoPublishVO videoPublishVO = new VideoPublishVO();
        videoPublishVO.setId(video.getId()); // Get the generated ID after saving
        videoPublishVO.setDescription(video.getDescription());
        videoPublishVO.setVideoUrl(video.getVideoUrl());
        // Note: VideoPublishVO has List<String> tags, but Video entity has String tags.
        // You might need to convert back if VideoPublishVO is used to display tags.
        // For now, only setting fields present in both or manually handled.
        videoPublishVO.setUserId(video.getUserId());
        videoPublishVO.setStatus(video.getStatus());

        return videoPublishVO;
    }

    @Override
    public VideoInfoVO getVideoById(String videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            return null; // 或者抛出异常
        }
        VideoInfoVO videoInfoVO = new VideoInfoVO();
        BeanUtils.copyProperties(video, videoInfoVO);
        return videoInfoVO;
    }

    @Override
    public List<VideoInfoVO> listVideosByUserId(String userId) {
        List<Video> videos = videoMapper.selectList(new LambdaQueryWrapper<Video>().eq(Video::getUserId, userId));
        return videos.stream().map(video -> {
            VideoInfoVO videoInfoVO = new VideoInfoVO();
            BeanUtils.copyProperties(video, videoInfoVO);
            return videoInfoVO;
        }).collect(Collectors.toList());
    }

} 