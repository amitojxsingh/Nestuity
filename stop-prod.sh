#!/bin/bash

# Nestuity Production Stop Script
# Safely stops all production services

set -e

echo "=========================================="
echo "Stopping Nestuity Production Services"
echo "=========================================="
echo ""

# Stop and remove containers
echo "🛑 Stopping containers..."
docker compose -f docker-compose.prod.yml down

echo ""
echo "✅ All production services stopped"
echo ""
echo "Note: Database volumes are preserved."
echo "To completely remove all data, run:"
echo "  docker compose -f docker-compose.prod.yml down -v"
echo ""
