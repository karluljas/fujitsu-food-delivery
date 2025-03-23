package com.fujitsu.fujitsu_food_delivery.entities;

import jakarta.persistence.*;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;

/**
 * Entity representing a fee rule used for calculating delivery fees.
 * <p>
 * A fee rule contains details about the type of rule,
 * the applicable city and vehicle type, an optional condition (such as a temperature range),
 * and the associated fee amount.
 * </p>
 */
@Entity
public class FeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ruleType;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private String condition;
    private double fee;

    @Column private long effectiveTimestamp;

    public FeeRule() {
    }

     /**
     * Constructs a new FeeRule with the specified details.
     *
     * @param ruleType    the type of the rule
     * @param city        the city for which the rule applies
     * @param vehicleType the vehicle type for which the rule applies
     * @param condition   the condition under which the fee applies (e.g., temperature range)
     * @param fee         the fee amount
     */
    public FeeRule(String ruleType, City city, VehicleType vehicleType, String condition, double fee) {
        this.ruleType = ruleType;
        this.city = city;
        this.vehicleType = vehicleType;
        this.condition = condition;
        this.fee = fee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
