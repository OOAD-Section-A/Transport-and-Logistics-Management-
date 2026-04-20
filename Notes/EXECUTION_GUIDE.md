# Execution Guide - Transport and Logistics Management

## Overview
This subsystem is integrated with the latest shared exception and database contracts:

- Exceptions are local popup plus local Windows Event Viewer logging.
- Exception GUI reads from Windows Event Viewer (not from database tables).
- Database module auto bootstraps schema when facade or adapters are created.

## Prerequisites

- JDK available in PATH (`javac`, `java`)
- Exception dependency set in `exception-handling`:
  - `scm-exception-handler-v3.jar` (required)
  - `jna-5.18.1.jar` (required)
  - `jna-platform-5.18.1.jar` (required)
  - `scm-exception-viewer-gui.jar` (optional, only for GUI)
- DB integration dependency in `libs`:
  - `database-module-1.0.0-SNAPSHOT-standalone.jar` (optional, required for DB smoke checks)
- DB config file when DB module is used: `libs\database.properties`
  - Must include `db.url`, `db.username`, `db.password`
  - `db.pool.size` is optional
- MySQL server must be reachable for DB smoke checks

## One-time Event Viewer source registration (Administrator)

```batch
reg add "HKLM\SYSTEM\CurrentControlSet\Services\EventLog\Application\SCM-TransportandLogisticsManagement" /v EventMessageFile /t REG_SZ /d "%SystemRoot%\System32\EventCreate.exe" /f
```

## Quick Run (Windows)

```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
clean_and_run.bat
```

## Build Integration JAR (Windows)

```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
build_integration_jar.bat
```

Generated outputs:

- `libs\transport-and-logistics-management.jar` (primary artifact)
- `dist\transport-and-logistics-management.jar` (mirror copy)

## Manual Build and Run

```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-"
if exist bin rmdir /S /Q bin
mkdir bin

dir /s /b src\*.java > sources.list
javac -cp "src;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -cp "bin;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.TransportApplication
del /F /Q sources.list
```

## Smoke test run (Exception + DB)

```powershell
& java '-Djava.awt.headless=true' '-cp' 'bin;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar' 'transport.ExceptionDbIntegrationSmokeTest'
```

## GUI launch (optional)

```batch
java -cp "exception-handling\scm-exception-handler-v3.jar;exception-handling\scm-exception-viewer-gui.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar" com.scm.gui.ExceptionViewerGUI
```

On first launch, choose subsystem in dropdown. GUI then reads from local Event Viewer.

## Database behavior reminder

- Do not run local `schema.sql` manually for normal integration flow.
- DB module auto-drops and recreates `OOAD` from embedded schema.
- Any local `OOAD` data will be reset when facade/adapter initializes schema.

## Troubleshooting

- `NoClassDefFoundError: com/sun/jna/...`
  - Add both JNA jars to classpath.
- Exception popup appears but no Event Viewer entries
  - Run source registration command as Administrator first.
- GUI opens but no entries
  - Confirm source registration and click `Refresh Now`.
- DB module check fails
  - Verify MySQL server status and `db.url`, `db.username`, `db.password` values.
- Compile errors about SCM packages
  - Confirm exception handler JAR exists in `exception-handling` and classpath includes it.
