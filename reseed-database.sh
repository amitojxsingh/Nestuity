#!/bin/bash

###############################################################################
# Database Re-seeder Script
#
# This script clears all existing baby products and triggers the seeder
# to repopulate the database with fresh sample data.
#
# Usage: ./reseed-database.sh
###############################################################################

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}           Database Re-seeder for Nestuity              ${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Check if backend is running
if ! curl -s http://localhost:8080/baby-products >/dev/null 2>&1; then
  echo -e "${RED}❌ Backend is not running on http://localhost:8080${NC}"
  echo -e "${YELLOW}Please start the backend first:${NC}"
  echo -e "   ./start"
  echo -e "   or"
  echo -e "   docker-compose up -d"
  exit 1
fi

echo -e "${YELLOW}Step 1/3: Fetching existing products...${NC}"
PRODUCTS=$(curl -s http://localhost:8080/baby-products)
PRODUCT_COUNT=$(echo "$PRODUCTS" | jq '. | length' 2>/dev/null || echo "0")

if [ "$PRODUCT_COUNT" -eq 0 ]; then
  echo -e "${GREEN}✅ Database is already empty${NC}"
else
  echo -e "${BLUE}Found $PRODUCT_COUNT products${NC}"

  echo -e "${YELLOW}Step 2/3: Deleting all products...${NC}"
  echo "$PRODUCTS" | jq -r '.[].id' | while read -r id; do
    if curl -s -X DELETE http://localhost:8080/baby-products/"$id" >/dev/null 2>&1; then
      echo -e "  ${GREEN}✓${NC} Deleted product ID: $id"
    else
      echo -e "  ${RED}✗${NC} Failed to delete product ID: $id"
    fi
  done
  echo -e "${GREEN}✅ All products deleted${NC}"
fi

echo ""
echo -e "${YELLOW}Step 3/3: Restarting backend to trigger seeder...${NC}"
docker restart nestuity-service >/dev/null 2>&1
echo -e "${BLUE}Waiting for backend to start...${NC}"

# Wait for backend to be ready (max 30 seconds)
WAIT_TIME=0
MAX_WAIT=30
while [ $WAIT_TIME -lt $MAX_WAIT ]; do
  if curl -s http://localhost:8080/baby-products >/dev/null 2>&1; then
    break
  fi
  sleep 2
  WAIT_TIME=$((WAIT_TIME + 2))
  echo -ne "  Waiting... ${WAIT_TIME}s\r"
done

if [ $WAIT_TIME -ge $MAX_WAIT ]; then
  echo -e "${RED}❌ Backend did not start within ${MAX_WAIT} seconds${NC}"
  echo -e "${YELLOW}Check logs: docker logs nestuity-service${NC}"
  exit 1
fi

echo ""
echo -e "${GREEN}✅ Backend is ready!${NC}"

# Wait a bit more for seeder to complete
sleep 3

# Verify seeding
NEW_PRODUCT_COUNT=$(curl -s http://localhost:8080/baby-products | jq '. | length' 2>/dev/null || echo "0")

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Re-seeding Complete!${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${GREEN}📦 Products in database: $NEW_PRODUCT_COUNT${NC}"
echo ""
echo -e "${BLUE}🔗 Test URLs:${NC}"
echo -e "   API:      http://localhost:8080/baby-products"
echo -e "   Frontend: http://localhost:3000/product/$(curl -s http://localhost:8080/baby-products | jq -r '.[0].id' 2>/dev/null || echo '1')"
echo ""
echo -e "${BLUE}📋 Check seeder logs:${NC}"
echo -e "   docker logs nestuity-service | grep 'Seeding\\|Created:'"
echo ""
echo -e "${GREEN}Done! 🎉${NC}"
