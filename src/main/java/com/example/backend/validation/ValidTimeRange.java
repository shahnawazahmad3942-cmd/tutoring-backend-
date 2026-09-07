package com.example.backend.validation;
/* Class-level constraint asserting that a slot's end time is after its start time. */

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TimeRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimeRange {

    String message() default "End time must be after start time";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload() default {};
    
}
