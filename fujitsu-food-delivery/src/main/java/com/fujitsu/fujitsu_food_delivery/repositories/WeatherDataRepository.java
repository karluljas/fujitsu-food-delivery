package com.fujitsu.fujitsu_food_delivery.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fujitsu.fujitsu_food_delivery.entities.WeatherData;

/**
 * Repository interface for managing entities.
 * This interface extends JpaRepository, providing standard CRUD operations and query derivation for
 * WeatherData objects. Spring Data JPA will generate the implementation at runtime.
 */
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    /**
     * Finds the most recent WeatherData record for the specified station name.
     * The query is derived from the method name: it orders the weather data in descending order based on the
     * timestamp and returns the first record.
     *
     * @param stationName the name of the weather station
     * @return an Optional containing the latest WeatherData if found, or empty otherwise
     */
    Optional<WeatherData> findFirstByStationNameOrderByTimestampDesc(String stationName);

     /**
     * Finds the most recent WeatherData record for the specified station name with a timestamp
     * less than or equal to the provided value.
     * The query is derived from the method name: it orders the matching records in descending order by the timestamp and returns the first record.
     *
     * @param stationName the name of the weather station
     * @param timestamp   the maximum timestamp (in UNIX time seconds) for which data should be retrieved
     * @return an Optional containing the appropriate WeatherData if found, or empty otherwise
     */
    Optional<WeatherData> findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(String stationName, long timestamp);
}
