# Execution Guide - Transport and Logistics Management

## Overview
This project now uses the shared SCM exception modules directly, aligned with the realtime-delivery-monitoring integration style and team instructions.

## Prerequisites
- JDK available in PATH (`javac`, `java`)
- These module folders present inside the project:
  - `libs\scm-exception-handler-v3.jar`
  - `libs\scm-exception-foundation.jar`
  - Required for DB-backed logging: `libs\database-module-1.0.0-SNAPSHOT-standalone.jar`
  - Required config file: `libs\database.properties`
    - Must include `db.url`, `db.user`, `db.password`
    - Keep `db.username` too for compatibility with database-module config readers
  - Environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) may be used by database-module config readers, but do not replace handler requirement for `db.user`
  - No external schema file is required at runtime; canonical schema is embedded in the database module JAR

## Quick Run (Windows)
```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
clean_and_run.bat
```

## Build Integration JAR (Windows)
Create a reusable JAR for partner teams:

```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
build_integration_jar.bat
```

Generated outputs:
- `libs\transport-and-logistics-management.jar` (primary artifact to share)
- `dist\transport-and-logistics-management.jar` (mirror copy)

Example usage by another subsystem:

```batch
javac -cp "lib\transport-and-logistics-management.jar" MyIntegration.java
java -cp "lib\transport-and-logistics-management.jar;." MyIntegrationMain
```

## Manual Build and Run
```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-"
if exist bin rmdir /S /Q bin
mkdir bin

dir /s /b src\*.java > sources.list
javac -cp "src;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -cp "bin;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.TransportApplication
del /F /Q sources.list
```

## Exception Handling Integration Notes
- Transport exception calls use `TransportLogisticsSubsystem.INSTANCE`.
- Catch blocks report through `SCMExceptionHandler.INSTANCE.handle(...)` where needed.
- No local custom exception stack is required for runtime handling.

## Optional Viewer Launch
If the viewer module is available:
```batch
java -cp "libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-viewer-gui.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" com.scm.gui.ExceptionViewerGUI
```

## PowerShell-safe smoke test run

```powershell
& java '-Djava.awt.headless=true' '-cp' 'bin;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar' 'transport.ExceptionDbIntegrationSmokeTest'
```

## Troubleshooting
- If `package com.scm... does not exist`, verify both SCM module paths.
- Ensure `libs\database.properties` contains `db.url`, `db.user`, and `db.password`.
- Keep `db.username` in the same file for compatibility with database-module config readers.
- Ensure MySQL is reachable.
- If DB-backed logging is enabled, ensure both `libs\database-module-1.0.0-SNAPSHOT-standalone.jar` and `libs\scm-exception-handler-v3.jar` are present on classpath.
- If runtime fails, re-run `cmd\clean_and_run.bat` after deleting `bin`.