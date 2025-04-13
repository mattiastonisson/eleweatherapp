package com.example.eleweatherapp.services.WeatherData;

import com.example.eleweatherapp.services.ElectricityPriceService;
import com.example.eleweatherapp.utils.InclusiveDateRange;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MeteoScheduler {
    private final ElectricityPriceService electricityPriceService;
    private final WeatherDataService weatherDataService;

    public MeteoScheduler (ElectricityPriceService electricityPriceService, WeatherDataService weatherDataService) {
        this.electricityPriceService = electricityPriceService;
        this.weatherDataService = weatherDataService;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void fetchWeatherData () {
        System.out.println("Starting weather data fetch!");
        List<InclusiveDateRange> continuousPriceDataRanges = electricityPriceService.getContinuousElectricityPriceDataRanges();
        weatherDataService.fetchAndStoreWeatherData(continuousPriceDataRanges);
        System.out.println("Weather data fetch finished!");
    }
}
