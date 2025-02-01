# City Data Management System

A robust Java application for managing city data using Hibernate ORM and Redis, following SOLID principles and clean code practices. The application provides an interactive console interface for exploring and analyzing city data.

## Features

- Interactive menu-driven interface with clear Redis caching indicators
- Data retrieval options:
  - Paginated city data with Redis caching for optimal performance
  - Population-based filtering with direct database access
  - City categorization by population size
  - Performance comparison between Redis cache and direct database access
- Automatic city categorization:
  - Metropolis (1,000,000+ population)
  - Large (500,000 - 999,999 population)
  - Medium (100,000 - 499,999 population)
  - Small (< 100,000 population)
- Redis caching implementation:
  - Automatic caching of paginated results
  - Cache expiration after 5 minutes
  - Performance metrics comparison
- Clean architecture with separation of concerns
- Automatic port management and cleanup

## Architecture

The application follows a layered architecture with clear separation of concerns:

### Domain Layer
- `City`: Entity representing city data
- `Country`: Entity representing country data
- `CountryLanguage`: Entity representing country language data

### Data Access Layer
- `ICityRepository`: Interface defining data access operations
- `CityDAO`: Implementation of data access operations using Hibernate

### Service Layer
- `ICityService`: Interface defining business operations
- `CityService`: Implementation of business logic and transaction management

### Data Transfer Objects
- `CityDTO`: Handles data transformation and categorization with population categories

## Dependencies

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- MySQL 8.0 (runs in Docker)
- Redis 7.0 (runs in Docker)
- Hibernate 5.6.14.Final
- SLF4J and Logback for logging

## Docker Setup

The application uses Docker to run its dependencies:

```bash
# Start Redis container
docker run -d --name hibernate_redis -p 6379:6379 redis:7.0

# Start MySQL container (if not already running)
docker run -d --name hibernate_mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=world \
  -e MYSQL_USER=hibernate_user \
  -e MYSQL_PASSWORD=hibernate_password \
  -p 3306:3306 \
  mysql:8.0
```

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

- Country table with fields:
  - Code
  - Name
  - Continent
  - Region
  - Population
  - Capital (foreign key to City)

- CountryLanguage table with fields:
  - CountryCode (foreign key to Country)
  - Language
  - IsOfficial
  - Percentage

## Building and Running

1. Clone the repository
2. Start Docker containers (see Docker Setup section)
3. Set up environment variables
4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.javarush.Main"
   ```

## Code Organization

The codebase follows SOLID principles and clean architecture practices:

1. **Single Responsibility Principle**: Each class has a single, well-defined purpose
   - `CityDAO`: Handles data access operations
   - `CityService`: Manages business logic and transactions
   - `CityDTO`: Handles data transformation and presentation

2. **Interface Segregation**: Clean interfaces for different responsibilities
   - `ICityRepository`: Data access contract
   - `ICityService`: Business operations contract

3. **Dependency Inversion**: High-level modules depend on abstractions
   - Services depend on repository interfaces
   - Main class depends on service interfaces

4. **Open/Closed Principle**: Extensible design
   - New city categories can be added without modifying existing code
   - New data operations can be added by extending interfaces

## Usage

After starting the application, you'll be presented with an interactive menu:

1. View Cities (Paginated) - Utilizes Redis caching
   - Enter offset and limit to view a specific range of cities
   - Results are cached in Redis for 5 minutes
   - Subsequent identical queries are served from cache
2. Find Cities by Population Range
   - Enter minimum and maximum population to filter cities
   - Direct database access without caching
3. View Cities by Category
   - See cities grouped by population size categories
   - Direct database access without caching
4. Compare Cache vs Database Performance
   - Compare response times between Redis and direct database access
   - View detailed performance metrics and speed differences
5. Exit

The application handles port conflicts automatically and ensures clean shutdown of resources.

### Performance Comparison

The application includes a performance comparison feature that allows you to:
- Compare response times between Redis cache and direct database access
- View the number of cities retrieved in each method
- See the time difference and speed factor between methods
- Understand when to use caching for optimal performance

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

This project is licensed under the MIT License - see the LICENSE file for details
