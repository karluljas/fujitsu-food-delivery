package com.fujitsu.fujitsu_food_delivery.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.services.FeeRuleService;

/**
 * REST controller for managing fee rules.
 * This controller provides CRUD endpoints to create, retrieve, and delete fee rules that
 * define the business logic for calculating delivery fees.
 */
@RestController
@RequestMapping("/api/feerules")
public class FeeRuleController {

    private final FeeRuleService feeRuleService;

    /**
     * Constructs a new FeeRuleController with the given FeeRuleService.
     *
     * @param feeRuleService the service for managing fee rules
     */
    public FeeRuleController(FeeRuleService feeRuleService) {
        this.feeRuleService = feeRuleService;
    }

    /**
     * Creates a new fee rule.
     *
     * @param feeRule the FeeRule object to create
     * @return a ResponseEntity containing the created FeeRule and HTTP status CREATED
     */
    @PostMapping
    public ResponseEntity<FeeRule> createFeeRule(@RequestBody FeeRule feeRule) {
        FeeRule created = feeRuleService.createFeeRule(feeRule);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Retrieves all fee rules.
     *
     * @return a ResponseEntity containing a list of all fee rules
     */
    @GetMapping
    public ResponseEntity<List<FeeRule>> getAllFeeRules() {
        List<FeeRule> feeRules = feeRuleService.getAllFeeRules();
        
        return ResponseEntity.ok(feeRules);
    }

    /**
     * Retrieves a fee rule by its ID.
     *
     * @param id the ID of the fee rule
     * @return a ResponseEntity containing the FeeRule if found, or NOT FOUND status if not
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeeRule> getFeeRuleById(@PathVariable Long id) {
        FeeRule feeRule = feeRuleService.getFeeRuleById(id);
        if (feeRule != null) {
            return ResponseEntity.ok(feeRule);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a fee rule by its ID.
     *
     * @param id the ID of the fee rule to delete
     * @return a ResponseEntity with NO CONTENT status upon successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeeRule(@PathVariable Long id) {
        feeRuleService.deleteFeeRule(id);
        return ResponseEntity.noContent().build();
    }
}
