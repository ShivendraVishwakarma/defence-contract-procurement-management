# Procurement Requisitions API

Base path: `/api/requisitions`. Every endpoint requires an authenticated session. POST, PUT and PATCH require the existing CSRF cookie/header pair. Request and response DTOs are used; JPA entities are not exposed. The requester is taken from the signed-in user and is not supplied in the request body.

## Read endpoints

- `GET /api/requisitions` returns requisition summaries (`200`).
- `GET /api/requisitions/{id}` returns the requisition and its items (`200`) or `404` when missing.
- `GET /api/requisitions/options` returns the status records already present in `REQUISITION_STATUSES` (`200`).

## Write endpoints

- `POST /api/requisitions` creates a requisition (`201` with `Location`). Required fields are requisition number, title, justification, needed-by date and an existing status code.
- `PUT /api/requisitions/{id}` edits requisition number, title, justification and needed-by date (`200`).
- `PATCH /api/requisitions/{id}/status` changes to an existing status code (`200`). The API does not impose a transition or approval workflow.
- `POST /api/requisitions/{id}/items` adds an item (`200` with the updated requisition).
- `PUT /api/requisitions/{id}/items/{itemId}` edits an item belonging to that requisition (`200`).

Invalid fields, unknown status codes, duplicate requisition numbers or duplicate line numbers return `400`. Missing requisitions or items return `404`; unauthenticated requests return `401`; writes without CSRF protection are rejected. Quantities must be positive and fit `NUMBER(12,3)`; unit costs must be nonnegative and fit `NUMBER(16,2)`; line numbers must be positive and unique within a requisition. Dates must parse as ISO `yyyy-MM-dd`; no additional date policy is imposed.

No delete, vendor selection, invoice/payment, export, advanced filtering, pagination, approval workflow, or role permissions are implemented. All authenticated users can access the feature while the official workbook is unavailable.
