package com.fujitsu.fujitsu_food_delivery.repositories;

import com.fujitsu.fujitsu_food_delivery.entities.FeeRule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing FeeRule entities.
 * This interface extends JpaRepository, providing CRUD operations and query derivation for
 * FeeRule objects. Spring Data JPA automatically implements this interface at runtime.
 */
@Repository
public interface FeeRuleRepository extends JpaRepository<FeeRule, Long> {
}
