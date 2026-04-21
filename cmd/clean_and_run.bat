@echo off
setlocal enabledelayedexpansion

REM Clean and Run Transport Management System

set "ROOT=%~dp0.."
set "SRC=%ROOT%\src"
set "TEST=%ROOT%\test"
set "BIN=%ROOT%\bin"
set "LIB_DIR=%ROOT%\libs"
set "EXCEPTION_DIR=%ROOT%\exception-handling"

set "HANDLER_JAR=%EXCEPTION_DIR%\scm-exception-handler-v3.jar"
set "VIEWER_JAR=%EXCEPTION_DIR%\scm-exception-viewer-gui.jar"
set "JNA_JAR=%EXCEPTION_DIR%\jna-5.18.1.jar"
set "JNA_PLATFORM_JAR=%EXCEPTION_DIR%\jna-platform-5.18.1.jar"
set "DB_JAR=%LIB_DIR%\database-module-1.0.0-SNAPSHOT-standalone.jar"
set "DELIVERY_JAR=%LIB_DIR%\ramen-noodles-delivery-monitoring.jar"

set "SOURCES_FILE=%ROOT%\sources.list"

set "WMS_SRC=C:\AIML\OOAD\SCM-Subsystem-2-WMS\src"
set "WMS_BIN=C:\AIML\OOAD\SCM-Subsystem-2-WMS\bin"

REM Base classpaths
set "LIB_CP=%SRC%;%WMS_SRC%;%LIB_DIR%;%HANDLER_JAR%;%JNA_JAR%;%JNA_PLATFORM_JAR%"
set "RUN_CP=%BIN%;%WMS_BIN%;%LIB_DIR%;%HANDLER_JAR%;%JNA_JAR%;%JNA_PLATFORM_JAR%"

REM Optional JARs
if exist "%DELIVERY_JAR%" (
    set "LIB_CP=!LIB_CP!;%DELIVERY_JAR%"
    set "RUN_CP=!RUN_CP!;%DELIVERY_JAR%"
)

if exist "%DB_JAR%" (
    set "LIB_CP=!LIB_CP!;%DB_JAR%"
    set "RUN_CP=!RUN_CP!;%DB_JAR%"
)

if exist "%VIEWER_JAR%" (
    set "RUN_CP=!RUN_CP!;%VIEWER_JAR%"
)

REM Dependency checks
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

echo ========================================
echo RESETTING DATABASE...
echo ========================================
mysql -u root -p1016 -e "DROP DATABASE IF EXISTS ooad;"
mysql -u root -p1016 -e "CREATE DATABASE ooad;"
mysql -u root -p1016 --force ooad < "%ROOT%\Notes\database\schema.sql"
echo Database reset complete.

echo ========================================
echo CLEANING OLD COMPILED FILES...
echo ========================================
if exist "%BIN%" rmdir /S /Q "%BIN%"
if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"

for /r "%SRC%" %%f in (*.class) do del /F /Q "%%f"
for /r "%TEST%" %%f in (*.class) do del /F /Q "%%f"

mkdir "%BIN%"
copy "%LIB_DIR%\database.properties" "%BIN%\database.properties"

echo ========================================
echo COMPILING SOURCE CODE...
echo ========================================

for /r "%SRC%" %%f in (*.java) do (
    echo %%f>>"%SOURCES_FILE%"
)

javac -cp "!LIB_CP!" -d "%BIN%" @"%SOURCES_FILE%"

if errorlevel 1 (
    echo COMPILATION FAILED!
    pause
    exit /b 1
)

echo ========================================
echo RUNNING TRANSPORT APPLICATION...
echo ========================================

java -Ddb.url=jdbc:mysql://localhost:3306/ooad?createDatabaseIfNotExist=true ^
     -Ddb.username=root ^
     -Ddb.password=1016 ^
     -Ddb.pool.size=5 ^
     -cp "!RUN_CP!" transport.TransportApplication

if errorlevel 1 (
    echo EXECUTION FAILED!
    pause
    exit /b 1
)

if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"

echo ========================================
echo EXECUTION COMPLETED SUCCESSFULLY!
echo ========================================
pause