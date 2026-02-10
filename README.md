# Autoflex ERP

[![CI Pipeline](https://github.com/YOUR_USERNAME/autoflex-erp/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/autoflex-erp/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A modern ERP module for **Product and Raw Material Management**, built with a focus on clean architecture, testability, and maintainability.

## 🏗️ Architecture

This project follows **Hexagonal Architecture (Ports & Adapters)** to ensure strict separation between business logic and infrastructure concerns.

```text
┌─────────────────────────────────────────────────────────────┐
│                     INFRASTRUCTURE                          │
│  ┌────────────────┐              ┌────────────────────────┐ │
│  │   REST API     │              │    Oracle Database     │ │
│  │   (Adapter)    │              │      (Adapter)         │ │
│  └───────┬────────┘              └───────────┬────────────┘ │
│          │                                   │              │
│          ▼                                   ▼              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                 APPLICATION LAYER                     │  │
│  │              (Use Cases / Services)                   │  │
│  └───────────────────────────────────────────────────────┘  │
│                            │                                │
│                            ▼                                │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                     DOMAIN LAYER                      │  │
│  │     (Entities, Value Objects, Domain Services)        │  │
│  │              ⚠️ NO FRAMEWORK DEPENDENCIES             │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

### Backend

- **Framework**: Quarkus 3.31.2 LTS
- **Language**: Java 21
- **Database**: Oracle Database 19c
- **API Documentation**: OpenAPI / Swagger (SmallRye)
- **Testing**: JUnit 5, Mockito, RestAssured
- **Build**: Maven

### Frontend

- **Framework**: React 19 (Vite)
- **State Management**: Redux Toolkit
- **UI Library**: Material UI (MUI) v5
- **Testing**: Cypress (E2E)
- **Quality**: ESLint, Prettier, TypeScript (strict mode)

### DevOps

- **CI/CD**: GitHub Actions
- **Deployment**: Render.com (Dockerized)
- **Git Hooks**: Husky (pre-commit linting)

## 📁 Project Structure

```text
autoflex-erp/
├── backend/                    # Quarkus Java API
│   ├── src/main/java/
│   │   └── com/autoflex/
│   │       ├── domain/         # Core business logic (NO annotations!)
│   │       │   ├── model/      # Entities, Value Objects
│   │       │   └── port/       # Interfaces (in/out)
│   │       ├── application/    # Use cases, orchestration
│   │       └── infrastructure/ # Adapters (REST, JPA)
│   ├── docker/
│   │   ├── Dockerfile.jvm      # Production Docker image
│   │   └── entrypoint.sh       # Oracle Wallet handler
│   └── pom.xml
├── frontend/                   # React SPA
│   ├── src/
│   │   ├── components/         # Reusable UI components
│   │   ├── pages/              # Route pages
│   │   ├── store/              # Redux store & slices
│   │   ├── services/           # API clients
│   │   └── types/              # TypeScript interfaces
│   ├── cypress/                # E2E tests
│   └── package.json
├── .github/
│   ├── workflows/ci.yml        # CI/CD pipeline
│   └── copilot-instructions.md # AI coding guidelines
└── package.json                # Root scripts & Husky
```

## 🚀 Getting Started

### Prerequisites

- **Java 21+** (Temurin/OpenJDK recommended)
- **Maven 3.9+**
- **Bun 1.3.5+**
- **Docker** (for containerized development)
- **Oracle Database** (or use H2 for testing)

### Local Development

1. **Clone the repository:**

   ```bash
   git clone https://github.com/SeltikHD/autoflex-erp.git
   cd autoflex-erp
   ```

2. **Install dependencies:**

   ```bash
   # Root (Husky)
   bun install
   
   # Frontend
   cd frontend && bun install
   ```

3. **Start the backend:**

   ```bash
   cd backend
   mvn quarkus:dev
   ```

   API available at: <http://localhost:8080/swagger-ui>

4. **Start the frontend:**

   ```bash
   cd frontend
   bun run dev
   ```

   App available at: <http://localhost:5173>

### Running Tests

```bash
# Backend unit tests
cd backend && mvn test

# Frontend E2E tests
cd frontend && bun run test:e2e
```

## 🔐 Oracle Wallet Configuration (Production)

For secure connections to Oracle Cloud databases, this project supports Oracle Wallet via environment variables.

### Setting up on Render.com

1. Download your Oracle Wallet from OCI Console
2. Create a Base64-encoded ZIP:

   ```bash
   cd /path/to/wallet_directory
   zip -r wallet.zip .
   base64 -w 0 wallet.zip > wallet_base64.txt
   ```

3. In Render.com dashboard, set these environment variables:
   - `WALLET_BASE64`: Content of `wallet_base64.txt`
   - `ORACLE_USER`: Your database username
   - `ORACLE_PASSWORD`: Your database password
   - `ORACLE_JDBC_URL`: `jdbc:oracle:thin:@your_tns_alias`

The Docker entrypoint automatically decodes the wallet and configures the JDBC driver.

## 📜 API Documentation

Once the backend is running, access the OpenAPI documentation at:

- **Swagger UI**: <http://localhost:8080/swagger-ui>
- **OpenAPI JSON**: <http://localhost:8080/openapi>

## 🧪 Testing Strategy

| Layer       | Test Type         | Tools                 |
| ----------- | ----------------- | --------------------- |
| Domain      | Unit Tests        | JUnit 5, AssertJ      |
| Application | Integration Tests | Quarkus Test, Mockito |
| REST API    | Contract Tests    | RestAssured           |
| Frontend    | E2E Tests         | Cypress               |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (Husky will run linting)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
