package com.debug.steadyjack.annotation.validate;

import com.debug.steadyjack.annotation.SexAnnotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 校验性别字段的自定义校验器
 * Created by Administrator on 2018/9/22.
 */
public class SexValidation implements ConstraintValidator<SexAnnotation,Integer> {

    Set<Integer> sexArr;

    public void initialize(SexAnnotation constraintAnnotation) {
        sexArr=new HashSet<Integer>();
        sexArr.add(1);
        sexArr.add(2);
    }

    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (sexArr.contains(value)){
            return true;
        }
        return false;
    }
}