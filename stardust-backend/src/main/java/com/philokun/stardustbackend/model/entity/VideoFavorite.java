package com.philokun.stardustbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("video_favorite")
public class VideoFavorite {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String userId;
    private String videoId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}