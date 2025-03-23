package com.fujitsu.fujitsu_food_delivery.services;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.FeeRuleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeeRuleServiceTest {

    private FeeRuleRepository feeRuleRepository;
    private FeeRuleService feeRuleService;

    @BeforeEach
    void setUp() {
        feeRuleRepository = Mockito.mock(FeeRuleRepository.class);
        feeRuleService = new FeeRuleService(feeRuleRepository);
    }

    @Test
    void testCreateFeeRule() {
        FeeRule feeRule = new FeeRule("BASE_FEE", City.TALLINN, VehicleType.CAR, null, 4.0);
        when(feeRuleRepository.save(feeRule)).thenReturn(feeRule);
        FeeRule created = feeRuleService.createFeeRule(feeRule);
        assertNotNull(created);
        assertEquals(4.0, created.getFee());
        verify(feeRuleRepository, times(1)).save(feeRule);
    }

    @Test
    void testGetAllFeeRules() {
        FeeRule feeRule1 = new FeeRule("BASE_FEE", City.TALLINN, VehicleType.CAR, null, 4.0);
        FeeRule feeRule2 = new FeeRule("AIR_TEMP", City.TALLINN, VehicleType.SCOOTER, "<-10", 1.0);
        when(feeRuleRepository.findAll()).thenReturn(Arrays.asList(feeRule1, feeRule2));
        assertEquals(2, feeRuleService.getAllFeeRules().size());
        verify(feeRuleRepository, times(1)).findAll();
    }

    @Test
    void testGetFeeRuleByIdFound() {
        FeeRule feeRule = new FeeRule("BASE_FEE", City.TARTU, VehicleType.BIKE, null, 2.5);
        when(feeRuleRepository.findById(1L)).thenReturn(Optional.of(feeRule));
        FeeRule found = feeRuleService.getFeeRuleById(1L);
        assertNotNull(found);
        assertEquals(2.5, found.getFee());
        verify(feeRuleRepository, times(1)).findById(1L);
    }

    @Test
    void testGetFeeRuleByIdNotFound() {
        when(feeRuleRepository.findById(1L)).thenReturn(Optional.empty());
        FeeRule found = feeRuleService.getFeeRuleById(1L);
        assertNull(found);
    }

    @Test
    void testUpdateFeeRuleFound() {
        FeeRule existing = new FeeRule("BASE_FEE", City.PÄRNU, VehicleType.CAR, null, 3.0);
        existing.setId(1L);
        FeeRule updated = new FeeRule("BASE_FEE", City.PÄRNU, VehicleType.CAR, null, 3.5);
        when(feeRuleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(feeRuleRepository.save(existing)).thenReturn(existing);
        FeeRule result = feeRuleService.updateFeeRule(1L, updated);
        assertNotNull(result);
        assertEquals(3.5, result.getFee());
        verify(feeRuleRepository, times(1)).findById(1L);
        verify(feeRuleRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateFeeRuleNotFound() {
        FeeRule updated = new FeeRule("BASE_FEE", City.PÄRNU, VehicleType.CAR, null, 3.5);
        when(feeRuleRepository.findById(1L)).thenReturn(Optional.empty());
        FeeRule result = feeRuleService.updateFeeRule(1L, updated);
        assertNull(result);
    }

    @Test
    void testDeleteFeeRule() {
        feeRuleService.deleteFeeRule(1L);
        verify(feeRuleRepository, times(1)).deleteById(1L);
    }
}
