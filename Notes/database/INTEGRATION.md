# Database Module Integration (Transport & Logistics)

This note keeps Transport and Logistics aligned with the latest `database_module` contract.

## Source of truth

- Primary reference: `c:\AIML\OOAD\database_module\INTEGRATION.md`
- Integration artifact consumed by this subsystem:
  - `libs\database-module-1.0.0-SNAPSHOT-standalone.jar`

## Current integration contract

1. Use `database-module-1.0.0-SNAPSHOT-standalone.jar` on classpath.
2. Keep `scm-exception-handler-v3.jar` and `scm-exception-foundation.jar` on classpath when DB-backed exception logging is enabled.
3. Keep `libs\database.properties` on classpath for current handler runtime and include:
  - `db.url`
  - `db.user`
  - `db.password`
4. Keep `db.username` in the same file for compatibility with database-module config readers.
5. Environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) can still be used by database-module readers but do not replace handler's `db.user` expectation.
6. Do not require a local external `schema.sql` copy at runtime; canonical schema is embedded in the database module JAR and bootstrapped by the module.

## Classpath examples (Windows)

```bat
javac -cp "src;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -cp "bin;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.TransportApplication
```

## Existing-database migration

For existing local `OOAD` schemas created from older DB module versions, run migration from the database module repository:

```text
c:\AIML\OOAD\database_module\src\main\resources\sql\migration-to-canonical-schema.sql
```

Example:

```bash
mysql -u <user> -p OOAD < c:\AIML\OOAD\database_module\src\main\resources\sql\migration-to-canonical-schema.sql
```

## Quick validation from this subsystem

Run:

- `transport.ExceptionDbIntegrationSmokeTest`

Expected first integration signal:

- `DB module check: facade loaded successfully`

If this message appears, transport-to-database module linkage is active.