# J14 Angular frontend

Angular 14 client for the Defence Contract and Procurement Management project.

From this directory, install dependencies with `npm.cmd install` and run `npm.cmd start`. The development server listens on `http://localhost:4200` and proxies `/api` requests to the Spring Boot service at port 8080. Build with `npm.cmd run build`; run browser tests with `npm.cmd test -- --watch=false --browsers=ChromeHeadless` (set `CHROME_BIN` to an installed Chromium-compatible browser when Chrome is not installed).

See the root `README.md` for Oracle, backend and application setup. The module routes are navigation placeholders until their official user stories are allocated.
