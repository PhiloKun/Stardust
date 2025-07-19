package com.philokun.stardustbackend.model.vo.video;

import lombok.Data;

import java.util.List;

@Data
public class VideoInfoVO {
    private String id;
    private String description;
    private String videoUrl;
    private List<String> tags;
    private String userId;
    private Integer status;
    private String coverUrl;
    private String username;
    private String avatar;
    private Integer likes;
    private Integer comments;
    private int favorites; // 收藏数
}