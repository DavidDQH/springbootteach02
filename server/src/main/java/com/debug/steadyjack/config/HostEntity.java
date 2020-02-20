package com.debug.steadyjack.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2018/9/16.
 */
@Configuration
@ConfigurationProperties(prefix = "system.entity.variable.host")
@Data
@ToString
public class HostEntity {

    private String ip;
    private String name;

}