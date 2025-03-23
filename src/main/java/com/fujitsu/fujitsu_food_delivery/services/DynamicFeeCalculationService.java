package com.fujitsu.fujitsu_food_delivery.services;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.FeeRuleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DynamicFeeCalculationService {

    private final FeeRuleRepository feeRuleRepository;

    public DynamicFeeCalculationService(FeeRuleRepository feeRuleRepository) {
        this.feeRuleRepository = feeRuleRepository;
    }

    /**
     * Calculates the total delivery fee using dynamic fee rules stored in the database.
     * The fee is computed as the sum of:
     *   The regional base fee (retrieved as a FeeRule of type "BASE_FEE")
     *   An extra fee for air temperature (if applicable; FeeRule type "AIR_TEMP")
     *   An extra fee for wind speed (if applicable; FeeRule type "WIND_SPEED")
     *   An extra fee for weather phenomenon (if applicable; FeeRule type "PHENOMENON")
     *
     * @param city         the city for the delivery
     * @param vehicleType  the vehicle type used for delivery
     * @param weatherData  the weather data record for the city
     * @return the calculated delivery fee
     * @throws IllegalArgumentException if any forbidden condition (e.g. wind speed >20 m/s or forbidden phenomenon) is met
     */
    public double calculateDeliveryFee(City city, VehicleType vehicleType, WeatherData weatherData) {
        double totalFee = 0.0;

        // Retrieve the base fee rule for the specified city and vehicle type.
        FeeRule baseFeeRule = getFeeRule("BASE_FEE", city, vehicleType, null);
        if (baseFeeRule != null) {
            totalFee += baseFeeRule.getFee();
        }

        // Fee for air temperature (applies for Scooter or Bike).
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            Double temp = weatherData.getAirTemperature();
            if (temp != null) {
                FeeRule airTempRule = null;
                if (temp < -10) {
                    airTempRule = getFeeRule("AIR_TEMP", null, vehicleType, "< -10");
                } else if (temp >= -10 && temp < 0) {
                    airTempRule = getFeeRule("AIR_TEMP", null, vehicleType, "[-10,0)");
                }
                if (airTempRule != null) {
                    totalFee += airTempRule.getFee();
                }
            }
        }

        // Fee for wind speed (applies only for Bike).
        if (vehicleType == VehicleType.BIKE && weatherData.getWindSpeed() != null) {
            double windSpeed = weatherData.getWindSpeed();
            if (windSpeed > 20) {
                throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
            } else if (windSpeed >= 10 && windSpeed <= 20) {
                FeeRule windSpeedRule = getFeeRule("WIND_SPEED", null, vehicleType, "[10,20]");
                if (windSpeedRule != null) {
                    totalFee += windSpeedRule.getFee();
                }
            }
        }

        // Fee for weather phenomenon (applies for Scooter or Bike).
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            String phenomenon = weatherData.getWeatherPhenomenon();
            if (phenomenon != null && !phenomenon.isEmpty()) {
                String phenomenonLower = phenomenon.toLowerCase();
                if (phenomenonLower.contains("glaze") || phenomenonLower.contains("hail") || phenomenonLower.contains("thunder")) {
                    throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
                } else if (phenomenonLower.contains("snow") || phenomenonLower.contains("sleet")) {
                    FeeRule phRule = getFeeRule("PHENOMENON", null, vehicleType, "snow/sleet");
                    if (phRule != null) {
                        totalFee += phRule.getFee();
                    }
                } else if (phenomenonLower.contains("rain")) {
                    FeeRule phRule = getFeeRule("PHENOMENON", null, vehicleType, "rain");
                    if (phRule != null) {
                        totalFee += phRule.getFee();
                    }
                }
            }
        }

        return totalFee;
    }

    /**
     * Retrieves a FeeRule matching the given parameters.
     * If a rule's city is not relevant, you can pass null.
     * Similarly, pass null for condition when not needed.
     *
     * @param ruleType     the type of rule ("BASE_FEE", "AIR_TEMP", "WIND_SPEED", or "PHENOMENON")
     * @param city         the city for which the rule applies, or null if not city-specific
     * @param vehicleType  the vehicle type for which the rule applies
     * @param condition    the condition string (for extra fees), or null for base fee rules
     * @return the FeeRule if found; otherwise, null
     */
    private FeeRule getFeeRule(String ruleType, City city, VehicleType vehicleType, String condition) {
        List<FeeRule> rules = feeRuleRepository.findAll();
        return rules.stream().filter(rule ->
                ruleType.equals(rule.getRuleType()) &&
                (city == null || rule.getCity() == city) &&
                rule.getVehicleType() == vehicleType &&
                (condition == null || condition.equals(rule.getCondition()))
        ).findFirst().orElse(null);
    }
}
