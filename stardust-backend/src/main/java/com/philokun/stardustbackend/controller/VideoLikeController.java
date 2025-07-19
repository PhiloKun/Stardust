package com.philokun.stardustbackend.controller;

import com.philokun.stardustbackend.common.R;
import com.philokun.stardustbackend.service.VideoLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videolike")
public class VideoLikeController {
    @Autowired
    private VideoLikeService videoLikeService;

    @PostMapping("/{videoId}")
    public R<?> like(@RequestParam String userId, @PathVariable String videoId) {
        boolean result = videoLikeService.like(userId, videoId);
        return result ? R.success("点赞成功", null) : R.error(400, "已点赞");
    }

    @DeleteMapping("/{videoId}")
    public R<?> unlike(@RequestParam String userId, @PathVariable String videoId) {
        boolean result = videoLikeService.unlike(userId, videoId);
        return result ? R.success("取消点赞成功", null) : R.error(400, "未点赞");
    }
}