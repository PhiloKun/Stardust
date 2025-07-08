package com.philokun.stardustbackend.controller;

import com.philokun.stardustbackend.common.R;
import com.philokun.stardustbackend.service.VideoFavoriteService;
import com.philokun.stardustbackend.model.entity.VideoFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video/favorite")
public class VideoFavoriteController {
    @Autowired
    private VideoFavoriteService videoFavoriteService;

    @PostMapping("/{videoId}")
    public R<?> favorite(@RequestParam String userId, @PathVariable String videoId) {
        boolean result = videoFavoriteService.favorite(userId, videoId);
        return result ? R.success("收藏成功", null) : R.error(400, "已收藏");
    }

    @DeleteMapping("/{videoId}")
    public R<?> unfavorite(@RequestParam String userId, @PathVariable String videoId) {
        boolean result = videoFavoriteService.unfavorite(userId, videoId);
        return result ? R.success("取消收藏成功", null) : R.error(400, "未收藏");
    }

    @GetMapping("/{videoId}/status")
    public R<Boolean> isFavorited(@RequestParam String userId, @PathVariable String videoId) {
        boolean result = videoFavoriteService.hasFavorited(userId, videoId);
        return R.success(result);
    }

    @GetMapping("/list")
    public R<List<String>> listFavorites(@RequestParam String userId) {
        List<VideoFavorite> favorites = videoFavoriteService.lambdaQuery()
                .eq(VideoFavorite::getUserId, userId)
                .list();
        List<String> videoIds = favorites.stream().map(VideoFavorite::getVideoId).toList();
        return R.success(videoIds);
    }
}