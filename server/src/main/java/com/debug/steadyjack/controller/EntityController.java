package com.debug.steadyjack.controller;

import com.debug.steadyjack.config.ConfigEntity;
import com.debug.steadyjack.config.ConfigEntityV2;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/9/15.
 */
@RestController
public class EntityController {

    private static final Logger log= LoggerFactory.getLogger(EntityController.class);

    private static final String prefix="entity";

    @Autowired
    private ConfigEntity configEntity;

    @Autowired
    private ConfigEntityV2 configEntityV2;

    /**
     * @return
     */
    @RequestMapping(value = prefix+"/info",method = RequestMethod.GET)
    public BaseResponse detail(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("对象实体信息： {} ",configEntity);


        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @return
     */
    @RequestMapping(value = prefix+"/info/v2",method = RequestMethod.GET)
    public BaseResponse detailV2(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("对象实体信息-lombok： {} ",configEntityV2);


        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}






















