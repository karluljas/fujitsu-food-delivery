# Fujitsu Food Delivery

A Spring Boot application for calculating food delivery fees based on city, vehicle type, and real-time or historical weather data from the Estonian Environment Agency.

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Project Structure](#project-structure)
5. [Setup & Installation](#setup--installation)
6. [Usage](#usage)
7. [API Endpoints](#api-endpoints)
8. [Testing](#testing)
9. [Future Improvements](#future-improvements)
10. [Contributing](#contributing)
11. [License](#license)

## Overview
This application calculates delivery fees for food couriers by considering:
- **City** (Tallinn, Tartu, Pärnu)
- **Vehicle Type** (Car, Scooter, Bike)
- **Weather Conditions** (temperature, wind speed, and phenomenon)

The application imports weather data from the [Estonian Environment Agency](https://www.ilmateenistus.ee/) on a schedule, stores historical records in an H2 database, and exposes a REST API to calculate fees.

## Features
- **Dynamic Fee Rules**: Fee rules are stored in a database and can be managed through CRUD endpoints.
- **Historical Data Support**: Optionally pass a datetime parameter to calculate fees based on historical weather data.
- **Scheduled Weather Import**: Weather data is imported automatically on a schedule (configured for testing and production).
- **Validation & Error Handling**: Returns meaningful error messages if conditions (like forbidden vehicle usage) are met.

## Technologies Used
- **Java 17+**
- **Spring Boot** (Web, Data JPA, Scheduling)
- **H2 Database** (In-memory or file-based)
- **Gradle** (for dependency management and build)
- **JUnit 5** and **Mockito** (for testing)


## Setup & Installation
1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/fujitsu-food-delivery.git

2. **Navigate to the project directory**:
    cd fujitsu-food-delivery

3. **Build the project using Gradle**:
    ./gradlew clean build

4. **Run the application**:
    ./gradlew bootRun

The application should now be accessible at http://localhost:8080.

## Usage
### Running Locally
- The application uses an H2 in-memory database by default. You can access the H2 console at `http://localhost:8080/h2-console` (if enabled in your configuration).
- Weather data is imported automatically at the configured schedule.
- Fee rules are seeded by default; you can manage them using the provided REST endpoints.

### Configuration
- **Scheduling**: Adjust the cron expression in `WeatherDataScheduler.java` for production or testing.
- **Database**: To use a file-based H2 database (or switch to another DB like PostgreSQL), modify the configuration in `src/main/resources/application.properties` or `application.yml`.

## API Endpoints

### Delivery Fee
GET /api/deliveryfee
**Query Parameters:**
- `city` (required): TALLINN, TARTU, or PÄRNU
- `vehicleType` (required): CAR, SCOOTER, or BIKE
- `dateTime` (optional): ISO 8601 datetime string (e.g., `2025-03-23T10:15:00`)

**Examples:**
- `GET /api/deliveryfee?city=TALLINN&vehicleType=CAR`
- `GET /api/deliveryfee?city=TALLINN&vehicleType=CAR&dateTime=2025-03-23T10:15:00`

**Responses:**
- `200 OK`: Returns the calculated fee (a numeric value).
- `404 NOT_FOUND`: No weather data available for the specified city.
- `400 BAD_REQUEST`: Invalid parameters or forbidden vehicle usage.

### Fee Rules
POST /api/feerules GET /api/feerules GET /api/feerules/{id} DELETE /api/feerules/{id}

- **Create a Fee Rule**:
  ```json
  POST /api/feerules
  {
    "ruleType": "BASE_FEE",
    "city": "TALLINN",
    "vehicleType": "CAR",
    "condition": null,
    "fee": 4.0
  }

* Get All Fee Rules: GET /api/feerules
* Get Fee Rule by ID: GET /api/feerules/{id}
* Delete Fee Rule: DELETE /api/feerules/{id}

## Testing
- **Run Tests**:
   ```bash
   ./gradlew test
