# Authentication API

Base path: `/api`. JSON request and response content uses UTF-8. Authentication uses an Oracle-backed user record, BCrypt password hashing and an HTTP session cookie. All unsafe requests require the CSRF cookie/header pair. The Angular development proxy keeps browser requests same-origin.

## `GET /api/health`

Returns `200` with `{"status":"UP"}`. Liveness only; it does not claim Oracle is reachable.

## `GET /api/auth/csrf`

Unauthenticated. Issues the `XSRF-TOKEN` cookie and returns `{"token":"..."}`. Angular sends the cookie value in `X-XSRF-TOKEN` for `POST` requests.

## `POST /api/auth/login`

Unauthenticated apart from CSRF validation.

Request:

```json
{"username":"a.sharma","password":"<assigned password>"}
```

Both values are required. Username is limited to 50 characters and password to 72. On success, the server rotates the session ID, saves the authenticated security context in the session, and returns the user profile without the password hash:

```json
{"id":1,"username":"a.sharma","displayName":"Aarav Sharma","email":"aarav.sharma@example.invalid","roles":["ROLE_SAFETY_OFFICER"]}
```

The browser receives an `HttpOnly`, `SameSite=Lax` session cookie. Failed credentials return `401` and the generic message `Invalid username or password`. Invalid input returns `400` with a generic validation message.

## `GET /api/auth/me`

Requires an authenticated session. Returns the same profile shape. Unauthenticated calls return `401`.

## `POST /api/auth/logout`

Requires an authenticated session and CSRF token. Invalidates the session and returns `204`.

## Account provisioning

`database/seed_data.sql` inserts the two role names from the brief and active sample account records with random, irrecoverable BCrypt placeholder hashes. They cannot be used for login until passwords are reset. Do not create shared or committed passwords. Exact role permissions remain pending the companion workbook.

The application connects to the existing `J14_APP` schema in `XEPDB1` through JDBC Thin at `jdbc:oracle:thin:@//127.0.0.1:1521/XEPDB1`. Supply the database password locally through `ORACLE_DB_PASSWORD`; it is not stored in project files. JDBC connectivity, Hibernate schema validation and the live CSRF-protected login flow have been verified locally.
