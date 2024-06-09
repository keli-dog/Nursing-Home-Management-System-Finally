package com.ew.gerocomium.common.config.swagger;

import com.ew.gerocomium.common.constant.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket(Environment environment) {
        Profiles profiles = Profiles.of("dev", "test", "prod");
        // 判断是否处于自己设置的环境中
        boolean flag = environment.acceptsProfiles(profiles);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(flag)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ew.gerocomium.controller")) // 设置扫描路径
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("数据服务API") // 标题
                .description("") // 描述
                .version("1.0") // 版本
                .contact(new Contact(Constant.EMPEROR_WEN, "", ""))
                .build();
    }
}
