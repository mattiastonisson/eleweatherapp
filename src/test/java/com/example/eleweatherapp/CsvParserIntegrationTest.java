package com.example.eleweatherapp;

import com.example.eleweatherapp.models.ElectricityPrice;
import com.example.eleweatherapp.repositories.ElectricityPriceRepository;
import com.example.eleweatherapp.services.ElectricityPriceService;
import com.example.eleweatherapp.utils.CsvParser;
import com.example.eleweatherapp.utils.CsvSourceDataDto;
import com.opencsv.exceptions.CsvValidationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class CsvParserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private ElectricityPriceService electricityPriceService;

    @Autowired
    private ElectricityPriceRepository electricityPriceRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testCsvParsingAndStoring() throws CsvValidationException, IOException {
        List<CsvSourceDataDto> parsed = CsvParser.parseElectricityPricesCsv(getMockMultipartFile());
        electricityPriceService.storeElectricityPrices(parsed);

        List<ElectricityPrice> prices = electricityPriceRepository.findAll();
        assertEquals(4, prices.size(), "The CSV data should be parsed and stored in the database.");

        ElectricityPrice firstEntry = prices.get(0);
        assertEquals(new BigDecimal("40.01"), firstEntry.getPrice(), "Price should be parsed correctly.");
        assertEquals(LocalDateTime.parse("2024-01-01T00:00:00"), firstEntry.getDatetime(), "Datetime should be parsed correctly.");
    }

    private @NotNull MockMultipartFile getMockMultipartFile () {
        String csvData = """
            "Ajatempel (UTC)";"Kuupäev (Eesti aeg)";"NPS Läti";"NPS Leedu";"NPS Soome";"NPS Eesti"
            "1704060000";"01.01.2024 00:00";"40,01";"40,01";"40,01";"40,01"
            "1704063600";"01.01.2024 01:00";"38,37";"38,37";"38,37";"38,37"
            "1704067200";"01.01.2024 02:00";"28,46";"28,46";"28,46";"28,46"
            "1704070800";"01.01.2024 03:00";"26,66";"26,66";"26,66";"26,66"
        """;

        byte[] csvDataBytes = csvData.getBytes();

        return new MockMultipartFile(
            "file",
            "electricity_prices.csv",
            "text/csv",
            csvDataBytes
        );
    }
}
