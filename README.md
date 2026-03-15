# MyApp — Full-Stack Project

Spring Boot 4 (Java 21) + Angular 21 + PostgreSQL + Valkey + Azure Blob Storage

## Prerequisites

- **Java 21** (install via SDKMAN: `sdk install java 21-tem`)
- **Node.js 22+** and npm
- **Docker** and Docker Compose
- **Angular CLI**: `npm install -g @angular/cli@21`

## Quick Start

### 1. Start infrastructure services

```bash
docker compose up -d
```

This starts PostgreSQL (port 5433), Valkey (port 6379), and Azurite (ports 10010-10012).

### 2. Run the backend

```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The backend starts on http://localhost:8081.

### 3. Run the frontend

```bash
cd frontend
ng serve
```

The frontend starts on http://localhost:4200 with a proxy to the backend.

### 4. Verify

- Backend health: http://localhost:8081/api/health
- Frontend: http://localhost:4200

## Project Structure

```
backend/   — Spring Boot 4 (Gradle Kotlin DSL)
frontend/  — Angular 21 (standalone components, SCSS)
```

## Infrastructure (Docker Compose)

| Service    | Host Port   | Description                     |
|------------|-------------|---------------------------------|
| PostgreSQL | 5433        | Primary database                |
| Valkey     | 6379        | Cache (Redis-compatible)        |
| Azurite    | 10010-10012 | Azure Blob Storage emulator     |
