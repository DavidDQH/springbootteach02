package com.debug.steadyjack.controller;

import com.debug.steadyjack.entity.OrderRecord;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.mapper.OrderRecordMapper;
import com.debug.steadyjack.request.OrderRecordInsertUpdateDto;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.service.OrderRecordService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/13.
 */
@RestController
public class OrderRecordController {

    private static final Logger log= LoggerFactory.getLogger(OrderRecordController.class);

    private static final String prefix="order/record";

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Autowired
    private OrderRecordService orderRecordService;


    /**
     * 查询1
     * @return
     */
    @RequestMapping(value = prefix+"/detail/{id}",method = RequestMethod.GET)
    public BaseResponse detail(@PathVariable Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            OrderRecord record=orderRecordMapper.selectByPrimaryKey(id);
            response.setData(record);
        }catch (Exception e){
            log.error("查询发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 查询2
     * @return
     */
    @RequestMapping(value = prefix+"/list",method = RequestMethod.GET)
    public BaseResponse list(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            /*List<OrderRecord> dataList=orderRecordService.list();
            response.setData(dataList);*/

            List<OrderRecord> dataList=orderRecordService.list();
            Map<String,Object> resMap= Maps.newHashMap();
            resMap.put("dataList",dataList);

            response.setData(resMap);
        }catch (Exception e){
            log.error("查询2发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 新增
     * @return
     */
    @RequestMapping(value = prefix+"/save",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse insert(@RequestBody OrderRecordInsertUpdateDto dto){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("接收数据：{} ",dto);

            //TODO：进行属于你自己的控制层层面的校验以及处理


            orderRecordService.insert(dto);
        }catch (Exception e){
            log.error("新增发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    /**
     * 更新
     * @return
     */
    @RequestMapping(value = prefix+"/update",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse update(@RequestBody OrderRecordInsertUpdateDto dto){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("接收数据：{} ",dto);

            //TODO：进行属于你自己的控制层层面的校验以及处理
            OrderRecord entity=orderRecordMapper.selectByPrimaryKey(dto.getId());
            if (entity==null){
                return new BaseResponse(StatusCode.Entity_Not_Exist);
            }

            orderRecordService.update(entity,dto);
        }catch (Exception e){
            log.error("更新发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}









































