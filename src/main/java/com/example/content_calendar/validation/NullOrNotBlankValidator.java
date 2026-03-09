package com.example.content_calendar.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null is valid (field is optional), but non-null must not be blank
        return value == null || !value.isBlank();
    }
}
