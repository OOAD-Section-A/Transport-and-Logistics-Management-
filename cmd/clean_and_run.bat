@echo off
REM Clean and Run Transport Management System
REM This script ensures clean execution with no interference from old .class files

echo ========================================
echo CLEANING OLD COMPILED FILES...
echo ========================================
if exist "bin" rmdir /S /Q bin
for /r src %%f in (*.class) do del /F /Q "%%f"
for /r test %%f in (*.class) do del /F /Q "%%f"
for /r . %%f in (*.class) do del /F /Q "%%f"
mkdir bin

echo ========================================
echo COMPILING SOURCE CODE...
echo ========================================
for /r src %%f in (*.java) do javac -cp src -d bin "%%f"
if %errorlevel% neq 0 (
    echo COMPILATION FAILED!
    pause
    exit /b 1
)

echo ========================================
echo RUNNING TRANSPORT APPLICATION...
echo ========================================
java -cp bin transport.TransportApplication
if %errorlevel% neq 0 (
    echo EXECUTION FAILED!
    pause
    exit /b 1
)

echo ========================================
echo EXECUTION COMPLETED SUCCESSFULLY!
echo ========================================
pause