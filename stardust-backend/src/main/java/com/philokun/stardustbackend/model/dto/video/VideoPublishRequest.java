package com.philokun.stardustbackend.model.dto.video;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoPublishRequest {

    /**
     * 视频文件
     */
    private MultipartFile videoFile;
    /**
     * 视频描述
     */
    private String description;

    /**
     * 视频标签列表
     */
    private List<String> tags;

    /**
     * 上传用户ID
     */
    private String userId;
}
