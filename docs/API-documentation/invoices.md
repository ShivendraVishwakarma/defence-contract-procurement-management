# Invoice and Payment API

All endpoints require an authenticated session. Mutating requests also require the CSRF token issued by `/api/auth/csrf`.

## Endpoints

- `GET /api/invoices` lists invoices by due date.
- `GET /api/invoices/options` returns contracts, invoice statuses and payment statuses.
- `POST /api/invoices` creates an invoice.
- `PATCH /api/invoices/{id}/status` changes an invoice status.
- `POST /api/invoices/{id}/payments` records a payment.

Invoice creation validates unique invoice numbers, positive amounts and due dates not earlier than invoice dates. Payments require a unique reference, positive amount, date, method and existing payment status.
