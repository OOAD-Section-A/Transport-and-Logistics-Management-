@echo off
REM Clean and Run Transport Management System
REM This script ensures clean execution with no interference from old .class files

set "ROOT=%~dp0.."
set "SRC=%ROOT%\src"
set "TEST=%ROOT%\test"
set "BIN=%ROOT%\bin"
set "LIB_DIR=%ROOT%\libs"
set "HANDLER_JAR=%ROOT%\libs\scm-exception-handler-v3.jar"
set "FOUNDATION_JAR=%ROOT%\libs\scm-exception-foundation.jar"
set "DB_JAR=%ROOT%\libs\database-module-1.0.0-SNAPSHOT-standalone.jar"
set "SOURCES_FILE=%ROOT%\sources.list"
set "LIB_CP=%SRC%;%LIB_DIR%;%HANDLER_JAR%;%FOUNDATION_JAR%"
set "RUN_CP=%BIN%;%LIB_DIR%;%HANDLER_JAR%;%FOUNDATION_JAR%"

if exist "%DB_JAR%" (
    set "LIB_CP=%LIB_CP%;%DB_JAR%"
    set "RUN_CP=%RUN_CP%;%DB_JAR%"
)

echo ========================================
echo CLEANING OLD COMPILED FILES...
echo ========================================
if exist "%BIN%" rmdir /S /Q "%BIN%"
if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"
for /r "%SRC%" %%f in (*.class) do del /F /Q "%%f"
for /r "%TEST%" %%f in (*.class) do del /F /Q "%%f"
mkdir "%BIN%"

echo ========================================
echo COMPILING SOURCE CODE...
echo ========================================
for /r "%SRC%" %%f in (*.java) do @echo %%f>>"%SOURCES_FILE%"
javac -cp "%LIB_CP%" -d "%BIN%" @"%SOURCES_FILE%"
if %errorlevel% neq 0 (
    echo COMPILATION FAILED!
    pause
    exit /b 1
)

echo ========================================
echo RUNNING TRANSPORT APPLICATION...
echo ========================================
java -cp "%RUN_CP%" transport.TransportApplication
if %errorlevel% neq 0 (
    echo EXECUTION FAILED!
    pause
    exit /b 1
)

if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"

echo ========================================
echo EXECUTION COMPLETED SUCCESSFULLY!
echo ========================================
pause