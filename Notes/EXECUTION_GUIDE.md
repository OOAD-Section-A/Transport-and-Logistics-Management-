# Execution Guide - Transport and Logistics Management

## Overview
This project now uses the shared SCM exception modules directly, aligned with the realtime-delivery-monitoring integration style and team instructions.

## Prerequisites
- JDK available in PATH (`javac`, `java`)
- These module folders present inside the project:
  - `libs\scm-exception-handler-v3.jar`
  - `libs\scm-exception-foundation.jar`
  - Optional for DB-backed logging: `libs\database-module-1.0.0-SNAPSHOT-standalone.jar`

## Quick Run (Windows)
```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
clean_and_run.bat
```

## Manual Build and Run
```batch
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-"
if exist bin rmdir /S /Q bin
mkdir bin

dir /s /b src\*.java > sources.list
javac -cp "src;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -cp "bin;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.TransportApplication
del /F /Q sources.list
```

## Exception Handling Integration Notes
- Transport exception calls use `TransportLogisticsSubsystem.INSTANCE`.
- Catch blocks report through `SCMExceptionHandler.INSTANCE.handle(...)` where needed.
- No local custom exception stack is required for runtime handling.

## Optional Viewer Launch
If the viewer module is available:
```batch
java -cp "libs\scm-exception-handler-v3.jar;libs\scm-exception-viewer-gui.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" com.scm.gui.ExceptionViewerGUI
```

## Troubleshooting
- If `package com.scm... does not exist`, verify both SCM module paths.
- If database-backed handler mode is enabled externally, keep DB properties file near the handler module as instructed by the integration team.
- If runtime fails, re-run `cmd\clean_and_run.bat` after deleting `bin`.