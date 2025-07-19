package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philokun.stardustbackend.mapper.VideoCommentMapper;
import com.philokun.stardustbackend.model.entity.VideoComment;
import com.philokun.stardustbackend.service.VideoCommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoComment>
        implements VideoCommentService {
    @Override
    public boolean addComment(String userId, String videoId, String content, Long parentId) {
        VideoComment comment = new VideoComment();
        comment.setUserId(userId);
        comment.setVideoId(videoId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setCreateTime(LocalDateTime.now());
        return this.save(comment);
    }
}