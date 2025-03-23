package com.fujitsu.fujitsu_food_delivery.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.FeeRuleRepository;


/**
 * Service class for managing FeeRule entities.
 * This service provides CRUD operations for FeeRule objects by interacting with the FeeRuleRepository.
 * It handles the creation, retrieval, updating, and deletion of fee rules used in delivery fee calculations.
 */
@Service
public class FeeRuleService {
    
    private final FeeRuleRepository feeRuleRepository;

    /**
     * Constructs a new FeeRuleService with the specified FeeRuleRepository.
     *
     * @param feeRuleRepository the repository used to manage FeeRule entities
     */
    public FeeRuleService(FeeRuleRepository feeRuleRepository) {
        this.feeRuleRepository = feeRuleRepository;
    }

    /**
     * Creates and saves a new FeeRule.
     *
     * @param feeRule the FeeRule object to be created
     * @return the saved FeeRule with any generated fields (e.g., id) populated
     */
    public FeeRule createFeeRule(FeeRule feeRule) {
        return feeRuleRepository.save(feeRule);
    }

    public FeeRule getBaseFeeRule(City city, VehicleType vehicleType) {
        return feeRuleRepository.findAll().stream()
                .filter(rule -> "BASE_FEE".equals(rule.getRuleType()) && rule.getCity() == city && rule.getVehicleType() == vehicleType).findFirst().orElse(null);
    }

    /**
     * Retrieves all FeeRule entities.
     *
     * @return a List containing all FeeRule objects
     */
    public List<FeeRule> getAllFeeRules() {
        return feeRuleRepository.findAll();
    }

    /**
     * Retrieves a FeeRule by its unique identifier.
     *
     * @param id the unique identifier of the FeeRule
     * @return the FeeRule if found; otherwise, returns null
     */
    public FeeRule getFeeRuleById(Long id) {
        return feeRuleRepository.findById(id).orElse(null);
    }

    /**
     * Deletes the FeeRule with the specified id.
     *
     * @param id the unique identifier of the FeeRule to be deleted
     */
    public void deleteFeeRule(Long id) {
        feeRuleRepository.deleteById(id);
    }

    /**
     * Updates an existing FeeRule with new values.
     * The method retrieves the existing FeeRule by its id, updates its properties with values from
     * the provided updatedFeeRule object, and saves the changes.
     *
     * @param id             the unique identifier of the FeeRule to update
     * @param updatedFeeRule the FeeRule object containing updated values
     * @return the updated FeeRule if the update was successful; otherwise, returns null if no FeeRule was found
     */
    public FeeRule updateFeeRule(Long id, FeeRule updatedFeeRule) {
        Optional<FeeRule> optionalFeeRule = feeRuleRepository.findById(id);

        if (optionalFeeRule.isPresent()) {
            FeeRule existingFeeRule = optionalFeeRule.get();

            existingFeeRule.setRuleType(updatedFeeRule.getRuleType());
            existingFeeRule.setCity(updatedFeeRule.getCity());
            existingFeeRule.setVehicleType(updatedFeeRule.getVehicleType());
            existingFeeRule.setCondition(updatedFeeRule.getCondition());
            existingFeeRule.setFee(updatedFeeRule.getFee());

            return feeRuleRepository.save(existingFeeRule);
        }
        return null;
    }
}
