# City Data Management System

A Java application for managing city data using Hibernate ORM and Redis, following SOLID principles and clean code practices. The system provides an interactive console interface for exploring and analyzing city data.

## Prerequisites

- Java 17 or later
- Docker and Docker Compose (handles MySQL and Redis automatically)
- Maven 3.6+

## Quick Start

1. Start the application with all its dependencies:
```bash
docker-compose up -d   # Starts MySQL and Redis containers
mvn compile exec:java -Dexec.mainClass="com.javarush.Main"
```

That's it! Docker will automatically:
- Set up MySQL with the correct configuration
- Set up Redis for caching
- Import all necessary data
- Handle all platform-specific differences

## Features

- Interactive menu interface with Redis caching
- Paginated city data retrieval with Redis caching
- Population-based filtering and city categorization
- Performance comparison between Redis cache and direct database access
- Automatic city categorization by population:
  - Metropolis: 1,000,000+
  - Large: 500,000 - 999,999
  - Medium: 100,000 - 499,999
  - Small: < 100,000

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
