package com.debug.steadyjack.controller;

import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.response.BaseResponse;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Administrator on 2018/9/14.
 */
@RestController
public class EnvironmentController {

    private static final Logger log= LoggerFactory.getLogger(EnvironmentController.class);

    private static final String prefix="environment";

    @Autowired
    private Environment env;

    @Value("${sample.user.name}")
    private String name;

    @Value("${sample.user.age}")
    private Integer age;


    /**
     * @return
     */
    @RequestMapping(value = prefix+"/detail",method = RequestMethod.GET)
    public BaseResponse detail(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            Map<String,Object> resMap= Maps.newHashMap();
            resMap.put("id",env.getProperty("sample.user.id"));
            resMap.put("name",env.getProperty("sample.user.name"));
            resMap.put("age",env.getProperty("sample.user.age"));

            resMap.put("name2",name);
            resMap.put("age2",age);

            response.setData(resMap);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

}
























