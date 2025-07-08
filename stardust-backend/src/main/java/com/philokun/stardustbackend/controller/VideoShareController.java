package com.philokun.stardustbackend.controller;

import com.philokun.stardustbackend.common.R;
import com.philokun.stardustbackend.service.VideoShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video/share")
public class VideoShareController {
    @Autowired
    private VideoShareService videoShareService;

    @PostMapping("")
    public R<?> share(@RequestParam String userId, @RequestParam String videoId, @RequestParam String platform) {
        boolean result = videoShareService.share(userId, videoId, platform);
        return result ? R.success("分享成功", null) : R.error(400, "分享失败");
    }
} 