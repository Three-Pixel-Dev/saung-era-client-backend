package org.threepixeldev.saungeraclient.features.product.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalRequiredValidator.class)
@Documented
public @interface ConditionalRequired {
    String message() default "Field is required when productCodeValues is not provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
