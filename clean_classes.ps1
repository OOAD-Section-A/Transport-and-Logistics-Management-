# clean_classes.ps1
# Remove all compiled Java .class files recursively from the current directory.

Write-Host "Cleaning all .class files from:" (Get-Location)
Get-ChildItem -Path . -Recurse -Filter *.class -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue
Write-Host "All .class files removed."
