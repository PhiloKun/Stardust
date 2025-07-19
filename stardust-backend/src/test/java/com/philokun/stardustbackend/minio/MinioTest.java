package com.philokun.stardustbackend.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
public class MinioTest {

    @Autowired
    private MinioClient minioClient;


    @Test
    public void testConnection() throws Exception {
        // 测试连接是否成功
        List<Bucket> buckets = minioClient.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.name());
        }
        System.out.println("MinIO连接成功！");
    }

    @Test
    public void testFileUpload() throws Exception {
        String bucketName = "stardust"; // 替换为您的bucket名称
        String objectName = "default/avatar.png"; // 替换为您的对象名称（例如：default/avatar.png）
        String localFilePath = "D:\\Code\\projects\\Stardust\\stardust-frontend\\public\\logo.png"; // 替换为您本地文件的实际路径

        try (InputStream is = new FileInputStream(localFilePath)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, is.available(), -1)
                            .contentType("application/octet-stream") // 根据文件类型设置正确的Content-Type
                            .build());

            System.out.println("文件上传成功：" + objectName + " 到bucket " + bucketName);
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            throw e;
        }
    }
}