package com.example.eleweatherapp.services;

import com.example.eleweatherapp.dtos.AggregatedDataDto;
import com.example.eleweatherapp.dtos.DailyAverageElectricityPriceDto;
import com.example.eleweatherapp.models.WeatherData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DataAggregationService {
    public List<AggregatedDataDto> aggregate (Map<LocalDate, DailyAverageElectricityPriceDto> dailyAverageElectricityPrices, List<WeatherData> weatherData) {
        return weatherData.stream()
            .filter(wd -> dailyAverageElectricityPrices.containsKey(wd.getDate()))
            .map(wd -> new AggregatedDataDto(
                wd.getDate(),
                wd.getAverageTemperature(),
                dailyAverageElectricityPrices.get(wd.getDate()).price()
            ))
            .toList();
    }
}
