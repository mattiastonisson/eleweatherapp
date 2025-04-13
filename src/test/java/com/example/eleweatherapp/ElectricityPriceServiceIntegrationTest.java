package com.example.eleweatherapp;

import com.example.eleweatherapp.repositories.ElectricityPriceRepository;
import com.example.eleweatherapp.services.ElectricityPriceService;
import com.example.eleweatherapp.utils.CsvSourceDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
@ExtendWith({SpringExtension.class})
class ElectricityPriceServiceIntegrationTest {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    private ElectricityPriceService electricityPriceService;

    @Autowired
    private ElectricityPriceRepository electricityPriceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testUpsertElectricityPrice() {
        jdbcTemplate.execute("create table if not exists electricity_price (id serial primary key , datetime timestamp, price decimal(6,2))");

        List<CsvSourceDataDto> prices = Arrays.asList(
            new CsvSourceDataDto(1704060000L, LocalDateTime.parse("01.01.2024 00:00", dateFormatter), new BigDecimal("40.01")),
            new CsvSourceDataDto(1704063600L, LocalDateTime.parse("01.01.2024 01:00", dateFormatter), new BigDecimal("38.37")),
            new CsvSourceDataDto(1704067200L, LocalDateTime.parse("01.01.2024 02:00", dateFormatter), new BigDecimal("28.46"))
        );

        electricityPriceService.storeElectricityPrices(prices);

        long count = electricityPriceRepository.count();
        assertThat(count).isEqualTo(3);
    }
}
