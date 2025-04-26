#!/bin/bash

# SignatureGuard Launcher Script
# This script builds and launches both the API and UI components

set -e  # Exit on any error

# Display banner
echo "================================================="
echo "       SignatureGuard Launcher                   "
echo "================================================="
echo "This script will launch both the API and UI components."
echo ""

# Set working directory to the project root
cd "$(dirname "$0")"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is required but not installed. Please install Maven first."
    exit 1
fi

# Build the project if needed
if [ "$1" == "--build" ] || [ "$1" == "-b" ]; then
    echo "Building all modules..."
    mvn clean install -DskipTests
    echo "Build complete."
fi

# Function to check if port is available
check_port() {
    local port=$1
    if netstat -tuln | grep -q ":$port "; then
        return 1
    fi
    return 0
}

# Check if the API port is already in use
if ! check_port 8080; then
    echo "Error: Port 8080 is already in use. Stop any running instances of the API first."
    exit 1
fi

# Start the API in the background
echo "Starting API server..."
cd api
nohup mvn spring-boot:run > ../log/api.log 2>&1 &
API_PID=$!
cd ..

# Wait for API to be ready
echo "Waiting for API to start on port 8080..."
TIMEOUT=30
while ! curl -s http://localhost:8080 > /dev/null && [ $TIMEOUT -gt 0 ]; do
    sleep 1
    ((TIMEOUT--))
    echo -n "."
done
echo ""

if [ $TIMEOUT -eq 0 ]; then
    echo "Timeout waiting for API to start. Check logs in log/api.log"
    kill $API_PID 2>/dev/null || true
    exit 1
fi

echo "API server started successfully!"

# Start the UI
echo "Starting SignatureGuard UI..."
cd ui
mvn javafx:run &
UI_PID=$!
cd ..

# Set up signal handler to clean up processes
cleanup() {
    echo "Shutting down SignatureGuard..."
    kill $API_PID 2>/dev/null || true
    kill $UI_PID 2>/dev/null || true
    exit 0
}

trap cleanup SIGINT SIGTERM

echo ""
echo "SignatureGuard is running!"
echo "API is available at http://localhost:8080"
echo "UI has been launched."
echo "Press Ctrl+C to stop both components."
echo ""

# Wait for user to press Ctrl+C
wait $UI_PID
cleanup
