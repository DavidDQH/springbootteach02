package com.debug.steadyjack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 配置跨域方式一
 * Created by Administrator on 2018/9/23.
 */
//@Configuration
//public class CustomerMvcConfig extends WebMvcConfigurerAdapter{
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .allowedMethods("*");
//    }
//}