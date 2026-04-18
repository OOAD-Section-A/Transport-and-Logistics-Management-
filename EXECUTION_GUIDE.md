# Clean Execution Guide - Transport Management System

## Overview
This guide ensures clean execution of the TMS without interference from old compiled files or cached data.

## Quick Clean & Run (Windows)
1. Double-click `clean_and_run.bat` in the project root
2. Or run manually in Command Prompt:
   ```batch
   cd "Transport-and-Logistics-Management-"
   clean_and_run.bat
   ```

## Manual Steps (Command Prompt)

### Step 1: Clean Old Files
```batch
REM Navigate to project
cd "c:\Users\Aarya-2\Documents\ADOG\PESU\3rd Year --PESU\6th Sem\OOPs\OOAD_project\OOAD_Main\Transport-and-Logistics-Management-"

REM Delete all compiled class files and remove old bin
for /r %%f in (*.class) do del /F /Q "%%f"
if exist bin rmdir /S /Q bin
mkdir bin
```

### Step 2: Compile Fresh
```batch
REM Compile all Java sources recursively
for /r src %%f in (*.java) do javac -cp src -d bin "%%f"
```

### Step 3: Run Application
```batch
REM Execute the main demo
java -cp bin transport.TransportApplication
```

### Optional Clean Scripts
```batch
clean_classes.bat
```

### Optional PowerShell Clean
```powershell
./clean_classes.ps1
```

## What the Process Does
- **Cleans**: Removes entire `bin/` directory to prevent stale code issues
- **Compiles**: Fresh compilation with proper classpath to `bin/`
- **Runs**: Executes the full TMS demo with all features
- **Verifies**: Checks for errors and reports status

## Features Demonstrated
- All 7 design patterns (Factory, Builder, Prototype, Adapter, Facade, Proxy, Flyweight)
- MVC architecture (Controller → Facade → Service → Repository)
- SOLID & GRASP principles
- New TMS features: Freight Audit, Constraint Planning, Territory Management, etc.
- In-memory database (no external dependencies)

## Troubleshooting
- If compilation fails: Check Java JDK installation and PATH
- If runtime fails: Ensure all source files are present in `src/`
- For tests: Need JUnit JAR, then `javac -cp "junit.jar;src" test/**/*.java`
- Permission issues: Run as Administrator or check folder permissions
- If `.class` files were previously tracked, untrack them with:
  ```batch
  git rm --cached -r *.class
  git add .gitignore
  git commit -m "Untrack compiled class files"
  ```

## Output
- Console demo showing all patterns and features
- No persistent files created (all in-memory)
- Clean exit with success message

## File Structure
```
Transport-and-Logistics-Management-/
├── src/                    # Source code
├── bin/                    # Compiled classes (created during build)
├── test/                   # Unit tests
├── clean_and_run.bat       # Automated clean + run script
├── EXECUTION_GUIDE.md      # This file
└── TMS_CHANGES.md          # Implementation details
```

**Status: READY FOR CLEAN EXECUTION**