@echo off
REM Database Integration Test Script
REM Compiles and runs the DatabaseIntegrationTest to verify database connectivity

setlocal enabledelayedexpansion

set "ROOT=%~dp0.."
set "SRC=%ROOT%\src"
set "TEST=%ROOT%\test"
set "BIN=%ROOT%\bin"
set "LIB_DIR=%ROOT%\libs"
set "EXCEPTION_DIR=%ROOT%\exception-handling"
set "CONFIG_FILE=%ROOT%\database.properties"
set "HANDLER_JAR=%EXCEPTION_DIR%\scm-exception-handler-v3.jar"
set "JNA_JAR=%EXCEPTION_DIR%\jna-5.18.1.jar"
set "JNA_PLATFORM_JAR=%EXCEPTION_DIR%\jna-platform-5.18.1.jar"
set "DB_JAR=%LIB_DIR%\database-module-1.0.0-SNAPSHOT-standalone.jar"
set "SOURCES_FILE=%ROOT%\sources.list"

REM Build classpath with database module
set "LIB_CP=%SRC%;%LIB_DIR%;%HANDLER_JAR%;%JNA_JAR%;%JNA_PLATFORM_JAR%"
if exist "%DB_JAR%" (
    set "LIB_CP=!LIB_CP!;%DB_JAR%"
) else (
    echo.
    echo WARNING: Database module JAR not found at:
    echo %DB_JAR%
    echo.
)

REM Runtime classpath - include src for database.properties
set "RUN_CP=%BIN%;%SRC%;%LIB_DIR%;%HANDLER_JAR%;%JNA_JAR%;%JNA_PLATFORM_JAR%"
if exist "%DB_JAR%" (
    set "RUN_CP=!RUN_CP!;%DB_JAR%"
)

echo.
echo ========================================
echo DATABASE INTEGRATION TEST
echo ========================================
echo.

REM Check for database.properties
if not exist "%CONFIG_FILE%" (
    echo ERROR: Configuration file not found!
    echo Expected location: %CONFIG_FILE%
    echo.
    echo Please create database.properties with the following content:
    echo   db.url=jdbc:mysql://localhost:3306/OOAD
    echo   db.username=root
    echo   db.password=your_password
    echo.
    pause
    exit /b 1
)

echo Configuration file found: %CONFIG_FILE%
echo.

REM Check for database module JAR
if not exist "%DB_JAR%" (
    echo ERROR: Database module JAR not found!
    echo Expected location: %DB_JAR%
    echo.
    echo Please verify the database_module has been built and placed in libs/
    echo.
    pause
    exit /b 1
)

echo Database module JAR found: %DB_JAR%
echo.

REM Check for exception handler JAR
if not exist "%HANDLER_JAR%" (
    echo ERROR: Exception handler JAR not found!
    echo Expected location: %HANDLER_JAR%
    echo.
    pause
    exit /b 1
)

echo ========================================
echo CLEANING OLD COMPILED FILES...
echo ========================================
if exist "%BIN%" (
    echo Removing bin directory...
    rmdir /S /Q "%BIN%"
)
mkdir "%BIN%"
echo.

echo ========================================
echo COMPILING INTEGRATION TEST...
echo ========================================
echo Classpath:
echo   Compilation: %LIB_CP%
echo.

REM Generate sources list
for /r "%SRC%" %%f in (*.java) do @echo %%f>>"%SOURCES_FILE%"

REM Compile
javac -cp "%LIB_CP%" -d "%BIN%" @"%SOURCES_FILE%"
if !errorlevel! neq 0 (
    echo.
    echo COMPILATION FAILED!
    echo Please check the error messages above.
    echo.
    if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"
    pause
    exit /b 1
)

echo Compilation successful!
echo.

echo ========================================
echo RUNNING DATABASE INTEGRATION TEST...
echo ========================================
echo.

REM Run the integration test with database.properties in classpath
java -cp "%RUN_CP%" transport.DatabaseIntegrationTest

set "TEST_RESULT=!errorlevel!"

echo.
if !TEST_RESULT! equ 0 (
    echo ========================================
    echo TEST EXECUTION COMPLETED SUCCESSFULLY!
    echo ========================================
) else (
    echo ========================================
    echo TEST EXECUTION FAILED!
    echo ========================================
)

if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"

echo.
pause
exit /b !TEST_RESULT!
