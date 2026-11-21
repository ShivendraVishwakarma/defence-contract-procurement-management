# Sprint 1 Contract Repository Test Report

Date: 2026-11-20

## Implemented scope

- Authenticated contract list and detail views.
- Contract creation and editing linked to existing vendors.
- Status changes through existing contract statuses.
- Spring DTO validation, repository projections, service/controller layers and CSRF-protected writes.
- No changes to schema or seed scripts; no delete, approval, audit history, upload, export, pagination or advanced filtering.
- Procurement, vendors, invoices and payments remain unchanged.

## Automated validation

- `mvn.cmd -f backend/pom.xml test`: **23 tests passed; 0 failures; 0 errors; BUILD SUCCESS.** Includes backend service/controller validation, unauthenticated reads, missing CSRF on writes, and CORS preflight for PUT/PATCH.
- From `frontend/`, `npm.cmd run build`: **passed.**
- From `frontend/`, with `CHROME_BIN` set to the installed Microsoft Edge executable, `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: **18 browser tests passed.** Includes list, detail, form, successful and failed saves, status, loading, empty and error states.
- `git diff --check`: passed.

## Live Oracle verification

**Verified against the live Oracle-backed application.** The user restarted the backend with `ORACLE_DB_PASSWORD` set locally. I observed the current backend return health HTTP 200 and PUT/PATCH CORS preflight HTTP 200; the Angular API proxy returned HTTP 200 for health and HTTP 401 for unauthenticated contract access. This is distinct from the earlier stale backend process, which was stopped.

The user-provided authenticated browser result reported list `200`, options `200`, create `201`, detail `200`, and update `200`. The supplied display was truncated after update; the verification script prints its result only after it checks the status PATCH returned `200` and a final detail GET returned `200`. A read-only SYSDBA query independently confirmed the fictional contract created by the script persisted as ID 21 with its updated title, status `ACTIVE`, value `1.23`, and link to an existing vendor. The fictional record remains in the database because this feature has no delete endpoint.

No password was requested, printed, stored in project files or committed. No schema or seed scripts were run. The earlier read-only SYSDBA checks confirmed XEPDB1 is READ WRITE, the J14_APP contract/vendor/status tables exist with their expected data, and constraints are enabled.

## Scope and remaining decisions

- The official workbook/story mapping is unavailable. No story IDs or role permissions were invented. Contracts currently follows the approved rule that all authenticated users may access it.
- The official workbook/story mapping is still unavailable, so no story IDs or role permissions have been invented. All authenticated users may use Contracts for this implementation as directed.
