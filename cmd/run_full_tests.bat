@echo off
REM Full Test Suite — Transport and Logistics Management
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%\.."
set "ROOT=%cd%"
set "SRC=%ROOT%\src"
set "BIN=%ROOT%\bin"
set "LIB_DIR=%ROOT%\libs"
set "EXCEPTION_DIR=%ROOT%\exception-handling"
set "HANDLER_JAR=%EXCEPTION_DIR%\scm-exception-handler-v3.jar"
set "JNA_JAR=%EXCEPTION_DIR%\jna-5.18.1.jar"
set "JNA_PLATFORM_JAR=%EXCEPTION_DIR%\jna-platform-5.18.1.jar"
set "DELIVERY_JAR=%LIB_DIR%\delivery-monitoring-1.0.0.jar"
set "DB_JAR=%LIB_DIR%\database-module-1.0.0-SNAPSHOT-standalone.jar"
set "SOURCES_FILE=%ROOT%\sources.list"

set "CP=%SRC%;%LIB_DIR%;%HANDLER_JAR%;%JNA_JAR%;%JNA_PLATFORM_JAR%;%DELIVERY_JAR%"
if exist "%DB_JAR%" set "CP=%CP%;%DB_JAR%"

set "RUN_CP=%BIN%;%LIB_DIR%;%HANDLER_JAR%;%JNA_JAR%;%JNA_PLATFORM_JAR%;%DELIVERY_JAR%"
if exist "%DB_JAR%" set "RUN_CP=%RUN_CP%;%DB_JAR%"

echo ========================================
echo  CLEANING...
echo ========================================
if exist "%BIN%" rmdir /S /Q "%BIN%"
if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"
mkdir "%BIN%"

echo ========================================
echo  COMPILING...
echo ========================================
for /r "%SRC%" %%f in (*.java) do @echo %%f>>"%SOURCES_FILE%"
javac -cp "%CP%" -d "%BIN%" @"%SOURCES_FILE%"
if %errorlevel% neq 0 (
    echo COMPILATION FAILED
    exit /b 1
)

echo ========================================
echo  RUNNING FULL TEST SUITE...
echo ========================================
java -cp "%RUN_CP%" integration.FullTestSuite
set "RESULT=%errorlevel%"

if exist "%SOURCES_FILE%" del /F /Q "%SOURCES_FILE%"

echo.
if %RESULT% equ 0 (
    echo All tests passed.
) else (
    echo Some tests failed - see output above.
)
exit /b %RESULT%
