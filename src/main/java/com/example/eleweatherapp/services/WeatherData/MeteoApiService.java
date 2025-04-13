package com.example.eleweatherapp.services.WeatherData;

import com.example.eleweatherapp.config.MeteoApiConfig;
import com.example.eleweatherapp.config.RestTemplateConfig;
import com.example.eleweatherapp.dtos.WeatherData.MeteoApiResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class MeteoApiService {
    private final Logger logger = LoggerFactory.getLogger(MeteoApiService.class);
    private final MeteoApiConfig meteoApiConfig;
    private final RestTemplate restTemplate;

    public MeteoApiService (MeteoApiConfig meteoApiConfig, RestTemplateConfig restTemplate) {
        this.meteoApiConfig = meteoApiConfig;
        this.restTemplate = restTemplate.restTemplate();
    }


    public MeteoApiResponseDto fetchData (LocalDate startDate, LocalDate endDate) {
        String url = this.buildUrl(startDate.toString(), endDate.toString());
        logger.info("Fetching data from {}", url);
        return restTemplate.getForObject(url, MeteoApiResponseDto.class);
    }

    private String buildUrl (String startDate, String endDate) {
        return String.format(
            "%s?latitude=%.2f&longitude=%.2f&start_date=%s&end_date=%s&hourly=temperature_2m&timezone=%s",
            meteoApiConfig.getUrl(),
            meteoApiConfig.getLatitude(),
            meteoApiConfig.getLongitude(),
            startDate,
            endDate,
            meteoApiConfig.getTimezone()
        );
    }
}
