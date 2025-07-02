package com.philokun.stardustbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.philokun.stardustbackend.model.dto.user.UserLoginRequest;
import com.philokun.stardustbackend.model.dto.user.UserRegisterRequest;
import com.philokun.stardustbackend.model.vo.user.UserRegisterVO;
import com.philokun.stardustbackend.model.vo.user.UserLoginVO;
import com.philokun.stardustbackend.model.vo.user.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;
import com.philokun.stardustbackend.model.entity.User;

public interface UserService extends IService<User> {

    /**
     * 登录
     * 
     * @param request 登录请求
     * @return 用户信息
     */
    UserLoginVO login(UserLoginRequest request);

    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 注册
     * 
     * @param request 注册请求
     * @return 用户信息
     */
    UserRegisterVO register(UserRegisterRequest request);

    String uploadAvatar(MultipartFile file, String userId);

}