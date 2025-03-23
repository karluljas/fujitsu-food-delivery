package com.fujitsu.fujitsu_food_delivery.scheduler;

import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;
import com.fujitsu.fujitsu_food_delivery.repositories.WeatherDataRepository;
import com.fujitsu.fujitsu_food_delivery.scheduler.WeatherDataScheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WeatherDataSchedulerTest {

    private WeatherDataRepository weatherDataRepository;
    private WeatherDataScheduler scheduler;
    private RestTemplate restTemplateMock;

    @BeforeEach
    public void setUp() {
        weatherDataRepository = mock(WeatherDataRepository.class);
        scheduler = new WeatherDataScheduler(weatherDataRepository);
        restTemplateMock = mock(RestTemplate.class);
        ReflectionTestUtils.setField(scheduler, "restTemplate", restTemplateMock);
    }

    @Test
    public void testImportWeatherData_parsesAndSavesData() throws Exception {
        String xmlData = "<observations timestamp=\"1742760780\">" +
                "<station>" +
                "  <name>Tallinn-Harku</name>" +
                "  <wmocode>26038</wmocode>" +
                "  <airtemperature>1.9</airtemperature>" +
                "  <windspeed>3.3</windspeed>" +
                "  <phenomenon>Clear</phenomenon>" +
                "</station>" +
                "<station>" +
                "  <name>Tartu-Tõravere</name>" +
                "  <wmocode>26242</wmocode>" +
                "  <airtemperature>0.3</airtemperature>" +
                "  <windspeed>2.4</windspeed>" +
                "  <phenomenon>Clear</phenomenon>" +
                "</station>" +
                "<station>" +
                "  <name>SomeOtherStation</name>" +
                "  <wmocode>12345</wmocode>" +
                "  <airtemperature>5.0</airtemperature>" +
                "  <windspeed>5.0</windspeed>" +
                "  <phenomenon>Rain</phenomenon>" +
                "</station>" +
                "</observations>";

        when(restTemplateMock.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(xmlData));

        scheduler.importWeatherData();

        verify(weatherDataRepository, times(2)).save(any(WeatherData.class));
    }

    @Test
    public void testImportWeatherData_handlesException() {
        when(restTemplateMock.getForEntity(any(String.class), eq(String.class)))
                .thenThrow(new RuntimeException("Test exception"));

        assertDoesNotThrow(() -> scheduler.importWeatherData());
    }
}
