package com.philokun.stardustbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
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
        // 只拷贝必要字段
        userRegisterVO.setId(user.getId().toString());
        userRegisterVO.setUsername(user.getUsername());
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
        userLoginVO.setId(user.getId().toString());
        userLoginVO.setUsername(user.getUsername());
        userLoginVO.setAvatar(user.getAvatar());
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
        userInfoVO.setId(user.getId().toString());
        userInfoVO.setUsername(user.getUsername());
        // 如果没有头像，使用默认头像
        String avatar = user.getAvatar();
        if (avatar == null || avatar.trim().isEmpty()) {
            avatar = "default/avatar.png";
        }
        // 从 MinIO 获取完整的头像 URL
        String avatarUrl = minioUtils.getFileUrl(minioConfig.getBucketName(), avatar);
        userInfoVO.setAvatar(avatarUrl);
        return userInfoVO;
    }

    @Override
    public String uploadAvatar(MultipartFile file, String userId) {
        if (file == null || file.isEmpty())
            throw new RuntimeException("文件不能为空");
        // 查询当前用户
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        // 删除旧头像（非默认头像）
        String oldAvatar = user.getAvatar();
        if (oldAvatar != null && !oldAvatar.equals("default/avatar.png")) {
            try {
                minioUtils.deleteFile(minioConfig.getBucketName(), oldAvatar);
            } catch (Exception e) {
                // 可选：记录日志，不影响后续流程
            }
        }
        // 上传新头像
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        String objectName = "avatar/" + userId + "/" + System.currentTimeMillis() + ext;
        try (InputStream in = file.getInputStream()) {
            minioUtils.uploadFile(in, objectName, file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
        // 更新用户表，只存储对象名
        user.setAvatar(objectName);
        this.updateById(user);
        // 返回完整url给前端
        return minioUtils.getFileUrl(minioConfig.getBucketName(), objectName);
    }

}