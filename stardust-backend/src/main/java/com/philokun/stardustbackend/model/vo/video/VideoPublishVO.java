package com.philokun.stardustbackend.model.vo.video;

import lombok.Data;

import java.util.List;

/**
 * 视频发布视图对象
 * 用于响应前端传来的视频发布请求数据
 */
@Data
public class VideoPublishVO {
    /**
     * 视频ID
     */
    private String id;
    
    /**
     * 视频描述
     */
    private String description;
    
    /**
     * 视频URL地址
     */
    private String videoUrl;
    
    /**
     * 视频标签列表
     */
    private List<String> tags;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 视频状态
     * 0: 待审核
     * 1: 已发布
     * 2: 已拒绝
     */
    private Integer status;
}