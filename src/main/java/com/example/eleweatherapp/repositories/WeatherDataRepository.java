package com.example.eleweatherapp.repositories;

import com.example.eleweatherapp.models.WeatherData;
import com.example.eleweatherapp.repositories.custom.CustomWeatherDataQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long>, CustomWeatherDataQueryRepository {
    List<WeatherData> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
