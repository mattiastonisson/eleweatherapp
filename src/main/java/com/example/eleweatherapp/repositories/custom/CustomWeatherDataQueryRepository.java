package com.example.eleweatherapp.repositories.custom;

import com.example.eleweatherapp.models.WeatherData;
import com.example.eleweatherapp.utils.InclusiveDateRange;

import java.util.List;

public interface CustomWeatherDataQueryRepository {
    boolean dataExistsForDateRange (InclusiveDateRange dateRange);
    void storeNewWeatherData (List<WeatherData> weatherData);
}
