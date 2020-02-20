package com.debug.steadyjack.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/16.
 */
@Configuration
@ConfigurationProperties(prefix = "system.entity.variable")
@Data
@ToString
public class ConfigEntityV2 implements Serializable{

    private String userName;
    private String password;
    private String signSystem;
    private HostEntity host;

    private List<String> strings=new ArrayList<String>();
}