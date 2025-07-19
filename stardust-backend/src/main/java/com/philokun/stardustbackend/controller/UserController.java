package com.philokun.stardustbackend.controller;

import com.philokun.stardustbackend.common.R;
import com.philokun.stardustbackend.model.dto.user.UserLoginRequest;
import com.philokun.stardustbackend.model.dto.user.UserRegisterRequest;
import com.philokun.stardustbackend.model.entity.User;
import com.philokun.stardustbackend.model.vo.user.UserRegisterVO;
import com.philokun.stardustbackend.model.vo.user.UserLoginVO;
import com.philokun.stardustbackend.model.vo.user.UserInfoVO;
import com.philokun.stardustbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public R<UserRegisterVO> register(@RequestBody UserRegisterRequest request) {
        UserRegisterVO userRegisterVO = userService.register(request);
        return R.success("注册成功", userRegisterVO);
    }

    @PostMapping("/login")
    public R<UserLoginVO> login(@RequestBody UserLoginRequest request) {
        UserLoginVO userLoginVO = userService.login(request);
        return R.success("登录成功", userLoginVO);
    }

    @GetMapping("/{id}")
    public R<UserInfoVO> getUserInfo(@PathVariable Long id) {
        UserInfoVO userInfoVO = userService.getUserInfo(id);
        return R.success("获取用户信息成功", userInfoVO);
    }

    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        String avatarUrl = userService.uploadAvatar(file, userId);
        return R.success("上传成功", avatarUrl);
    }

    @PostMapping("/update")
    public R<Boolean> updateUser(@RequestBody User user) {
        boolean result = userService.updateById(user);
        return R.success("更新成功", result);
    }
}