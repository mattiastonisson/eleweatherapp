package com.example.eleweatherapp.services.WeatherData;

import com.example.eleweatherapp.services.ElectricityPriceService;
import com.example.eleweatherapp.utils.InclusiveDateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MeteoScheduler {
    private final Logger logger = LoggerFactory.getLogger(MeteoScheduler.class);
    private final ElectricityPriceService electricityPriceService;
    private final WeatherDataService weatherDataService;

    public MeteoScheduler (ElectricityPriceService electricityPriceService, WeatherDataService weatherDataService) {
        this.electricityPriceService = electricityPriceService;
        this.weatherDataService = weatherDataService;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void fetchWeatherData () {
        logger.info("Starting weather data fetch!");
        List<InclusiveDateRange> continuousPriceDataRanges = electricityPriceService.getContinuousElectricityPriceDataRanges();
        weatherDataService.fetchAndStoreWeatherData(continuousPriceDataRanges);
        logger.info("Weather data fetch finished!");
    }
}
