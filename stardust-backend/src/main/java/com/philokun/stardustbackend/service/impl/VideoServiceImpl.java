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
import java.util.Collections;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import com.philokun.stardustbackend.service.VideoFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    private final VideoMapper videoMapper;
    private final MinioUtils minioUtils;
    private final UserMapper userMapper;
    @Autowired
    private VideoFavoriteService videoFavoriteService;

    @Override
    public VideoPublishVO uploadVideo(VideoPublishRequest request) {
        System.out.println(request.getTags());

        String videoUrl;
        MultipartFile videoFile = request.getVideoFile();
        File tempVideoFile = null;
        try {
            // Get file details from the request
            if (videoFile == null || videoFile.isEmpty()) {
                throw new RuntimeException("视频文件不能为空");
            }

            // Generate a unique object name (e.g., using UUID and original file extension)
            String originalFilename = videoFile.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".mp4";
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

            // 保存上传的视频为临时文件
            tempVideoFile = File.createTempFile("upload_", fileExtension);
            videoFile.transferTo(tempVideoFile);
        } catch (MinioException e) {
            e.printStackTrace();
            throw new RuntimeException("视频上传到存储服务失败: " + e.getMessage());
        } catch (java.io.IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取视频文件失败: " + e.getMessage());
        }

        Video video = new Video();
        BeanUtils.copyProperties(request, video);
        video.setVideoUrl(videoUrl);
        video.setTags(
                request.getTags() != null && !request.getTags().isEmpty() ? String.join(",", request.getTags()) : "");
        video.setStatus(0); // Set initial status, e.g., 0 for pending review

        // 自动提取视频第一帧并上传为封面，失败则生成带标题的默认封面
        try {
            File coverFile = null;
            if (tempVideoFile != null && tempVideoFile.exists()) {
                try {
                    // 尝试提取第一帧
                    FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(tempVideoFile));
                    Picture picture = grab.getNativeFrame();
                    BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
                    coverFile = File.createTempFile("cover_", ".jpg");
                    ImageIO.write(bufferedImage, "jpg", coverFile);
                } catch (Exception e) {
                    // 提取失败则生成默认封面
                    User user = userMapper.selectById(request.getUserId());
                    String username = user != null ? user.getUsername() : "未知用户";
                    String title = (video.getDescription() != null && !video.getDescription().isEmpty())
                            ? video.getDescription()
                            : "未命名视频";
                    coverFile = generatePlaceholderCover(title, username);
                }
                String coverObjectName = String.format("covers/%s/%s.jpg", request.getUserId(),
                        java.util.UUID.randomUUID());
                try (InputStream coverInput = new java.io.FileInputStream(coverFile)) {
                    minioUtils.uploadFile(coverInput, coverObjectName, "image/jpeg");
                    video.setCover(coverObjectName);
                }
                coverFile.delete();
            }
        } catch (Exception e) {
            video.setCover(null);
            e.printStackTrace();
        } finally {
            if (tempVideoFile != null && tempVideoFile.exists()) {
                tempVideoFile.delete();
            }
        }

        // Insert video into database
        boolean success = this.save(video);
        if (!success) {
            throw new RuntimeException("视频保存失败");
        }

        VideoPublishVO videoPublishVO = new VideoPublishVO();
        videoPublishVO.setId(video.getId());
        videoPublishVO.setDescription(video.getDescription());
        videoPublishVO.setVideoUrl(video.getVideoUrl());
        videoPublishVO.setUserId(video.getUserId());
        videoPublishVO.setStatus(video.getStatus());
        return videoPublishVO;
    }

    @Override
    public VideoInfoVO getVideoById(String videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            return null;
        }
        VideoInfoVO videoInfoVO = new VideoInfoVO();
        BeanUtils.copyProperties(video, videoInfoVO);
        if (video.getVideoUrl() != null) {
            videoInfoVO.setVideoUrl(minioUtils.getFileUrl("stardust", video.getVideoUrl()));
        }
        if (video.getCover() != null) {
            videoInfoVO.setCoverUrl(minioUtils.getFileUrl("stardust", video.getCover()));
        }
        // 设置收藏数
        videoInfoVO.setFavorites(videoFavoriteService.countByVideoId(videoId));
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
            // 设置收藏数
            videoInfoVO.setFavorites(videoFavoriteService.countByVideoId(video.getId()));
            return videoInfoVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VideoInfoVO> listAllVideos() {
        List<Video> videos = videoMapper.selectList(null);
        // 随机打乱顺序
        Collections.shuffle(videos);
        // TODO: 可扩展：支持前端传递已看过视频ID列表，过滤已看过的视频
        return videos.stream().map(video -> {
            VideoInfoVO vo = new VideoInfoVO();
            BeanUtils.copyProperties(video, vo);
            // 查询作者信息
            User user = userMapper.selectById(video.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    vo.setAvatar(minioUtils.getFileUrl("stardust", user.getAvatar()));
                } else {
                    vo.setAvatar(null);
                }
            }
            if (video.getTags() != null && !video.getTags().isEmpty()) {
                vo.setTags(Arrays.asList(video.getTags().split(",")));
            } else {
                vo.setTags(null);
            }
            if (video.getCover() != null) {
                vo.setCoverUrl(minioUtils.getFileUrl("stardust", video.getCover()));
            }
            vo.setLikes(0);
            vo.setComments(0);
            if (video.getVideoUrl() != null) {
                vo.setVideoUrl(minioUtils.getFileUrl("stardust", video.getVideoUrl()));
            }
            // 设置收藏数
            vo.setFavorites(videoFavoriteService.countByVideoId(video.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VideoInfoVO> getVideosByIds(List<String> videoIds) {
        if (videoIds == null || videoIds.isEmpty())
            return Collections.emptyList();
        List<Video> videos = videoMapper.selectBatchIds(videoIds);
        return videos.stream().map(video -> {
            VideoInfoVO vo = new VideoInfoVO();
            BeanUtils.copyProperties(video, vo);
            // 查询作者信息
            User user = userMapper.selectById(video.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar() != null && !user.getAvatar().isEmpty()
                        ? minioUtils.getFileUrl("stardust", user.getAvatar())
                        : null);
            }
            if (video.getTags() != null && !video.getTags().isEmpty()) {
                vo.setTags(Arrays.asList(video.getTags().split(",")));
            } else {
                vo.setTags(null);
            }
            if (video.getCover() != null) {
                vo.setCoverUrl(minioUtils.getFileUrl("stardust", video.getCover()));
            }
            vo.setLikes(0);
            vo.setComments(0);
            if (video.getVideoUrl() != null) {
                vo.setVideoUrl(minioUtils.getFileUrl("stardust", video.getVideoUrl()));
            }
            // 设置收藏数
            vo.setFavorites(videoFavoriteService.countByVideoId(video.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean deleteVideoById(String videoId, String userId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null || !video.getUserId().equals(userId)) {
            return false;
        }
        // 删除视频文件和封面
        try {
            if (video.getVideoUrl() != null) {
                minioUtils.deleteFile("stardust", video.getVideoUrl());
            }
            if (video.getCover() != null) {
                minioUtils.deleteFile("stardust", video.getCover());
            }
        } catch (Exception e) {
            // 可选：记录日志
        }
        // 删除数据库记录
        return this.removeById(videoId);
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