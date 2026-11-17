# Development Status

Last updated: 2026-11-18

## Current sprint

Sprint 0 - repository foundation, database design, wireframes, project skeleton and login.

## Completed tasks

- [x] Inspected the existing repository and preserved its initial commit.
- [x] Created `develop` from `main` and pushed it to `origin`.
- [x] Created descriptive feature branch `feature/sprint-0-foundation-login` from `develop`.
- [x] Committed the implementation on the feature branch and pushed it to `origin`.
- [x] Generated the Angular 14.2.13 workspace without overwriting existing files.
- [x] Added the Spring Boot API skeleton and session-based, BCrypt-backed login implementation.
- [x] Added Oracle 21c XE schema and fictional seed scripts for the four project domains.
- [x] Added initial wireframes, database design notes and project setup documentation.

## Pending tasks

- [x] Finish the Angular login/dashboard and initial navigation screens; module routes remain informational placeholders.
- [x] Generate valid BCrypt placeholder hashes from discarded random values for the seeded accounts.
- [x] Complete backend and frontend build/test runs and address failures.
- [ ] Validate Oracle scripts and schema using an application schema owner; only SYSDBA read-only connectivity has been confirmed so far.
- [ ] Configure a local application schema account and non-placeholder user passwords before end-to-end login verification.
- [ ] Review the feature branch and the partial login-module verification with the user.
- [ ] After Oracle-backed validation and user review, open a pull request from the feature branch into `develop`; do not merge without review.
- [ ] Complete Sprint 0 review and seek approval before any Sprint 1 work.

## Modules and user stories

- Completed modules: none fully verified against the Oracle application schema.
- Completed official user stories: none recorded. The companion workbook is not available, so no IDs or allocations are assumed.
- Login module implementation: complete. API/security flow tests pass with the user repository mocked; Oracle-backed runtime verification is pending an application schema account and SQL execution.

## Files created or modified

Commit `0302aa5` contains 70 added files: backend Maven/config/source/tests; Angular workspace, login, dashboard, navigation and unit tests; Oracle schema, fictional seed data and ER diagram; API, requirements, wireframe, database and test documentation; root README and this status file. Use `git show --name-status 0302aa5` for the exact path list.

## Tests executed

- `mvn.cmd -f backend/pom.xml test -q`: passed; 10 tests, 0 failures/errors.
- `npm.cmd run build` in `frontend/`: passed using Angular CLI 14.2.13 / Node 16.20.2.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` in `frontend/`, with `CHROME_BIN` set to installed Microsoft Edge: passed; 4 tests.
- `git diff --check`: passed.
- SQL checks: read-only service/listener/XEPDB1 verification passed; project schema and seed scripts have not been executed.

## Actual test results

- Maven: `AuthControllerTest` 4/4, `AuthenticationApiFlowTest` 2/2, `AuthenticationServiceTest` 2/2, `AuthenticationHttpIntegrationTest` 2/2.
- The HTTP integration test exercises CSRF token flow, BCrypt login through the Spring Security filter chain, session profile access, logout and invalid-password handling. It uses a mocked user repository, so it does not prove Oracle connectivity or the Oracle schema mapping.
- Angular: 4/4 browser tests passed on Edge 157. Production bundle generated with an initial bundle of about 304 kB raw.
- Oracle read-only check: `OracleServiceXE` Running; listener reported `XE` and `xepdb1` READY; local SYSDBA queries returned instance `OPEN/ACTIVE` and `XEPDB1 READ WRITE` (SQL*Plus exit code 0).
- First Angular build exposed incompatible latest `@types/node` declarations; pinned `@types/node` to 16.18.126 to keep Node 16/Angular 14 compatibility. First invalid-login test had malformed tab JSON; corrected and reran successfully.
- Initial Karma run could not find Chrome; rerun using installed Edge via `CHROME_BIN` passed.

## Known issues

- No dedicated Oracle application schema credentials have been configured. The backend intentionally requires explicit environment variables and will not connect as SYS.
- Oracle schema and seed SQL have not been run. Creating an application schema owner and granting its required privileges is an administrator-level operation and requires approval.
- Seeded user password hashes are placeholders, requiring a password reset before a live login.
- Role permissions and official user-story allocation need the companion workbook.
- Browser-level UI checks are not yet performed.

## Git branches and commits

- `main` - initial commit `c96730c chore: initialize project repository`, preserved.
- `develop` — created from `main` and pushed to `origin`.
- `feature/sprint-0-foundation-login` - current branch, created from `develop`, committed and pushed.
- Implementation commit `0302aa5 feat(auth): implement Sprint 0 foundation and login` is present on the feature branch and `origin/feature/sprint-0-foundation-login`.
- No pull request or sprint tag has been created; feature remains unmerged, and Sprint 0 acceptance is pending Oracle checks and user review.

## Next objective

Complete the Oracle schema/application-account validation needed for Sprint 0, then present the login module and Sprint 0 deliverables for user verification. Do not start Sprint 1 without explicit approval.
