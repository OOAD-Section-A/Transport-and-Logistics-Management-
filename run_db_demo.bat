@echo off
echo ==============================================================
echo Building Transport and Logistics Management System Integration
echo ==============================================================

if not exist bin mkdir bin

:: Find all java files
dir /s /B src\*.java > sources.list
dir /s /B libs\ramen-noodles-delivery-monitoring\*.java >> sources.list

echo Compiling sources...
javac -cp "src;libs\ramen-noodles-delivery-monitoring;libs\*;exception-handling\*" -d bin @sources.list

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)

echo ==============================================================
echo Running Database Integration Showcase
echo ==============================================================
java -cp "bin;libs\*;exception-handling\*" integration.DatabaseTeamIntegrationShowcase
