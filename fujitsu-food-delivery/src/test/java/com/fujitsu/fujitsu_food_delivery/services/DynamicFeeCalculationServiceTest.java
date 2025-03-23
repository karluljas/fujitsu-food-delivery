package com.fujitsu.fujitsu_food_delivery.services;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.FeeRuleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicFeeCalculationServiceTest {

    private FeeRuleRepository feeRuleRepository;
    private DynamicFeeCalculationService dynamicFeeCalculationService;

    @BeforeEach
    public void setup() {
        feeRuleRepository = Mockito.mock(FeeRuleRepository.class);
        List<FeeRule> feeRules = Arrays.asList(
            new FeeRule("BASE_FEE", City.TALLINN, VehicleType.CAR, null, 4.0),
            new FeeRule("BASE_FEE", City.TALLINN, VehicleType.SCOOTER, null, 3.5),
            new FeeRule("BASE_FEE", City.TALLINN, VehicleType.BIKE, null, 3.0),
            new FeeRule("BASE_FEE", City.TARTU, VehicleType.BIKE, null, 2.5),

            new FeeRule("AIR_TEMP", null, VehicleType.SCOOTER, "< -10", 1.0),
            new FeeRule("AIR_TEMP", null, VehicleType.SCOOTER, "[-10,0)", 0.5),
            new FeeRule("AIR_TEMP", null, VehicleType.BIKE, "< -10", 1.0),
            new FeeRule("AIR_TEMP", null, VehicleType.BIKE, "[-10,0)", 0.5),

            new FeeRule("WIND_SPEED", null, VehicleType.BIKE, "[10,20]", 0.5),

            new FeeRule("PHENOMENON", null, VehicleType.SCOOTER, "snow/sleet", 1.0),
            new FeeRule("PHENOMENON", null, VehicleType.BIKE, "rain", 0.5)
        );
        Mockito.when(feeRuleRepository.findAll()).thenReturn(feeRules);
        dynamicFeeCalculationService = new DynamicFeeCalculationService(feeRuleRepository);
    }

    @Test
    public void testCalculateDeliveryFee_BaseFeeOnly() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(10.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Clear");

        double fee = dynamicFeeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.CAR, weatherData);
        assertEquals(4.0, fee);
    }

    @Test
    public void testCalculateDeliveryFee_AirTempLowForScooter() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(-15.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Clear");

        double fee = dynamicFeeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.SCOOTER, weatherData);
        assertEquals(4.5, fee);
    }

    @Test
    public void testCalculateDeliveryFee_AirTempModerateForBike() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(-5.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Clear");

        double fee = dynamicFeeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.BIKE, weatherData);
        assertEquals(3.5, fee);
    }

    @Test
    public void testCalculateDeliveryFee_WindSpeedForBike() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(10.0);
        weatherData.setWindSpeed(15.0);
        weatherData.setWeatherPhenomenon("Clear");

        double fee = dynamicFeeCalculationService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE, weatherData);
        assertEquals(3.0, fee);
    }

    @Test
    public void testCalculateDeliveryFee_WindSpeedForbiddenForBike() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(10.0);
        weatherData.setWindSpeed(21.0);
        weatherData.setWeatherPhenomenon("Clear");

        assertThrows(IllegalArgumentException.class, () ->
            dynamicFeeCalculationService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE, weatherData)
        );
    }

    @Test
    public void testCalculateDeliveryFee_PhenomenonForbiddenForScooter() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(10.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Hail storm");

        assertThrows(IllegalArgumentException.class, () ->
            dynamicFeeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.SCOOTER, weatherData)
        );
    }

    @Test
    public void testCalculateDeliveryFee_PhenomenonSnowForScooter() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(10.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Light snow shower");

        double fee = dynamicFeeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.SCOOTER, weatherData);
        assertEquals(4.5, fee);
    }

    @Test
    public void testCalculateDeliveryFee_PhenomenonRainForBike() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(10.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Heavy rain");

        double fee = dynamicFeeCalculationService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE, weatherData);
        assertEquals(3.0, fee);
    }
}
