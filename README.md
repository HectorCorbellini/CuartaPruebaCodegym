# World Database with Hibernate

This project demonstrates the use of Hibernate ORM with MySQL and Redis, implementing a world database with information about countries, cities, and languages.

## Technologies Used

- Java
- Hibernate 5.6.14.Final
- MySQL 8.0
- Redis (via Lettuce client)
- Maven
- Jackson (for JSON processing)
- P6Spy (for SQL monitoring)

## Prerequisites

Before running this application, make sure you have:

1. JDK 21 or later installed
2. MySQL 8.0 or later installed and running
3. Redis server installed and running
4. Maven installed

## Project Structure

```
hibernate-final-/
├── final/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── com/
│   │   │   │   │   ├── codegym/
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── domain/
│   │   │   │   │   │   └── util/
│   │   │   │   │   └── javarush/
│   │   │   └── resources/
│   ├── .env
│   └── pom.xml
└── README.md
```

## Database Schema

The project uses three main entities:

1. **Country**: Stores information about countries including:
   - Basic details (name, code, continent)
   - Geographic information (region, surface area)
   - Demographics (population, life expectancy)
   - Economic data (GNP)
   - Political information (government form, head of state)

2. **City**: Contains city information:
   - Name
   - District
   - Population
   - Country reference

3. **CountryLanguage**: Tracks languages spoken in countries:
   - Language name
   - Whether it's official
   - Percentage of speakers
   - Country reference

## Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd hibernate-final-
   ```

2. Create a `.env` file in the `final` directory with the following content:
   ```
   MYSQL_URL=jdbc:mysql://localhost:3306/world
   MYSQL_USER=hibernate_user
   MYSQL_PASSWORD=hibernate_password
   REDIS_HOST=localhost
   REDIS_PORT=6379
   ```

3. Create the MySQL database and user:
   ```sql
   CREATE DATABASE world;
   CREATE USER 'hibernate_user'@'localhost' IDENTIFIED BY 'hibernate_password';
   GRANT ALL PRIVILEGES ON world.* TO 'hibernate_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

4. Import the database schema:
   ```bash
   mysql -u hibernate_user -phibernate_password world < final/dump-hibernate-final.sql
   ```

5. Build the project:
   ```bash
   cd final
   mvn clean install
   ```

## Running the Application

You can run the application in two ways:

1. Using Maven:
   ```bash
   cd final
   mvn exec:java -Dexec.mainClass="com.javarush.Main"
   ```

2. Using your IDE:
   - Open the project in your IDE (e.g., IntelliJ IDEA)
   - Run the `Main` class in `com.javarush.Main`

## Features

The application demonstrates several Hibernate features:

- Entity mapping with annotations
- One-to-One, One-to-Many relationships
- Custom DAO layer for database operations
- Transaction management
- Second-level caching with Redis
- Batch processing
- HQL queries

## Sample Queries

The application includes examples of:

1. Fetching all cities
2. Filtering cities by population range
3. Relationship navigation between entities
4. Caching frequently accessed data in Redis

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is licensed under the MIT License - see the LICENSE file for details.
