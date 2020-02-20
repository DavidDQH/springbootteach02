package com.debug.steadyjack.service;

import com.debug.steadyjack.ExcelUtil;
import com.debug.steadyjack.entity.Product;
import com.debug.steadyjack.enums.WorkBookVersion;
import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/24.
 */
@Service
public class PoiService {

    private static final Logger log= LoggerFactory.getLogger(PoiService.class);

    private static final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private Environment env;



    //TODO：dataList.subList(0,100)
    //TODO：截取 0-100 总数100 调用一次fillExcelSheetData
    //TODO：截取 100-200 总数 100  调用一次fillExcelSheetData

    /**
     * 分sheet导出实战
     * @param dataList
     * @param headers
     * @param sheetName
     * @return
     */
    public Workbook manageSheet(List<Map<Integer, Object>> dataList,String[] headers, String sheetName){
        final Integer sheetSize=6;
        //final Integer sheetSize=env.getProperty("poi.product.excel.sheet.size",Integer.class);
        int dataTotal=dataList.size();

        int sheetTotal = (dataTotal%sheetSize==0)? dataTotal/sheetSize : (dataTotal/sheetSize + 1);
        int start=0;
        int end=sheetSize;

        List<Map<Integer, Object>> subList;
        Workbook wb=new HSSFWorkbook();

        for (int i=0;i<sheetTotal;i++){
            subList=dataList.subList(start,end);
            wb=this.fillExcelSheetDataV2(subList,headers,sheetName+"_"+(i+1),wb);

            start += sheetSize;
            end += sheetSize;
            if (end>=dataTotal){
                end=dataTotal;
            }
        }
        return wb;
    }


    /**
     * 填充数据到excel的sheet中 - 分sheet实战
     * @param dataList
     * @param headers
     * @param sheetName
     */
    public Workbook fillExcelSheetDataV2(List<Map<Integer, Object>> dataList, String[] headers, String sheetName,Workbook wb){
        Sheet sheet=wb.createSheet(sheetName);

        //TODO：创建sheet的第一行数据-即excel的表头
        Row headerRow=sheet.createRow(0);
        for(int i=0;i<headers.length;i++){
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        //TODO：从第二行开始塞入真正的数据列表
        int rowIndex=1;
        Row row;
        Object obj;

        if (dataList!=null && dataList.size()>0){
            for(Map<Integer, Object> rowMap:dataList){
                try {
                    row=sheet.createRow(rowIndex++);

                    //TODO：遍历表头行-每个key -> 取到实际的value
                    for(int i=0;i<headers.length;i++){
                        obj=rowMap.get(i);

                        if (obj==null) {
                            log.debug("--");

                            row.createCell(i).setCellValue("");
                        }else if (obj instanceof Date) {
                            String tempDate=simpleDateFormat.format((Date)obj);
                            row.createCell(i).setCellValue((tempDate==null)?"":tempDate);
                        }else {
                            row.createCell(i).setCellValue(String.valueOf(obj));
                        }
                    }
                } catch (Exception e) {
                    log.debug("充数据到excel的sheet中 - 分sheet实战 发生异常： ",e.fillInStackTrace());
                }
            }
        }


        return wb;
    }










    /**
     * 填充数据到excel的sheet中 - v1
     * @param dataList
     * @param headers
     * @param sheetName
     */
    public Workbook fillExcelSheetData(List<Map<Integer, Object>> dataList, String[] headers, String sheetName){
        Workbook wb=new HSSFWorkbook();
        Sheet sheet=wb.createSheet(sheetName);

        //TODO：创建sheet的第一行数据-即excel的表头
        Row headerRow=sheet.createRow(0);
        for(int i=0;i<headers.length;i++){
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        //TODO：从第二行开始塞入真正的数据列表
        int rowIndex=1;
        Row row;
        Object obj;

        if (dataList!=null && dataList.size()>0){
            for(Map<Integer, Object> rowMap:dataList){
                try {
                    row=sheet.createRow(rowIndex++);

                    //TODO：遍历表头行-每个key -> 取到实际的value
                    for(int i=0;i<headers.length;i++){
                        obj=rowMap.get(i);

                        if (obj==null) {
                            row.createCell(i).setCellValue("");
                        }else if (obj instanceof Date) {
                            String tempDate=simpleDateFormat.format((Date)obj);
                            row.createCell(i).setCellValue((tempDate==null)?"":tempDate);
                        }else {
                            row.createCell(i).setCellValue(String.valueOf(obj));
                        }
                    }
                } catch (Exception e) {
                    log.debug("excel sheet填充数据 发生异常： ",e.fillInStackTrace());
                }
            }
        }

        return wb;
    }


    /**
     * 根据file与后缀名区分获取workbook实例
     * @param file
     * @param suffix
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook(MultipartFile file, String suffix) throws Exception{
        Workbook wk=null;
        if (WorkBookVersion.WorkBook2003Xls.getCode().equalsIgnoreCase(suffix)){
            wk=new HSSFWorkbook(file.getInputStream());
        }else if (WorkBookVersion.WorkBook2007Xlsx.getCode().equalsIgnoreCase(suffix)) {
            wk=new XSSFWorkbook(file.getInputStream());
        }
        return wk;
    }


    /**
     * 读取excel数据
     * @param wb
     * @return
     * @throws Exception
     */
    public List<Product> readExcelData(Workbook wb) throws Exception{
        Product product;

        List<Product> products=new ArrayList<Product>();
        Row row;
        int numSheet=wb.getNumberOfSheets();
        if (numSheet>0) {
            for(int i=0;i<numSheet;i++){
                Sheet sheet=wb.getSheetAt(i);
                int numRow=sheet.getLastRowNum();
                if (numRow>0) {
                    for(int j=1;j<=numRow;j++){
                        //TODO：跳过excel sheet表格头部
                        row=sheet.getRow(j);
                        product=new Product();

                        String name= ExcelUtil.manageCell(row.getCell(0), null);
                        String unit=ExcelUtil.manageCell(row.getCell(1), null);
                        Double price=Double.valueOf(ExcelUtil.manageCell(row.getCell(2), null));
                        String stock=ExcelUtil.manageCell(row.getCell(3), null);
                        String remark=ExcelUtil.manageCell(row.getCell(4), null);

                        product.setName(name);
                        product.setUnit(unit);
                        product.setPrice(price);
                        product.setStock((!Strings.isNullOrEmpty(stock) && stock.contains("."))?
                                Integer.valueOf(stock.substring(0,stock.lastIndexOf("."))) :
                                Integer.valueOf(stock));

                        String value=ExcelUtil.manageCell(row.getCell(5), "yyyy-MM-dd");
                        product.setPurchaseDate(simpleDateFormat.parse(value));
                        product.setRemark(remark);

                        products.add(product);
                    }
                }
            }
        }
        log.info("获取数据列表: {} ",products);
        return products;
    }

}



















































