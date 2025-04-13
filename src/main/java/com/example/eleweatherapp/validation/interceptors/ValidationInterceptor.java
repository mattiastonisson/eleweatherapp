package com.example.eleweatherapp.validation.interceptors;

import com.example.eleweatherapp.utils.api.ApiValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.util.stream.Collectors;

@Component
public class ValidationInterceptor extends WebRequestHandlerInterceptorAdapter {
    private final Validator validator;

    public ValidationInterceptor (WebRequestInterceptor requestInterceptor, Validator validator) {
        super(requestInterceptor);
        this.validator = validator;
    }

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Object[] methodParams = handlerMethod.getMethodParameters();

        for (Object param : methodParams) {
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(param, "param");
            ValidationUtils.invokeValidator(validator, param, errors);

            if (errors.hasErrors()) {
                String errorMessage = errors.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
                throw new ApiValidationException("Validation failed: " + errorMessage);
            }
        }

        return true;
    }
}
