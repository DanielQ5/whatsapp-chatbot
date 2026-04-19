# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=WhatsappChatbotApplicationTests
```

Before running, load environment variables:
```bash
source .env
```

## Environment Variables

All secrets are stored in `.env` (never committed). Required variables:

| Variable | Used by |
|---|---|
| `ANTHROPIC_API_KEY` | Claude API integration |
| `PROD_DB_PASSWORD` | Production PostgreSQL datasource |
| `ANALYTICS_DB_PASSWORD` | Chat analytics PostgreSQL datasource |
| `WEATHER_API_KEY` | WeatherAPI.com via `WeatherService` |

Spring resolves these automatically via `${VAR_NAME}` placeholders in `application.properties`.

## Architecture

This is a Spring Boot 3.2 / Java 17 WhatsApp chatbot for an insurance company, integrated with Twilio.

### Request Flow

```
Twilio (WhatsApp) → POST /webhook/insurance
    → InsuranceWhatsAppController
        → InsuranceMessageService.processMessages()
            → reads/writes UserSession (in-memory)
            → queries PolicyRepository (production DB, read-only)
            → writes InteractionLogRepository (analytics DB)
        ← TwiML XML response
```

### Dual Database Setup

The app connects to two separate PostgreSQL databases, each with its own Spring configuration:

- **`insurance_production`** — read-only source of truth for insurance policies. Managed by `ProductionDataSourceConfig`. Entity: `Policy` (`policies` table). Queried by national ID (DUI) on login, then by policy number for menu responses.

- **`insurance_chatanalytics`** — write-only analytics store. Managed by `ChatAnalyticsDataSourceConfig`. Entity: `InteractionLog` (`insurance_chatanalytics` table). A log is written when a session ends (`endConversation`).

Both datasources have their own `EntityManagerFactory` and `TransactionManager` beans. `production` is marked `@Primary`.

### Conversation State Machine

`InsuranceMessageService` drives a stateful conversation using `UserSession` objects stored in a `ConcurrentHashMap<phoneNumber, UserSession>`.

Session lifecycle:
1. **No session** — user sends "hola" / "buenos dias" → welcome message prompting for DUI
2. **DUI received** (`\d{8}-\d` format) → `registerPolicy()` looks up policy, stores `policyNumber` in session, shows menu
3. **Menu option (1–8)** — only routed here if `session.policyNumber != null` → `handleMenuChoice()` queries policy and returns the requested field + menu again
4. **"adios" or "0"** → `endConversation()` saves `InteractionLog`, removes session from map

### Key Classes

| Class | Role |
|---|---|
| `InsuranceWhatsAppController` | Twilio webhook entry point (`/webhook/insurance`) |
| `InsuranceMessageService` | All conversation logic and session management |
| `UserSession` | In-memory session: phone, policyNumber, actions taken, session start time |
| `Policy` | JPA entity mapping `policies` table in production DB |
| `InteractionLog` | JPA entity mapping `insurance_chatanalytics` table |
| `WeatherService` | Standalone service calling weatherapi.com (Gson for JSON parsing) |

### Lombok

Entities and `UserSession` use `@Data` for getters/setters. Some classes have commented-out manual getters left from a previous refactor — these are safe to remove.
