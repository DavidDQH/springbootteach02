package com.debug.steadyjack.controller;

import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.exception.GlobalSystemException;
import com.debug.steadyjack.exception.NotFoundException;
import com.debug.steadyjack.response.BaseResponse;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 类似于spring的aop
 * Created by Administrator on 2018/9/23.
 */
@ControllerAdvice
public class GlobalExceptionController {

    private static final Logger log= LoggerFactory.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(value = GlobalSystemException.class)
    @ResponseBody
    public BaseResponse systemException(Exception e, HttpServletRequest request){
        BaseResponse response=new BaseResponse(StatusCode.Fail);
        Map<String,Object> resMap= Maps.newHashMap();
        resMap.put("uri",request.getRequestURI());
        resMap.put("exp",e.getMessage());

        response.setData(resMap);
        return response;
    }


    @ExceptionHandler(value = NotFoundException.class)
    public String notFoundPage(Exception e, HttpServletRequest request){
        log.info("异常信息：{} ",e.getMessage());
        request.setAttribute("errorInfo",e.getMessage());
        return "notFound";
    }


}
































