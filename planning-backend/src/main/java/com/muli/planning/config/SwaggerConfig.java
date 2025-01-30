package com.muli.planning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket docket(){
        return  new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //标注控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.muli.planning.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    // 配置swagger基本信息
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Planning")
                .description("接口文档")
                .termsOfServiceUrl("https://github.com/muliwu11/planning")
                .contact(new Contact("muli", "https://github.com/muliwu11","xxx@qq.com"))
                .build();
    }
}

