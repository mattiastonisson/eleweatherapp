package com.example.eleweatherapp.repositories.custom;

import com.example.eleweatherapp.models.WeatherData;
import com.example.eleweatherapp.utils.InclusiveDateRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CustomWeatherDataQueryRepositoryImpl implements CustomWeatherDataQueryRepository {
    @Value("${spring.liquibase.parameters.db.schema}")
    private String schema;
    private final JdbcTemplate jdbcTemplate;

    public CustomWeatherDataQueryRepositoryImpl (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean dataExistsForDateRange (InclusiveDateRange range) {
        LocalDate start = range.startDate();
        LocalDate end = range.endDate();

        List<LocalDate> existingDates = jdbcTemplate.query(
            String.format("""
                select date
                from %s.weather_data
                where date between ? and ?
                """, schema),
            (rs, _rowNum) -> rs.getObject(1, LocalDate.class),
            start, end
        );

        Set<LocalDate> expectedDates = start.datesUntil(end.plusDays(1)).collect(Collectors.toSet());

        return new HashSet<>(existingDates).containsAll(expectedDates);
    }

    @Override
    public void storeNewWeatherData (List<WeatherData> weatherData) {
        String sql = String.format("""
            insert into %s.weather_data (date, average_temperature)
            values (?, ?)
            on conflict (date) do nothing;
            """,
        schema);

        List<Object[]> batchArgs = weatherData.stream()
            .map(wd -> new Object[]{wd.getDate(), wd.getAverageTemperature()})
            .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
