package com.philokun.stardustbackend.model.vo.video;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoVO {

    private Long id;
    private String title;
    private String videoUrl;
    private String cover;
    private String tags;
    private Long userId;
    private Integer status;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
} 