# Sprint 2 Procurement Requisition Test Report

Date: 2026-11-21

## Implemented scope

- Authenticated requisition list and detail views, create and edit forms, status updates, and item add/edit.
- REST DTO validation, repository projections, service/controller layers, authenticated requester attribution, and CSRF-protected writes.
- Status options are read from existing Oracle status records. No status transition or approval policy is applied.
- Existing schema and seed scripts were not changed. No delete, vendor evaluation/selection, invoice/payment, export, pagination, or advanced filtering was added.

## Automated validation

- `mvn.cmd -f backend/pom.xml test`: passed; 36 tests, 0 failures, 0 errors; `BUILD SUCCESS`. Includes endpoint response and request validation checks, service behavior, unauthenticated access and CSRF enforcement.
- Focused regression command `mvn.cmd -f backend/pom.xml '-Dtest=RequisitionServiceTest,RequisitionControllerTest' test`: passed; 10 tests, 0 failures/errors. `mvn.cmd -f backend/pom.xml -DskipTests compile`: passed.
- `scripts/verify-sprint2-existing-record.ps1` passed PowerShell syntax parsing; it was not run against the authenticated application.
- From `frontend/`, `npm.cmd run build`: passed.
- From `frontend/`, with `CHROME_BIN` set to the installed Microsoft Edge executable, `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: passed; 23 browser tests.
- `git diff --check`: passed.

## Live Oracle verification

Passed. Oracle XE and its listener are running; XEPDB1 is `READ WRITE`. The no-create verification script authenticated successfully and returned HTTP 200 for options, detail, item update, status update and final detail. Requisition ID `21` (`J14-REQ-VERIFY-20261121115106504`) and item ID `21` remain as fictional verification data. No credentials were requested or printed; no schema or seed scripts were run, and the fictional row is retained.

## Pending decisions

Official story IDs, role permissions and approval workflows remain pending the official workbook or explicit user-provided requirements. This implementation does not assign Sprint 2 story IDs or invent access rules.
