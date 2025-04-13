package com.example.eleweatherapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "meteo.api")
@Getter
@Setter
public class MeteoApiConfig {
    private String url;
    private double longitude;
    private double latitude;
    private String timezone;
}
