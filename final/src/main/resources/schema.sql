CREATE TABLE IF NOT EXISTS world.country (
    id INT NOT NULL AUTO_INCREMENT,
    code VARCHAR(3) NOT NULL,
    code_2 VARCHAR(2),
    name VARCHAR(255) NOT NULL,
    continent TINYINT NOT NULL,
    region VARCHAR(255),
    surface_area DECIMAL(10,2),
    indep_year SMALLINT,
    population INT,
    life_expectancy DECIMAL(3,1),
    gnp DECIMAL(10,2),
    gnpo_id DECIMAL(10,2),
    local_name VARCHAR(255),
    government_form VARCHAR(255),
    head_of_state VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS world.city (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    country_id INT NOT NULL,
    district VARCHAR(255),
    population INT,
    PRIMARY KEY (id),
    FOREIGN KEY (country_id) REFERENCES country(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS world.country_language (
    id INT NOT NULL AUTO_INCREMENT,
    country_id INT NOT NULL,
    language VARCHAR(255) NOT NULL,
    is_official BIT NOT NULL DEFAULT 0,
    percentage DECIMAL(4,1),
    PRIMARY KEY (id),
    FOREIGN KEY (country_id) REFERENCES country(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
