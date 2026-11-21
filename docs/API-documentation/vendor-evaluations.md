# Vendor Evaluation API

All endpoints require an authenticated session. Mutating requests also require the CSRF token issued by `/api/auth/csrf`.

## Endpoints

- `GET /api/vendor-evaluations` lists evaluations ordered by newest first.
- `GET /api/vendor-evaluations/options` returns active vendor and requisition choices.
- `POST /api/vendor-evaluations` creates an evaluation for the authenticated user.
- `PUT /api/vendor-evaluations/{id}` updates an evaluation.

Evaluation requests contain `requisitionId`, `vendorId`, `score` (0-100), and `recommendation`. A vendor can be evaluated only once per requisition.
