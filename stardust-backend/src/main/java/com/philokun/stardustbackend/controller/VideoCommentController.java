package com.philokun.stardustbackend.controller;

import com.philokun.stardustbackend.common.R;
import com.philokun.stardustbackend.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video/comment")
public class VideoCommentController {
    @Autowired
    private VideoCommentService videoCommentService;

    @PostMapping("")
    public R<?> addComment(@RequestParam String userId, @RequestParam String videoId, @RequestParam String content,
            @RequestParam(required = false) Long parentId) {
        boolean result = videoCommentService.addComment(userId, videoId, content, parentId);
        return result ? R.success("评论成功", null) : R.error(400, "评论失败");
    }
}