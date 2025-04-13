package com.example.eleweatherapp.config;

import com.example.eleweatherapp.validation.interceptors.ValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {
    private final ValidationInterceptor validationInterceptor;

    public ApiConfig (ValidationInterceptor validationInterceptor) {
        this.validationInterceptor = validationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validationInterceptor).addPathPatterns("api/aggregated");
    }
}
