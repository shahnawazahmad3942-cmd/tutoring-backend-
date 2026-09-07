package com.example.backend.validation;

/* Validates the ValidTimeRange constraint on a SlotRequest. */

import com.example.backend.dto.slot.SlotRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, SlotRequest> {

    @Override
    public boolean isValid(SlotRequest request, ConstraintValidatorContext context) {

        if(request.startTime() == null || request.endTime() == null) {
            return true;
        }

        if(request.endTime().isAfter(request.startTime())) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        context
        .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode("endTime")
        .addConstraintViolation();

        return false;
    }
    
}
