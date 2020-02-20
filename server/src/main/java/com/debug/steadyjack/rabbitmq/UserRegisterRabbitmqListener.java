package com.debug.steadyjack.rabbitmq;

import com.debug.steadyjack.entity.User;
import com.debug.steadyjack.rabbitmq.message.UserRegisterMessage;
import com.debug.steadyjack.service.MailService;
import com.debug.steadyjack.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Administrator on 2018/9/28.
 */
@Component
public class UserRegisterRabbitmqListener {

    private static final Logger log= LoggerFactory.getLogger(UserRegisterRabbitmqListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private Environment env;


    @RabbitListener(queues = "${rabbitmq.user.register.queue.name}",containerFactory = "singleListenerContainer")
    public void consume(@Payload byte[] msg){
        try {
            UserRegisterMessage message=objectMapper.readValue(msg, UserRegisterMessage.class);
            log.info("监听到的消息： {} ",message);


            User user=message.getUser();
            if (user!=null){
                log.info("");
                userService.updateCache(user.getId());

                Map<String,Object> paramsMap= Maps.newHashMap();
                paramsMap.put("userName",user.getUserName());
                paramsMap.put("url",message.getUrl());
                String html=mailService.renderTemplate(env.getProperty("mail.template.file.location.register"),paramsMap);
                mailService.sendHTMLMail("成功入职通知",html,new String[]{user.getEmail()});
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
















































