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
| `ANTHROPIC_API_KEY` | Not used by any code in `src/` — reserved for external tooling, not an application integration |
| `PROD_DB_PASSWORD` | Production PostgreSQL datasource |
| `ANALYTICS_DB_PASSWORD` | Chat analytics PostgreSQL datasource |
| `WEATHER_API_KEY` | WeatherAPI.com via `WeatherService` |
| `GEMINI_API_KEY` | Google Gemini LLM via `GeminiService` |
| `TWILIO_AUTH_TOKEN` | Validates the `X-Twilio-Signature` header on `/webhook/insurance` via `TwilioSignatureValidator` |

Spring resolves these automatically via `${VAR_NAME}` placeholders in `application.properties`.

## Architecture

This is a Spring Boot 3.2 / Java 17 WhatsApp chatbot for an insurance company, integrated with Twilio.

### Request Flow

```
Twilio (WhatsApp) → POST /webhook/insurance
    → InsuranceWhatsAppController
        → TwilioSignatureValidator.isValid() (403 if signature invalid)
        → InsuranceMessageService.processMessages()
            → reads/writes UserSession (in-memory)
            → queries PolicyRepository (production DB, read-only)
            → GeminiIntentDetectionService.detectIntent() for free-text messages
            → InteractionLogService writes InteractionLogRepository (analytics DB) on session end
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
4. **Free text** (not a menu option, not a DUI) — only routed here if `session.policyNumber != null` → `handleFreeText()` calls `GeminiIntentDetectionService.detectIntent()` with the last 3 conversation turns; a 1–8 result routes to `handleMenuChoice()`, otherwise returns a static clarification message + menu. Actual policy data is never sent to Gemini — only intent classification.
5. **"adios" or "0"** → `endConversation()` saves `InteractionLog` via `InteractionLogService`, removes session from map

### Key Classes

| Class | Role |
|---|---|
| `InsuranceWhatsAppController` | Twilio webhook entry point (`/webhook/insurance`) |
| `InsuranceMessageService` | All conversation logic and session management |
| `UserSession` | In-memory session: phone, policyNumber, actions taken, session start time |
| `Policy` | JPA entity mapping `policies` table in production DB |
| `InteractionLog` | JPA entity mapping `insurance_chatanalytics` table |
| `InteractionLogService` | Builds and saves `InteractionLog` from a `UserSession` at conversation end |
| `MenuOption` | Enum backing the menu — key, tracked action, description, and policy-field handler per option |
| `TwilioSignatureValidator` | HMAC-SHA1 validation of the `X-Twilio-Signature` header |
| `GeminiIntentDetectionService` | Classifies free-text messages against the menu options via Gemini (with recent conversation history for context) |
| `WeatherService` | Standalone service calling weatherapi.com (Gson for JSON parsing) |
| `GeminiService` | Standalone service calling Google Gemini LLM via REST API |
| `RestTemplateConfig` | Shared `RestTemplate` bean (5s connect / 10s read timeout) used by `GeminiService` and `WeatherService` |

`WeatherService`/`WeatherController` (`/weather` endpoint) are an unrelated standalone demo feature — not part of the insurance conversation flow.

### Lombok

Entities and `UserSession` use `@Data` for getters/setters.
