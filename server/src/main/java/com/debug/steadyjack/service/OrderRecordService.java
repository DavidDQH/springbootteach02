package com.debug.steadyjack.service;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.debug.steadyjack.entity.OrderRecord;
import com.debug.steadyjack.mapper.OrderRecordMapper;
import com.debug.steadyjack.request.OrderRecordInsertUpdateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/9/13.
 */
@Service
public class OrderRecordService {

    private static final Logger log= LoggerFactory.getLogger(OrderRecordService.class);

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    public List<OrderRecord> list(){
        return orderRecordMapper.selectAll();
    }

    /**
     * 新增
     * @param dto
     * @return
     */
    //@Transactional(rollbackFor = Exception.class)
    public void insert(OrderRecordInsertUpdateDto dto) throws Exception{
        /*OrderRecord record=new OrderRecord();
        record.setOrderNo(dto.getOrderNo());
        record.setOrderType(dto.getOrderType());*/

        //TODO：采用spring提供的BeanUtils进行操作
        OrderRecord record=new OrderRecord();
        BeanUtils.copyProperties(dto,record);

        orderRecordMapper.insertSelective(record);
    }


    /**
     * 更新
     * @return
     */
    public void update(OrderRecord entity,OrderRecordInsertUpdateDto dto) throws Exception{
        //TODO：采用spring提供的BeanUtils进行操作
        final Integer pk=entity.getId();
        BeanUtils.copyProperties(dto,entity);
        entity.setUpdateTime(new Date());
        entity.setId(pk);

        orderRecordMapper.updateByPrimaryKeySelective(entity);
    }
}























