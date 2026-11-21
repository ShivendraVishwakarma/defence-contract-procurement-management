# Development Status

Last updated: 2026-11-21

## Current sprint
Sprints 0-4 are implemented, automated-tested and live-verified against Oracle using fictional data.

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

- [x] Implement authenticated contract list, detail, create, edit and status change API and Angular views against the existing schema.
- [x] Add DTO validation, service/repository/controller layers, protected writes with CSRF, and backend/frontend tests.
- [x] Run backend tests, Angular production build, browser tests and diff whitespace validation.
- [x] Verify the authenticated contract list, detail, create, update and status endpoints against Oracle using fictional data.
- [ ] Reconcile official story IDs and role permissions when the workbook or explicit requirements are supplied.
- [x] Implement authenticated Vendor Evaluation and Selection flows.
- [x] Implement authenticated Invoice and Payment Processing flows.
- [x] Live-verify Vendor Evaluation and Invoice/Payment flows against Oracle with approved fictional data.
## Files created or modified

Commit `0302aa5` contains 70 added files: backend Maven/config/source/tests; Angular workspace, login, dashboard, navigation and unit tests; Oracle schema, fictional seed data and ER diagram; API, requirements, wireframe, database and test documentation; root README and this status file. Use `git show --name-status 0302aa5` for the exact path list.

The current uncommitted Sprint 0-2 implementation includes authentication, Contracts and Requisitions backend/frontend slices, their tests, API documentation and verification scripts. `database/schema.sql` and `database/seed_data.sql` are unchanged.
The current worktree also contains the Sprint 3 Vendor Evaluation and Sprint 4 Invoice/Payment backend/frontend slices and API documentation. The schema and seed scripts remain unchanged.

## Tests executed

- `mvn.cmd -f backend/pom.xml test`: passed; 40 tests, 0 failures/errors.
- `npm.cmd run build` from `frontend/`: passed.
- `$env:CHROME_BIN = 'C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe'; npm.cmd test -- --watch=false --browsers=ChromeHeadless` from `frontend/`: passed; 23 browser tests.
- `git diff --check`: passed.
- Read-only local SYSDBA checks confirmed XEPDB1 is READ WRITE, the three J14_APP tables exist with contracts/vendors/status row counts of 3/5/5, contract constraints are enabled, and all 3 original contracts join to existing vendors and statuses.
- The backend returned health HTTP 200 and PUT/PATCH CORS preflight HTTP 200; the Angular proxy returned health HTTP 200 and unauthenticated GET `/api/contracts` returned 401.
- Authenticated live verification reported successful Contracts and Requisitions list, options, create, detail, update, item update, status update and final detail requests.
- A read-only SYSDBA query confirmed the fictional API-created contract row (ID 21) persisted with the updated title, status ACTIVE, value 1.23, and an existing vendor link. The test row is retained because the approved API has no delete operation.
- No password was requested, printed, stored in the repository or committed. No schema or seed scripts were run.
## Known issues

- Official story IDs and any role permission mapping remain pending the workbook or explicit user-provided requirements.
- No Oracle schema or seed changes were made.
## Git branches and commits

- `main` - initial commit `c96730c chore: initialize project repository`, preserved.
- `develop` - created from `main` and pushed to `origin`.
- `feature/sprint-0-foundation-login` - current branch, created from `develop`, committed and pushed.
- Implementation commit `0302aa5 feat(auth): implement Sprint 0 foundation and login` is present on the feature branch and `origin/feature/sprint-0-foundation-login`.
- Follow-up commits: `3e64312 docs: record Sprint 0 verification status`; `27df140 refactor(auth): configure the DAO provider explicitly`.
- No pull request or sprint tag has been created; the feature remains unmerged pending final review and user approval.

## Sprint 2: Procurement Planning and Requisition Management

- [x] Implement authenticated requisition list and detail APIs and Angular views.
- [x] Implement create/edit requisition forms, updates using existing status rows, and add/edit requisition items.
- [x] Validate required fields, date formats, quantities, prices, line numbers and existing status references.
- [x] Derive requester identity from the authenticated session and protect writes with the existing CSRF behavior.
- [x] Preserve the existing schema and seed scripts; no deletion, vendor selection, invoices/payments, export, advanced filtering, approval flow or role rules were added.
- [x] Maven tests (40 passed) and Angular production build passed.
- [x] Complete Edge browser tests (23 passed), Angular production build and `git diff --check`.
- [x] Finish live Oracle endpoint verification. Oracle XE/listener are running and XEPDB1 is `READ WRITE`; requisition ID 21 (`J14-REQ-VERIFY-20261121115106504`) and item ID 21 persist. The no-create resume script verified item update, status update and final detail successfully. No credentials or schema/seed changes were made.
- Official story IDs, role permissions and approval workflow remain pending the official workbook or explicit user-provided requirements.

See `docs/API-documentation/requisitions.md` and `docs/test-reports/sprint-2-test-report.md` for endpoint details and validation status.
