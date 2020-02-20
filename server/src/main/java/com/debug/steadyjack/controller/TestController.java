package com.debug.steadyjack.controller;

import com.debug.steadyjack.entity.OrderRecord;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.exception.GlobalSystemException;
import com.debug.steadyjack.exception.NotFoundException;
import com.debug.steadyjack.mapper.OrderRecordMapper;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.scheduler.MailOrderRecordScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

/**
 * Created by Administrator on 2018/9/23.
 */
@RestController
public class TestController {

    private static final Logger log= LoggerFactory.getLogger(TestController.class);

    private static final String prefix="test";

    @Autowired
    private MailOrderRecordScheduler mailOrderRecordScheduler;

    @Autowired
    private OrderRecordMapper orderRecordMapper;


    @RequestMapping(value = prefix+"/mail/order/record",method = RequestMethod.GET)
    public BaseResponse testMailOrderRecord(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        mailOrderRecordScheduler.sendOrderRecordAppendixInfo();
        return response;
    }

    @RequestMapping(value = prefix+"/exception/advice/{id}",method = RequestMethod.GET)
    public BaseResponse exceptionAdvice(@PathVariable Integer id) throws GlobalSystemException{
        OrderRecord record=orderRecordMapper.selectByPrimaryKey(id);
        if (id==null || id<=0 || record==null){
            throw new GlobalSystemException("请求实体信息不存在");
        }

        BaseResponse response=new BaseResponse(StatusCode.Success);
        response.setData(record);
        return response;
    }

    @RequestMapping(value = prefix+"/exception/advice/not/found/{id}",method = RequestMethod.GET)
    public BaseResponse exceptionNotFound(@PathVariable Integer id) throws NotFoundException{
        OrderRecord record=orderRecordMapper.selectByPrimaryKey(id);
        if (id==null || id<=0 || record==null){
            throw new NotFoundException("请求实体信息不存在V2");
        }

        BaseResponse response=new BaseResponse(StatusCode.Success);
        response.setData(record);
        return response;
    }
}






































