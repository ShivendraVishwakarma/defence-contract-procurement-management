# Contracts API

Base path: `/api/contracts`. Every endpoint requires an authenticated session. POST, PUT and PATCH also require the existing CSRF cookie/header pair. Responses use DTOs and do not expose JPA entities.

## Read endpoints

- `GET /api/contracts` returns the repository list (`200`).
- `GET /api/contracts/{id}` returns one contract (`200`) or `404` when it does not exist.
- `GET /api/contracts/options` returns existing vendor and contract-status options for forms (`200`).

## Create, edit and status

- `POST /api/contracts` creates a record (`201` and a `Location` header).
- `PUT /api/contracts/{id}` updates the contract number, title, vendor, dates, value and summary (`200`). It does not change status.
- `PATCH /api/contracts/{id}/status` changes status using an existing status code (`200`).
- Invalid input or references return `400`; missing contract IDs return `404`; unauthenticated requests return `401`. Invalid writes without a valid CSRF token are rejected by Spring Security.

Create request example:

```json
{
  "contractNumber": "J14-CN-2026-010",
  "title": "Fictional training support",
  "vendorId": 5,
  "statusCode": "DRAFT",
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "contractValue": 1500.00,
  "summary": "Fictional sample record"
}
```

Create and update validate required values, existing vendor/status references, unique contract numbers, schema field lengths, positive value and date order. No delete, approval, audit, upload, export, pagination or advanced filtering endpoints are included. All authenticated users can use Contracts for this implementation; official story IDs and role permission mapping remain pending the official workbook.

The implementation uses the existing `CONTRACTS`, `VENDORS` and `CONTRACT_STATUSES` schema. Authenticated list, detail, create, update and status operations were exercised against the live Oracle-backed application using fictional data. The API-created test record remains in the database because no delete endpoint is in scope.
