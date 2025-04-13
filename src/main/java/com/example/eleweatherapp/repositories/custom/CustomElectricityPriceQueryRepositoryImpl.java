package com.example.eleweatherapp.repositories.custom;

import com.example.eleweatherapp.models.ElectricityPrice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomElectricityPriceQueryRepositoryImpl implements CustomElectricityPriceQueryRepository {
    @Value("${spring.liquibase.parameters.db.schema}")
    private String schema;
    private final JdbcTemplate jdbcTemplate;

    public CustomElectricityPriceQueryRepositoryImpl (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void upsertElectricityPriceData (List<ElectricityPrice> priceData) {
        String query = String.format("""
            insert into %s.electricity_price (datetime, price)
            values (?, ?)
            on conflict (datetime) do update set price = excluded.price;
            """, schema);

        List<Object[]> batchArgs = new ArrayList<>();
        for (ElectricityPrice datum : priceData) {
            batchArgs.add(new Object[]{datum.getDatetime(), datum.getPrice()});
        }

        jdbcTemplate.batchUpdate(query, batchArgs);
    }
}
