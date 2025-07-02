package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philokun.stardustbackend.mapper.VideoMapper;
import com.philokun.stardustbackend.mapper.UserMapper;
import com.philokun.stardustbackend.model.dto.video.VideoPublishRequest;
import com.philokun.stardustbackend.model.entity.Video;
import com.philokun.stardustbackend.model.entity.User;
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
import java.util.Arrays;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    private final VideoMapper videoMapper;
    private final MinioUtils minioUtils;
    private final UserMapper userMapper;

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
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            // 使用用户ID和UUID组合生成唯一的文件名，便于管理和追踪
            String objectName = String.format("videos/%s/%s%s", request.getUserId(),
                    java.util.UUID.randomUUID().toString(), fileExtension);

            // Get input stream and content type
            InputStream inputStream = videoFile.getInputStream();
            String contentType = videoFile.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream"; // Default content type
            }

            // Upload file to MinIO
            minioUtils.uploadFile(inputStream, objectName, contentType);
            videoUrl = objectName;

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
        video.setTags(
                request.getTags() != null && !request.getTags().isEmpty() ? String.join(",", request.getTags()) : "");
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
            // 封面url
            if (video.getCover() != null) {
                videoInfoVO.setCoverUrl(minioUtils.getFileUrl("stardust", video.getCover()));
            }
            // 视频url
            if (video.getVideoUrl() != null) {
                videoInfoVO.setVideoUrl(minioUtils.getFileUrl("stardust", video.getVideoUrl()));
            }
            return videoInfoVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VideoInfoVO> listAllVideos() {
        List<Video> videos = videoMapper.selectList(null);
        return videos.stream().map(video -> {
            VideoInfoVO vo = new VideoInfoVO();
            BeanUtils.copyProperties(video, vo);
            // 查询作者信息
            User user = userMapper.selectById(video.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar());
            }
            // 修复标签字段
            if (video.getTags() != null && !video.getTags().isEmpty()) {
                vo.setTags(Arrays.asList(video.getTags().split(",")));
            } else {
                vo.setTags(null);
            }
            // 封面url
            if (video.getCover() != null) {
                vo.setCoverUrl(minioUtils.getFileUrl("stardust", video.getCover()));
            }
            // 假设点赞和评论数暂为0，后续可扩展
            vo.setLikes(0);
            vo.setComments(0);
            // 视频url
            if (video.getVideoUrl() != null) {
                vo.setVideoUrl(minioUtils.getFileUrl("stardust", video.getVideoUrl()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    // 占位图生成方法
    private File generatePlaceholderCover(String title, String username) throws Exception {
        int width = 720, height = 1280;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // 背景渐变
        GradientPaint gp = new GradientPaint(0, 0, Color.BLUE, width, height, Color.CYAN);
        g.setPaint(gp);
        g.fillRect(0, 0, width, height);
        // 标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 48));
        g.drawString(title != null ? title : "视频", 40, height / 2);
        // 用户名
        g.setFont(new Font("微软雅黑", Font.PLAIN, 32));
        g.drawString(username != null ? ("@" + username) : "", 40, height / 2 + 60);
        g.dispose();
        File tempCover = File.createTempFile("cover_", ".jpg");
        ImageIO.write(image, "jpg", tempCover);
        return tempCover;
    }

}