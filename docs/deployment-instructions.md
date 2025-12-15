
# Nestuity Production Deployment Guide

This guide walks you through deploying Nestuity to a Linux server with public internet access.

## Prerequisites

### Server Requirements
- **OS**: Ubuntu 20.04+ or similar Linux distribution
- **CPU**: Min 4 cores (8 recommended)
- **RAM**: Min 8GB (16GB recommended)
- **Storage**: 50GB+ free space

### Software Requirements
- Docker
- Docker Compose
- Git

## Step 1: Server Setup

### 1.1 Install Docker

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Log out and back in for group changes to take effect
exit
```

### 1.2 Install Docker Compose

```bash
sudo apt install docker-compose-plugin

# Verify installation
docker compose version
```

## Step 2: Clone Repository

```bash
# Clone the repository
git clone https://github.com/UAlberta-CMPUT401/f25project-Nestuity.git
cd f25project-Nestuity

# Checkout the main branch
git checkout main
```

## Step 3: Configure Environment Variables

### 3.1 Create Production Environment File

```bash
# Copy the template
cp .env.production.template .env.production

# Edit the file
nano .env.production
```

### 3.2 Generate Secure Passwords

```bash
# Generate PostgreSQL password
openssl rand -base64 32

# Generate Auth secret
openssl rand -base64 32
```

### 3.3 Update Required Variables

Edit `.env.production` and replace ALL `<...>` placeholders:

**Critical Variables:**
- `POSTGRES_PASSWORD` - Use generated strong password
- `AUTH_SECRET` - Use generated secret
- `NEXT_PUBLIC_API_URL` - Set to `http://YOUR_SERVER_IP/api`
- `CORS_ALLOWED_ORIGINS` - Set to `http://YOUR_SERVER_IP`

**Example:**
```bash
POSTGRES_PASSWORD=x8K9mP2nQ5rT7uV3wY6zA1bC4dE8fG0h
AUTH_SECRET=K8mP9nQ2rT5uV7wY0zA3bC6dE9fG1hJ4
NEXT_PUBLIC_API_URL=http://192.168.1.100/api
CORS_ALLOWED_ORIGINS=http://192.168.1.100
```

**Optional Variables (can leave blank for MVP):**
- `AUTH_GOOGLE_ID` / `AUTH_GOOGLE_SECRET` - Only if using Google OAuth
- `SENDGRID_API_KEY` - Only if using email features
- `SCRAPER_API_KEY` - Only if using price scraping

## Step 4: Update Google OAuth (Optional)

1. Add to **Authorized JavaScript origins**:
   - `http://YOUR_SERVER_IP`
2. Add to **Authorized redirect URIs**:
   - `http://YOUR_SERVER_IP/api/auth/callback/google`

## Step 5: Deploy

### 5.1 Run Deployment Script

```bash
./deploy.sh
```

Deploy script will:
- Validate environment variables
- Build Docker images (this takes 5-10 minutes)
- Start all services
- Show service status

### 5.2 Verify Deployment

```bash
# Check if all containers are running
docker ps

# Should see 4 services running:
# - nestuity-postgres-prod
# - nestuity-backend-prod
# - nestuity-frontend-prod
# - nestuity-nginx-prod
```

### 5.3 View Logs

```bash
# View all logs
docker compose -f docker-compose.prod.yml logs -f

# View specific service
docker compose -f docker-compose.prod.yml logs -f nestuity-backend
docker compose -f docker-compose.prod.yml logs -f nestuity-frontend
```

## Step 6: Access Your Application

Open your browser and navigate to:
```
http://YOUR_SERVER_IP
```

### View Logs
```bash
# All services
docker compose -f docker-compose.prod.yml logs -f

# Specific service
docker compose -f docker-compose.prod.yml logs -f nestuity-backend
```

### Restart Services
```bash
# Stop
./stop-prod.sh

# Start
./deploy.sh
```

### Update Application
```bash
# Pull latest code
git pull origin main

# Rebuild and redeploy
./deploy.sh
```

## Troubleshooting

### Container Won't Start

```bash
# Check logs
docker compose -f docker-compose.prod.yml logs nestuity-backend

# Check if port 80 is in use
sudo netstat -tulpn | grep :80

# Restart a specific service
docker compose -f docker-compose.prod.yml restart nestuity-frontend
```

### Database Connection Issues

```bash
# Access PostgreSQL container
docker exec -it nestuity-postgres-prod psql -U nestuity_prod_user -d nestuity_prod

# Check database tables
\dt

# Check specific table
\d <table-name>

# Exit
\q
```

### Cannot Access Application

1. **Check firewall:**
   ```bash
   sudo ufw status
   ```

2. **Check if NGINX is running:**
   ```bash
   docker compose -f docker-compose.prod.yml ps nginx
   ```

3. **Test from server:**
   ```bash
   curl http://localhost
   ```

4. **Verify environment variables:**
   ```bash
   cat .env.production | grep NEXT_PUBLIC_API_URL
   cat .env.production | grep CORS_ALLOWED_ORIGINS
   ```

### Backend API Errors

```bash
# View backend logs
docker compose -f docker-compose.prod.yml logs -f nestuity-backend
```

## Backup and Restore

### Backup Database

```bash
# Create backup
docker exec nestuity-postgres-prod pg_dump -U nestuity_prod_user nestuity_prod > backup_$(date +%Y%m%d).sql
```

### Restore Database

```bash
# Restore from backup
cat backup_20250112.sql | docker exec -i nestuity-postgres-prod psql -U nestuity_prod_user -d nestuity_prod
```

## Monitoring

### Check Resource Usage

```bash
# CPU and memory
docker stats

# Disk usage
docker system df
```

### Check Container Health

```bash
# All containers
docker compose -f docker-compose.prod.yml ps

# Inspect specific container
docker inspect nestuity-backend-prod | grep -A 10 Health
```

## Security Notes

- ✅ Only port 80 is exposed to internet
- ✅ Database not accessible from outside
- ✅ Backend not accessible from outside
- ✅ `.env.production` excluded from git
- ⚠️ HTTP only (no encryption) - Add SSL when you get a domain
- ⚠️ Use strong passwords for production
- ⚠️ Keep `.env.production` secure and backed up

## Support

For issues:
1. Check logs: `docker compose -f docker-compose.prod.yml logs -f`
2. Review this guide
3. Check GitHub issues
4. Contact the development team

# Backend logs
docker logs nestuity-backend-prod

# Backend logs - last 50 lines
docker logs nestuity-backend-prod --tail 50

# Backend logs - follow/stream in real-time
docker logs nestuity-backend-prod --follow

# Frontend logs
docker logs nestuity-frontend-prod

# Frontend logs - last 50 lines
docker logs nestuity-frontend-prod --tail 50

# Frontend logs - follow/stream in real-time
docker logs nestuity-frontend-prod --follow

# Nginx logs
docker logs nestuity-nginx-prod --tail 50

# Postgres logs
docker logs nestuity-postgres-prod --tail 50

# All containers at once
docker compose -f docker-compose.prod.yml logs

# Follow all logs in real-time
docker compose -f docker-compose.prod.yml logs --follow