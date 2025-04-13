package com.example.eleweatherapp;

import com.example.eleweatherapp.repositories.WeatherDataRepository;
import com.example.eleweatherapp.services.WeatherData.WeatherDataService;
import com.example.eleweatherapp.utils.InclusiveDateRange;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MeteoSchedulerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private WeatherDataService weatherDataService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testMeteoScheduler() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            weatherDataService.fetchAndStoreWeatherData(List.of(new InclusiveDateRange(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5)
            )));
            latch.countDown();
        }).start();

        boolean completedInTime = latch.await(5, TimeUnit.SECONDS);

        if (!completedInTime) {
            throw new IllegalStateException("Weather data fetch did not complete in time.");
        }

        long storedCount = weatherDataRepository.count();
        assertThat(storedCount).isEqualTo(5);
    }
}
