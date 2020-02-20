package com.debug.steadyjack.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString
public class Product {
    private Integer id;

    private String name;

    private String unit;

    private Double price;

    private Integer stock;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date purchaseDate;

    private Integer isDelete=0;

    /*private Date createTime;

    private Date updateTime;*/
}