#!/usr/bin/env bash

# BabyProduct API Unit Test Runner
# Runs all unit tests for the BabyProduct backend

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Running BabyProduct API Unit Tests${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

cd nestuity-service

# Check if we should run in Docker or locally
if [ "$1" == "--docker" ]; then
    echo -e "${YELLOW}→ Running tests in Docker container...${NC}"
    docker exec -it nestuity-service ./gradlew test --no-daemon
else
    echo -e "${YELLOW}→ Running tests locally...${NC}"
    ./gradlew test --no-daemon
fi

# Check exit code
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}✓ All tests passed!${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "${YELLOW}Test Summary:${NC}"
    echo "  - Service Layer Tests: ✓"
    echo "  - Controller Layer Tests: ✓"
    echo "  - Exception Handler Tests: ✓"
    echo ""
    echo -e "${YELLOW}View detailed report:${NC}"
    echo "  open nestuity-service/build/reports/tests/test/index.html"
else
    echo ""
    echo -e "${RED}========================================${NC}"
    echo -e "${RED}✗ Tests failed!${NC}"
    echo -e "${RED}========================================${NC}"
    echo ""
    echo -e "${YELLOW}Check the output above for details${NC}"
    exit 1
fi

