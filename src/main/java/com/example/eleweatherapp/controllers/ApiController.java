package com.example.eleweatherapp.controllers;

import com.example.eleweatherapp.dtos.AggregatedDataDto;
import com.example.eleweatherapp.dtos.ApiResponseDto;
import com.example.eleweatherapp.services.DataAggregationService;
import com.example.eleweatherapp.services.ElectricityPriceService;
import com.example.eleweatherapp.services.WeatherData.WeatherDataService;
import com.example.eleweatherapp.utils.CsvParser;
import com.example.eleweatherapp.utils.CsvSourceDataDto;
import com.example.eleweatherapp.utils.api.ApiResponse;
import com.example.eleweatherapp.validation.AggregatedDataParams;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/")
public class ApiController {
    private final ElectricityPriceService electricityPriceService;
    private final WeatherDataService weatherDataService;
    private final DataAggregationService dataAggregationService;

    public ApiController (
        ElectricityPriceService electricityPriceService,
        WeatherDataService weatherDataService,
        DataAggregationService dataAggregationService
    ) {
        this.electricityPriceService = electricityPriceService;
        this.weatherDataService = weatherDataService;
        this.dataAggregationService = dataAggregationService;
    }

    @Validated
    @GetMapping(value = {"/aggregated"})
    public ResponseEntity<ApiResponseDto<List<AggregatedDataDto>>> getAggregatedData (
        @ModelAttribute AggregatedDataParams params
    ) {
        LocalDate startDate = params.startDate();
        LocalDate endDate = params.endDate();

        List<AggregatedDataDto> aggregated = dataAggregationService.aggregate(
            electricityPriceService.getDailyAverageElectricityPrices(startDate, endDate),
            weatherDataService.getWeatherDataBetween(startDate, endDate)
        );

        ApiResponse<List<AggregatedDataDto>> apiResponse = new ApiResponse<>(aggregated);
        return apiResponse.asResponseEntity();
    }

    @PostMapping(value = "/upload-electricity-prices")
    public ResponseEntity<ApiResponseDto<String>> uploadElectricityPriceData (@RequestParam("file") MultipartFile file) throws CsvValidationException, IOException {
        List<CsvSourceDataDto> parsed = CsvParser.parseElectricityPricesCsv(file);
        electricityPriceService.storeElectricityPrices(parsed);
        ApiResponse<String> apiResponse = new ApiResponse<>("Electricity prices stored successfully!");
        return apiResponse.asResponseEntity();
    }
}
