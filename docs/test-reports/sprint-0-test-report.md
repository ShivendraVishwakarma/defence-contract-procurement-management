# Sprint 0 Test Report — Implementation Checks

Date: 2026-11-18. This report records executed checks only. It is not the final Sprint 0 acceptance review because the project schema has not yet been executed on Oracle.

## Backend

Command from the project root:

```powershell
mvn.cmd -f backend/pom.xml test -q
```

Result: **passed, 10 tests, 0 failures, 0 errors, 0 skipped.** The tests cover request validation, generic bad-credential responses, BCrypt matching, CSRF enforcement, session persistence/profile access/logout, and API security flow. The HTTP integration test uses a mocked `AppUserRepository`; it does not validate Oracle connectivity or the Oracle/JPA schema mapping.

## Frontend

Commands from `frontend/`:

```powershell
npm.cmd run build
$env:CHROME_BIN = 'C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe'
npm.cmd test -- --watch=false --browsers=ChromeHeadless
```

Results: production build **passed** (initial output bundle approximately 304 kB raw); Karma browser tests **passed, 4 tests**, using Microsoft Edge 157 because Google Chrome was not installed at the default path.

The first Angular build surfaced a current `@types/node` declaration set incompatible with the project's TypeScript 4.7 toolchain. The dependency was pinned to `@types/node` 16.18.126; the subsequent build passed. The first Karma launch could not locate Chrome; the Edge rerun passed.

## Database and repository

- Read-only commands: `Get-Service -Name OracleServiceXE`; `lsnrctl.exe status`; `sqlplus -L -S / as sysdba` with `SELECT instance_name, status, database_status FROM v$instance` and `SELECT name, open_mode FROM v$pdbs WHERE name = 'XEPDB1'`.
- Output: service `Running`; listener reported services `XE` and `xepdb1` as `READY`; database instance `xe` was `OPEN/ACTIVE`; `XEPDB1` was `READ WRITE`; SQL*Plus exit code `0`.
- These checks did not run through the application JDBC datasource.
- `database/schema.sql` and `database/seed_data.sql` have **not** been executed. A dedicated application schema owner is not configured, and creating/granting one requires administrator-level approval.
- `git diff --check` passed. No environment credential values are included in project files.
