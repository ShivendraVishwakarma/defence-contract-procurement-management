# Database scripts

Connect to Oracle Database 21c XE service `XEPDB1` as the application schema owner. Execute `schema.sql` once, then `seed_data.sql`. Both scripts target the current schema and create fictional, non-sensitive training records. The schema script contains no destructive DDL. Seeded user hashes are BCrypt placeholders and are not usable for login until passwords are reset.
