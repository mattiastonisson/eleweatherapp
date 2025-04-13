package com.example.eleweatherapp.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CsvParser {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static List<CsvSourceDataDto> parseElectricityPricesCsv (MultipartFile file) throws IOException, CsvValidationException {
        List<CsvSourceDataDto> parsed = new ArrayList<>();
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').withIgnoreQuotations(true).build();

        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
            // skip the first line as it contains the headers
            .withSkipLines(1)
            .withCSVParser(csvParser)
            .build()
        ) {
            int rowIndex = 1;
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                validateAndAddCsvRow(parsed, values, rowIndex);
                rowIndex++;
            }
        }

        return parsed;
    }

    public static void validateAndAddCsvRow (List<CsvSourceDataDto> parsed, String[] values, int rowIndex) throws CsvValidationException {
        if (values.length < 6) {
            throw new CsvValidationException("Row is missing required values: " + Arrays.toString(values));
        }

        long timestamp;
        LocalDateTime date;
        BigDecimal price;

        try {
            timestamp = Long.parseLong(values[0]);
        } catch (NumberFormatException e) {
            throw new CsvValidationException(String.format("Invalid number format for timestamp in row %d: %s", rowIndex, Arrays.toString(values)));
        }

        try {
            date = LocalDateTime.parse(values[1], dateFormatter);
        } catch (DateTimeParseException e) {
            throw new CsvValidationException(String.format("Invalid date format %s in row %d: %s", values[1], rowIndex, Arrays.toString(values)));
        }

        try {
            price = new BigDecimal(values[5].replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new CsvValidationException(String.format("Invalid number format for price in row %d: %s", rowIndex, Arrays.toString(values)));
        }

        parsed.add(new CsvSourceDataDto(timestamp, date, price));
    }
}
