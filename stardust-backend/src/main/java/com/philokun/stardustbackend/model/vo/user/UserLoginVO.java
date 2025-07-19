package com.philokun.stardustbackend.model.vo.user;

import lombok.Data;


@Data
public class UserLoginVO {
    private String id;        // 用户ID
    private String username;    // 用户名
    private String avatar;      // 用户头像URL
    private String token;  // JWT token，用于用户认证
} 