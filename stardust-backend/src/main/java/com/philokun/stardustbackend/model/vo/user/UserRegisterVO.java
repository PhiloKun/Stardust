package com.philokun.stardustbackend.model.vo.user;

import lombok.Data;

@Data
public class UserRegisterVO {
    private String id;        // 用户ID
    private String username;    // 用户名   
    private String token;  // JWT token，用于用户认证
} 