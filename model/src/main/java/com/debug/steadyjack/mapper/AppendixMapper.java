package com.debug.steadyjack.mapper;

import com.debug.steadyjack.entity.Appendix;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppendixMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Appendix record);

    int insertSelective(Appendix record);

    Appendix selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Appendix record);

    int updateByPrimaryKey(Appendix record);

    List<Appendix> selectModuleAppendix(@Param("moduleType") String moduleType,@Param("recordId") Integer recordId);

    List<Appendix> selectModuleAppendixV2(@Param("moduleType") String moduleType,@Param("recordId") Integer recordId,@Param("rootUrl") String rootUrl);
}