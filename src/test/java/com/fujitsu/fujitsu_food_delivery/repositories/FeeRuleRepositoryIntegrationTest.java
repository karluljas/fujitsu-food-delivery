package com.fujitsu.fujitsu_food_delivery.repositories;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.FeeRuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FeeRuleRepositoryIntegrationTest {

    @Autowired
    private FeeRuleRepository feeRuleRepository;

    @Test
    public void testFeeRuleCount() {
        long count = feeRuleRepository.count();
        assertEquals(20, count, "There should be 20 fee rules in the database");
    }

    @Test
    public void testBaseFeeForTallinnCar() {
        List<FeeRule> rules = feeRuleRepository.findAll();
        Optional<FeeRule> rule = rules.stream()
                .filter(r -> "BASE_FEE".equals(r.getRuleType())
                        && City.TALLINN.equals(r.getCity())
                        && VehicleType.CAR.equals(r.getVehicleType()))
                .findFirst();
        assertTrue(rule.isPresent(), "Fee rule for TALLINN CAR should be present");
        assertEquals(4.0, rule.get().getFee(), 0.001, "Fee for TALLINN CAR should be 4.0");
    }

    @Test
    public void testAirTempFeeForScooterUnderMinus10() {
        List<FeeRule> rules = feeRuleRepository.findAll();
        Optional<FeeRule> rule = rules.stream()
                .filter(r -> "AIR_TEMP".equals(r.getRuleType())
                        && VehicleType.SCOOTER.equals(r.getVehicleType())
                        && "< -10".equals(r.getCondition()))
                .findFirst();
        assertTrue(rule.isPresent(), "Air temperature rule for Scooter (< -10) should be present");
        assertEquals(1.0, rule.get().getFee(), 0.001, "Air temperature fee for Scooter (< -10) should be 1.0");
    }

    @Test
    public void testWindSpeedFeeForBike() {
        List<FeeRule> rules = feeRuleRepository.findAll();
        Optional<FeeRule> rule = rules.stream()
                .filter(r -> "WIND_SPEED".equals(r.getRuleType())
                        && VehicleType.BIKE.equals(r.getVehicleType())
                        && "[10,20]".equals(r.getCondition()))
                .findFirst();
        assertTrue(rule.isPresent(), "Wind speed rule for Bike should be present");
        assertEquals(0.5, rule.get().getFee(), 0.001, "Wind speed fee for Bike should be 0.5");
    }

    @Test
    public void testPhenomenonRuleForScooterRain() {
        List<FeeRule> rules = feeRuleRepository.findAll();
        Optional<FeeRule> rule = rules.stream()
                .filter(r -> "PHENOMENON".equals(r.getRuleType())
                        && VehicleType.SCOOTER.equals(r.getVehicleType())
                        && "rain".equals(r.getCondition()))
                .findFirst();
        assertTrue(rule.isPresent(), "Phenomenon rule for Scooter (rain) should be present");
        assertEquals(0.5, rule.get().getFee(), 0.001, "Phenomenon fee for Scooter (rain) should be 0.5");
    }
}
