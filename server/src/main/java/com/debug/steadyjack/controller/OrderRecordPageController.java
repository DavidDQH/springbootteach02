package com.debug.steadyjack.controller;

import com.debug.steadyjack.entity.Appendix;
import com.debug.steadyjack.entity.OrderRecord;
import com.debug.steadyjack.mapper.AppendixMapper;
import com.debug.steadyjack.mapper.OrderRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by Administrator on 2018/9/14.
 */
@Controller
public class OrderRecordPageController {

    private static final Logger log= LoggerFactory.getLogger(OrderRecordController.class);

    private static final String prefix="order/record/page";

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Autowired
    private AppendixMapper appendixMapper;

    @Autowired
    private Environment env;



    @RequestMapping(value = prefix+"/index",method = RequestMethod.GET)
    public String index(){
        return "orderRecord";
    }

    /**
     * 请求塞入模型实体对象数据
     * @param id
     * @param modelMap
     * @return
     */
    @RequestMapping(value = prefix+"/index/v2",method = RequestMethod.GET)
    public String indexV2(Integer id, ModelMap modelMap){
        OrderRecord record=orderRecordMapper.selectByPrimaryKey(id);
        modelMap.addAttribute("record",record);
        return "orderRecord";
    }


    /**
     * 将订单信息以及拥有的附件列表返回给前端
     * @param id
     * @param modelMap
     * @return
     */
    @RequestMapping(value = prefix+"/detail/appendix/list/{id}",method = RequestMethod.GET)
    public String recordDetailAppendix(@PathVariable Integer id, ModelMap modelMap){
        //TODO：需要进行校验

        OrderRecord record=orderRecordMapper.selectByPrimaryKey(id);
        modelMap.addAttribute("record",record);

        /*List<Appendix> appendixList=appendixMapper.selectModuleAppendix("orderRecord",id);
        for (Appendix a:appendixList){
            a.setLocation(env.getProperty("file.upload.root.url") + a.getLocation());
        }
        modelMap.addAttribute("appendixList",appendixList);*/  //v1

        List<Appendix> appendixList=appendixMapper.selectModuleAppendixV2("orderRecord",id,env.getProperty("file.upload.root.url"));
        modelMap.addAttribute("appendixList",appendixList);

        return "orderRecord";
    }

}




















