package com.philokun.stardustbackend.model.dto.user;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private String username;

    private String password;

    private String confirmPassword;
}