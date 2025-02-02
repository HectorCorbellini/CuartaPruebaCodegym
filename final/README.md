# City Data Management System

A Java application for managing city data using Hibernate ORM and Redis, following SOLID principles and clean code practices. The system provides an interactive console interface for exploring and analyzing city data.

## Key Features

- Interactive menu interface with Redis caching
- Paginated city data retrieval with Redis caching
- Population-based filtering and city categorization
- Performance comparison between Redis cache and direct database access
- Automatic city categorization by population:
  - Metropolis: 1,000,000+
  - Large: 500,000 - 999,999
  - Medium: 100,000 - 499,999
  - Small: < 100,000

## Technical Stack

- Java 17
- Maven 3.6+
- Hibernate ORM
- Redis for caching
- MySQL database
- Docker for dependencies

## Quick Start

1. Start Docker containers:
```bash
# Redis
docker run -d --name hibernate_redis -p 6379:6379 redis:7.0

# MySQL
docker run -d --name hibernate_mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=world \
  -e MYSQL_USER=hibernate_user \
  -e MYSQL_PASSWORD=hibernate_password \
  -p 3306:3306 \
  mysql:8.0
```

2. Build and run:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.javarush.Main"
```

## Architecture

The application follows a layered architecture:

### Domain Layer
- `City`, `Country`, and `CountryLanguage` entities

### Data Access Layer
- `ICityRepository`: Data access operations interface
- `CityDAO`: Hibernate implementation

### Service Layer
- `ICityService`: Business operations interface
- `CityService`: Business logic implementation

### Data Transfer Objects
- `CityDTO`: Data transformation and categorization

## Configuration

### Environment Variables
```bash
MYSQL_URL=jdbc:mysql://localhost:3306/world
MYSQL_USER=hibernate_user
MYSQL_PASSWORD=hibernate_password
REDIS_URL=redis://localhost:6379/0
```

### Database Schema
MySQL database 'world' with tables:
- City (ID, Name, District, Population, Country FK)
- Country (Code, Name, Continent, Region, Population, Capital FK)
- CountryLanguage (CountryCode FK, Language, IsOfficial, Percentage)

## Usage

The application provides an interactive menu with options:
1. View Cities (Paginated) - Uses Redis cache
2. Find Cities by Population Range
3. View Cities by Category
4. Compare Cache vs Database Performance
5. Exit

Each option provides clear instructions and feedback during execution.
