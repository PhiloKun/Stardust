package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philokun.stardustbackend.mapper.VideoMapper;
import com.philokun.stardustbackend.model.entity.Video;
import com.philokun.stardustbackend.model.vo.video.VideoVO;
import com.philokun.stardustbackend.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    private final VideoMapper videoMapper;

    @Override
    public boolean uploadVideo(Video video) {
        // TODO: 实现视频上传逻辑，包括文件存储（如MinIO）和数据库记录
        // 插入视频记录到数据库
        int result = videoMapper.insert(video);
        return result > 0;
    }

    @Override
    public VideoVO getVideoById(Long videoId) {
        // TODO: 实现根据ID获取视频详情的逻辑
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            return null; // 或者抛出异常
        }
        VideoVO videoVO = new VideoVO();
        BeanUtils.copyProperties(video, videoVO);
        // TODO: 可以根据需要填充其他VO字段，如作者信息等
        return videoVO;
    }

    @Override
    public List<VideoVO> listVideosByUserId(Long userId) {
        // TODO: 实现获取用户发布的视频列表逻辑
        List<Video> videos = videoMapper.selectList(new LambdaQueryWrapper<Video>().eq(Video::getUserId, userId));
        return videos.stream().map(video -> {
            VideoVO videoVO = new VideoVO();
            BeanUtils.copyProperties(video, videoVO);
            // TODO: 可以根据需要填充其他VO字段
            return videoVO;
        }).collect(Collectors.toList());
    }

    // 可以根据需要添加其他ServiceImpl方法
} 