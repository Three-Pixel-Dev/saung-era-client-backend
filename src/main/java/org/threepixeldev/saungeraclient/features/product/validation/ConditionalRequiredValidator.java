package org.threepixeldev.saungeraclient.features.product.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductRequest;

public class ConditionalRequiredValidator implements ConstraintValidator<ConditionalRequired, ProductRequest> {

    @Override
    public void initialize(ConditionalRequired constraintAnnotation) {
    }

    @Override
    public boolean isValid(ProductRequest request, ConstraintValidatorContext context) {
        return true;
    }
}
