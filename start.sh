#!/bin/bash

# ATM System - Start Script
# This script builds and runs the ATM application

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Building ATM System...${NC}"

# Check if gradlew exists, if not use gradle
if [ -f "gradlew" ]; then
    ./gradlew clean build -q
else
    gradle clean build -q
fi

echo -e "${GREEN}Build completed!${NC}"
echo -e "${BLUE}Starting ATM System...${NC}\n"

# Run the application
java -cp build/classes/java/main com.atm.ATMApplication
