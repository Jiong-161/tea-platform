package com.tea.content.Controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.tea.common.result.Result;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/content/upload")
public class UploadController {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 文件上传接口
     * Sentinel资源: fileUpload — 限流防止磁盘被打满
     */
    @PostMapping
    @SentinelResource(value = "fileUpload",
            blockHandler = "uploadBlockHandler")
    public Result<String> upload(
            @RequestParam("file") MultipartFile file)
            throws Exception {

        // 清理文件名：去除首尾空格、替换中间空格为下划线
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            originalFilename = originalFilename.trim().replaceAll("\\s+", "_");
        }

        String fileName =
                UUID.randomUUID() + "-"
                        + originalFilename;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(
                                file.getInputStream(),
                                file.getSize(),
                                -1
                        )
                        .contentType(file.getContentType())
                        .build()
        );

        String url =
                "http://localhost:9000/"
                        + bucketName
                        + "/"
                        + fileName;

        return Result.success(url);
    }

    /**
     * 文件上传被限流时的降级方法
     */
    public Result<String> uploadBlockHandler(MultipartFile file, BlockException e) {
        log.warn("[Sentinel降级] 文件上传被拦截: type={}", e.getClass().getSimpleName());
        return Result.error(429, "上传操作过于频繁，请稍后再试");
    }
}