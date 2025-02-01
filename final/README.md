# City Data Management System

A robust Java application for managing city data using Hibernate ORM, following SOLID principles and clean code practices.

## Features

- Paginated city data retrieval
- Population-based filtering
- City categorization (Small, Medium, Large, Metropolis)
- Redis caching support
- Clean architecture with separation of concerns

## Architecture

The application follows a layered architecture with clear separation of concerns:

### Data Access Layer
- `ICityRepository`: Interface defining data access operations
- `CityDAO`: Implementation of data access operations using Hibernate

### Service Layer
- `ICityService`: Interface defining business operations
- `CityService`: Implementation of business logic and transaction management

### Data Transfer Objects
- `CityDTO`: Handles data transformation and categorization

## Dependencies

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0
- Hibernate 5.6.14.Final
- Redis (optional, for caching)
- SLF4J and Logback for logging

## Configuration

### Environment Variables

The application uses environment variables for configuration:

```bash
MYSQL_URL=jdbc:mysql://localhost:3306/world
MYSQL_USER=hibernate_user
MYSQL_PASSWORD=hibernate_password
REDIS_URL=redis://localhost:6379/0
```

### Database

The application expects a MySQL database named 'world' with the following schema:

- City table with fields:
  - ID
  - Name
  - District
  - Population
  - Country (foreign key)

## Building and Running

1. Clone the repository
2. Set up environment variables
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.javarush.Main"
   ```

## Code Organization

The codebase follows SOLID principles:

1. **Single Responsibility Principle**: Each class has a single, well-defined purpose
   - `CityDAO`: Handles data access
   - `CityService`: Manages business logic
   - `CityDTO`: Handles data transformation

2. **Open/Closed Principle**: Components are open for extension but closed for modification
   - Interfaces define contracts
   - New implementations can be added without changing existing code

3. **Liskov Substitution Principle**: Implementations properly fulfill their interface contracts
   - `CityDAO` implements `ICityRepository`
   - `CityService` implements `ICityService`

4. **Interface Segregation**: Focused interfaces for specific purposes
   - `ICityRepository` for data access
   - `ICityService` for business operations

5. **Dependency Inversion**: High-level modules depend on abstractions
   - Dependencies are injected via constructors
   - Runtime components are configured in the `Main` class

## City Categories

Cities are categorized based on population:
- Small: < 100,000
- Medium: 100,000 - 500,000
- Large: 500,000 - 1,000,000
- Metropolis: > 1,000,000

## Error Handling

- Comprehensive validation for input parameters
- Graceful error handling with appropriate error messages
- Transaction management for database operations

## Logging

- Logback configuration for structured logging
- Different log levels for various components
- Console and file appenders available

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
