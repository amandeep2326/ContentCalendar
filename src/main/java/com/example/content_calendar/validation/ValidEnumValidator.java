package com.example.content_calendar.validation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEnumValidator implements ConstraintValidator<ValidEnum, Object> {

    private Set<String> allowedValues;

    @Override
    public void initialize(ValidEnum annotation) {
        allowedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // let @NotNull / @NotBlank handle nulls
        }
        String stringValue = value.toString().toUpperCase();
        boolean valid = allowedValues.contains(stringValue);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Invalid value '" + value + "'. Allowed values are: " + allowedValues
            ).addConstraintViolation();
        }
        return valid;
    }
}
