# Defence Contract and Procurement Management (J14)

A full-stack MCA project for managing fictional defence contracts, procurement requests, vendor assessments, invoices and payments. Sprint 0 establishes the application foundation, Oracle schema, initial wireframes and secure login. All sample data is fictional and non-sensitive.

## Project details

- College: ABES Engineering College
- Course: MCA 2nd Year
- Skill track: FSD Java
- Project code: J14
- Users named in the brief: Safety Officer and Contracts Manager

The companion workbook defining official user stories, role permissions and later sprint allocation was not present when Sprint 0 began. No story IDs or later sprint allocations have been invented.

## Technology stack

- Java 17, Spring Boot 3.5, Spring Web, Spring Security, Spring Data JPA, Hibernate and Maven
- Angular 14, TypeScript, HTML and CSS
- Oracle Database 21c XE, Oracle SQL and JDBC, using `XEPDB1`
- Git, GitHub and Postman-compatible REST endpoints

## Structure

```text
backend/       Spring Boot REST API and tests
frontend/      Angular 14 application
database/      Oracle schema and fictional seed data
docs/          database design, wireframes, API and sprint review notes
README.md
DEVELOPMENT_STATUS.md
```

## Prerequisites

- Java 17 and Maven 3.6.3 or later
- Node.js 16.14 or later and npm 8
- Angular CLI 14.2 (the generated project pins Angular CLI 14.2)
- Oracle Database 21c XE with `XEPDB1` open

## Oracle setup

1. Connect to `XEPDB1` as a dedicated application schema owner. The project does not use `SYS` for application runtime.
2. Run `database/schema.sql` once in that schema. It is create-only and does not drop objects.
3. Run `database/seed_data.sql` after the schema. It inserts fictional reference and transaction records, including five documented boundary cases. Seed account hashes are irrecoverable placeholders and cannot be used to log in; arrange an approved password reset before a live login demonstration.
4. Supply database access through process environment variables. Do not commit credentials:

```powershell
$env:ORACLE_JDBC_URL = 'jdbc:oracle:thin:@//localhost:1521/XEPDB1'
$env:ORACLE_DB_USERNAME = Read-Host 'Application schema username'
$securePassword = Read-Host 'Application schema password' -AsSecureString
$env:ORACLE_DB_PASSWORD = [System.Net.NetworkCredential]::new('', $securePassword).Password
```

The application validates the schema at startup (`ddl-auto: validate`) and does not create or alter tables automatically.

With SQL*Plus installed, connect using `sqlplus APP_SCHEMA@//localhost:1521/XEPDB1` (SQL*Plus prompts for the schema password), then run `@database/schema.sql` and `@database/seed_data.sql` once each from the repository root.

## Backend setup and run

From the project root:

```powershell
mvn.cmd -f backend/pom.xml test
mvn.cmd -f backend/pom.xml spring-boot:run
```

The API defaults to port 8080. Set `ORACLE_JDBC_URL`, `ORACLE_DB_USERNAME`, and `ORACLE_DB_PASSWORD` in the same shell before starting it. Optional settings are `SERVER_PORT` and `APP_CORS_ALLOWED_ORIGIN`.

## Frontend setup and run

```powershell
cd frontend
npm.cmd install
npm.cmd start
```

Angular serves on port 4200 and forwards `/api` requests to `http://localhost:8080` through `proxy.conf.json`. The login flow obtains a CSRF token, then submits credentials; the server stores the authenticated context in a protected HTTP session cookie.

## Initial authentication API

- `GET /api/health` — liveness response
- `GET /api/auth/csrf` — issues a CSRF token and cookie
- `POST /api/auth/login` — validates credentials against `APP_USERS`, using BCrypt, and returns the signed-in user profile
- `GET /api/auth/me` — returns the current session profile
- `POST /api/auth/logout` — invalidates the authenticated session

Login JSON: `{"username":"...","password":"..."}`. The API uses generic invalid-credential responses and does not return password hashes.

## Sprint records

- Initial wireframes: `docs/wireframes/sprint-0-wireframes.md`
- Database design and ER diagram: `docs/database-design.md`
- Sprint 0 review: `docs/sprint-reviews/sprint-0-review.md`
- Current task and test ledger: `DEVELOPMENT_STATUS.md`

The branch workflow keeps `main` stable, uses `develop` for integration and uses descriptive feature branch names while the official story workbook is unavailable. No sprint tag is created until Sprint 0 is reviewed and approved.
