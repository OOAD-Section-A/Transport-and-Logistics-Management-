# Database Module Integration (Transport and Logistics)

This note keeps Transport and Logistics aligned with the latest `database_module` integration contract.

## Source of truth

- Primary reference: `c:\AIML\OOAD\database_module\INTEGRATION.md`
- DB integration artifact consumed by transport:
  - `libs\database-module-1.0.0-SNAPSHOT-standalone.jar`

## Contract summary

1. Add `database-module-1.0.0-SNAPSHOT-standalone.jar` to classpath when DB integration is required.
2. Configure credentials using one of the supported methods:
   - JVM properties (`-Ddb.url`, `-Ddb.username`, `-Ddb.password`)
   - environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)
   - `database.properties` file with `db.url`, `db.username`, `db.password`
3. Credentials are mandatory. There are no default credential fallbacks.
4. Do not use `db.user` as a primary key for this module; use `db.username`.
5. No manual schema setup is required in normal flow.
6. Schema initialization is automatic and drops plus recreates `OOAD` each run when facade or adapters initialize.

## Data safety warning

Because the module recreates `OOAD`, any local data in that database is reset during initialization.
Use a dedicated local/test instance for integration runs.

## Classpath example (Windows)

```bat
javac -cp "src;libs;libs\database-module-1.0.0-SNAPSHOT-standalone.jar;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar" -d bin @sources.list
java -cp "bin;libs;libs\database-module-1.0.0-SNAPSHOT-standalone.jar;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar" transport.ExceptionDbIntegrationSmokeTest
```

## Validation from transport

Run:

- `transport.ExceptionDbIntegrationSmokeTest`

Expected DB integration signal:

- `DB module check: facade loaded successfully`

If DB credentials or MySQL availability are wrong, smoke test prints a `DB module check failed` message.
