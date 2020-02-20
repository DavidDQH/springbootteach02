package com.debug.steadyjack.request;

import com.debug.steadyjack.annotation.SexAnnotation;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2018/9/22.
 */
@Data
@ToString
public class UserRequest {

    /*@NotBlank
    private String name;

    @NotNull
    private Integer sex;

    @NotNull
    @Min(18)
    private Integer age;*/ //v1


    @NotBlank
    private String name;

    @SexAnnotation
    private Integer sex;

    @NotNull
    @Min(18)
    private Integer age;

}









