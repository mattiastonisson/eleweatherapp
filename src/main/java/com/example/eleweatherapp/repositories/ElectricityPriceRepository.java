package com.example.eleweatherapp.repositories;

import com.example.eleweatherapp.models.ElectricityPrice;
import com.example.eleweatherapp.repositories.custom.CustomElectricityPriceQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectricityPriceRepository extends JpaRepository<ElectricityPrice, Long>, CustomElectricityPriceQueryRepository {
    @Query(value = "select distinct ep.datetime::date from electricity_price ep", nativeQuery = true)
    List<Date> findDistinctDates();

    List<ElectricityPrice> findByDatetimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
