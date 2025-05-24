package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.philokun.stardustbackend.mapper.UserMapper;
import com.philokun.stardustbackend.model.dto.user.UserLoginRequest;
import com.philokun.stardustbackend.model.dto.user.UserRegisterRequest;
import com.philokun.stardustbackend.model.entity.User;
import com.philokun.stardustbackend.model.vo.user.UserRegisterVO;
import com.philokun.stardustbackend.model.vo.user.UserInfoVO;
import com.philokun.stardustbackend.model.vo.user.UserLoginVO;
import com.philokun.stardustbackend.service.UserService;
import com.philokun.stardustbackend.utils.JwtUtils;
import com.philokun.stardustbackend.config.MinioConfig;
import com.philokun.stardustbackend.utils.MinioUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final MinioConfig minioConfig;
    private final MinioUtils minioUtils;

    @Override
    @Transactional
    public UserRegisterVO register(UserRegisterRequest request) {

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAvatar("default/avatar.png");
        userMapper.insert(user);
        UserRegisterVO userRegisterVO = new UserRegisterVO();
        BeanUtils.copyProperties(user, userRegisterVO);
        userRegisterVO.setToken(jwtUtils.generateToken(user.getId()));
        // 生成token并返回用户信息
        return userRegisterVO;
    }

    @Override
    public UserLoginVO login(UserLoginRequest request) {
        // 查找用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVO);
        userLoginVO.setToken(jwtUtils.generateToken(user.getId()));
        return userLoginVO;
    }



    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        // 从 MinIO 获取完整的头像 URL
        String avatarUrl = minioUtils.getFileUrl(minioConfig.getBucketName(), user.getAvatar());
        userInfoVO.setAvatar(avatarUrl);
        return userInfoVO;
    }


} 