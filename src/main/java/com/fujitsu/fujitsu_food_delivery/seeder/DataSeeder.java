package com.fujitsu.fujitsu_food_delivery.seeder;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;
import com.fujitsu.fujitsu_food_delivery.enums.City;
import com.fujitsu.fujitsu_food_delivery.enums.VehicleType;
import com.fujitsu.fujitsu_food_delivery.repositories.FeeRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for seeding the fee rules into the database at startup.
 */
@Configuration
public class DataSeeder {

    /**
     * Seeds the database with all required fee rules if none are already present.
     *
     * This method inserts the following rules:
     *   Regional Base Fees for each city and vehicle type
     *   Extra fee rules for air temperature:
     *         If air temperature is less than -10°C, fee = 1.0
     *         If air temperature is between -10°C and 0°C, fee = 0.5
     *   Extra fee rules for wind speed (for Bikes):
     *         If wind speed is between 10 m/s and 20 m/s, fee = 0.5
     *         If wind speed is greater than 20 m/s, usage is forbidden (handled in code)
     *   Extra fee rules for weather phenomenon (for Scooters and Bikes):
     *         If phenomenon contains "snow" or "sleet", fee = 1.0
     *         If phenomenon contains "rain", fee = 0.5
     *         If phenomenon contains "glaze", "hail", or "thunder", usage is forbidden (handled in code)
     *
     * @param feeRuleRepository the repository used for persisting FeeRule entities
     * @return a CommandLineRunner bean that seeds fee rules into the database
     */
    @Bean
    public CommandLineRunner seedFeeRules(FeeRuleRepository feeRuleRepository) {
        return args -> {
            if (feeRuleRepository.count() == 0) {
                // Tallinn
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.TALLINN, VehicleType.CAR, null, 4.0));
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.TALLINN, VehicleType.SCOOTER, null, 3.5));
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.TALLINN, VehicleType.BIKE, null, 3.0));

                // Tartu
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.TARTU, VehicleType.CAR, null, 3.5));
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.TARTU, VehicleType.SCOOTER, null, 3.0));
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.TARTU, VehicleType.BIKE, null, 2.5));

                // Pärnu
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.PÄRNU, VehicleType.CAR, null, 3.0));
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.PÄRNU, VehicleType.SCOOTER, null, 2.5));
                feeRuleRepository.save(new FeeRule("BASE_FEE", City.PÄRNU, VehicleType.BIKE, null, 2.0));

                feeRuleRepository.save(new FeeRule("AIR_TEMP", null, VehicleType.SCOOTER, "< -10", 1.0));
                feeRuleRepository.save(new FeeRule("AIR_TEMP", null, VehicleType.BIKE, "< -10", 1.0));

                feeRuleRepository.save(new FeeRule("AIR_TEMP", null, VehicleType.SCOOTER, "[-10,0)", 0.5));
                feeRuleRepository.save(new FeeRule("AIR_TEMP", null, VehicleType.BIKE, "[-10,0)", 0.5));

                feeRuleRepository.save(new FeeRule("WIND_SPEED", null, VehicleType.BIKE, "[10,20]", 0.5));

                feeRuleRepository.save(new FeeRule("PHENOMENON", null, VehicleType.SCOOTER, "snow/sleet", 1.0));
                feeRuleRepository.save(new FeeRule("PHENOMENON", null, VehicleType.BIKE, "snow/sleet", 1.0));

                feeRuleRepository.save(new FeeRule("PHENOMENON", null, VehicleType.SCOOTER, "rain", 0.5));
                feeRuleRepository.save(new FeeRule("PHENOMENON", null, VehicleType.BIKE, "rain", 0.5));

                // Maybe remove the fee
                feeRuleRepository.save(new FeeRule("PHENOMENON", null, VehicleType.SCOOTER, "glaze/hail/thunder", 0.0));
                feeRuleRepository.save(new FeeRule("PHENOMENON", null, VehicleType.BIKE, "glaze/hail/thunder", 0.0));
            }
        };
    }
}
