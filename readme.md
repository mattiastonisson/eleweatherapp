# Electricity price & temperature statistics (EE2024)

## Dependency version information
- Spring Boot: 3.4.4
- PostgreSQL: 16 (latest)
- Java: 17
- Gradle: 7.4

## Overview

**EleWeatherApp** is a weather and electricity pricing application designed to provide real-time temperature data and electricity pricing information. The application fetches temperature data from Open Meteo and matches it with electricity price data to provide an integrated view of pricing trends based on the weather. It's built with Spring Boot, PostgreSQL, and utilizes Spring Data JPA for database operations, with a REST API exposed for integration and interaction. The app also comes with a Swagger UI to visualize the API endpoints and easily test them.

---

## Features

- **Temperature Data**: Fetches hourly temperature data for a given location (defaults to Estonia) from the Open Meteo API.
- **Electricity Pricing Data**: Parses uploaded electricity price data (CSV) and stores it in a PostgreSQL database.
- **Integrated View**: Combines temperature and electricity price data and makes it available through a REST API.
- **Swagger UI**: Provides an interactive interface to explore and test API endpoints.
- **Logging**: Uses the standard Slf4j/Logback logging.
- **Observability**: Uses a minimal standard Actuator setup for observability and health-checks.
- **Integration tests**: Some basic integration tests can be found [here](src/test/java/com/example/eleweatherapp).

---

## Prerequisites

Before running the application, ensure you have the following software installed:

- **Java 17+**: The application is built with Java 17, so make sure you have a compatible JDK installed.
- **Gradle**: For building and running the application. You can download it from [here](https://gradle.org/install/).
- **PostgreSQL 14+**: The app uses PostgreSQL as the database. Ensure that PostgreSQL is installed and running locally or remotely.
- **Docker (Optional)**: If you want to run PostgreSQL using Docker (recommended), you can skip the manual installation steps.

---

## Setup

### 1. Clone the repository

```
git clone https://github.com/mattiastonisson/eleweatherapp.git
cd ele-weather-app
```

Rename the [example application config file (example-application.yml)](src/main/resources/application.yml) to `application.yml`. 

#### [Optional] 1.1 Adjust config files (see [docker-compose.yml](docker-compose.yml) and [application.yml](src/main/resources/application.yml))

## Running the application

### Running the tests
Execute the following command:
```
./gradlew test
```

### Natively/locally
Start your PostgreSQL service at the location you specified in the config above.
Execute the following command:
```
./gradlew bootRun
```
This will start the application on port 3001 (default in the config file). You can access it at: http://localhost:3001

### Using docker
Just run the following command (will spin up both the database and the application).
```
docker-compose up
```

## Accessing the Swagger UI
Once the application is running, you can view the Swagger UI at: http://localhost:3001/swagger-ui/index.html

The Swagger UI allows you to view and interact with the available API endpoints.

## Retrieving the necessary data
### Electricity price data
Upload the included Nordpool Spot Price [electricity price data file](src/main/resources/electricity-nps%20price_2024.csv) to the corresponding endpoint.

#### Upsert Feature for Electricity Price Records
The application uses the upsert method (update-if-exists-otherwise-insert) of electricity price records through CSV file uploads.

### Weather data
This is automatically fetched from the Open Meteo API every 5 minutes by default.

After both datasets are stored in the database, you can query the endpoint `/api/aggregated` and expect to receive
aggregated daily average electricity price & temperature data from Estonia for 2024.

## Troubleshooting
### Empty Data in the Swagger UI responses
If you see empty data when testing the aggregated data request in Swagger, double-check that the date parameters are valid and that the corresponding data is actually present in the database.

### Database Connection Issues
If you can't connect to the database, check the database settings in application.yml and make sure PostgreSQL is running.
