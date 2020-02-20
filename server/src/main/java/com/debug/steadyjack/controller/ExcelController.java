package com.debug.steadyjack.controller;

import com.debug.steadyjack.entity.Product;
import com.debug.steadyjack.enums.StatusCode;
import com.debug.steadyjack.mapper.ProductMapper;
import com.debug.steadyjack.response.BaseResponse;
import com.debug.steadyjack.service.PoiService;
import com.debug.steadyjack.service.ProductService;
import com.debug.steadyjack.service.WebOperationService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/24.
 */
@Controller
public class ExcelController {

    private static final Logger log= LoggerFactory.getLogger(ExcelController.class);

    private static final String prefix="excel";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private PoiService poiService;

    @Autowired
    private Environment env;

    @Autowired
    private WebOperationService webOperationService;




    /**
     * 产品信息列表
     * @param name
     * @return
     */
    @RequestMapping(value = prefix+"/product/list",method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse list(String name){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            List<Product> products=productMapper.selectAll(name);
            response.setData(products);
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 导出Excel
     */
    @RequestMapping(value = prefix+"/product/export",method = RequestMethod.GET)
    public @ResponseBody String export(String name, HttpServletResponse response){
        final String[] headers=new String[]{"名称","单位","单价","库存量","备注","采购日期"};
        List<Product> products=productMapper.selectAll(name);
        try {
            if (products!=null && products.size()>0){
                //TODO：将产品信息列表list->list-map
                List<Map<Integer, Object>> listMap=productService.manageProductList(products);

                //TODO：将list-map塞入真正的excel对应的workbook
                //Workbook wb=poiService.fillExcelSheetData(listMap,headers,env.getProperty("poi.product.excel.sheet.name")); //v1

                //TODO：v2 分sheet导出实战
                Workbook wb=poiService.manageSheet(listMap,headers,env.getProperty("poi.product.excel.sheet.name"));


                //TODO：将excel实例以流的形式写回浏览器
                webOperationService.downloadExcel(response,wb,env.getProperty("poi.product.excel.file.name"));
                return env.getProperty("poi.product.excel.file.name");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 导入excel
     * @param request
     */
    @RequestMapping(value=prefix+"/product/upload",method=RequestMethod.POST,consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public BaseResponse uploadExcel(MultipartHttpServletRequest request){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            MultipartFile file=request.getFile("productFile");
            if (file!=null){
                String fileName=file.getOriginalFilename();
                String suffix=StringUtils.substring(fileName,fileName.lastIndexOf(".")+1);

                //TODO：根据上传的excel文件构造workbook实例-注意区分xls与xlsx版本对应的实例
                Workbook wb=poiService.getWorkbook(file,suffix);

                //TODO：读取上传上来的excel的数据到List<Product>中
                List<Product> products=poiService.readExcelData(wb);
                log.debug("读取excel得到的数据：{} ",products);

                //TODO：插入数据到数据库
                /*for (Product p:products){
                    productMapper.insertSelective(p);
                }*/ //v1

                productMapper.insertBatch(products);

            }else{
                return new BaseResponse(StatusCode.Invalid_Params);
            }
        } catch (Exception e) {
            log.error("上传excel导入数据 发生异常：",e.fillInStackTrace());
            return new BaseResponse(StatusCode.Fail);
        }
        return response;
    }



    /**
     * 导出Excel模板
     */
    @RequestMapping(value = prefix+"/product/export/template",method = RequestMethod.GET)
    public @ResponseBody String exportTemplate(HttpServletResponse response){
        final String[] headers=new String[]{"名称","单位","单价","库存量","备注","采购日期"};
        try {
            Workbook wb=poiService.fillExcelSheetData(null,headers,env.getProperty("poi.product.excel.sheet.name"));
            webOperationService.downloadExcel(response,wb,env.getProperty("poi.product.excel.file.name"));
            return env.getProperty("poi.product.excel.file.name");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

































































