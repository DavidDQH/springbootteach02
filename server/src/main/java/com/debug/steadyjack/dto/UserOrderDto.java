package com.debug.steadyjack.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
public class UserOrderDto implements Serializable{

    @NotBlank
    private String orderNo;

    @NotNull
    private Integer userId;

}