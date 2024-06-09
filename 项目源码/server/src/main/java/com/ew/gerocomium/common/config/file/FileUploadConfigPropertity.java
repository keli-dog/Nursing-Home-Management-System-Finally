package com.ew.gerocomium.common.config.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传路径配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "filesave")
public class FileUploadConfigPropertity {
    private String windows;
    private String macos;
    private String linux;
    private String uploadHead;
    private String localHead;
}
