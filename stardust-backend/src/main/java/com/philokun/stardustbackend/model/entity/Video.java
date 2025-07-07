package com.philokun.stardustbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频表
 * 
 * @TableName video
 */
@TableName(value = "video")
@Data
public class Video {

    /**
     * 视频ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 视频描述
     */
    private String description;

    /**
     * 视频标签
     */
    private String tags;

    /**
     * 视频对象名（目录格式）
     */
    private String videoUrl;

    /**
     * 上传用户ID
     */
    private String userId;

    /**
     * 视频状态 (例如: 0-待审核, 1-已发布, 2-已屏蔽)
     */
    private Integer status;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 视频封面对象名（目录格式）
     */
    private String cover;
}