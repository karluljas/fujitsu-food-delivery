package com.fujitsu.fujitsu_food_delivery.services;

import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Service for calculating the delivery fee for food couriers based on weather conditions,
 * regional base fees, and additional fees determined by vehicle type and weather data.
 * The total delivery fee is the sum of the regional base fee, air temperature fee, wind speed fee,
 * and weather phenomenon fee. The regional base fees are stored in a nested Map, and the extra fees
 * are calculated based on predefined business rules.
 */
@Service
public class FeeCalculationService {

    private final Map<City, Map<VehicleType, Double>> regionalBaseFees;

    /**
     * Constructs a new FeeCalculationService and initializes the regional base fees.
     * For each supported city (Tallinn, Tartu, and Pärnu), the base fees for Car, Scooter, and Bike
     * are set as per the business rules.
     */
    public FeeCalculationService() {
        regionalBaseFees = new HashMap<>();
        // Tallinn
        Map<VehicleType, Double> tallinnFees = new HashMap<>();
        tallinnFees.put(VehicleType.CAR, 4.0);
        tallinnFees.put(VehicleType.SCOOTER, 3.5);
        tallinnFees.put(VehicleType.BIKE, 3.0);
        regionalBaseFees.put(City.TALLINN, tallinnFees);

        // Tartu
        Map<VehicleType, Double> tartuFees = new HashMap<>();
        tartuFees.put(VehicleType.CAR, 3.5);
        tartuFees.put(VehicleType.SCOOTER, 3.0);
        tartuFees.put(VehicleType.BIKE, 2.5);
        regionalBaseFees.put(City.TARTU, tartuFees);

        // Pärnu
        Map<VehicleType, Double> parnuFees = new HashMap<>();
        parnuFees.put(VehicleType.CAR, 3.0);
        parnuFees.put(VehicleType.SCOOTER, 2.5);
        parnuFees.put(VehicleType.BIKE, 2.0);
        regionalBaseFees.put(City.PÄRNU, parnuFees);
    }

    /**
     * Calculates the total delivery fee based on the regional base fee and extra fees derived from weather data.
     * The total fee is calculated as the sum of:
     *   The regional base fee for the specified city and vehicle type
     *   The extra fee based on air temperature (if applicable)
     *   The extra fee based on wind speed (if applicable)
     *   The extra fee based on weather phenomenon (if applicable)
     *
     * @param city         the city where the delivery is taking place
     * @param vehicleType  the type of vehicle used for delivery
     * @param weatherData  the weather data containing air temperature, wind speed, and weather phenomenon
     * @return the calculated delivery fee as a double
     */
    public double calculateDeliveryFee(City city, VehicleType vehicleType, WeatherData weatherData) {
        double regionalFee = calculateRegionalBaseFee(city, vehicleType);
        double airTempFee = calculateAirTemperatureFee(vehicleType, weatherData);
        double windSpeedFee = calculateWindSpeedFee(vehicleType, weatherData);
        double weatherPhenomenonFee = calculateWeatherPhenomenonFee(vehicleType, weatherData);
        return regionalFee + airTempFee + windSpeedFee + weatherPhenomenonFee;
    }

    /**
     * Retrieves the regional base fee for the given city and vehicle type.
     * The fee is retrieved from a nested map. If no fee is found, it defaults to 0.0.
     *
     * @param city         the city for which to get the base fee
     * @param vehicleType  the vehicle type for which to get the base fee
     * @return the regional base fee, or 0.0 if not defined
     */
    private double calculateRegionalBaseFee(City city, VehicleType vehicleType) {
        return Optional.ofNullable(regionalBaseFees.get(city))
                       .map(map -> map.get(vehicleType))
                       .orElse(0.0);
    }

    /**
     * Calculates an extra fee based on the air temperature.
     * This fee applies only for vehicles of type Scooter or Bike. If the air temperature is less than -10°C,
     * a fee of 1.0 is applied. If the temperature is between -10°C and 1°C, a fee of 0.5 is applied.
     *
     * @param vehicleType  the type of vehicle
     * @param weatherData  the weather data containing the air temperature
     * @return the extra fee based on air temperature, or 0.0 if not applicable
     */
    private double calculateAirTemperatureFee(VehicleType vehicleType, WeatherData weatherData) {
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            Double temp = weatherData.getAirTemperature();
            if (temp < -10) {
                return 1.0;
            } else if (temp > -11 && temp < 1) {
                return 0.5;
            }
        }
        return 0.0;
    }

    /**
     * Calculates an extra fee based on the wind speed.
     * This fee applies only for bikes. If the wind speed is greater than 20 m/s, an exception is thrown.
     * If the wind speed is between 10 m/s and 20 m/s, a fee of 0.5 is applied.
     *
     * @param vehicleType  the type of vehicle
     * @param weatherData  the weather data containing the wind speed
     * @return the extra fee based on wind speed, or 0.0 if not applicable
     * @throws IllegalArgumentException if the wind speed exceeds the allowed threshold for Bikes
     */
    private double calculateWindSpeedFee(VehicleType vehicleType, WeatherData weatherData) {
        if (vehicleType == VehicleType.BIKE) {
            Double windSpeed = weatherData.getWindSpeed();
            if (windSpeed != null) {
                if (windSpeed > 20) {
                    throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
                } else if (windSpeed > 9 && windSpeed < 21) {
                    return 0.5;
                }
            }
        }
        return 0.0;
    }

    /**
     * Calculates an extra fee based on the weather phenomenon.
     * This fee applies only for vehicles of type Scooter or Bike. Specific weather conditions trigger additional fees:
     *   If the phenomenon contains "Snow" or "Sleet", a fee of 1.0 is applied.
     *   If the phenomenon contains "Rain", a fee of 0.5 is applied.
     *   If the phenomenon contains "Glaze", "Hail", or "Thunder", an exception is thrown to forbid usage.
     *
     * @param vehicleType  the type of vehicle
     * @param weatherData  the weather data containing the weather phenomenon description
     * @return the extra fee based on the weather phenomenon, or 0.0 if not applicable
     * @throws IllegalArgumentException if the weather phenomenon indicates that usage of the vehicle is forbidden
     */
    private double calculateWeatherPhenomenonFee(VehicleType vehicleType, WeatherData weatherData) {
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            String phenomenon = weatherData.getWeatherPhenomenon();
            if (phenomenon != null && !phenomenon.isEmpty()) {
                String phenomenonLower = phenomenon.toLowerCase();
                if (phenomenonLower.contains("glaze") ||
                    phenomenonLower.contains("hail") ||
                    phenomenonLower.contains("thunder")) {
                    throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
                } else if (phenomenonLower.contains("snow") ||
                           phenomenonLower.contains("sleet")) {
                    return 1.0;
                } else if (phenomenonLower.contains("rain")) {
                    return 0.5;
                }
            }
        }
        return 0.0;
    }
}
