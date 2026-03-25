💬WhatsApp Insurance Query System

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?style=flat&logo=postgresql&logoColor=white)
![Twilio](https://img.shields.io/badge/Twilio-F22F46?style=flat&logo=twilio&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)

A production-grade backend system that enables insurance clients to retrieve policy information through WhatsApp using their national ID (DUI). Built with Java 17, Spring Boot 3.x, Twilio API, and a dual-PostgreSQL architecture.

📌Overview
This system handles inbound WhatsApp messages via Twilio webhooks, routes users through a conversational menu, queries an insurance database by national ID, and persists session and analytics data — all through a clean Spring Boot REST backend.
Industry: Insurance / Messaging Automation
Type: Self-initiated backend project
Status: Functional

✨Features

📲 WhatsApp integration via Twilio API and TwiML
🔗 Webhook handling for inbound message events
🧭 Conversational menu navigation with session state tracking
🔍 Policy lookup by national ID (DUI) against an insurance database
🗄️ Dual-database architecture — separate DBs for insurance data and analytics
📊 Analytics persistence — session tracking and interaction logging
🏗️ Clean layered architecture — Controller → Service → Repository pattern
💉 Dependency Injection / IoC via Spring


🛠️ Tech Stack
LayerTechnologyLanguageJava 17FrameworkSpring Boot 3.2.1WebSpring Web (REST)PersistenceSpring Data JPA / Hibernate ORMDatabasesPostgreSQL (dual — insurance + analytics)MessagingTwilio WhatsApp API / TwiMLTunneling (dev)ngrokBuildMavenUtilitiesLombok, GsonDev ToolsSpring Boot DevTools

🏛️ Architecture
WhatsApp User
     │
     ▼
Twilio API  ──► Webhook POST  ──► WebhookController
                                        │
                                        ▼
                                  SessionService
                                  (menu routing)
                                        │
                          ┌─────────────┴─────────────┐
                          ▼                           ▼
                   PolicyService              AnalyticsService
                          │                           │
                          ▼                           ▼
                  Insurance DB (PG)         Analytics DB (PG)

🚀 Getting Started
Prerequisites

Java 17+
Maven 3.8+
PostgreSQL (two database instances)
Twilio account with WhatsApp sandbox enabled
ngrok (for local webhook testing)

1. Clone the repository
bashgit clone https://github.com/DanielQ5/whatsapp-chatbot.git
cd whatsapp-chatbot
2. Configure environment
Create an application.properties file under src/main/resources/ (or use environment variables):
properties# Insurance Database
spring.datasource.url=jdbc:postgresql://localhost:5432/insurance_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# Analytics Database
analytics.datasource.url=jdbc:postgresql://localhost:5432/analytics_db
analytics.datasource.username=your_username
analytics.datasource.password=your_password

# Twilio
twilio.account.sid=your_account_sid
twilio.auth.token=your_auth_token
twilio.whatsapp.number=whatsapp:+14155238886

⚠️ Never commit credentials to the repository. Use environment variables or a secrets manager in production.

3. Run the application
bash./mvnw spring-boot:run
4. Expose locally with ngrok
bashngrok http 8080
Copy the generated HTTPS URL and set it as your Twilio webhook URL:
https://your-ngrok-url.ngrok.io/webhook

📁 Project Structure
src/
└── main/
    └── java/com/chatbot/
        ├── controller/      # Webhook endpoint
        ├── service/         # Business logic & menu routing
        ├── repository/      # JPA repositories (dual DB)
        ├── entity/           # JPA entities
        └── config/          # DataSource configuration

💡 Key Design Decisions

- **Dual DataSource config** : Spring's @Primary and custom @Bean configurations allow seamless querying across two separate PostgreSQL instances.
- **Stateless session tracking** : Session state is persisted to the analytics DB rather than held in memory, making the system resilient to restarts.
- **TwiML responses** : All WhatsApp replies are built as TwiML XML responses returned directly from the webhook controller.


📄 License
This project is for portfolio and learning purposes.

👤 Author
Daniel Quintanilla
github.com/DanielQ5
linkedin.com/in/daniel-quintanilla-7868051a0
