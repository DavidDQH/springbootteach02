package com.debug.steadyjack.controller;

import com.debug.steadyjack.entity.User;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.mapper.UserMapper;
import com.debug.steadyjack.request.EmployeeRequest;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.service.UserService;
import com.debug.steadyjack.utils.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Administrator on 2018/9/27.
 */
@RestController
public class UserController {

    private static final Logger log= LoggerFactory.getLogger(UserController.class);

    private static final String prefix="user";

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 获取详情
     * @return
     */
    @RequestMapping(value = prefix+"/detail/{userId}",method = RequestMethod.GET)
    public BaseResponse detail(@PathVariable Integer userId){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        if (userId<=0){
            return new BaseResponse(StatusCode.Invalid_Params);
        }

        try {
            //response.setData(userService.getUserInfoV1(userId));
            //response.setData(userService.getUserInfoV2(userId));
            //response.setData(userService.getUserInfoV3(userId));
            //response.setData(userService.getUserInfoV4(userId));
            //response.setData(userService.getUserInfoV5(userId));
            response.setData(userService.getUserInfoV6(userId));


        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail);
            e.printStackTrace();
        }
        return response;
    }



    /**
     * 新增-更新用户信息
     * @return
     */
    @RequestMapping(value = prefix+"/insert/update",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    //@Transactional(rollbackFor = Exception.class)
    public BaseResponse insertUpdate(@RequestBody @Validated EmployeeRequest employeeRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.Invalid_Params);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            if (employeeRequest.getId()!=null && employeeRequest.getId()>0){
                User entity=userMapper.selectByPrimaryKey(employeeRequest.getId());
                BeanUtils.copyProperties(employeeRequest,entity);
                userMapper.updateByPrimaryKeySelective(entity);

                userService.updateCache(entity.getId());
            }else{
                User user=new User();
                BeanUtils.copyProperties(employeeRequest,user);
                userMapper.insertSelective(user);

                userService.updateCache(user.getId());
            }
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail);
            e.printStackTrace();
        }
        return response;
    }


    /**
     * 注册
     * @param employeeRequest
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/register",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse register(@RequestBody @Validated EmployeeRequest employeeRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new BaseResponse(StatusCode.Invalid_Params);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            //userService.register(employeeRequest);
            //userService.registerV2(employeeRequest);
            userService.registerV3(employeeRequest);

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail);
            e.printStackTrace();
        }
        return response;
    }


    /**
     * 验证注册用户邮箱-有效性
     * @return
     */
    @RequestMapping(value = prefix+"/register/validate",method = RequestMethod.GET)
    public BaseResponse registerValidate(@RequestParam String userName,@RequestParam String timestamp,@RequestParam String encryptStr){
        try {
            String resStr=AESUtil.decrypt(encryptStr);
            log.info("验证注册用户邮箱-有效性!!： {},{},{},{}",userName,timestamp,encryptStr,resStr);

            Map<String,String> resMap=objectMapper.readValue(resStr,Map.class);
            log.info("resMap:{} ",resMap);


            String userNameDecrypt=resMap.get("userName");
            String timeStampDecrypt=resMap.get("timestamp");

            //TODO：校验其是否在有效时间内
            final String key=userNameDecrypt + timeStampDecrypt;
            if (stringRedisTemplate.hasKey(key)){
                return new BaseResponse(StatusCode.Validate_UserName_Expire);
            }


            if (userName.equals(userNameDecrypt) && timestamp.equals(timeStampDecrypt)){
                return new BaseResponse(StatusCode.Success);
            }else{
                return new BaseResponse(StatusCode.Fail);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new BaseResponse(StatusCode.Fail);
        }
    }
}





































































