package com.example.eleweatherapp.services;

import com.example.eleweatherapp.utils.CsvSourceDataDto;
import com.example.eleweatherapp.dtos.DailyAverageElectricityPriceDto;
import com.example.eleweatherapp.models.ElectricityPrice;
import com.example.eleweatherapp.repositories.ElectricityPriceRepository;
import com.example.eleweatherapp.utils.InclusiveDateRange;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElectricityPriceService {
    private final ElectricityPriceRepository electricityPriceRepository;

    public ElectricityPriceService (ElectricityPriceRepository electricityPriceRepository) {
        this.electricityPriceRepository = electricityPriceRepository;
    }

    public List<ElectricityPrice> getElectricityPricesBetween(LocalDate startDate, LocalDate endDate) {
        return electricityPriceRepository.findByDatetimeBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    public Map<LocalDate, DailyAverageElectricityPriceDto> getDailyAverageElectricityPrices (LocalDate startDate, LocalDate endDate) {
        List<ElectricityPrice> electricityPrices = getElectricityPricesBetween(startDate, endDate);
        Map<LocalDate, List<BigDecimal>> groupedByDate = new HashMap<>();

        for (ElectricityPrice electricityPrice : electricityPrices) {
            LocalDate date = electricityPrice.getDatetime().toLocalDate();
            BigDecimal price = electricityPrice.getPrice();
            groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(price);
        }

        return groupedByDate.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    List<BigDecimal> prices = entry.getValue();
                    BigDecimal averagePrice = prices.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(prices.size()), 1, RoundingMode.HALF_UP);

                    return new DailyAverageElectricityPriceDto(entry.getKey(), averagePrice);
                }
            ));
    }

    public List<InclusiveDateRange> getContinuousElectricityPriceDataRanges() {
        List<Date> sqlDates = electricityPriceRepository.findDistinctDates();
        if (sqlDates.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocalDate> localDates = sqlDates.stream().map(Date::toLocalDate).sorted(Comparator.naturalOrder()).toList();
        return this.findContinuousPeriods(localDates);
    }

    /**
     * Checks for gaps in the electricity price data dates. If there's a gap of a week or more,
     * returns the start and end date of the continuous period for which we do have data available
     * before the gap.
     */
    private List<InclusiveDateRange> findContinuousPeriods (List<LocalDate> allDates) {
        List<InclusiveDateRange> periods = new ArrayList<>();

        LocalDate currentStartDate = allDates.get(0);
        LocalDate currentEndDate = allDates.get(0);

        if (currentStartDate == null || currentEndDate == null) {
            return periods;
        }

        for (int i = 1; i < allDates.size(); i++) {
            LocalDate nextDate = allDates.get(i);

            if (
                !currentEndDate.plusDays(1).equals(nextDate) &&
                    currentEndDate.plusWeeks(1).isBefore(nextDate)
            ) {
                periods.add(new InclusiveDateRange(currentStartDate, currentEndDate));
                currentStartDate = nextDate;
            }

            currentEndDate = nextDate;
        }

        periods.add(new InclusiveDateRange(currentStartDate, currentEndDate));

        return periods;
    }

    public void storeElectricityPrices (List<CsvSourceDataDto> parsedCsvPrices) {
        List<ElectricityPrice> mapped = parsedCsvPrices
            .stream()
            .map(p -> new ElectricityPrice(p.date(), p.price()))
            .toList();

        electricityPriceRepository.upsertElectricityPriceData(mapped);
    }
}
