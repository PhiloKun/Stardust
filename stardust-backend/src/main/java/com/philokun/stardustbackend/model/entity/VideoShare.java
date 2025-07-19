package com.philokun.stardustbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("video_share")
public class VideoShare {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String userId;
    private String videoId;
    private String platform;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}