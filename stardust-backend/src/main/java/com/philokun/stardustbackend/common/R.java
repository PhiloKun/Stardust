package com.philokun.stardustbackend.common;

import lombok.Data;

@Data
public class R<T> {
    private int code; // 业务状态码，例如 200 成功，400 失败等
    private String message; // 响应消息
    private T data; // 实际返回的数据

    // 私有构造函数，强制使用静态工厂方法创建实例
    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应的静态工厂方法
    public static <T> R<T> success(T data) {
        return new R<>(200, "成功", data);
    }

    public static <T> R<T> success(String message, T data) {
        return new R<>(200, message, data);
    }

    public static <T> R<T> success() {
        return new R<>(200, "成功", null);
    }

    // 失败响应的静态工厂方法
    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }

     public static <T> R<T> error(int code, String message, T data) {
        return new R<>(code, message, data);
    }
} 