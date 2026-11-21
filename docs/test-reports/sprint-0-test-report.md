# Sprint 0 Test Report — Implementation Checks

Date: 2026-11-20. This report records executed checks only and is ready for final Sprint 0 acceptance review.

## Backend

Command from the project root:

```powershell
mvn.cmd -f backend/pom.xml test -q
```

Result: **passed, 12 tests, 0 failures, 0 errors, 0 skipped.** The tests cover request validation, generic bad-credential responses, BCrypt matching, CSRF token flow, session persistence/profile access/logout, API security flow, and the unassigned read-only Contracts listing service/controller. The auth HTTP integration test uses a mocked `AppUserRepository`; it does not validate Oracle connectivity or the Oracle/JPA schema mapping.

## Frontend

Commands from `frontend/`:

```powershell
npm.cmd run build
$env:CHROME_BIN = 'C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe'
npm.cmd test -- --watch=false --browsers=ChromeHeadless
```

Results: production build **passed**; Karma browser tests **passed, 8 tests**, using Microsoft Edge 157 because Google Chrome was not installed at the default path.

The first Angular build surfaced a current `@types/node` declaration set incompatible with the project's TypeScript 4.7 toolchain. The dependency was pinned to `@types/node` 16.18.126; the subsequent build passed. The first Karma launch could not locate Chrome; the Edge rerun passed.

## Database and repository

- Read-only commands: `Get-Service -Name OracleServiceXE`; `lsnrctl.exe status`; `sqlplus -L -S / as sysdba` with `SELECT instance_name, status, database_status FROM v$instance` and `SELECT name, open_mode FROM v$pdbs WHERE name = 'XEPDB1'`.
- Output: service `Running`; listener reported services `XE` and `xepdb1` as `READY`; database instance `xe` was `OPEN/ACTIVE`; `XEPDB1` was `READ WRITE`; SQL*Plus exit code `0`.
- The application was subsequently started with the local `ORACLE_DB_PASSWORD`; Hikari obtained an Oracle connection and Hibernate schema validation passed against `J14_APP` in `XEPDB1`.
- `database/schema.sql` was applied successfully, creating 15 tables and 13 explicit project indexes. `database/seed_data.sql` was applied successfully and seeded rows were verified across all 15 tables.
- A temporary local password was provisioned directly for the fictional `a.sharma` account without storing the password in the repository. The live CSRF-protected login returned `200`, and `/api/auth/me` returned the matching authenticated profile.
- The running application health endpoint returned `200` with `{"status":"UP"}`.
- The unassigned Contracts listing has service and controller unit tests. The contract repository JPQL query was not exercised against Oracle during this run because `ORACLE_DB_PASSWORD` was not available to this process.
- `git diff --check` passed. No environment credential values are included in project files.
