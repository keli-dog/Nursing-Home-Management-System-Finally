package com.ew.gerocomium.common.config.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("ignore")
public class IgnoreConfig {
    List<String> ignoreUrl;
    List<String> tokenUrl;
}