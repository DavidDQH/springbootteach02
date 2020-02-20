package com.debug.steadyjack.annotation;

import com.debug.steadyjack.annotation.validate.SexValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Administrator on 2018/9/22.
 */
@Documented
@Constraint(
        validatedBy = {SexValidation.class}
)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SexAnnotation {

    String message() default "性别校验取值为：1=男;2=女";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
