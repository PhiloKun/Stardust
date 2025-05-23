package com.philokun.stardustbackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码哈希
     */
    private String password;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String signature;

    /**
     * 粉丝数
     */
    private Integer followers;

    /**
     * 关注数
     */
    private Integer following;

    /**
     * 作品数
     */
    private Integer posts;

    /**
     * 获赞数
     */
    private Integer likes;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;
}