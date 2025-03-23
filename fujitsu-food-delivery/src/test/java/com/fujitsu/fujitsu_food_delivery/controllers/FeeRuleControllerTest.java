package com.fujitsu.fujitsu_food_delivery.controllers;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.services.FeeRuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeeRuleController.class)
@AutoConfigureMockMvc
public class FeeRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeeRuleService feeRuleService;

    @Test
    public void testCreateFeeRule() throws Exception {
        FeeRule feeRule = new FeeRule("BASE_FEE", City.TALLINN, VehicleType.CAR, null, 4.0);
        feeRule.setId(1L);

        Mockito.when(feeRuleService.createFeeRule(any(FeeRule.class))).thenReturn(feeRule);

        mockMvc.perform(post("/api/feerules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeRule)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"id\":1")))
                .andExpect(content().string(containsString("\"ruleType\":\"BASE_FEE\"")));
    }

    @Test
    public void testGetAllFeeRules() throws Exception {
        FeeRule feeRule1 = new FeeRule("BASE_FEE", City.TALLINN, VehicleType.CAR, null, 4.0);
        feeRule1.setId(1L);
        FeeRule feeRule2 = new FeeRule("BASE_FEE", City.TARTU, VehicleType.BIKE, null, 2.5);
        feeRule2.setId(2L);
        List<FeeRule> feeRules = Arrays.asList(feeRule1, feeRule2);

        Mockito.when(feeRuleService.getAllFeeRules()).thenReturn(feeRules);

        mockMvc.perform(get("/api/feerules")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":1")))
                .andExpect(content().string(containsString("\"id\":2")));
    }

    @Test
    public void testGetFeeRuleByIdFound() throws Exception {
        FeeRule feeRule = new FeeRule("BASE_FEE", City.PÄRNU, VehicleType.SCOOTER, null, 2.5);
        feeRule.setId(3L);

        Mockito.when(feeRuleService.getFeeRuleById(eq(3L))).thenReturn(feeRule);

        mockMvc.perform(get("/api/feerules/3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":3")))
                .andExpect(content().string(containsString("PÄRNU")));
    }

    @Test
    public void testGetFeeRuleByIdNotFound() throws Exception {
        Mockito.when(feeRuleService.getFeeRuleById(eq(99L))).thenReturn(null);

        mockMvc.perform(get("/api/feerules/99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteFeeRule() throws Exception {
        Mockito.doNothing().when(feeRuleService).deleteFeeRule(eq(1L));

        mockMvc.perform(delete("/api/feerules/1"))
                .andExpect(status().isNoContent());
    }
}
