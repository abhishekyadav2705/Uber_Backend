
# Uber Backend

Backend service for an Uber-like ride-sharing application built with Spring Boot.

## Overview

This project provides REST APIs for managing rides, users, drivers, and locations. It leverages:

- Spring Boot 3.5.0  
- Spring Data JPA with PostgreSQL  
- Hibernate Spatial for geolocation queries  
- ModelMapper for DTO mapping  
- Springdoc OpenAPI for API documentation  

## Features

- User registration and authentication  
- Ride request and tracking  
- Driver management and assignment  
- Location-based ride calculations  
- OpenAPI Swagger UI documentation  

## Getting Started

### Prerequisites

- Java 24 or higher  
- Maven 3.x  
- PostgreSQL database  

### Installation

1. Clone the repo:

   ```bash
   git clone https://github.com/abhishekyadav2705/uber_backend.git
   cd uber_backend
   ```

2. Configure database in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/uberdb
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build the project:

   ```bash
   ./mvnw clean install
   ```

4. Run the app:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Access API docs at:

   ```
   http://localhost:8080/swagger-ui/index.html
   ```

## Project Structure

- `com.backend.abhishek.uber_backend` - base package  
- `controller` - REST API controllers  
- `service` - business logic services  
- `repository` - JPA repositories  
- `model` - entity classes  
- `config` - app configurations (Swagger, security, etc.)  

## Dependencies

- Spring Boot Starter Web  
- Spring Boot Starter Data JPA  
- PostgreSQL Driver  
- Hibernate Spatial  
- ModelMapper  
- Springdoc OpenAPI UI  
- Lombok (optional)  

## Contributing

Contributions are welcome! Please open issues or submit pull requests.

---

*Developed by Abhishek Yadav*
