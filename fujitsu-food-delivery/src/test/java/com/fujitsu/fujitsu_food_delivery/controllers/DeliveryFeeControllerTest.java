package com.fujitsu.fujitsu_food_delivery.controllers;

import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.WeatherDataRepository;
import com.fujitsu.fujitsu_food_delivery.services.FeeCalculationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryFeeController.class)
@AutoConfigureMockMvc
public class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeeCalculationService feeCalculationService;

    @MockBean
    private WeatherDataRepository weatherDataRepository;

    @Test
    public void testGetDeliveryFeeWithoutDateTime() throws Exception {
        WeatherData weatherData = new WeatherData();
        weatherData.setStationName("Tallinn-Harku");
        weatherData.setTimestamp(1742760780);
        weatherData.setAirTemperature(1.9);
        weatherData.setWindSpeed(3.3);
        weatherData.setWeatherPhenomenon("Clear");

        Mockito.when(weatherDataRepository.findFirstByStationNameOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(java.util.Optional.of(weatherData));

        Mockito.when(feeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.CAR, weatherData))
                .thenReturn(4.0);

        mockMvc.perform(get("/api/deliveryfee")
                .param("city", "TALLINN")
                .param("vehicleType", "CAR")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0"));
    }

    @Test
    public void testGetDeliveryFeeWithDateTime() throws Exception {
        WeatherData weatherData = new WeatherData();
        weatherData.setStationName("Tallinn-Harku");
        weatherData.setTimestamp(1742760780);
        weatherData.setAirTemperature(1.9);
        weatherData.setWindSpeed(3.3);
        weatherData.setWeatherPhenomenon("Clear");

        String dateTimeParam = "2025-03-23T22:36:00";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeParam);
        long effectiveTimestamp = dateTime.atZone(ZoneId.of("UTC")).toEpochSecond();

        Mockito.when(weatherDataRepository
                .findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc("Tallinn-Harku", effectiveTimestamp))
                .thenReturn(java.util.Optional.of(weatherData));

        Mockito.when(feeCalculationService.calculateDeliveryFee(City.TALLINN, VehicleType.CAR, weatherData))
                .thenReturn(4.0);

        mockMvc.perform(get("/api/deliveryfee")
                .param("city", "TALLINN")
                .param("vehicleType", "CAR")
                .param("dateTime", dateTimeParam)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0"));
    }

    @Test
    public void testGetDeliveryFeeNotFound() throws Exception {
        Mockito.when(weatherDataRepository.findFirstByStationNameOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/deliveryfee")
                .param("city", "TALLINN")
                .param("vehicleType", "CAR")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No weather data found for city: TALLINN"));
    }

    @Test
    public void testGetDeliveryFeeInvalidCity() throws Exception {
        mockMvc.perform(get("/api/deliveryfee")
                .param("city", "INVALID_CITY")
                .param("vehicleType", "CAR")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid city or vehicle type")));
    }

    @Test
    public void testGetDeliveryFeeExceptionHandling() throws Exception {
        Mockito.when(weatherDataRepository.findFirstByStationNameOrderByTimestampDesc("Tallinn-Harku"))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/deliveryfee")
                .param("city", "TALLINN")
                .param("vehicleType", "CAR")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Database error")));
    }
}
