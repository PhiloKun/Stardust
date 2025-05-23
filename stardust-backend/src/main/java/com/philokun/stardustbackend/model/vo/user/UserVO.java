package com.philokun.stardustbackend.model.vo.user;

import lombok.Data;

@Data
public class UserVO {
    private Long id;        // 用户ID
    private String username;    // 用户名
    private String avatar;      // 用户头像URL
    private String signature;   // 用户个性签名
    private Integer followersCount;     // 粉丝数量
    private Integer followingCount;     // 关注数量
    private Integer postsCount;         // 发帖数量
    private Integer likesCount;         // 获赞数量
    private String token;  // JWT token，用于用户认证
} 