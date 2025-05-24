package com.philokun.stardustbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频表
 * @TableName video
 */
@TableName(value = "video")
@Data
public class Video {

    /**
     * 视频ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频封面
     */
    private String cover;

    /**
     * 视频标签
     */
    private String tags;

    /**
     * 视频存储路径或URL
     */
    private String videoUrl;

    /**
     * 上传用户ID
     */
    private Long userId;

    /**
     * 视频状态 (例如: 0-待审核, 1-已发布, 2-已屏蔽)
     */
    private Integer status;

    /**
     * 点赞数
     */ 
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

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
}