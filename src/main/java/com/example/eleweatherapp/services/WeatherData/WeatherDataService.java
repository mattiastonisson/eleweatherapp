package com.example.eleweatherapp.services.WeatherData;

import com.example.eleweatherapp.dtos.WeatherData.MeteoApiResponseDto;
import com.example.eleweatherapp.models.WeatherData;
import com.example.eleweatherapp.repositories.WeatherDataRepository;
import com.example.eleweatherapp.utils.InclusiveDateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WeatherDataService {
    private final Logger logger = LoggerFactory.getLogger(WeatherDataService.class);
    private final WeatherDataRepository weatherDataRepository;
    private final MeteoApiService meteoApiService;

    public WeatherDataService (WeatherDataRepository weatherDataRepository, MeteoApiService meteoApiService) {
        this.weatherDataRepository = weatherDataRepository;
        this.meteoApiService = meteoApiService;
    }

    public void fetchAndStoreWeatherData (List<InclusiveDateRange> continuousPriceDataRanges) {
        if (continuousPriceDataRanges.isEmpty()) {
            logger.info("Skipping fetching weather data as we have no electricity price data!");
            return;
        }

        for (InclusiveDateRange range : continuousPriceDataRanges) {
            if (this.weatherDataRepository.dataExistsForDateRange(range)) {
                logger.info("Skipping fetching weather data for {} as we already have it!", range.toHRString());
                continue;
            }

            logger.info("Fetching weather data for {}", range.toHRString());
            MeteoApiResponseDto meteoResponse = meteoApiService.fetchData(range.startDate(), range.endDate());
            storeWeatherDataRange(meteoResponse, range);

            logger.info("Weather data for {} fetched and stored successfully.", range.toHRString());
        }
    }

    public List<WeatherData> getWeatherDataBetween(LocalDate startDate, LocalDate endDate) {
        return weatherDataRepository.findByDateBetween(startDate, endDate);
    }

    private void storeWeatherDataRange (MeteoApiResponseDto meteoResponse, InclusiveDateRange range) {
        if (!isValidMeteoResponse(meteoResponse, range)) {
            return;
        }

        logger.info("Processing meteo data for {}", range.toHRString());
        Map<LocalDate, List<BigDecimal>> groupedByDate = new HashMap<>();
        List<String> hourlyTimeData = meteoResponse.hourly().time();
        List<BigDecimal> hourlyTemperatureData = meteoResponse.hourly().temperature_2m();

        for (int i = 0; i < hourlyTimeData.size(); i++) {
            LocalDate date = LocalDateTime.parse(hourlyTimeData.get(i)).toLocalDate();
            groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(hourlyTemperatureData.get(i));
        }

        List<WeatherData> mappedData = new ArrayList<>(groupedByDate.entrySet().stream()
            .map(entry -> {
                List<BigDecimal> temps = entry.getValue();
                BigDecimal avgTemp = temps.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(temps.size()), 1, RoundingMode.HALF_UP);

                return new WeatherData(entry.getKey(), avgTemp);
            })
            .toList());

        mappedData.sort(Comparator.comparing(WeatherData::getDate));

        weatherDataRepository.storeNewWeatherData(mappedData);
    }

    private boolean isValidMeteoResponse (MeteoApiResponseDto meteoResponse, InclusiveDateRange range) {
        String emptyResponseMessage = "No data returned from Meteo for period " + range.toHRString();

        if (meteoResponse == null || meteoResponse.hourly() == null) {
            logger.debug(emptyResponseMessage);
            return false;
        }

        List<String> hourlyTimeData = meteoResponse.hourly().time();
        List<BigDecimal> hourlyTemperatureData = meteoResponse.hourly().temperature_2m();
        if (hourlyTimeData == null || hourlyTemperatureData == null) {
            logger.debug(emptyResponseMessage);
            return false;
        }

        if (hourlyTemperatureData.size() != hourlyTimeData.size()) {
            logger.debug("Error in Meteo response for period {} | different number of time and temperature data points returned!", range.toHRString());
            return false;
        }

        return true;
    }
}
