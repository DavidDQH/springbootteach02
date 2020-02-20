package com.debug.steadyjack.mapper;

import com.debug.steadyjack.entity.ItemRecord;

public interface ItemRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemRecord record);

    int insertSelective(ItemRecord record);

    ItemRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemRecord record);

    int updateByPrimaryKey(ItemRecord record);
}