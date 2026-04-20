@echo off
REM Build Integration JAR for Transport and Logistics Management

setlocal

set "ROOT=%~dp0.."
set "SRC=%ROOT%\src"
set "BIN=%ROOT%\bin"
set "LIB_DIR=%ROOT%\libs"
set "DIST=%ROOT%\dist"
set "EXCEPTION_DIR=%ROOT%\exception-handling"
set "HANDLER_JAR=%EXCEPTION_DIR%\scm-exception-handler-v3.jar"
set "VIEWER_JAR=%EXCEPTION_DIR%\scm-exception-viewer-gui.jar"
set "JNA_JAR=%EXCEPTION_DIR%\jna-5.18.1.jar"
set "JNA_PLATFORM_JAR=%EXCEPTION_DIR%\jna-platform-5.18.1.jar"
set "DB_JAR=%ROOT%\libs\database-module-1.0.0-SNAPSHOT-standalone.jar"
set "SOURCES_FILE=%ROOT%\sources.list"
set "MANIFEST_FILE=%ROOT%\manifest.transport.tmp"
set "OUTPUT_JAR=%LIB_DIR%\transport-and-logistics-management.jar"
set "DIST_JAR=%DIST%\transport-and-logistics-management.jar"
set "LIB_CP=%SRC%;%LIB_DIR%;%HANDLER_JAR%;%JNA_JAR%;%JNA_PLATFORM_JAR%"

if not exist "%HANDLER_JAR%" (
    echo MISSING DEPENDENCY: %HANDLER_JAR%
    exit /b 1
)

if not exist "%JNA_JAR%" (
    echo MISSING DEPENDENCY: %JNA_JAR%
    exit /b 1
)

if not exist "%JNA_PLATFORM_JAR%" (
    echo MISSING DEPENDENCY: %JNA_PLATFORM_JAR%
    exit /b 1
)

if exist "%DB_JAR%" (
    set "LIB_CP=%LIB_CP%;%DB_JAR%"
)

echo ========================================
echo BUILDING TRANSPORT INTEGRATION JAR...
echo ========================================

if exist "%BIN%" rmdir /S /Q "%BIN%"
if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"
if exist "%MANIFEST_FILE%" del /F /Q "%MANIFEST_FILE%"
if exist "%OUTPUT_JAR%" del /F /Q "%OUTPUT_JAR%"
if exist "%DIST_JAR%" del /F /Q "%DIST_JAR%"

if not exist "%BIN%" mkdir "%BIN%"
if not exist "%DIST%" mkdir "%DIST%"

echo Compiling source files...
for /r "%SRC%" %%f in (*.java) do @echo %%f>>"%SOURCES_FILE%"
javac -cp "%LIB_CP%" -d "%BIN%" @"%SOURCES_FILE%"
if %errorlevel% neq 0 (
    echo COMPILATION FAILED!
    if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"
    if exist "%MANIFEST_FILE%" del /F /Q "%MANIFEST_FILE%"
    exit /b 1
)

echo Creating JAR manifest...
(
    echo Manifest-Version: 1.0
    echo Main-Class: transport.TransportApplication
) > "%MANIFEST_FILE%"

echo Packaging integration JAR...
jar cfm "%OUTPUT_JAR%" "%MANIFEST_FILE%" -C "%BIN%" .
if %errorlevel% neq 0 (
    echo JAR PACKAGING FAILED!
    if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"
    if exist "%MANIFEST_FILE%" del /F /Q "%MANIFEST_FILE%"
    exit /b 1
)

copy /Y "%OUTPUT_JAR%" "%DIST_JAR%" > nul

if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"
if exist "%MANIFEST_FILE%" del /F /Q "%MANIFEST_FILE%"

echo ========================================
echo JAR CREATED SUCCESSFULLY!
echo ========================================
echo Output (for partner teams):
echo   %OUTPUT_JAR%
echo Mirror copy:
echo   %DIST_JAR%
echo Runtime exception dependencies:
echo   %HANDLER_JAR%
echo   %JNA_JAR%
echo   %JNA_PLATFORM_JAR%
if exist "%VIEWER_JAR%" echo Optional GUI dependency: %VIEWER_JAR%
if exist "%DB_JAR%" echo Optional DB integration dependency: %DB_JAR%
