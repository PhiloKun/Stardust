package com.philokun.stardustbackend.service;

import com.philokun.stardustbackend.model.dto.user.UserLoginRequest;
import com.philokun.stardustbackend.model.dto.user.UserRegisterRequest;
import com.philokun.stardustbackend.model.vo.user.UserRegisterVO;
import com.philokun.stardustbackend.model.vo.user.UserVO;

public interface UserService {
    UserVO login(UserLoginRequest request);
    UserVO getUserInfo(Long userId);
    UserRegisterVO register(UserRegisterRequest request);
   
} 