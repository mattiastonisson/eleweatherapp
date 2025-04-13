package com.example.eleweatherapp.validation;

import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AggregatedDataParamsValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return AggregatedDataParams.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        AggregatedDataParams params = (AggregatedDataParams) target;
        if (params.startDate().isAfter(params.endDate())) {
            errors.rejectValue("startDate", "startDate.after.endDate", "Start date cannot be after end date");
        }
    }
}
