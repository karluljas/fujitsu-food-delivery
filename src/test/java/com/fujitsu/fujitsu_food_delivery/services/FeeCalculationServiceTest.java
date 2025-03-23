package com.fujitsu.fujitsu_food_delivery.services;

import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeeCalculationServiceTest {

    private final FeeCalculationService feeCalculationService = new FeeCalculationService();

    @Test
    void testCalculateDeliveryFeeForTallinnCar() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(5.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("");

        double fee = feeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.CAR, weatherData);
        assertEquals(4.0, fee, 0.001, "Fee for Tallinn Car should be exactly 4.0");
    }

    @Test
    void testCalculateDeliveryFeeForTartuBikeWithColdTemperature() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(-12.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Clear");

        double fee = feeCalculationService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE, weatherData);
        assertEquals(3.5, fee, 0.001, "Fee for Tartu Bike with cold temperature should be 3.5");
    }

    @Test
    void testAirTemperatureExtraFeeForScooter() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(-5.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("");

        double fee = feeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.SCOOTER, weatherData);
        assertEquals(4.0, fee, 0.001, "Fee for Tallinn Scooter with temperature -5°C should be 4.0");
    }

    @Test
    void testCalculateDeliveryFeeForBikeWithHighWindSpeed() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(5.0);
        weatherData.setWindSpeed(22.0);
        weatherData.setWeatherPhenomenon("Clear");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feeCalculationService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE, weatherData);
        });
        String expectedMessage = "Usage of selected vehicle type is forbidden";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testWindSpeedExtraFeeForBike() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(5.0);
        weatherData.setWindSpeed(15.0);
        weatherData.setWeatherPhenomenon("");

        double fee = feeCalculationService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE, weatherData);
        assertEquals(3.0, fee, 0.001, "Fee for Tartu Bike with wind speed 15 should be 3.0");
    }

    @Test
    void testCalculateDeliveryFeeForScooterWithRain() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(5.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Rain");

        double fee = feeCalculationService.calculateDeliveryFee(City.TARTU, VehicleType.SCOOTER, weatherData);
        assertEquals(3.5, fee, 0.001, "Fee for Tartu Scooter with rain should be 3.5");
    }

    @Test
    void testCalculateDeliveryFeeForScooterWithSnow() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(5.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Snow");

        double fee = feeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.SCOOTER, weatherData);
        assertEquals(4.5, fee, 0.001, "Fee for Tallinn Scooter with snow should be 4.5");
    }

    @Test
    void testCalculateDeliveryFeeForScooterWithForbiddenPhenomenon() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(5.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("Hail");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feeCalculationService.calculateDeliveryFee(City.PÄRNU, VehicleType.SCOOTER, weatherData);
        });
        String expectedMessage = "Usage of selected vehicle type is forbidden";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAirTemperatureBoundary() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(-10.0);
        weatherData.setWindSpeed(5.0);
        weatherData.setWeatherPhenomenon("");

        double fee = feeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.SCOOTER, weatherData);
        assertEquals(4.0, fee, 0.001, "Fee for Tallinn Scooter with -10°C should be 4.0");
    }

    @Test
    void testNoExtraFeesForCar() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(-20.0);
        weatherData.setWindSpeed(30.0);
        weatherData.setWeatherPhenomenon("Hail");

        double fee = feeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.CAR, weatherData);
        assertEquals(4.0, fee, 0.001, "Fee for Tallinn Car should be exactly 4.0 even with extreme weather conditions");
    }
}
