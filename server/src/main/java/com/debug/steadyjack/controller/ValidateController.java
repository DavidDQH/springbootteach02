package com.debug.steadyjack.controller;

import com.debug.steadyjack.dto.ProductMapperDto;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.request.Product;
import com.debug.steadyjack.request.UserRequest;
import com.debug.steadyjack.response.BaseResponse;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/9/22.
 */
@RestController
public class ValidateController {
    private static final Logger log= LoggerFactory.getLogger(ProductController.class);

    private static final String prefix="validate";

    @RequestMapping(value = prefix+"/insert",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse insert(@RequestBody @Validated UserRequest userRequest, BindingResult result){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            /*if (Strings.isNullOrEmpty(userRequest.getName()) || userRequest.getSex()==null){
                return new BaseResponse(StatusCode.Invalid_Params);
            }*/
            //log.info("校验结果：{} ",result.getAllErrors().toArray());

            if (result.hasErrors()){
                return new BaseResponse(StatusCode.Invalid_Params);
            }
            log.info("接收前端数据；{} ",userRequest);

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
            e.printStackTrace();
        }
        return response;
    }



    @RequestMapping(value = prefix+"/insert/v2",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse insertV2(@RequestBody @Validated UserRequest userRequest, BindingResult result){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            if (result.hasErrors()){
                return new BaseResponse(StatusCode.Invalid_Params);
            }
            log.info("接收前端数据v2；{} ",userRequest);

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}




























