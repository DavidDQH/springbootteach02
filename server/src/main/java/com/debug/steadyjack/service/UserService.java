package com.debug.steadyjack.service;

import com.debug.steadyjack.entity.User;
import com.debug.steadyjack.listener.event.UserRegisterEvent;
import com.debug.steadyjack.mapper.UserMapper;
import com.debug.steadyjack.rabbitmq.message.UserRegisterMessage;
import com.debug.steadyjack.request.EmployeeRequest;
import com.debug.steadyjack.utils.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/9/27.
 */
@Service
public class UserService {

    private static final Logger log= LoggerFactory.getLogger(UserService.class);

    @Autowired
    private Environment env;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper; //序列化为json格式字符串

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;




    /**
     * 用户详情
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV1(Integer userId) throws Exception{
        User user=userMapper.selectByPrimaryKey(userId);
        return user;
    }

    /**
     * 用户详情
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV2(Integer userId) throws Exception{
        //TODO：先查询缓存，再查数据库
        final String key=String.format(env.getProperty("redis.user.info.key"),userId);

        User user;
        if (stringRedisTemplate.hasKey(key)){
            //TODO：key存在于缓存
            String value=stringRedisTemplate.opsForValue().get(key);
            user=objectMapper.readValue(value,User.class);

        }else{
            //TODO：key不存在于缓存->查数据库并存入缓存
            user=userMapper.selectByPrimaryKey(userId);
            if (user!=null){
                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user));
            }
        }

        return user;
    }



    /**
     * 用户详情-缓存雪崩
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV3(Integer userId) throws Exception{
        //TODO：先查询缓存，再查数据库
        final String key=String.format(env.getProperty("redis.user.info.key"),userId);

        User user;
        if (stringRedisTemplate.hasKey(key)){
            //TODO：key存在于缓存
            String value=stringRedisTemplate.opsForValue().get(key);
            user=objectMapper.readValue(value,User.class);

        }else{
            //TODO：key不存在于缓存->查数据库并存入缓存
            user=userMapper.selectByPrimaryKey(userId);
            if (user!=null){
                //stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user),env.getProperty("redis.user.info.timeout",Long.class), TimeUnit.SECONDS);
                Long expire=RandomUtils.nextLong(10,30);
                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user),expire, TimeUnit.SECONDS);
                log.info("过期的随机时间：{} ",expire);
            }
        }

        return user;
    }




    /**
     * 用户详情-缓存穿透
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV4(Integer userId) throws Exception{
        //TODO：先查询缓存，再查数据库
        final String key=String.format(env.getProperty("redis.user.info.key"),userId);

        User user=null;
        if (stringRedisTemplate.hasKey(key)){
            //TODO：key存在于缓存
            String value=stringRedisTemplate.opsForValue().get(key);
            if (!Strings.isNullOrEmpty(value)){
                user=objectMapper.readValue(value,User.class);
            }

        }else{
            //TODO：key不存在于缓存->查数据库并存入缓存
            user=userMapper.selectByPrimaryKey(userId);
            Long expire=RandomUtils.nextLong(10,30);
            if (user!=null){
                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user),expire, TimeUnit.SECONDS);
            }else{
                stringRedisTemplate.opsForValue().set(key,"",expire, TimeUnit.SECONDS);
            }
            log.info("过期的随机时间：{} ",expire);
        }

        return user;
    }


    /**
     * 更新缓存
     * @param userId
     */
    public void updateCache(Integer userId){
        try {
            User user=userMapper.selectByPrimaryKey(userId);
            if (user!=null){
                final String key=String.format(env.getProperty("redis.user.info.key"),userId);

                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(user),RandomUtils.nextLong(10,30),TimeUnit.SECONDS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 用户详情-hash散列存储
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV5(Integer userId) throws Exception{
        final String key=env.getProperty("redis.user.info.hash.key");
        HashOperations<String,String,User> hashOperations=redisTemplate.opsForHash();

        User user;
        if (hashOperations.hasKey(key,String.valueOf(userId))){
            user=hashOperations.get(key,String.valueOf(userId));

        }else{
            user=userMapper.selectByPrimaryKey(userId);
            if (user!=null){
                hashOperations.putIfAbsent(key,String.valueOf(userId),user);
            }
        }

        return user;
    }


    /**
     * 用户详情-hash散列存储
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUserInfoV6(Integer userId) throws Exception{
        final String key=env.getProperty("redis.user.info.hash.key");
        HashOperations<String,String,String> hashOperations=redisTemplate.opsForHash();

        User user=null;
        if (hashOperations.hasKey(key,String.valueOf(userId))){
            String value=hashOperations.get(key,String.valueOf(userId));
            if (!Strings.isNullOrEmpty(value)){
                user=objectMapper.readValue(value,User.class);
            }
        }else{
            user=userMapper.selectByPrimaryKey(userId);
            if (user!=null){
                hashOperations.putIfAbsent(key,String.valueOf(userId),objectMapper.writeValueAsString(user));
            }else{
                hashOperations.putIfAbsent(key,String.valueOf(userId),"");
            }
        }
        //redisTemplate.expire(key,10L,TimeUnit.DAYS);

        return user;
    }


    /**
     * 更新缓存V2
     * @param userId
     */
    public void updateCacheV2(Integer userId){
        try {
            final String key=env.getProperty("redis.user.info.hash.key");
            HashOperations<String,String,String> hashOperations=redisTemplate.opsForHash();

            User user=userMapper.selectByPrimaryKey(userId);
            if (user!=null){
                hashOperations.put(key,String.valueOf(userId),objectMapper.writeValueAsString(user));
            }else{
                hashOperations.put(key,String.valueOf(userId),"");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 注册信息
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(EmployeeRequest request) throws Exception{
        //TODO：录入信息
        User user=new User();
        BeanUtils.copyProperties(request,user);
        userMapper.insertSelective(user);

        //TODO：更新缓存
        this.updateCache(user.getId());

        //TODO：发送邮件
        Map<String,Object> paramsMap= Maps.newHashMap();
        paramsMap.put("userName",user.getUserName());
        paramsMap.put("url","http://www.baidu.com");
        String html=mailService.renderTemplate(env.getProperty("mail.template.file.location.register"),paramsMap);
        mailService.sendHTMLMail("成功入职通知",html,new String[]{user.getEmail()});
    }


    @Transactional(rollbackFor = Exception.class)
    public void registerV2(EmployeeRequest request) throws Exception{
        //TODO：录入信息
        User user=new User();
        BeanUtils.copyProperties(request,user);
        userMapper.insertSelective(user);

        //TODO：异步发送消息
        String url=env.getProperty("system.common.config.domain")+"/teach02/user/register/validate?";
        Long timestamp=System.currentTimeMillis();

        Map<String,String> dataMap=new HashMap<String, String>();
        dataMap.put("userName",user.getUserName());
        dataMap.put("timestamp",String.valueOf(timestamp));
        String dataMapStr=objectMapper.writeValueAsString(dataMap);
        String encryptStr=URLEncoder.encode(AESUtil.encrypt(dataMapStr),"utf-8");
        log.info("加密后的串：{} ",encryptStr);

        String params=String.format("userName=%s&timestamp=%s&encryptStr=%s",user.getUserName(),timestamp,encryptStr);
        UserRegisterEvent event=new UserRegisterEvent(this,user,url+params);
        publisher.publishEvent(event);
    }


    /**
     * rabbitmq-消息异步通信-业务服务异步解耦
     * @param request
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void registerV3(EmployeeRequest request) throws Exception{
        //TODO：录入信息
        User user=new User();
        BeanUtils.copyProperties(request,user);
        userMapper.insertSelective(user);

        //TODO：异步发送消息
        String url=env.getProperty("system.common.config.domain")+"/teach02/user/register/validate?";
        Long timestamp=System.currentTimeMillis();

        Map<String,String> dataMap=new HashMap<String, String>();
        dataMap.put("userName",user.getUserName());
        dataMap.put("timestamp",String.valueOf(timestamp));
        String dataMapStr=objectMapper.writeValueAsString(dataMap);
        String encryptStr=URLEncoder.encode(AESUtil.encrypt(dataMapStr),"utf-8");

        String params=String.format("userName=%s&timestamp=%s&encryptStr=%s",user.getUserName(),timestamp,encryptStr);

        //TODO：发送消息
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        UserRegisterMessage message=new UserRegisterMessage(user,url+params);
        Message msg=MessageBuilder.withBody(objectMapper.writeValueAsBytes(message)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        rabbitTemplate.send(env.getProperty("rabbitmq.user.register.exchange.name"),env.getProperty("rabbitmq.user.register.routing.key.name"),msg);

        //TODO：加上30min的限制-开发时可以采用3min
        final Long expire=3L;
        final String key=user.getUserName() + String.valueOf(timestamp);
        stringRedisTemplate.opsForValue().set(key,user.getUserName(),expire,TimeUnit.MINUTES);
    }


}































