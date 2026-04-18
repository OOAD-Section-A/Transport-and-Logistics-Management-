@echo off
REM Delete all compiled Java .class files recursively from the current project.

echo Cleaning .class files from the project...
for /r %%f in (*.class) do del /F /Q "%%f"
echo Done.
