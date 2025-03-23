package com.fujitsu.fujitsu_food_delivery.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.WeatherDataRepository;
import com.fujitsu.fujitsu_food_delivery.services.FeeCalculationService;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST controller for handling delivery fee requests.
 * This controller provides an endpoint to calculate the delivery fee based on the city,
 * vehicle type, and optionally a specific date/time. It retrieves the latest weather data
 * (or historical data if a dateTime parameter is provided) from the database and delegates
 * fee calculation to the FeeCalculationService.
 */
@RestController
@RequestMapping("/api")
public class DeliveryFeeController {

    private final FeeCalculationService feeCalculationService;
    private final WeatherDataRepository weatherDataRepository;

    /**
     * Constructs a new DeliveryFeeController with the given service and repository.
     *
     * @param feeCalculationService the service used to calculate delivery fees
     * @param weatherDataRepository the repository to retrieve weather data
     */
    public DeliveryFeeController(FeeCalculationService feeCalculationService, WeatherDataRepository weatherDataRepository) {
        this.feeCalculationService = feeCalculationService;
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Helper method to map a City enum to its corresponding weather station name.
     *
     * @param city the City enum
     * @return the corresponding weather station name
     */
    private String mapCityToStationName(City city) {
        switch (city) {
            case TALLINN:
                return "Tallinn-Harku";
            case TARTU:
                return "Tartu-Tõravere";
            case PÄRNU:
                return "Pärnu";
            default:
                return city.name();
        }
    }

    /**
     * Calculates and returns the delivery fee based on the provided city, vehicle type,
     * and an optional date/time parameter.
     *
     * @param cityParam         the name of the city (expected: Tallinn, Tartu, or Pärnu)
     * @param vehicleTypeParam  the type of vehicle (expected: Car, Scooter, or Bike)
     * @param dateTimeParam     an optional ISO-formatted date/time string to retrieve historical data
     * @return a ResponseEntity containing the delivery fee or an error message
     */
    @GetMapping("/deliveryfee")
    public ResponseEntity<?> getDeliveryFee(
            @RequestParam("city") String cityParam,
            @RequestParam("vehicleType") String vehicleTypeParam,
            @RequestParam(value = "dateTime", required = false) String dateTimeParam) {
        try {
            City city = City.valueOf(cityParam.toUpperCase());
            VehicleType vehicleType = VehicleType.valueOf(vehicleTypeParam.toUpperCase());

            // Map the city to the corresponding weather station name
            String stationName = mapCityToStationName(city);

            WeatherData weatherData;
            long effectiveTimestamp;
            if (dateTimeParam != null) {
                // Parse the datetime (assume ISO format) and convert to UNIX timestamp
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeParam);
                effectiveTimestamp = dateTime.atZone(ZoneId.of("UTC")).toEpochSecond();
                weatherData = weatherDataRepository
                        .findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(stationName, effectiveTimestamp)
                        .orElse(null);
            } else {
                // Use the current time
                effectiveTimestamp = System.currentTimeMillis() / 1000L;
                weatherData = weatherDataRepository
                        .findFirstByStationNameOrderByTimestampDesc(stationName)
                        .orElse(null);
            }

            if (weatherData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No weather data found for city: " + city);
            }

            // Pass the effective timestamp to the fee calculation service
            double fee = feeCalculationService.calculateDeliveryFee(city, vehicleType, weatherData);
            return ResponseEntity.ok(fee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid city or vehicle type. " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
