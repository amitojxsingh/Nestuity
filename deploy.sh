#!/bin/bash

# Nestuity Production Deployment Script
# This script builds and deploys the production environment

set -e  # Exit on error

echo "=========================================="
echo "Nestuity Production Deployment"
echo "=========================================="
echo ""

# Check if .env.production exists
if [ ! -f .env.production ]; then
    echo "❌ ERROR: .env.production file not found!"
    echo ""
    echo "Please create .env.production from the template:"
    echo "  1. Copy: cp .env.production.template .env.production"
    echo "  2. Edit .env.production and fill in all <...> placeholders"
    echo "  3. Run this script again"
    echo ""
    exit 1
fi

# Source the production environment file
set -a
source .env.production
set +a

echo "✅ Environment file loaded"
echo ""

# Validate required variables
REQUIRED_VARS=(
    "POSTGRES_PASSWORD"
    "AUTH_SECRET"
    "NEXT_PUBLIC_API_URL"
    "CORS_ALLOWED_ORIGINS"
)

MISSING_VARS=()
for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        MISSING_VARS+=("$var")
    fi
done

if [ ${#MISSING_VARS[@]} -gt 0 ]; then
    echo "❌ ERROR: Missing required environment variables:"
    for var in "${MISSING_VARS[@]}"; do
        echo "  - $var"
    done
    echo ""
    echo "Please update .env.production with all required values"
    exit 1
fi

echo "✅ All required variables set"
echo ""

# Stop any running production containers
echo "🛑 Stopping any running production containers..."
docker compose -f docker-compose.prod.yml down 2>/dev/null || true
echo ""

# Build the images
echo "🏗️  Building production Docker images..."
docker compose -f docker-compose.prod.yml build --no-cache
echo ""

# Start the services
echo "🚀 Starting production services..."
docker compose -f docker-compose.prod.yml up -d
echo ""

# Wait for services to be healthy
echo "⏳ Waiting for services to become healthy..."
sleep 5

# Check service status
echo ""
echo "📊 Service Status:"
docker compose -f docker-compose.prod.yml ps
echo ""

echo "=========================================="
echo "✅ Deployment Complete!"
echo "=========================================="
echo ""
echo "Your application should be accessible at:"
echo "  ${NEXT_PUBLIC_API_URL%/api}"
echo ""
echo "Useful commands:"
echo "  - View logs: docker compose -f docker-compose.prod.yml logs -f"
echo "  - Stop services: ./stop-prod.sh"
echo "  - Restart: ./deploy.sh"
echo ""
