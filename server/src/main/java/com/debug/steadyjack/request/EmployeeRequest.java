package com.debug.steadyjack.request;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2018/9/28.
 */
@Data
@ToString
public class EmployeeRequest {

    private Integer id;

    @NotBlank
    private String userName;

    private String password;

    @NotBlank
    private String posName;

    private Integer age;

    private String mobile;

    private String profile;

    @NotBlank
    private String email;
}