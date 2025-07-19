package com.philokun.stardustbackend.utils;

import com.philokun.stardustbackend.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import io.minio.GetObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import io.minio.http.Method;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class MinioUtils {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 上传文件
     *
     * @param inputStream 文件输入流
     * @param objectName  对象名称 (文件路径 + 文件名)
     * @param contentType 文件MIME类型
     * @return 文件URL
     * @throws MinioException Minio操作异常
     */
    public String uploadFile(InputStream inputStream, String objectName, String contentType) throws MinioException {
        String bucketName = minioConfig.getBucketName();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build());
            return getFileUrl(bucketName, objectName);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            // 包装其他异常为 MinioException
            throw new MinioException(e.getMessage());
        }
    }

    /**
     * 获取文件URL
     *
     * @param bucketName bucket名称
     * @param objectName 对象名称
     * @return 文件URL
     */
    public String getFileUrl(String bucketName, String objectName) {
        try {
            // 使用 getPresignedObjectUrl 生成预签名 URL，设置过期时间 (例如 7 天)
            return minioClient.getPresignedObjectUrl(
                    io.minio.GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(60 * 60 * 24 * 7) // 过期时间，单位秒 (这里设置为 7 天)
                            .method(Method.GET)
                            .build());
        } catch (Exception e) {
            // 处理生成 URL 过程中的异常
            e.printStackTrace(); // 打印异常堆栈方便调试
            return null; // 或者返回一个默认图片 URL
        }
    }

    /**
     * 下载文件
     *
     * @param bucketName bucket名称
     * @param objectName 对象名称
     * @return 文件输入流
     * @throws MinioException Minio操作异常
     */
    public InputStream downloadFile(String bucketName, String objectName) throws MinioException {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 对象名称
     * @throws MinioException Minio操作异常
     */
    public void deleteFile(String bucketName, String objectName) throws MinioException {
        try {
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }
} 