package com.debug.steadyjack.controller;

import com.debug.steadyjack.dto.UserOrderDto;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.doclint.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/9/29.
 */
@RestController
public class UserOrderController {

    private static final Logger log= LoggerFactory.getLogger(EntityController.class);

    private static final String prefix="user/order";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;




    /**
     * 用户下单记录
     * @param userOrderDto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/push",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse pushUserOrder(@RequestBody @Validated UserOrderDto userOrderDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.Invalid_Params);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("用户下单信息： {} ",userOrderDto);

            //TODO：此处可能会有其他业务逻辑


            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("rabbitmq.user.order.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("rabbitmq.user.order.routing.key.name"));

            Message message= MessageBuilder.withBody(objectMapper.writeValueAsBytes(userOrderDto)).setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            rabbitTemplate.convertAndSend(message);

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}

















