package com.debug.steadyjack;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ImportResource(locations = "classpath:spring/spring-jdbc.xml")
@MapperScan(basePackages = "com.debug.steadyjack.mapper")
@EnableScheduling
@EnableAsync
public class ServerApplication extends SpringBootServletInitializer{

	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper objectMapper=new ObjectMapper();
		return objectMapper;
	}

	//不使用spring boot内嵌tomcat启动方式
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ServerApplication.class);
	}

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(ServerApplication.class, args);
	}
}
