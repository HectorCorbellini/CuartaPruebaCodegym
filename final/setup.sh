#!/bin/bash

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if a port is in use
port_in_use() {
    if command_exists lsof; then
        lsof -i :"$1" >/dev/null 2>&1
    elif command_exists netstat; then
        netstat -tuln | grep :"$1" >/dev/null 2>&1
    else
        echo "Warning: Cannot check if port $1 is in use (neither lsof nor netstat found)"
        return 1
    fi
}

echo "🔍 Checking prerequisites..."

# Check for Docker
if ! command_exists docker; then
    echo "❌ Docker is not installed. Please install Docker first:"
    echo "   https://docs.docker.com/get-docker/"
    exit 1
fi

# Check for Docker Compose
if ! command_exists docker-compose; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first:"
    echo "   https://docs.docker.com/compose/install/"
    exit 1
fi

# Check for Maven
if ! command_exists mvn; then
    echo "❌ Maven is not installed. Please install Maven first:"
    echo "   https://maven.apache.org/install.html"
    exit 1
fi

echo "✅ All prerequisites are installed"

# Check if ports are in use
echo "🔍 Checking if required ports are available..."
PORTS_IN_USE=false

if port_in_use 3306; then
    echo "⚠️ Port 3306 (MySQL) is already in use"
    PORTS_IN_USE=true
fi

if port_in_use 6379; then
    echo "⚠️ Port 6379 (Redis) is already in use"
    PORTS_IN_USE=true
fi

if [ "$PORTS_IN_USE" = true ]; then
    echo "❌ Please stop the services using these ports and try again"
    exit 1
fi

echo "✅ Required ports are available"

# Stop and remove existing containers if they exist
echo "🔄 Cleaning up existing containers..."
docker-compose down 2>/dev/null
docker rm -f hibernate_mysql hibernate_redis 2>/dev/null

# Start containers using docker-compose
echo "🚀 Starting Docker containers..."
docker-compose up -d

# Wait for MySQL to be ready
echo "⏳ Waiting for MySQL to be ready..."
for i in {1..30}; do
    if docker exec hibernate_mysql mysqladmin ping -h localhost -u hibernate_user -phibernate_password --silent; then
        break
    fi
    echo "   Still waiting... ($i/30)"
    sleep 2
done

# Import database dump
echo "📥 Importing database dump..."
if docker exec -i hibernate_mysql mysql -uhibernate_user -phibernate_password world < dump-hibernate-final.sql; then
    echo "✅ Database imported successfully"
else
    echo "❌ Failed to import database"
    exit 1
fi

# Build and run the application
echo "🏗️ Building and running the application..."
mvn clean compile exec:java -Dexec.mainClass="com.javarush.Main"
