package com.debug.steadyjack.controller;

import com.debug.steadyjack.dto.AppendixDto;
import com.debug.steadyjack.entity.Appendix;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.mapper.AppendixMapper;
import com.debug.steadyjack.request.AppendixRequest;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.service.AppendixService;
import com.debug.steadyjack.service.WebOperationService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 附件管理controller
 * Created by Administrator on 2018/9/23.
 */
@RestController
public class AppendixController {

    private static final Logger log= LoggerFactory.getLogger(AppendixController.class);

    private static final String prefix="appendix";

    @Autowired
    private AppendixService appendixService;

    @Autowired
    private AppendixMapper appendixMapper;

    @Autowired
    private Environment env;

    @Autowired
    private WebOperationService webOperationService;



    /**
     * 上传附件
     * @return
     */
    @RequestMapping(value = prefix+"/upload",method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public BaseResponse upload(MultipartHttpServletRequest request){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            String moduleType=request.getParameter("moduleType");
            if (Strings.isNullOrEmpty(moduleType)){
                return new BaseResponse(StatusCode.Invalid_Params);
            }

            MultipartFile file=request.getFile("fileName");
            if (file==null){
                return new BaseResponse(StatusCode.Invalid_Params);
            }
            AppendixDto dto=new AppendixDto();
            dto.setModuleType(moduleType);

            //TODO：通用上传服务
            final String location=appendixService.uploadFile(file,dto);
            log.info("该附件最终上传位置： {} ",location);

            //TODO：保存上传附件记录
            dto.setLocation(location);
            Integer id=appendixService.saveRecord(file,dto);
            response.setData(id);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail);
            e.printStackTrace();
        }
        return response;
    }



    /**
     * 更新附件所属模块: 在此之前，你需要先插入模块记录相关信息
     * @param appendixRequest
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = prefix+"/module/update",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse updateModuleAppendix(@RequestBody @Validated AppendixRequest appendixRequest, BindingResult bindingResult){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            if (bindingResult.hasErrors()){
                return new BaseResponse(StatusCode.Invalid_Params);
            }
            final Integer recordId=appendixRequest.getRecordId();

            String[] appendixIds=StringUtils.split(appendixRequest.getAppendixIds(),",");
            Appendix a;
            for (String aId:appendixIds){

                try {
                    a=appendixMapper.selectByPrimaryKey(Integer.valueOf(Integer.valueOf(aId)));
                    a.setRecordId(recordId);
                    if (a!=null){
                        appendixMapper.updateByPrimaryKeySelective(a);
                    }
                }catch (Exception e){
                    log.error("更新附件所属模块发生异常：当前附件id={} ",aId);
                }

            }
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail);
            e.printStackTrace();
        }
        return response;
    }


    /**
     * 下载附件
     * @throws Exception
     */
    @RequestMapping(value = prefix+"/download/{id}",method = RequestMethod.GET)
    public @ResponseBody String downloadAppendix(@PathVariable Integer id, HttpServletResponse response) throws Exception{
        if (id==null || id<=0){
            return null;
        }
        try {
            //TODO：开发通用文件下载服务
            Appendix appendix=appendixMapper.selectByPrimaryKey(id);
            if (appendix!=null){
                String fileLocation=env.getProperty("file.upload.root.url") + appendix.getLocation();
                InputStream is=new FileInputStream(fileLocation);
                webOperationService.downloadFile(response,is,appendix.getName());
                return appendix.getName();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}














































