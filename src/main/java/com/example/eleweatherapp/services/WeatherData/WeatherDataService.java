package com.example.eleweatherapp.services.WeatherData;

import com.example.eleweatherapp.dtos.WeatherData.MeteoApiResponseDto;
import com.example.eleweatherapp.models.WeatherData;
import com.example.eleweatherapp.repositories.WeatherDataRepository;
import com.example.eleweatherapp.utils.InclusiveDateRange;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WeatherDataService {
    private final WeatherDataRepository weatherDataRepository;
    private final MeteoApiService meteoApiService;

    public WeatherDataService (WeatherDataRepository weatherDataRepository, MeteoApiService meteoApiService) {
        this.weatherDataRepository = weatherDataRepository;
        this.meteoApiService = meteoApiService;
    }

    public void fetchAndStoreWeatherData (List<InclusiveDateRange> continuousPriceDataRanges) {
        if (continuousPriceDataRanges.isEmpty()) {
            System.out.println("Skipping fetching weather data as we have no electricity price data!");
            return;
        }

        for (InclusiveDateRange range : continuousPriceDataRanges) {
            if (this.weatherDataRepository.dataExistsForDateRange(range)) {
                System.out.println("Skipping fetching weather data for " + range.toHRString() + " as we already have it!");
                continue;
            }

            System.out.println("Fetching weather data for " + range.toHRString());
            MeteoApiResponseDto meteoResponse = meteoApiService.fetchData(range.startDate(), range.endDate());
            storeWeatherDataRange(meteoResponse, range);

            System.out.println("Weather data for " + range.toHRString() + " fetched and stored successfully.");
        }
    }

    public List<WeatherData> getWeatherDataBetween(LocalDate startDate, LocalDate endDate) {
        return weatherDataRepository.findByDateBetween(startDate, endDate);
    }

    private void storeWeatherDataRange (MeteoApiResponseDto meteoResponse, InclusiveDateRange range) {
        if (!isValidMeteoResponse(meteoResponse, range)) {
            return;
        }

        System.out.println("Processing meteo data for " + range.toHRString());
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
            System.out.println(emptyResponseMessage);
            return false;
        }

        List<String> hourlyTimeData = meteoResponse.hourly().time();
        List<BigDecimal> hourlyTemperatureData = meteoResponse.hourly().temperature_2m();
        if (hourlyTimeData == null || hourlyTemperatureData == null) {
            System.out.println(emptyResponseMessage);
            return false;
        }

        if (hourlyTemperatureData.size() != hourlyTimeData.size()) {
            System.out.println(
                "Error in Meteo response for period " + range.toHRString()
                    + " | different number of time and temperature data points returned!"
            );
            return false;
        }

        return true;
    }
}
