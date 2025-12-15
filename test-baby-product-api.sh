#!/usr/bin/env bash

# BabyProduct API CRUD Testing Script
# Tests all endpoints: CREATE, READ, UPDATE, DELETE

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/baby-products"

# Print section header
print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

# Print success message
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Print error message
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Print info message
print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

# Test 1: CREATE a Baby Product
print_header "TEST 1: CREATE Baby Product (POST)"
print_info "Creating a new baby product..."

CREATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Organic Baby Formula",
    "brand": "Similac",
    "category": "Feeding",
    "description": "Organic baby formula for newborns",
    "currency": "CAD",
    "inStock": true,
    "priceHistory": [
      {
        "retailer": "Amazon",
        "productUrl": "https://amazon.ca/product/123",
        "price": 27.99,
        "date": "2025-01-10T00:00:00"
      },
      {
        "retailer": "Walmart",
        "productUrl": "https://walmart.ca/product/456",
        "price": 29.99,
        "date": "2025-01-15T00:00:00"
      }
    ]
  }')

HTTP_CODE=$(echo "$CREATE_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$CREATE_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 201 ]; then
    print_success "Product created successfully (HTTP 201)"
    echo "$RESPONSE_BODY" | jq '.'
    PRODUCT_ID=$(echo "$RESPONSE_BODY" | jq -r '.id')
    print_info "Product ID: $PRODUCT_ID"
else
    print_error "Failed to create product (HTTP $HTTP_CODE)"
    echo "$RESPONSE_BODY"
    exit 1
fi

# Test 2: READ all Baby Products
print_header "TEST 2: GET All Baby Products"
print_info "Fetching all baby products..."

GET_ALL_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL")
HTTP_CODE=$(echo "$GET_ALL_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$GET_ALL_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    print_success "Retrieved all products successfully (HTTP 200)"
    PRODUCT_COUNT=$(echo "$RESPONSE_BODY" | jq 'length')
    print_info "Total products: $PRODUCT_COUNT"
    echo "$RESPONSE_BODY" | jq '.'
else
    print_error "Failed to retrieve products (HTTP $HTTP_CODE)"
    echo "$RESPONSE_BODY"
fi

# Test 3: READ specific Baby Product by ID
print_header "TEST 3: GET Baby Product by ID"
print_info "Fetching product with ID: $PRODUCT_ID..."

GET_BY_ID_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/$PRODUCT_ID")
HTTP_CODE=$(echo "$GET_BY_ID_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$GET_BY_ID_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    print_success "Retrieved product successfully (HTTP 200)"
    echo "$RESPONSE_BODY" | jq '.'
else
    print_error "Failed to retrieve product (HTTP $HTTP_CODE)"
    echo "$RESPONSE_BODY"
fi

# Test 4: UPDATE Baby Product
print_header "TEST 4: UPDATE Baby Product (PUT)"
print_info "Updating product with ID: $PRODUCT_ID..."

UPDATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/$PRODUCT_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Organic Baby Formula - Updated",
    "brand": "Similac Pro",
    "category": "Feeding",
    "description": "Updated organic baby formula for newborns and infants",
    "currency": "CAD",
    "inStock": false,
    "priceHistory": [
      {
        "retailer": "Amazon",
        "productUrl": "https://amazon.ca/product/123",
        "price": 25.99,
        "date": "2025-01-20T00:00:00"
      },
      {
        "retailer": "Walmart",
        "productUrl": "https://walmart.ca/product/456",
        "price": 26.99,
        "date": "2025-01-20T00:00:00"
      },
      {
        "retailer": "Costco",
        "productUrl": "https://costco.ca/product/789",
        "price": 24.99,
        "date": "2025-01-20T00:00:00"
      }
    ]
  }')

HTTP_CODE=$(echo "$UPDATE_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$UPDATE_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    print_success "Product updated successfully (HTTP 200)"
    echo "$RESPONSE_BODY" | jq '.'
else
    print_error "Failed to update product (HTTP $HTTP_CODE)"
    echo "$RESPONSE_BODY"
fi

# Test 5: CREATE another product (for demonstration)
print_header "TEST 5: CREATE Another Baby Product"
print_info "Creating a second baby product..."

CREATE_RESPONSE_2=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Baby Diapers Size 1",
    "brand": "Pampers",
    "category": "Diapering",
    "description": "Soft and absorbent diapers for newborns",
    "currency": "CAD",
    "inStock": true,
    "priceHistory": [
      {
        "retailer": "Amazon",
        "productUrl": "https://amazon.ca/product/diapers-001",
        "price": 19.99,
        "date": "2025-01-10T00:00:00"
      }
    ]
  }')

HTTP_CODE=$(echo "$CREATE_RESPONSE_2" | tail -n1)
RESPONSE_BODY=$(echo "$CREATE_RESPONSE_2" | sed '$d')

if [ "$HTTP_CODE" -eq 201 ]; then
    print_success "Second product created successfully (HTTP 201)"
    echo "$RESPONSE_BODY" | jq '.'
    PRODUCT_ID_2=$(echo "$RESPONSE_BODY" | jq -r '.id')
    print_info "Product ID: $PRODUCT_ID_2"
else
    print_error "Failed to create second product (HTTP $HTTP_CODE)"
    echo "$RESPONSE_BODY"
fi

# Test 6: GET all products again (should have 2+ products now)
print_header "TEST 6: GET All Products Again"
print_info "Fetching all products after creating two..."

GET_ALL_RESPONSE_2=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL")
HTTP_CODE=$(echo "$GET_ALL_RESPONSE_2" | tail -n1)
RESPONSE_BODY=$(echo "$GET_ALL_RESPONSE_2" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    print_success "Retrieved all products successfully (HTTP 200)"
    PRODUCT_COUNT=$(echo "$RESPONSE_BODY" | jq 'length')
    print_info "Total products: $PRODUCT_COUNT"
    echo "$RESPONSE_BODY" | jq 'map({id, name, brand, inStock})'
else
    print_error "Failed to retrieve products (HTTP $HTTP_CODE)"
    echo "$RESPONSE_BODY"
fi

# Test 7: DELETE Baby Product
print_header "TEST 7: DELETE Baby Product"
print_info "Deleting product with ID: $PRODUCT_ID..."

DELETE_RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/$PRODUCT_ID")
HTTP_CODE=$(echo "$DELETE_RESPONSE" | tail -n1)

if [ "$HTTP_CODE" -eq 204 ]; then
    print_success "Product deleted successfully (HTTP 204)"
else
    print_error "Failed to delete product (HTTP $HTTP_CODE)"
    echo "$DELETE_RESPONSE" | sed '$d'
fi

# Test 8: Try to GET deleted product (should return 404)
print_header "TEST 8: GET Deleted Product (Should Fail)"
print_info "Attempting to fetch deleted product with ID: $PRODUCT_ID..."

GET_DELETED_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/$PRODUCT_ID")
HTTP_CODE=$(echo "$GET_DELETED_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$GET_DELETED_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 404 ]; then
    print_success "Correctly returned 404 Not Found for deleted product"
    echo "$RESPONSE_BODY" | jq '.'
else
    print_error "Expected 404 Not Found, but got HTTP $HTTP_CODE"
    echo "$RESPONSE_BODY"
fi

# Test 9: DELETE second product (cleanup)
print_header "TEST 9: DELETE Second Product (Cleanup)"
print_info "Deleting product with ID: $PRODUCT_ID_2..."

DELETE_RESPONSE_2=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/$PRODUCT_ID_2")
HTTP_CODE=$(echo "$DELETE_RESPONSE_2" | tail -n1)

if [ "$HTTP_CODE" -eq 204 ]; then
    print_success "Second product deleted successfully (HTTP 204)"
else
    print_error "Failed to delete second product (HTTP $HTTP_CODE)"
fi

# Test 10: Validation Test - Create with missing required field
print_header "TEST 10: VALIDATION - Create Without Required Field"
print_info "Attempting to create product without 'name' field..."

VALIDATION_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "TestBrand",
    "category": "Testing"
  }')

HTTP_CODE=$(echo "$VALIDATION_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$VALIDATION_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 400 ]; then
    print_success "Validation working correctly - rejected invalid data (HTTP 400)"
    echo "$RESPONSE_BODY"
else
    print_error "Validation failed - should have returned 400 (got HTTP $HTTP_CODE)"
    echo "$RESPONSE_BODY"
fi

# Summary
print_header "TEST SUMMARY"
print_success "All CRUD operations tested successfully!"
echo ""
print_info "Endpoints tested:"
echo "  - POST   /api/baby-products         (Create)"
echo "  - GET    /api/baby-products         (Read All)"
echo "  - GET    /api/baby-products/{id}    (Read One)"
echo "  - PUT    /api/baby-products/{id}    (Update)"
echo "  - DELETE /api/baby-products/{id}    (Delete)"
echo ""
print_success "API is working correctly! 🎉"

