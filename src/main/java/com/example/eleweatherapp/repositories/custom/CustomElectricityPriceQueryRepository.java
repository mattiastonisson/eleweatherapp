package com.example.eleweatherapp.repositories.custom;

import com.example.eleweatherapp.models.ElectricityPrice;

import java.util.List;

public interface CustomElectricityPriceQueryRepository {
    void upsertElectricityPriceData (List<ElectricityPrice> priceData);
}
