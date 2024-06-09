package com.ew.gerocomium.common.config.web;

import com.ew.gerocomium.common.config.file.FileTempPath;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final FileTempPath fileTempPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // swagger资源
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("favicon.ico").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/v2/api-docs").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-*/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 文件资源
        registry.addResourceHandler("/download/**").addResourceLocations("file:" + fileTempPath.downloadPath());
        registry.addResourceHandler("/upload/img/**").addResourceLocations("file:" + fileTempPath.imgSrcPath());
        registry.addResourceHandler("/upload/video/**").addResourceLocations("file:" + fileTempPath.videoSrcPath());
        registry.addResourceHandler("/upload/file/**").addResourceLocations("file:" + fileTempPath.filePath());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否允许cookie
                .allowCredentials(true)
                // 设置允许的请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                // 设置允许的header属性
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }
}
